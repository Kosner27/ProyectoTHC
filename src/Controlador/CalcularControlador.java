package Controlador;

import Modelo.*;
import Modelo.Consultas.CalcularConsultas;
import Modelo.Consultas.ConsultaUsuario;
import Modelo.modelo.*;
import Vistas.Calcular;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CalcularControlador implements ActionListener {
    private final CalcularModelo mod;
    private final CalcularConsultas consul;
    private final Calcular view;
    private final InstitucionModelo mod2;
    private final ConsultaUsuario consultaUsuario;
    private final UsuarioModel modUser;
    private municipio m;
    public CalcularControlador(CalcularModelo mod, CalcularConsultas consul,
                               Calcular view, InstitucionModelo mod2,ConsultaUsuario consultaUsuario,UsuarioModel modUser,municipio m) {
        this.mod = mod;
        this.consul = consul;
        this.view = view;
        this.mod2 = mod2;
        this.consultaUsuario = consultaUsuario;
        this.modUser=modUser;
        this.m=m;
        inciarListeners();
    }
private void inciarListeners() {
    this.view.guardarCalculoButton.addActionListener(this);
    this.view.fuente.addActionListener(this::comboBoxActionPerformed);
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
                int a =3;
                if (e.getColumn() == a && e.getType() == TableModelEvent.UPDATE) { // Columna "Carga ambiental"
                    int row = e.getFirstRow();
                    String cargaAmbiental = view.Emisiones.getValueAt(row, a).toString();
                    calcularTotal();
                    System.out.println("Carga ambiental en fila " + row + ": " + cargaAmbiental);
                }
            }
        });
    }


    private void calcularTotal() {
        DefaultTableModel model = (DefaultTableModel) view.Emisiones.getModel();
        double total = 0.0;
        int a = 3;
        int b = 5;
        for (int row = 0; row < model.getRowCount(); row++) {
            String valor1Str = model.getValueAt(row, a).toString();
            String valor2Str = model.getValueAt(row, b).toString();

            if (!valor1Str.isEmpty() && !valor2Str.isEmpty()) {
                int c= 6;
                try {
                    double valor1 = Double.parseDouble( valor1Str ); // Primer valor
                    double valor2 = Double.parseDouble( valor2Str ); // Segundo valor
                    double resultado = valor1 * valor2;
                    model.setValueAt(resultado, row, c); // Agregar el resultado en la tercera columna
                    System.out.println( row);
                    total += resultado;
                } catch (NumberFormatException e) {
                    System.out.println("Error de formato numérico en fila " + row);
                }
            }
        }

        view.total.setText(String.valueOf(total));
    }
    public void comboBoxActionPerformed(ActionEvent e) {
        JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
        String selectedItem = (String) comboBox.getSelectedItem();

        if (selectedItem != null && !selectedItem.isEmpty()) {

            DefaultTableModel tableModel = (DefaultTableModel) view.Emisiones.getModel();

            String fuenteSeleccionada = view.fuente.getSelectedItem().toString();
            List<EmisionModelo> emisiones = consul.getEmisiones(fuenteSeleccionada);

            for (EmisionModelo emision : emisiones) {
                // Verificar si la fuente ya existe en la tabla
                boolean exists = false;
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    String fuenteEnTabla = (String) tableModel.getValueAt(i, 0); // Asumiendo que el nombre de la fuente está en la primera columna
                    if (fuenteEnTabla.equals(emision.getNombreFuente())) {
                        exists = true;
                        break;
                    }
                }

                if (!exists) {
                    // Si la fuente no existe, agregarla a la tabla
                    Object[] rowData = {emision.getNombreFuente(), emision.getEstadoFuente(),
                            emision.getAlcance(), "", emision.getUnidadMedidad(), emision.getFactorEmision()};
                    tableModel.addRow(rowData);
                }
                view.fuente.setEnabled(false);
                view.anio.setEditable(false);
            }
            view.fuente.setEnabled(false);
            view.anio.setEditable(false);
            // Actualizar la tabla y sus configuraciones
            view.Emisiones.setModel(tableModel);
            view.Emisiones.setVisible(true);
            view.Contenedor.setVisible(true);

            SwingUtilities.invokeLater(() -> {
                TableColumnModel columnModel = view.Emisiones.getColumnModel();
                int columnCount = columnModel.getColumnCount();
                System.out.println("Número de columnas en la tabla: " + columnCount);

                for (int i = 0; i < columnCount; i++) {
                    System.out.println("Columna " + i + ": " + columnModel.getColumn(i).getHeaderValue());
                }

                if (columnCount > 4) {
                    columnModel.getColumn(0).setPreferredWidth(150);
                    columnModel.getColumn(1).setPreferredWidth(150);
                    columnModel.getColumn(2).setPreferredWidth(150);
                    columnModel.getColumn(3).setPreferredWidth(150);
                    columnModel.getColumn(4).setPreferredWidth(150);
                    columnModel.getColumn(5).setPreferredWidth(150);
                    columnModel.getColumn(6).setPreferredWidth(150);
                } else {
                    System.out.println("La tabla no tiene suficientes columnas");
                }
                view.Emisiones.repaint();
            });
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {


        if (e.getSource() == view.guardarCalculoButton) {
            view.fuente.setEditable(true);
            System.out.println("Botón guardarCalculoButton presionado"); // Mensaje de depuración
            try {
                mod.setAnioBase(Integer.parseInt(view.anio.getText()));
                mod.setNombreFuente(view.fuente.getSelectedItem().toString());
                String nombre= view.Institucion.getText();
                String nombreM = m.getNombreM();
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
                    System.out.println(nombre);
                    System.out.println(nombreM);
                    if (consul.registrarCargaAmbienta(mod, nombre,nombreM)) {
                        JOptionPane.showMessageDialog(null, "Registro guardado");
                        view.fuente.setEnabled(true);
                        view.anio.setEditable(true);
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
     String nombre = mod2.getNombreInstitucion();
     String municipio = m.getNombreM();
     String n = consul.Institucion(nombre,municipio);
     view.Institucion.setText(n);
     view.Institucion.setEditable(false);
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
