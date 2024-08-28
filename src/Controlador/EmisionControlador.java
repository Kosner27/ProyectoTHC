package Controlador;
import Diccionario.Diccionario;
import Modelo.Consultas.ConsultasEmision;
import Modelo.modelo.EmisionModelo;
import Vistas.Emision;


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class EmisionControlador implements ActionListener {
    private EmisionModelo mod;
    private ConsultasEmision consul;
    private Emision view;
    private TableRowSorter<DefaultTableModel> sorter;
    public static Diccionario factorEmision = new Diccionario();

    public EmisionControlador(EmisionModelo mod, ConsultasEmision consul, Emision view) {
        this.mod = mod;
        this.consul = consul;
        this.view = view;
        this.view.guardarButton.addActionListener(this);
        this.view.inicioButton.addActionListener(this);
        view.Fuente.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.Fuente.getSelectedItem() != "Energia") {
                    view.Estado.setVisible(true);
                    view.estado.setVisible(true);
                    view.nombre.setText(" ");
                    view.Factor.setText(" ");
                } else {
                    view.Estado.setVisible(false);
                    view.estado.setVisible(false);
                    view.nombre.setText("Energia");
                }
                if (view.Fuente.getSelectedItem() == " ") {
                    view.Estado.setVisible(false);
                    view.estado.setVisible(false);
                    view.nombre.setText(" ");
                    view.Factor.setText(" ");
                }
            }
        });

       view.nombre.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizar();
                actualizar2();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizar();
                actualizar2();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizar();
                actualizar2();
            }
        });


    }
    private void actualizar2() {
        // Obtiene el texto actual del JTextField
        String text = view.nombre.getText();

        // Convierte todo el texto a mayúsculas
        String upperCaseText = text.toUpperCase();

        // Si el texto en mayúsculas es diferente del actual, actualiza el JTextField
        if (!text.equals(upperCaseText)) {
            // Se usa un SwingUtilities.invokeLater para asegurar que el cambio de texto ocurra en el hilo de eventos
            SwingUtilities.invokeLater(() -> view.nombre.setText(upperCaseText));
        }
    }


    private void actualizar() {
        String Nombre = view.nombre.getText().trim();

        if (!Nombre.isEmpty()) {
            Double valor = factorEmision.buscarClave(Nombre);
            if (valor != null) {
                view.Factor.setText(valor.toString());
            } else {
                view.Factor.setText("");
            }
        }
    }
    public void iniciar(){
        view.setTitle("Registrar Emision");
        view.setLocationRelativeTo(null);
        loadEmisionesExistentes();
        DefaultTableModel tableModel = (DefaultTableModel) view.Emisiones.getModel();
        sorter = new TableRowSorter<>(tableModel);
        view.Emisiones.setRowSorter(sorter);
    }
    public void actionPerformed(ActionEvent e) {
        String EmisionNombre=view.nombre.getText();
        if (e.getSource() == view.guardarButton) {
            if(consul.ExisteEmision(EmisionNombre)==0){
                mod.setTipoFuente(view.Fuente.getSelectedItem().toString());
                mod.setNombreFuente(view.nombre.getText());
                mod.setEstadoFuente(view.Estado.getSelectedItem().toString());
                mod.setFactorEmision(Double.parseDouble(view.Factor.getText()));
                mod.setUnidadMedidad(view.Unidad.getSelectedItem().toString());
                mod.setAlcance(view.Alcance.getSelectedItem().toString());
                if (!view.Fuente.getSelectedItem().toString().isEmpty() && !view.Unidad.getSelectedItem().toString().isEmpty()
                        && !view.Alcance.getSelectedItem().toString().isEmpty() && !view.Factor.getText().isEmpty()
                        && !view.nombre.getText().isEmpty()) {
                    String[] rows = {view.Fuente.getSelectedItem().toString(),view.Estado.getSelectedItem().toString(),view.nombre.getText(),view.Unidad.getSelectedItem().toString(),view.Factor.getText(), view.Alcance.getSelectedItem().toString()};
                    DefaultTableModel tableModel = (DefaultTableModel) view.Emisiones.getModel();
                    tableModel.addRow(rows);
                    view.Emisiones.setModel(tableModel);
                    view.Emisiones.setVisible(true);
                    view.Contenedor.setVisible(true);

                    SwingUtilities.invokeLater(() -> {
                        view.Emisiones.getColumnModel().getColumn(0).setPreferredWidth(150);
                        view.Emisiones.getColumnModel().getColumn(2).setPreferredWidth(150);
                        view.Emisiones.getColumnModel().getColumn(3).setPreferredWidth(150);
                        view.Emisiones.getColumnModel().getColumn(4).setPreferredWidth(150);
                        view.Emisiones.repaint();
                    });

                    if (consul.registrarEmision(mod)) {
                        JOptionPane.showMessageDialog(null, "Registro guardado");
                        Limpiar();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error");
                        Limpiar();
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Por favor, llene todos los campos.");
                }
            }else{
                JOptionPane.showMessageDialog(null,"La fuente de emisión ya ha sido insertada, por favor revisa la tabla\n" +
                        "y mira que tu emision que desea registrar no esté alli");
                Limpiar();
            }


            }
        }

        public void Limpiar(){
            view.Fuente.setSelectedIndex(0);
            view.Estado.setSelectedIndex(0);
            view.Unidad.setSelectedIndex(0);
            view.Alcance.setSelectedIndex(0);
            view.Factor.setText(null);
            view.nombre.setText(null);
        }
            public void loadEmisionesExistentes(){
                DefaultTableModel tableModel = (DefaultTableModel) view.Emisiones.getModel();
                List<EmisionModelo> emisiones= consul.dato();

                for(EmisionModelo dato : emisiones){
                    Object [] rowData = {
                            dato.getTipoFuente(),
                            dato.getEstadoFuente(),
                            dato.getNombreFuente(),
                            dato.getUnidadMedidad(),
                            dato.getFactorEmision(),
                            dato.getAlcance()
                    };
                    tableModel.addRow(rowData);
                }
                view.Emisiones.setModel(tableModel);
                view.Emisiones.setVisible(true);
                view.Contenedor.setVisible(true);
            }

    }
