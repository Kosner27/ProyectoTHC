package Controlador;
import Diccionario.Diccionario;
import Modelo.ConsultasEmision;
import Modelo.EmisionModelo;
import Vistas.Emision;


import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class EmisionControlador implements ActionListener {
    private EmisionModelo mod;
    private ConsultasEmision consul;
    private Emision view;
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
                } else {
                    view.Estado.setVisible(false);
                    view.estado.setVisible(false);
                }
                if (view.Fuente.getSelectedItem() == " ") {
                    view.Estado.setVisible(false);
                    view.estado.setVisible(false);
                }
            }
        });

        view.nombre.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizar();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizar();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizar();
            }
        });
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
    }
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.guardarButton) {
            mod.setTipoFuente(view.Fuente.getSelectedItem().toString());
            mod.setNombreFuente(view.nombre.getText());
            mod.setEstadoFuente(view.Estado.getSelectedItem().toString());
            mod.setFactorEmision(Double.parseDouble(view.Factor.getText()));
            mod.setUnidadMedidad(view.Unidad.getSelectedItem().toString());
            mod.setAlcance(view.Alcance.getSelectedItem().toString());


            if (!view.Fuente.getSelectedItem().toString().isEmpty() && !view.Unidad.getSelectedItem().toString().isEmpty() && !view.Alcance.getSelectedItem().toString().isEmpty() && !view.Factor.getText().isEmpty() && !view.nombre.getText().isEmpty()) {
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


    }
