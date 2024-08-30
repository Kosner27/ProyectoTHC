package Controlador;

import Modelo.Conexion;
import Modelo.Consultas.ConsultaInforme;
import Modelo.modelo.InstitucionModelo;
import Modelo.modelo.ModeloInforme;
import Modelo.modelo.municipio;
import Vistas.Informe;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class ControladorInforme {
    private  Informe view;
    private ModeloInforme mod;
    private ConsultaInforme consul;
    private municipio m;
    private InstitucionModelo ins;
    private TableRowSorter<DefaultTableModel> sorter;
    public ControladorInforme(Informe view,ModeloInforme mod,ConsultaInforme consul,municipio m,InstitucionModelo ins){
        this.view=view;
        this.consul=consul;
        this.mod=mod;
        this.m=m;
        this.ins=ins;
        this.view.buscarButton.addActionListener(this::actionPerformed);
        this.view.descargarButton.addActionListener(this::actionPerformed);
        this.view.institucion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.institucion.getItemCount() > 0) {
                    cargarAnioBase();
                }
            }
        });

    }

    public void iniciar() {
        view.setTitle("Informe");
        view.setLocationRelativeTo(null);
        cargarInstitucion();
        cargarAnioBase();
        DefaultTableModel tableModel = (DefaultTableModel) view.Emisiones.getModel();
        sorter = new TableRowSorter<>(tableModel);
        view.Emisiones.setRowSorter(sorter);
    }

    public void actionPerformed(ActionEvent e){
        double total= 0.0;
        if(e.getSource()==view.buscarButton){

            String anio = view.anio.getSelectedItem().toString();
            String Institucion= view.institucion.getSelectedItem().toString();
            List<ModeloInforme>informes= consul.tablaInforme(Institucion,Integer.parseInt(anio));


            DefaultTableModel tableModel2 = (DefaultTableModel) view.Emisiones.getModel();

            tableModel2.setRowCount(0);
            if(!anio.isEmpty() && !Institucion.isEmpty()){


                for (ModeloInforme informe : informes) {

                    Object[] row = new Object[]{
                            informe.getNombreFuente(),
                            informe.getTipoFuente(),
                            informe.getAlcance(),
                            informe.getCantidadConsumidad(),
                            informe.getUnidadMedidad(),
                            informe.getFactorEmision(),
                            informe.getCargaAmnbiental(),
                            informe.getAnioBase()

                    };

                    tableModel2.addRow(row);
                }
                for(int row =0; row<tableModel2.getRowCount();row++){
                  String valor = tableModel2.getValueAt(row,6).toString();
                  if(!valor.isEmpty()){
                      try{
                          double valor1 = Double.parseDouble(valor);
                          total=valor1+total;
                          view.total.setText(String.valueOf(total));
                      }catch (NumberFormatException ex){
                          System.out.println("error"+ ex.getMessage());
                      }
                  }if(valor.isEmpty()) {
                      view.total.setText(String.valueOf(total));
                  }

                }


            }else{
                System.out.println("Error");
            }
        }
        if(e.getSource()==view.descargarButton) {
            String Nombre = new SimpleDateFormat("dd-MM-yyyy").format(new Date());
            Document document = new Document();
            String b = view.institucion.getSelectedItem().toString()+Nombre;
            List<ModeloInforme> a = consul.datos(view.institucion.getSelectedItem().toString(),m.getNombreM());
            //codgio para eligir el lugar en donde se descarga el archivo
            JFileChooser fileChooser = new JFileChooser();

            FileNameExtensionFilter filter = new FileNameExtensionFilter("PDF Files", "pdf");
            fileChooser.setFileFilter(filter);

            fileChooser.setDialogTitle("Guardar archivo PDF");
            fileChooser.setSelectedFile(new File(b + ".pdf"));
            int userSelection = fileChooser.showSaveDialog(null);
            ////

            if (userSelection == JFileChooser.APPROVE_OPTION) {
                File c = fileChooser.getSelectedFile();
                try {
                    PdfWriter.getInstance(document, new FileOutputStream(c));
                    document.open();
                    PdfPTable table = new PdfPTable(view.Emisiones.getColumnCount());
                    // Obtener la fecha actual
                    String fechaActual = new SimpleDateFormat("dd-MM-yyyy").format(new Date());

                    // Añadir la fecha al documento
                    Paragraph fecha = new Paragraph("Fecha de creación: " + fechaActual);
                    fecha.setAlignment(Element.ALIGN_RIGHT);
                    document.add(fecha);
                    document.add(new Paragraph(" ")); // Añadir espacio entre la fecha y la tabla
                    String nombre = view.institucion.getSelectedItem().toString();
                    Paragraph nombreInstitucion = new Paragraph("Nombre de la Institucion: " + nombre);
                    nombreInstitucion.setAlignment(Element.ALIGN_LEFT);
                    document.add(new Paragraph(("")));
                    document.add(nombreInstitucion);
                    document.add(new Paragraph("  "));



                    for (ModeloInforme info : a) {
                        String nombreMunicipi = info.getMunicipio();
                        Paragraph municipio = new Paragraph("Municipio: " + nombreMunicipi);
                        municipio.setAlignment(Element.ALIGN_LEFT);
                        document.add(municipio);
                        document.add(new Paragraph("  "));
                        //////////////////////////////
                        String d = info.getDepartamento();
                        Paragraph departamento = new Paragraph("Departamento: " + d);
                        departamento.setAlignment(Element.ALIGN_LEFT);
                        document.add(departamento);
                        document.add(new Paragraph("  "));
                        ///////////////////////////////7
                        String i = info.getNit();
                        Paragraph nit = new Paragraph("Nit de la institución: " + i);
                        nit.setAlignment(Element.ALIGN_LEFT);
                        document.add(nit);
                        document.add(new Paragraph("  "));
                    }
                    Paragraph text = new Paragraph("En la siguiente tabla vamos a encontrar las siguiente columnas\n" +
                            "Nombre de la fuente de emisión: Aparece el nombre de cada una de las fuentes de emisión asociadas a la institución en el año base seleccionado.\n" +
                            "Alcance: Corresponde al alcance de cada una de las fuentes de emisión.\n" +
                            "Cantidad consumida: Indica el consumo que tuvo la institución durante el año base de dicha fuente de emisión.\n" +
                            "Unidad de medida: Es la unidad en la que se mide cada fuente de emisión.\n" +
                            "Factor de emisión: Factor estandarizado por Colombia, utilizado para calcular el CO2 aportado por cada fuente de emisión.\n" +
                            "Año base: Año en que fueron recolectados los datos de la fuente de emisión.\n");
                    document.add(text);
                    document.add(new Paragraph(" "));

                    table.setWidthPercentage(100);


                    for (int i = 0; i < view.Emisiones.getColumnCount(); i++) {
                        PdfPCell cell = new PdfPCell(new Phrase(view.Emisiones.getColumnName(i)));
                        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                        table.addCell(cell);
                    }
                    for (int i = 0; i < view.Emisiones.getRowCount(); i++) {
                        for (int j = 0; j < view.Emisiones.getColumnCount(); j++) {
                            Object value = view.Emisiones.getValueAt(i, j);
                            String cellText = (value != null) ? value.toString() : "";
                            table.addCell(cellText);
                        }
                    }

                    document.add(table);
                    document.add(new Paragraph(" "));
                    String t = view.total.getText().toString();
                    Paragraph tot = new Paragraph("Total de CO2 emitido para el año base seleccionado: " + t);
                    document.add(tot);
                    document.add(new Paragraph(" "));
                    JOptionPane.showMessageDialog(null, "PDF generado correctamente.");

                } catch (DocumentException ex) {
                    throw new RuntimeException(ex);
                } catch (FileNotFoundException ex) {
                    throw new RuntimeException(ex);
                } finally {
                    document.close();
                    System.out.println("Documento cerrado.");
                }

            }
        }
    }

    public void cargarInstitucion() {
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        ResultSet rs = null;
        String sql = "Select * from institucion i inner join municipio m on i.idMunicipio = m.idMunicipio where m.NombreMunicipio = ? and i.NombreInstitucion = ? ";
        view.institucion.removeAllItems();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(sql)){
                    ps.setString(1,m.getNombreM());
                    ps.setString(2, ins.getNombreInstitucion());
                rs=ps.executeQuery();
                view.institucion.removeAllItems();
                view.institucion.addItem("");
                while (rs.next()) {
                    String nombre = rs.getString("NombreInstitucion");
                    view.institucion.addItem(nombre);
                }
                if (view.institucion.getItemCount() > 0) {
                    view.institucion.setSelectedIndex(0);
                } else {
                    // Manejar el caso en el que el JComboBox está vacío
                    System.out.println("El JComboBox institucion está vacío");
                }

                rs.close();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void cargarAnioBase() {
        Conexion conexion = new Conexion();
        ResultSet st = null;
        Connection conn = conexion.getConection();
        String sql = " Select  ei.anioBase from emisioninstitucion ei inner join institucion i on ei.idInsititucion=i.idInstitucionAuto inner join emision e on ei.idEmision = e.idEmision where i.NombreInstitucion = ?  group by anioBase";
        String nombre = view.institucion.getSelectedItem().toString();
        view.anio.addItem(" ");
        view.anio.removeAllItems();
        if (conn != null) {
            try (PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1,nombre);
                st = ps.executeQuery();
                while (st.next()) {
                    String anioBase = st.getString(1);
                    view.anio.addItem(" ");
                    view.anio.addItem(anioBase);
                }
                if (view.anio.getItemCount() > 0) {
                    view.anio.setSelectedIndex(0);
                } else {
                    // Manejar el caso en el que el JComboBox está vacío
                    System.out.println("El JComboBox año está vacío");
                }

                st.close();
                ps.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
