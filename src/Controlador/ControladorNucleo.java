    package Controlador;

    import Modelo.Consultas.ConsultaNucleo;
    import Modelo.modelo.InstitucionModelo;
    import Modelo.modelo.Municipio;
    import Modelo.modelo.Nucleo;
    import Vistas.NucleoView;

    import javax.swing.*;

    import javax.swing.text.AbstractDocument;
    import javax.swing.text.AttributeSet;
    import javax.swing.text.BadLocationException;
    import javax.swing.text.DocumentFilter;
    import java.awt.event.ActionEvent;

    public class ControladorNucleo {
        private  final NucleoView view;
        private  final Nucleo nucleo;
        private  final ConsultaNucleo consultaNucleo;
        private  final Municipio m;
        private  final InstitucionModelo institucionModelo;


        public ControladorNucleo(NucleoView view, ConsultaNucleo consultaNucleo,
                                 Municipio m, InstitucionModelo institucionModelo, Nucleo nucleo) {
            this.view = view;
            this.nucleo=nucleo;
            this.consultaNucleo = consultaNucleo;
            this.m = m;
            this.institucionModelo = institucionModelo;
            listeners();
            configurarDocumentFilter();

        }
        private void configurarDocumentFilter() {
            ((AbstractDocument) view.nucleotxt.getDocument()).setDocumentFilter(new DocumentFilter() {
                @Override
                public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                    // Convierte a mayúsculas antes de insertar
                    if (string != null) {
                        super.insertString(fb, offset, string.toUpperCase(), attr);
                    } else {
                        super.insertString(fb, offset, null, attr);
                    }
                }

                @Override
                public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                    // Convierte a mayúsculas antes de reemplazar
                    super.replace(fb, offset, length, text != null ? text.toUpperCase() : null, attrs);
                }

                @Override
                public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                    // Solo llama al método remove
                    super.remove(fb, offset, length);
                }
            });
        }
        public void inicio(){
            view.setTitle("Registrar Nucleo");
            view.setLocationRelativeTo(null);
            view.sede.setText(m.getNombreM());
            view.nombreInstitucion.setText(institucionModelo.getNombreInstitucion());
        }

        private void actionPerformed(ActionEvent e){
            if(e.getSource()==view.guardarButton){
            guardarButton();
            }if(e.getSource()==view.cancelarButton){
                view.dispose();
            }
        }
        public void establecerDatos(String nombreInstitucion, String nombreMunicipio) {
            view.nombreInstitucion.setText(nombreInstitucion);
            view.nombreInstitucion.setEnabled(false);
            view.sede.setText(nombreMunicipio);
            view.sede.setEnabled(false);
        }

        private void guardarButton (){
            String nucleo1 = view.nucleotxt.getText();

            if(!nucleo1.isEmpty()) {
                nucleo.setNombreNucleo(nucleo1);
                if(consultaNucleo.ExisteNucleo(nucleo)<1){
                if(consultaNucleo.RegistrarNucleo(nucleo, institucionModelo, m)) {
                    JOptionPane.showMessageDialog(null, "El nucleo " + nucleo1 + " fue registrado correctamente");
                    view.dispose(); // Cerrar el formulario de núcleo
                    System.out.println(nucleo.getNombreNucleo());
                } else {
                    JOptionPane.showMessageDialog(null, "ERROR");
                }
                }else{
                    JOptionPane.showMessageDialog(null, "EL nucleo ya esta registrado en la base de datos");
                }
            }
        }

        private void listeners(){
            this.view.guardarButton.addActionListener(this::actionPerformed);
            this.view.cancelarButton.addActionListener(this::actionPerformed);

        }



    }
