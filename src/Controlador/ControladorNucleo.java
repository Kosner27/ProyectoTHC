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
            view.setVisible(true);  // Esto debería hacerse solo cuando sea necesario

        }

        private void actionPerformed(ActionEvent e){
            if(e.getSource()==view.guardarButton){
            guardarButton();
            }if(e.getSource()==view.cancelarButton){
                view.dispose();
            }
        }
        public void establecerDatos(String nombreInstitucion, String nombreMunicipio) {
            System.out.println(nombreInstitucion);
            view.nombreInstitucion.setText(nombreInstitucion);
            view.nombreInstitucion.setEditable(false);
            view.sede.setText(nombreMunicipio);
            view.sede.setEditable(false);
        }

        private void guardarButton (){
            String nucleo1 = view.nucleotxt.getText();
            int hectares = Integer.parseInt(view.hectareas.getText());
            if(!nucleo1.isEmpty()) {
                nucleo.setNombreNucleo(nucleo1);
                nucleo.setHectareas(hectares);
                if(consultaNucleo.ExisteNucleo(nucleo)<1){
                if(consultaNucleo.RegistrarNucleo(nucleo, m,institucionModelo)) {
                    JOptionPane.showMessageDialog(view, "El nucleo " + nucleo1 + " fue registrado correctamente");
                    view.dispose(); // Cerrar el formulario de núcleo
                    System.out.println(nucleo.getNombreNucleo());
                } else {
                    JOptionPane.showMessageDialog(view, "ERROR");
                }
                }else{
                    JOptionPane.showMessageDialog(view, "EL nucleo ya esta registrado en la base de datos");
                }
            }
        }

        private void listeners(){
            this.view.guardarButton.addActionListener(this::actionPerformed);
            this.view.cancelarButton.addActionListener(this::actionPerformed);

        }



    }
