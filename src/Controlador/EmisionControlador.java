package Controlador;

import Diccionario.Diccionario;
import Modelo.Conexion;
import Modelo.Consultas.*;
import Modelo.modelo.*;
import Vistas.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

public class EmisionControlador implements ActionListener {
    private final EmisionModelo mod;
    private final ConsultasEmision consul;
    private final Emision view;
    private TableRowSorter<DefaultTableModel> sorter;
    public static Diccionario factorEmision = new Diccionario();
    public InstitucionModelo ins;
    public Municipio m;
    public Usuario user;
    private Timer timer;
    private Map<String, EmisionModelo> previousData;
    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");

    public EmisionControlador(EmisionModelo mod, ConsultasEmision consul, Emision view, InstitucionModelo ins,
                              Municipio m, Usuario user) {
        this.mod = mod;
        this.consul = consul;
        this.view = view;

        this.m = m;
        this.ins = ins;
        this.user = user;
        Listeners();

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

    public void iniciar() {
        view.Graficos.add(GraficoPrincipal);
        view.Graficos.add(GraficosCompararInstitucion);
        view.Graficos.add(GraficoHistorico);
        Map<String, EmisionModelo> datosMap = new HashMap<>();
        List<EmisionModelo> datos = consul.dato();

        for (EmisionModelo emi : datos) {
            datosMap.put(emi.getNombreFuente(), emi); // Usa el correo como clave
        }
        loadEmisionesExistentes(datosMap);
        startPolling();
        DefaultTableModel tableModel = (DefaultTableModel) view.Emisiones.getModel();
        sorter = new TableRowSorter<>(tableModel);
        view.Emisiones.setRowSorter(sorter);
        switch (user.getTipoUsuario()){
            case "Administrador":
                view.VerPerfiles.setVisible(false);
                view.Graficos.add(GraficoPrincipal);
                view.Graficos.add(GraficosCompararInstitucion);
                view.Graficos.add(GraficoHistorico);
                view.setTitle("Registrar Emision");
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
                view.setTitle("Registrar Emision");
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
    }

    public void actionPerformed(ActionEvent e) {
        String EmisionNombre = view.nombre.getText();
        if (e.getSource() == view.guardarButton) {
            if (consul.ExisteEmision(EmisionNombre) == 0) {
                mod.setTipoFuente(view.Fuente.getSelectedItem().toString());
                mod.setNombreFuente(view.nombre.getText());
                mod.setEstadoFuente(view.Estado.getSelectedItem().toString());
                mod.setFactorEmision(Double.parseDouble(view.Factor.getText()));
                mod.setUnidadMedidad(view.Unidad.getSelectedItem().toString());
                mod.setAlcance(view.Alcance.getSelectedItem().toString());
                if (!view.Fuente.getSelectedItem().toString().isEmpty() && !view.Unidad.getSelectedItem().toString().isEmpty()
                        && !view.Alcance.getSelectedItem().toString().isEmpty() && !view.Factor.getText().isEmpty()
                        && !view.nombre.getText().isEmpty()) {
                    String[] rows = {view.Fuente.getSelectedItem().toString(), view.Estado.getSelectedItem().toString(), view.nombre.getText(), view.Unidad.getSelectedItem().toString(), view.Factor.getText(), view.Alcance.getSelectedItem().toString()};
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
            } else {
                JOptionPane.showMessageDialog(null, "La fuente de emisión ya ha sido insertada, por favor revisa la tabla\n" +
                        "y mira que tu emision que desea registrar no esté alli");
                Limpiar();
            }


        }
        if (e.getSource() == view.inicioButton) {
            BotonInicio();
        }
        if (e.getSource() == view.perfil) {
            vistaPerfil();
        }
        if (e.getSource() == view.Calcular) {
            vistaCalcular();
        }
        if (e.getSource() == view.Informes) {
            vistaInforme();
        }
        if (e.getSource() == view.buscarButton) {
            Buscar();
        }
        if (e.getSource() == view.editar) {
            view.Factor.setFocusable(true);
            view.Factor.setText("");
            desactivarCampos();

        }
        if (e.getSource() == view.guardarCambiosButton) {
            guardarCambios();

        }
        if (e.getSource() == view.eliminarButton1) {
            eliminar();
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
    }

    public void Limpiar() {
        view.Fuente.setSelectedIndex(0);
        view.Estado.setSelectedIndex(0);
        view.Unidad.setSelectedIndex(0);
        view.Alcance.setSelectedIndex(0);
        view.Factor.setText(null);
        view.nombre.setText(null);
    }

    public void loadEmisionesExistentes(Map<String, EmisionModelo> data) {
        DefaultTableModel tableModel = (DefaultTableModel) view.Emisiones.getModel();
        tableModel.setRowCount(0);

        for (EmisionModelo dato : data.values()) {
            Object[] rowData = {
                    dato.getTipoFuente(),
                    dato.getEstadoFuente(),
                    dato.getNombreFuente(),
                    dato.getUnidadMedidad(),
                    dato.getFactorEmision(),
                    dato.getAlcance()
            };
            tableModel.addRow(rowData);
        }
        SwingUtilities.invokeLater(() -> {
            view.Emisiones.getColumnModel().getColumn(0).setPreferredWidth(150);
            view.Emisiones.getColumnModel().getColumn(2).setPreferredWidth(150);
            view.Emisiones.getColumnModel().getColumn(3).setPreferredWidth(150);
            view.Emisiones.getColumnModel().getColumn(4).setPreferredWidth(150);
            view.Emisiones.repaint();
        });
        view.Emisiones.setModel(tableModel);
        view.Emisiones.setVisible(true);
        view.Contenedor.setVisible(true);
    }

    public void BotonInicio() {
        ControladoInicio control = new ControladoInicio(ins, user, m);
        control.inicio();
        view.dispose();
    }

    public void vistaPerfil() {
        Conexion conn = new Conexion();
        Perfil per = new Perfil();
        ConsultaUsuario cons = new ConsultaUsuario(conn);
        PerfilCOntrolador control = new PerfilCOntrolador(user, per, ins, m, cons);
        control.Iniciar();
        per.setVisible(true);
        view.dispose();
    }

    public void vistaCalcular() {
        Conexion con = new Conexion();
        Vistas.Calcular viewCal = new Calcular();
        CalcularModelo mod = new CalcularModelo();
        CalcularConsultas consul = new CalcularConsultas(con);
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        CalcularControlador controlador = new CalcularControlador(mod, consul, viewCal, ins, consultaUsuario, user, m);
        controlador.iniciar();
        viewCal.setVisible(true);
        view.dispose();
    }

    public void vistaInforme() {
        Conexion con = new Conexion();
        ConsultaInforme consul = new ConsultaInforme(con);
        ModeloInforme mod = new ModeloInforme();
        Informe viewInfo = new Informe();
        ControladorInforme contro = new ControladorInforme(viewInfo, mod, consul, m, ins, user);
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
        GraficoControlador contro = new GraficoControlador(mod, consul, viewGraf, modelo, m, user, ins);
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
        ComparaInstitucion contro = new ComparaInstitucion(mod, consultas, comIns, viewGraf, ins, m, user);
        contro.iniciar();
        view.dispose();
    }

    public void vistaGraficoHistorico() {
        Conexion con = new Conexion();
        TendenciaModelo mod = new TendenciaModelo();
        ConsultasTendencias consult = new ConsultasTendencias(con);
        GraficoTendencia viewGraf = new GraficoTendencia();
        TendenciaControlador control = new TendenciaControlador(mod, consult, viewGraf, m, ins, user);
        viewGraf.setVisible(true);
        control.iniciar();
        view.dispose();
    }

    public void vistaReducir() {
        Conexion con = new Conexion();
        Reducir2 vista = new Reducir2();
        GraficoConsulta consul = new GraficoConsulta(con);
        ControladorReducir redu = new ControladorReducir(ins, consul, vista, m, user);
        redu.Iniciar();
        view.dispose();
    }

    public void vistaActualizarInstitucion() {
        Conexion con = new Conexion();
        ConsultasInstitucion consul = new ConsultasInstitucion();
        RegistrarInstitucion view2 = new RegistrarInstitucion();
        InstitucionModelo mod = new InstitucionModelo();
        InstitucionControlador control = new InstitucionControlador(ins, m, view2, consul, mod, user);
        view2.setVisible(true);
        control.iniciar();
        view.dispose();


    }

    public void vistaVerPerfiles() {
        Conexion con = new Conexion();
        VerPerfiles verPerfiles = new VerPerfiles();
        ConsultaUsuario consul = new ConsultaUsuario(con);

        VerPerfilesControlador verControl = new VerPerfilesControlador(user, ins, m, verPerfiles, consul);
        verControl.Iniciar();
        view.dispose();
    }

    public void Buscar() {
        String nombre = view.nombre.getText();
        if (!nombre.isEmpty()) {
            List<EmisionModelo> dato = consul.buscarEmision(nombre);

            for (EmisionModelo a : dato) {
                view.Alcance.setSelectedItem(a.getAlcance());
                view.Fuente.setSelectedItem(a.getTipoFuente());
                view.Estado.setSelectedItem(a.getEstadoFuente());
                view.Unidad.setSelectedItem(a.getUnidadMedidad());
                view.nombre.setText(a.getNombreFuente());
                view.Factor.setText(a.getFactorEmision().toString());
            }

        }
    }

    public void desactivarCampos() {
        view.Alcance.setEnabled(false);
        view.Fuente.setEnabled(false);
        view.Estado.setEnabled(false);
        view.Unidad.setEnabled(false);
        view.nombre.setEnabled(false);

    }

    public void guardarCambios() {
        Double facto = Double.parseDouble(view.Factor.getText());

        if (!facto.isNaN()) {
            if (consul.ActualizarFactoreEmision(view.nombre.getText(), facto)) {
                JOptionPane.showMessageDialog(null, "Registro Actualizado");
                Limpiar();
            } else {
                JOptionPane.showMessageDialog(null, "ERROR");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Campo Vacio");
        }
    }

    public void eliminar() {
        String nombre = view.nombre.getText();
        if (!nombre.isEmpty()) {
            if (consul.eliminarFuente(nombre)) {
                JOptionPane.showMessageDialog(null, " la fuente de emision llamada " + nombre + "\n fue eliminiada");
            } else {
                JOptionPane.showMessageDialog(null, "ERROR EN LA CONSULTA");
            }
        } else {
            JOptionPane.showMessageDialog(null, " El nombre de la fuente está vacio");
        }
    }

    public void startPolling() {
        timer = new Timer(5000, new ActionListener() { // Intervalo de 5 segundos
            @Override
            public void actionPerformed(ActionEvent e) {
                checkForUpdates();
            }
        });
        timer.start();
    }

    private void checkForUpdates() {
        // Consulta la base de datos para obtener los datos actuales
        List<EmisionModelo> datos = consul.dato();

        // Crea un mapa para los datos nuevos
        Map<String, EmisionModelo> currentData = new HashMap<>();
        for (EmisionModelo emi : datos) {
            currentData.put(emi.getNombreFuente(), emi); // Usa el correo como clave
        }

        // Compara los datos anteriores con los nuevos
        if (isDataChanged(currentData)) {
            loadEmisionesExistentes(currentData);
            previousData = currentData; // Actualiza los datos anteriores
        }
    }

    private boolean isDataChanged(Map<String, EmisionModelo> newData) {
        if (previousData == null) {
            // Si previousData es null, consideramos que los datos han cambiado
            return true;
        }
        if (previousData.size() != newData.size()) {
            return true; // El tamaño de los datos ha cambiado, lo que indica un cambio
        }

        // Compara cada registro para detectar cambios
        for (Map.Entry<String, EmisionModelo> entry : newData.entrySet()) {
            EmisionModelo newEmi = entry.getValue();
            EmisionModelo oldEmi = previousData.get(entry.getKey());

            if (oldEmi == null || !oldEmi.equals(newEmi)) {
                return true; // Hay cambios en el registro
            }
        }

        return false; // No hay cambios
    }

    public void Listeners() {
        this.view.guardarButton.addActionListener(this::actionPerformed);
        this.view.inicioButton.addActionListener(this::actionPerformed);
        this.view.buscarButton.addActionListener(this::actionPerformed);
        this.view.editar.addActionListener(this::actionPerformed);
        this.view.eliminarButton1.addActionListener(this::actionPerformed);
        this.view.perfil.addActionListener(this::actionPerformed);
        this.view.Calcular.addActionListener(this::actionPerformed);
        this.view.Informes.addActionListener(this::actionPerformed);
        this.view.guardarCambiosButton.addActionListener(this::actionPerformed);
        this.view.Reducir.addActionListener(this::actionPerformed);
        this.view.RegistrarInstitucion.addActionListener(this::actionPerformed);
        this.view.VerPerfiles.addActionListener(this::actionPerformed);
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


}
