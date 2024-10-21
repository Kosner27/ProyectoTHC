package Controlador;

import Modelo.*;
import Modelo.Consultas.*;
import Modelo.modelo.*;
import Vistas.*;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.List;
import java.util.Objects;

public class CalcularControlador implements ActionListener {
    private final CalcularModelo calcularModelo;
    private final CalcularConsultas consul;
    private final Calcular view;
    private final InstitucionModelo institucionModelo;
    private final ConsultaUsuario consultaUsuario;
    private final Usuario modUser;
    private final Municipio municipio;

    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");

    public CalcularControlador(CalcularModelo calcularModelo, CalcularConsultas consul,
                               Calcular view, InstitucionModelo institucionModelo, ConsultaUsuario consultaUsuario, Usuario modUser, Municipio municipio) {
        this.institucionModelo = institucionModelo;
        this.consul = consul;
        this.view = view;
        this.municipio = municipio;
        this.consultaUsuario = consultaUsuario;
        this.modUser = modUser;
        this.calcularModelo = calcularModelo;

        inciarListeners();
    }


    private void inciarListeners() {
        this.view.guardarCalculoButton.addActionListener(this);
        this.view.inicioButton.addActionListener(this);
        this.view.fuente.addActionListener(this::comboBoxActionPerformed);
        this.view.perfil.addActionListener(this);
        this.view.RegistrarEmisión.addActionListener(this);
        this.view.Informes.addActionListener(this);
        this.view.Reducir.addActionListener(this);
        this.view.RegistrarInstitucion.addActionListener(this);
        this.view.actualizarButton.addActionListener(this);
        this.view.VerPerfiles.addActionListener(this);
        this.view.siCheckBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.siCheckBox.isSelected()) {
                    view.Registro.setVisible(true);
                    view.siRegistro.setVisible(true);
                    view.noRegistro.setVisible(true);

                }
                if (!view.siCheckBox.isSelected()) {
                    view.Registro.setVisible(false);
                    view.siRegistro.setVisible(false);
                    view.noRegistro.setVisible(false);
                }
            }
        });
        this.view.siRegistro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.siRegistro.isSelected()) {
                    view.hayNucleo.setVisible(true);
                    view.comboNucleo.setVisible(true);
                    view.noRegistro.setEnabled(false);
                    view.nucleo.setVisible(false);
                    view.Nucleo.setVisible(false);
                    view.actualizarButton.setVisible(false);

                }
                if (!view.siRegistro.isSelected()) {
                    view.hayNucleo.setVisible(false);
                    view.comboNucleo.setVisible(false);
                    view.noRegistro.setEnabled(true);
                    view.nucleo.setVisible(true);
                    view.Nucleo.setVisible(true);
                    view.actualizarButton.setVisible(true);
                }
            }

        });
        this.view.noRegistro.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (view.noRegistro.isSelected()) {
                    Conexion conn = new Conexion();
                    NucleoView nucleoView = new NucleoView();
                    Nucleo nucleo = new Nucleo();
                    ConsultaNucleo consultaNucleo = new ConsultaNucleo(conn);
                    ControladorNucleo controladorNucleo = new ControladorNucleo(nucleoView, consultaNucleo, municipio, institucionModelo, nucleo);
                    controladorNucleo.establecerDatos(institucionModelo.getNombreInstitucion(), municipio.getNombreM());
                    controladorNucleo.inicio();


                }
                if (!view.noRegistro.isSelected()) {
                    view.Nucleo.setVisible(false);
                    view.nucleo.setVisible(false);
                    view.siRegistro.setEnabled(true);
                }
            }
        });

    }

    public void iniciar() {

       switch(modUser.getTipoUsuario()){
           case "Administrador":
               view.VerPerfiles.setVisible(false);
               view.Graficos.add(GraficoPrincipal);
               view.Graficos.add(GraficosCompararInstitucion);
               view.Graficos.add(GraficoHistorico);
               cargarNucleosExistentes();
               cargarInstitucion();
               cargarFuentesPorNombre();
               calcular();
               view.sede.setText(municipio.getNombreM());
               view.setTitle("Calcular Emision");
               view.setLocationRelativeTo(null);
               GraficoPrincipal.addActionListener(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent e) {
                       vistaGraficoPrincipal();
                   }
               });
               GraficosCompararInstitucion.addActionListener(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent e) {
                       vistaCompararInstituciones();
                   }
               });
               GraficoHistorico.addActionListener(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent e) {
                       vistaGraficoHistorico();

                   }

               });
                break;
           case "SuperAdmin":
               view.Graficos.add(GraficoPrincipal);
               view.Graficos.add(GraficosCompararInstitucion);
               view.Graficos.add(GraficoHistorico);
               cargarNucleosExistentes();
               cargarInstitucion();
               cargarFuentesPorNombre();
               calcular();
               view.sede.setText(municipio.getNombreM());
               view.setTitle("Calcular Emision");
               view.setLocationRelativeTo(null);
               GraficoPrincipal.addActionListener(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent e) {
                       vistaGraficoPrincipal();
                   }
               });
               GraficosCompararInstitucion.addActionListener(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent e) {
                       vistaCompararInstituciones();
                   }
               });
               GraficoHistorico.addActionListener(new ActionListener() {
                   @Override
                   public void actionPerformed(ActionEvent e) {
                       vistaGraficoHistorico();

                   }

               });
               break;
           default:
               JOptionPane.showMessageDialog(null, "Usuario no definido en el sistema");
               break;

       }
    }

    private void calcular() {
        view.Emisiones.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent e) {
                int a = 3;
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
                int c = 6;
                try {
                    double valor1 = Double.parseDouble(valor1Str); // Primer valor
                    double valor2 = Double.parseDouble(valor2Str); // Segundo valor
                    double resultado = valor1 * valor2;
                    model.setValueAt(resultado, row, c); // Agregar el resultado en la tercera columna
                    System.out.println(row);
                    total += resultado;
                } catch (NumberFormatException e) {
                    System.out.println("Error de formato numérico en fila " + row);
                }
            }
        }

        view.total.setText(String.valueOf(total));
    }

    private void comboBoxActionPerformed(ActionEvent e) {
        JComboBox<String> comboBox = (JComboBox<String>) e.getSource();
        String selectedItem = (String) comboBox.getSelectedItem();

        if (selectedItem != null && !selectedItem.isEmpty()) {

            DefaultTableModel tableModel = (DefaultTableModel) view.Emisiones.getModel();

            String fuenteSeleccionada = Objects.requireNonNull(view.fuente.getSelectedItem()).toString();
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
            InsertarCalculo();
        }
        if (e.getSource() == view.inicioButton) {
            BotonInicio();
        }
        if (e.getSource() == view.perfil) {
            vistaPerfil();
        }
        if (e.getSource() == view.RegistrarEmisión) {
            vistaRegistrarEmision();
        }
        if (e.getSource() == view.Informes) {
            vistaInforme();
        }
        if (e.getSource() == view.Reducir) {
            vistaReducir();
        }
        if (e.getSource() == view.RegistrarInstitucion) {
            vistaActualizarInstitucion();
        }
        if (e.getSource() == view.VerPerfiles) {
            vistaVerPerfiles();
        }
        if (e.getSource() == view.actualizarButton) {
            actualizarButton();
        }
    }

    public void cargarInstitucion() {
        String nombre = institucionModelo.getNombreInstitucion();
        String Nombremunicipio = municipio.getNombreM();
        String n = consul.Institucion(nombre, Nombremunicipio);
        view.Institucion.setText(n);
        view.Institucion.setEditable(false);
    }

    public void cargarFuentesPorNombre() {
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        String sql = "select NombreFuente from emision group by NombreFuente";

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

    public void InsertarCalculo() {
        view.fuente.setEditable(true);
        System.out.println("Botón guardarCalculoButton presionado"); // Mensaje de depuración
        try {
            calcularModelo.setAnioBase(Integer.parseInt(view.anio.getText()));
            calcularModelo.setNombreFuente(Objects.requireNonNull(view.fuente.getSelectedItem()).toString());
            DefaultTableModel model = (DefaultTableModel) view.Emisiones.getModel();
            int lastRow = model.getRowCount() - 1; // Obtener el índice de la última fila
            // Obtener los datos de la última fila
            String cargaAmbientalStr = model.getValueAt(lastRow, 3).toString();
            String resultadoStr = model.getValueAt(lastRow, 6).toString();
            if (!cargaAmbientalStr.isEmpty() && !resultadoStr.isEmpty() && !view.siCheckBox.isSelected()) {
                double cargaAmbiental = Double.parseDouble(cargaAmbientalStr);
                double total1 = Double.parseDouble(resultadoStr);
                calcularModelo.setCantidadConsumidad(cargaAmbiental);
                calcularModelo.setTotal1(total1);
                if (consul.registrarCargaAmbienta(calcularModelo, institucionModelo, municipio)) {
                    JOptionPane.showMessageDialog(null, "Registro guardado");
                    view.fuente.setEnabled(true);
                    view.anio.setEditable(true);
                } else {
                    JOptionPane.showMessageDialog(null, "Error al guardar el registro");
                }

            }
            if (view.siCheckBox.isSelected() && view.noRegistro.isSelected()) {
                Conexion conn = new Conexion();
                ConsultaNucleo consultaNucleo = new ConsultaNucleo(conn);
                Nucleo nucleo = new Nucleo();
                double cargaAmbiental = Double.parseDouble(cargaAmbientalStr);
                double total1 = Double.parseDouble(resultadoStr);
                calcularModelo.setCantidadConsumidad(cargaAmbiental);
                calcularModelo.setTotal1(total1);
                nucleo.setNombreNucleo(view.nucleo.getText());
                if (consultaNucleo.InsertarCalculoConNucleo(calcularModelo, nucleo)) {
                    JOptionPane.showMessageDialog(null, "Se registro el calculo para el nucleo " + nucleo.getNombreNucleo() +
                            " fue exitosa");
                    view.fuente.setEnabled(true);
                    view.anio.setEditable(true);
                    view.nucleo.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(null, " ERROR");
                }
            }
            if (view.siCheckBox.isSelected() && view.siRegistro.isSelected()) {
                Conexion conn = new Conexion();
                ConsultaNucleo consultaNucleo = new ConsultaNucleo(conn);
                Nucleo nucleo = new Nucleo();
                double cargaAmbiental = Double.parseDouble(cargaAmbientalStr);
                double total1 = Double.parseDouble(resultadoStr);
                calcularModelo.setCantidadConsumidad(cargaAmbiental);
                calcularModelo.setTotal1(total1);
                nucleo.setNombreNucleo(view.comboNucleo.getSelectedItem().toString());
                if (consultaNucleo.InsertarCalculoConNucleo(calcularModelo, nucleo)) {
                    JOptionPane.showMessageDialog(null, "Se registro el calculo para el nucleo " + nucleo.getNombreNucleo() +
                            " fue exitosa");
                    view.fuente.setEnabled(true);
                    view.anio.setEditable(true);
                    view.nucleo.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(null, " ERROR");
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

    public void BotonInicio() {
        ControladoInicio control = new ControladoInicio(institucionModelo, modUser, municipio);
        control.inicio();
        view.dispose();
    }

    public void vistaPerfil() {
        Conexion conn = new Conexion();
        Perfil per = new Perfil();
        ConsultaUsuario cons = new ConsultaUsuario(conn);
        PerfilCOntrolador control = new PerfilCOntrolador(modUser, per, institucionModelo, municipio, cons);
        control.Iniciar();
        per.setVisible(true);
        view.dispose();
    }

    public void vistaRegistrarEmision() {
        Emision emisionView = new Emision();
        EmisionModelo mod = new EmisionModelo();
        ConsultasEmision consul = new ConsultasEmision();
        EmisionControlador controlador = new EmisionControlador(mod, consul, emisionView, institucionModelo, municipio, modUser);
        controlador.iniciar();
        emisionView.setVisible(true);
        view.dispose();
    }

    public void vistaInforme() {
        Conexion con = new Conexion();
        ConsultaInforme consul = new ConsultaInforme(con);
        ModeloInforme mod = new ModeloInforme();
        Informe viewInfo = new Informe();
        ControladorInforme contro = new ControladorInforme(viewInfo, mod, consul, municipio, institucionModelo, modUser);
        contro.iniciar();
        viewInfo.setVisible(true);
        view.dispose();
    }

    public void vistaGraficoPrincipal() {
        Conexion con = new Conexion();
        GraficoConsulta consul = new GraficoConsulta(con);
        Vistas.Graficos viewGraf = new Graficos();
        GraficorModelo mod = new GraficorModelo();
        InstitucionModelo modelo = new InstitucionModelo();
        GraficoControlador contro = new GraficoControlador(mod, consul, viewGraf, modelo, municipio, modUser, institucionModelo);
        contro.iniciar();
        viewGraf.setVisible(true);
        view.dispose();
    }

    public void vistaCompararInstituciones() {
        CompararOtrarInstituciones comIns = new CompararOtrarInstituciones();
        GraficoComparar viewGraf = new GraficoComparar();
        Conexion conn = new Conexion();
        GraficoCompararConsultas consultas = new GraficoCompararConsultas(conn);
        GraficoCompararModelo mod = new GraficoCompararModelo();
        ComparaInstitucion contro = new ComparaInstitucion(mod, consultas, comIns, viewGraf, institucionModelo, municipio, modUser);
        contro.iniciar();
        view.dispose();
    }

    public void vistaGraficoHistorico() {
        Conexion con = new Conexion();
        TendenciaModelo mod = new TendenciaModelo();
        ConsultasTendencias consult = new ConsultasTendencias(con);
        GraficoTendencia viewGraf = new GraficoTendencia();
        TendenciaControlador control = new TendenciaControlador(mod, consult, viewGraf, municipio, institucionModelo, modUser);
        viewGraf.setVisible(true);
        control.iniciar();
        view.dispose();
    }

    public void vistaReducir() {
        Conexion con = new Conexion();
        Reducir2 vista = new Reducir2();
        GraficoConsulta consul = new GraficoConsulta(con);
        ControladorReducir redu = new ControladorReducir(institucionModelo, consul, vista, municipio, modUser);
        redu.Iniciar();
        view.dispose();
    }

    public void vistaActualizarInstitucion() {
        ConsultasInstitucion consul = new ConsultasInstitucion();
        RegistrarInstitucion view2 = new RegistrarInstitucion();
        InstitucionModelo mod = new InstitucionModelo();
        InstitucionControlador control = new InstitucionControlador(institucionModelo, municipio, view2, consul, mod, modUser);
        view2.setVisible(true);
        control.iniciar();
        view.dispose();


    }

    public void vistaVerPerfiles() {
        Conexion con = new Conexion();
        VerPerfiles verPerfiles = new VerPerfiles();
        ConsultaUsuario consul = new ConsultaUsuario(con);

        VerPerfilesControlador verControl = new VerPerfilesControlador(modUser, institucionModelo, municipio, verPerfiles, consul);
        verControl.Iniciar();
        view.dispose();
    }

    private void actualizarButton() {
        Conexion conn = new Conexion();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conn);
        System.out.println(consultaNucleo.UltimoRegistro());
        view.nucleo.setText(consultaNucleo.UltimoRegistro());
    }

    private void cargarNucleosExistentes(){
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        view.comboNucleo.removeAllItems();
        view.comboNucleo.addItem(" ");
        String nombreInstitucion = institucionModelo.getNombreInstitucion();
        System.out.println(nombreInstitucion);
        try{
            if(conn!= null){
                String procedureCall = "Select NombreNucleo from emisionnucleo en inner join nucleoinstitucion ni on en.IdNucleo = ni.IdNucleo inner join institucion i on ni.idInstitucion = i.idInstitucionAuto where i.NombreInstitucion = ? group by NombreNucleo";
                try(CallableStatement statement = conn.prepareCall(procedureCall)) {
                    statement.setString(1, nombreInstitucion);
                    ResultSet rs = statement.executeQuery();
                    while(rs.next()){
                        String nombreNucleo = rs.getString("NombreNucleo");
                        view.comboNucleo.addItem(nombreNucleo);
                    }
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
            JOptionPane.showMessageDialog(view.PanelMain, "Error al cargar los municipios: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }finally {
            // Asegúrate de cerrar la conexión
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

    }


}
