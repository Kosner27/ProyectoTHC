package Controlador;

import Modelo.*;
import Vistas.Calcular;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.List;

public class CalcularControlador implements ActionListener {
    private CalcularModelo mod;
    private CalcularConsultas consul;
    private Calcular view;
private InstitucionModelo mod2;
    public CalcularControlador(CalcularModelo mod, CalcularConsultas consul, Calcular view,InstitucionModelo mod2) {
        this.mod = mod;
        this.consul = consul;
        this.view = view;
        this.mod2=mod2;
        this.view.guardarButton.addActionListener(this);
        this.view.añadirLeFuenteButton.addActionListener(this);
        this.view.guardarCalculoButton.addActionListener(this);
    }

    public void iniciar() {
        view.setTitle("Calcular Emision");
        view.setLocationRelativeTo(null);
        cargarInstitucion();
        cargarFuentesPorNombre();
        calcular();
    }

    public void calcular() {
        view.Emisiones.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                if (e.getColumn() == 3 && e.getType() == TableModelEvent.UPDATE) { // Columna "Carga ambiental"
                    int row = e.getFirstRow();
                    String cargaAmbiental = view.Emisiones.getValueAt(row, 3).toString();
                    calcularTotal();
                    System.out.println("Carga ambiental en fila " + row + ": " + cargaAmbiental);
                }
            }
        });
    }

    private void calcularTotal() {
        DefaultTableModel model = (DefaultTableModel) view.Emisiones.getModel();
        double total = 0.0;

        for (int row = 0; row < model.getRowCount(); row++) {
            String valor1Str = model.getValueAt(row, 3).toString();
            String valor2Str = model.getValueAt(row, 5).toString();

            if (!valor1Str.isEmpty() && !valor2Str.isEmpty()) {
                try {
                    double valor1 = Double.parseDouble(valor1Str); // Primer valor
                    double valor2 = Double.parseDouble(valor2Str); // Segundo valor
                    double resultado = valor1 * valor2;
                    model.setValueAt(resultado, row, 6); // Agregar el resultado en la tercera columna
                    System.out.println( row);
                    total += resultado;
                } catch (NumberFormatException e) {
                    System.out.println("Error de formato numérico en fila " + row);
                }
            }
        }

        view.total.setText(String.valueOf(total));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.añadirLeFuenteButton) {
            DefaultTableModel tableModel = (DefaultTableModel) view.Emisiones.getModel();

                String fuenteSeleccionada = view.fuente.getSelectedItem().toString();
                List<EmisionModelo> emisiones = consul.getEmisiones(fuenteSeleccionada);



            for (EmisionModelo emision : emisiones) {
                // Verificar si la fila ya existe
                boolean exists = false;
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (tableModel.getValueAt(i, 0).equals(emision.getNombreFuente())) {
                        exists = true;
                        break;
                    }
                }
                if (!exists) {
                    Object[] rowData = {emision.getNombreFuente(), emision.getEstadoFuente(),
                            emision.getAlcance(), "", emision.getUnidadMedidad(), emision.getFactorEmision()};
                    tableModel.addRow(rowData);
                }
            }


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



        }

        if (e.getSource() == view.guardarCalculoButton) {
            System.out.println("Botón guardarCalculoButton presionado"); // Mensaje de depuración
            try {
                mod.setAnioBase(Integer.parseInt(view.anio.getText()));
                mod.setNombreFuente(view.fuente.getSelectedItem().toString());
                mod2.setNombreInstitucion(view.institucion.getSelectedItem().toString());

                DefaultTableModel model = (DefaultTableModel) view.Emisiones.getModel();
                int lastRow = model.getRowCount() - 1; // Obtener el índice de la última fila

                // Obtener los datos de la última fila
                String cargaAmbientalStr = model.getValueAt(lastRow, 3).toString();
                String resultadoStr = model.getValueAt(lastRow, 6).toString();

                if (!cargaAmbientalStr.isEmpty() && !resultadoStr.isEmpty()) {
                    double cargaAmbiental = Double.parseDouble(cargaAmbientalStr);
                    double total1 = Double.parseDouble(resultadoStr);

                    mod.setCantidadConsumidad(cargaAmbiental);
                    mod.setTotal1(total1);

                    if (consul.registrarCargaAmbienta(mod, mod2)) {
                        JOptionPane.showMessageDialog(null, "Registro guardado");
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al guardar el registro");
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Error en el formato de los datos");
                ex.printStackTrace(); // Mensaje de depuración
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Ocurrió un error inesperado");
                ex.printStackTrace(); // Mensaje de depuración
            }
        }
    }

    public void cargarInstitucion() {
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        String sql = "CALL Seleccionarinstitucion()";

        view.institucion.removeAllItems();
        if (conn != null) {
            try {
                Statement stmt = conn.createStatement();
                ResultSet rst = stmt.executeQuery(sql);
                view.institucion.removeAllItems();
                view.institucion.addItem("");
                while (rst.next()) {
                    String nombre = rst.getString("NombreInstitucion");
                    view.institucion.addItem(nombre);
                }
                if (view.institucion.getItemCount() > 0) {
                    view.institucion.setSelectedIndex(0);
                } else {
                    // Manejar el caso en el que el JComboBox está vacío
                    System.out.println("El JComboBox departamento está vacío");
                }

                rst.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void cargarFuentesPorNombre() {
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        String sql = "CALL SeleccionarFuente()";

        view.fuente.removeAllItems();
        if (conn != null) {
            try {
                Statement stmt = conn.createStatement();
                ResultSet rst = stmt.executeQuery(sql);
                view.fuente.removeAllItems();
                view.fuente.addItem("");
                while (rst.next()) {
                    String nombre = rst.getString("NombreFuente");
                    view.fuente.addItem(nombre);
                }
                if (view.fuente.getItemCount() > 0) {
                    view.fuente.setSelectedIndex(0);
                } else {
                    // Manejar el caso en el que el JComboBox está vacío
                    System.out.println("El JComboBox fuente está vacío");
                }

                rst.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
