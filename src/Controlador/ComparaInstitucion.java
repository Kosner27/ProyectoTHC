package Controlador;

import Modelo.*;
import Vistas.CompararOtrarInstituciones;
import Vistas.GraficoComparar;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;



public class ComparaInstitucion {
    public GraficoCompararConsultas consul;
    public GraficoCompararModelo mod;
    public CompararOtrarInstituciones view;
    public GraficoComparar view2;
    public Conexion conn = new Conexion();
    public ComparaInstitucion(GraficoCompararModelo mod, GraficoCompararConsultas consul,CompararOtrarInstituciones view,GraficoComparar view2){
        this.mod=mod;
        this.consul=consul;
        this.view=view;
        this.view2=view2;
        this.view.añadirButton.addActionListener(this::actionPerformed);
        this.view.compararButton.addActionListener(this::actionPerformed);

    }
    public void iniciar() {
        view.setTitle("Seleccionar Instituciones");
        view.setLocationRelativeTo(null);
        cargarInstitucion();
        cargarAnioBase();
    }

public void actionPerformed(ActionEvent e){
        if(e.getSource()==view.añadirButton){
            String nombreInstitucion = view.institucion.getSelectedItem().toString();
            if(!nombreInstitucion.isEmpty()){
                DefaultTableModel tableModel = (DefaultTableModel) view.Instituciones.getModel();
                List<InstitucionModelo> datos = consul.llenarTabla(nombreInstitucion);

                for (InstitucionModelo dato : datos) {
                    Object[] rowData = {
                            dato.getNombreInstitucion(),
                            dato.getNit(),
                            dato.getMunicipio(),
                            dato.getDepartamento()
                    };
                    tableModel.addRow(rowData);
                }

                view.Instituciones.setModel(tableModel);
                view.Instituciones.setVisible(true);
                view.Contenedor.setVisible(true);


            }
        }
        if(e.getSource()==view.compararButton){

            String anio = view.anio.getSelectedItem().toString();
            String alcance = view.alcance.getSelectedItem().toString();

            if (!anio.isEmpty() && !alcance.isEmpty()) {

                DefaultCategoryDataset datos = new DefaultCategoryDataset();
                DefaultTableModel tableModel = (DefaultTableModel) view.Instituciones.getModel();
                int rowCount = tableModel.getRowCount();
                List<String> nombresInstituciones = new ArrayList<>();
                for (int i = 0; i < rowCount; i++) {
                    String nombreInstitucion = (String) tableModel.getValueAt(i, 0); // Asumiendo que el nombre de la institución está en la primera columna
                    nombresInstituciones.add("'" + nombreInstitucion + "'");
                }
                String instituciones = String.join(",", nombresInstituciones);
                List<GraficoCompararModelo> mod = consul.LlenarGrafico(instituciones, anio, alcance);

                if (mod.isEmpty()) {
                    JOptionPane.showMessageDialog(view,
                            "No se encontraron datos para la institución, año y alcance seleccionados.",
                            "Sin datos",
                            JOptionPane.INFORMATION_MESSAGE);
                    return; // Terminar la ejecución del método si no hay datos
                }

                for (GraficoCompararModelo dato : mod) {
                    String label = dato.getNombrefuente();
                    Double total = dato.getTotal();
                    String label2 = dato.getNombreInstitucion();
                    datos.setValue(total,  label2, label );
                    JFreeChart grafico = grafico(datos);
                    mostrarGrafico(grafico);
                }
            } else {
                JOptionPane.showMessageDialog(view,
                        "Por favor seleccione una institución, un año y un alcance antes de proceder.",
                        "Datos faltantes",
                        JOptionPane.WARNING_MESSAGE);
            }

        }
}

private JFreeChart grafico (DefaultCategoryDataset dataset){
    JFreeChart chart = ChartFactory.createBarChart(
            "Comparar instituciones por alcance ",   // Título del gráfico
            "Fuentes emision",       // Etiqueta del eje X
            "Cantidad de co2",       // Etiqueta del eje Y
            dataset   // Conjunto de datos
    );
    return chart;
}

    private void mostrarGrafico(JFreeChart chart) {
        ChartPanel panel = new ChartPanel(chart);
        panel.setMouseWheelEnabled(true);
        panel.setPreferredSize(new Dimension(300, 700));
        view2.PanelGrafico.setLayout(new BorderLayout());
        view2.PanelGrafico.removeAll();
        view2.PanelGrafico.add(panel,BorderLayout.CENTER);
        view2.PanelGrafico.revalidate();
        view2.PanelGrafico.repaint();
        view2.setVisible(true);

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
                    System.out.println("El JComboBox institucion está vacío");
                }

                rst.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void cargarAnioBase() {
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        String sql = "CALL SeleccionarAnioBase()";

        view.anio.removeAllItems();
        if (conn != null) {
            try {
                Statement stmt = conn.createStatement();
                ResultSet rst = stmt.executeQuery(sql);
                view.anio.removeAllItems();
                view.anio.addItem("");
                while (rst.next()) {
                    String nombre = rst.getString("anioBase");
                    view.anio.addItem(nombre);
                }
                if (view.anio.getItemCount() > 0) {
                    view.anio.setSelectedIndex(0);
                } else {
                    // Manejar el caso en el que el JComboBox está vacío
                    System.out.println("El JComboBox año está vacío");
                }

                rst.close();
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
