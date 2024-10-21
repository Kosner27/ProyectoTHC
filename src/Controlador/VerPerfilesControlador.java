package Controlador;

import Modelo.Conexion;
import Modelo.Consultas.*;
import Modelo.modelo.*;
import Vistas.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VerPerfilesControlador {
    // Variables de instancia

    public Usuario user;
    public InstitucionModelo ins;
    public Municipio m;
    public VerPerfiles view;
    public ConsultaUsuario consulUser;
    private Timer timer;
    private Map<String, Usuario> previousData;

    // Elementos del menú para gráficas
    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");
    // Constructor
    public VerPerfilesControlador(Usuario user, InstitucionModelo ins,
                                  Municipio m, VerPerfiles view, ConsultaUsuario consulUser) {
        this.user = user;
        this.ins = ins;
        this.m = m;
        this.view = view;
        this.consulUser = consulUser;
        this.previousData = new HashMap<>();
        Listeners(); // Inicia los listeners
    }

    private void actualizar2() {
        String text = view.rolUser.getText();

        // Si el texto no está vacío, realiza la conversión
        if (text != null && !text.isEmpty()) {
            // Convierte la primera letra a mayúscula y el resto a minúsculas
            String formattedText = text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();

            // Si el texto formateado es diferente del actual, actualiza el JTextField
            if (!text.equals(formattedText)) {
                // Se usa un SwingUtilities.invokeLater para asegurar que el cambio de texto ocurra en el hilo de eventos
                SwingUtilities.invokeLater(() -> view.rolUser.setText(formattedText));
            }
        }
    }
        // Método para iniciar la vista de perfiles
    public void Iniciar() {
        // Agregar elementos al menú de gráficos
        view.Graficos.add(GraficoPrincipal);
        view.Graficos.add(GraficosCompararInstitucion);
        view.Graficos.add(GraficoHistorico);
        view.setVisible(true);
        view.setTitle("Ver Perfiles");
        // Cargar datos de usuarios desde la base de datos
        Map<String, Usuario> datosMap = new HashMap<>();
        List<Usuario> datos = consulUser.datos();

        for (Usuario user : datos) {
            datosMap.put(user.getCorreo(), user); // Usa el correo como clave
        }

        CargarDatos(datosMap);// Carga los datos en la tabla
        CamposInhabilitados();// Inhabilita los campos de entrada
        startPolling();// Inicia el polling para actualizaciones

        // Agregar action listeners para los elementos del menú
        GraficoPrincipal.addActionListener(e -> vistaGraficoPrincipal());
        GraficosCompararInstitucion.addActionListener(e -> vistaCompararInstituciones());
        GraficoHistorico.addActionListener(e -> vistaGraficoHistorico());
    }
    // Maneja las acciones de los botones
    public void actionPerformed(ActionEvent e) {
        String Correo = view.correoUser.getText();
        if (e.getSource() == view.buscarButton) {
            view.correoUser.setEditable(true);
        }
        if (e.getSource() == view.editarButton) {
            // Permite editar los campos de rol
            view.correoUser.setEditable(false);
            view.rolUser.setEditable(true);
            if (!Correo.isEmpty()) {
                // Validación y búsqueda del usuario
                if (consulUser.esEmail(Correo)) {
                    if (consulUser.ExisteUsuario(Correo) > 0) {
                        List<Usuario> datos = consulUser.BuscarUsuario(Correo);
                        for (Usuario mod : datos) {
                            view.nombreUser.setText(mod.getNombre());
                            view.apellidoUser.setText(mod.getApellido());
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "El usuario no existe");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "No es un correo válido");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Campo Correo vacío");
            }
        }
        if (e.getSource() == view.guardarCambiosButton) {
            guardarCambios(); // Guarda los cambios realizados
        }
        if (e.getSource() == view.eliminarButton) {
            eliminar(); // Elimina el usuario
        }
        if (e.getSource() == view.inicioButton) {
            BotonInicio(); // Vuelve a la pantalla de inicio
        }
        if (e.getSource() == view.perfil) {
            vistaPerfil(); // Muestra la vista del perfil
        }
        if (e.getSource() == view.Calcular) {
            vistaCalcular(); // Muestra la vista de calcular
        }
        if (e.getSource() == view.RegistrarEmisión) {
            vistaRegistrarEmision(); // Muestra la vista de registrar emisión
        }
        if (e.getSource() == view.Informes) {
            vistaInforme(); // Muestra la vista de informes
        }
        if (e.getSource() == view.RegistrarInstitucion) {
            vistaActualizarInstitucion(); // Muestra la vista de registrar institución
        }
        if (e.getSource() == view.Reducir) {
            vistaReducir(); // Muestra la vista para reducir emisiones
        }
    }


    // Método para guardar cambios en los privilegios del usuario
    private void guardarCambios() {
        String correo = view.correoUser.getText(); // Obtener el correo del usuario a actualizar
        String rol = view.rolUser.getText();

        if (!correo.isEmpty() && !rol.isEmpty()) {
            int privilegio;
            // Determinar el privilegio basado en el rol
            if (rol.equals("Administrador")) {
                privilegio = 9;
            } else if (rol.equals("SuperAdmin")) {
                privilegio = 10;
            } else {
                JOptionPane.showMessageDialog(null, "Rol no válido");
                return; // Salir del método si el rol no es válido
            }

            // Llamar a EditarPrivilegios con el correo del usuario que quieres actualizar
            if (consulUser.EditarPrivilegios(correo, privilegio)) {
                JOptionPane.showMessageDialog(null, "Privilegios actualizados para el usuario con el correo " + correo);
                limpiarCampos(); // Limpia los campos
                CamposInhabilitados(); // Inhabilita campos
            } else {
                JOptionPane.showMessageDialog(null, "Error en la consulta");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Faltan datos para actualizar");
        }
    }


    // Método para eliminar un usuario
    private void eliminar() {
        String correo = view.correoUser.getText();
        if (!correo.isEmpty()) {
            try {
                if (consulUser.eliminarUsuario(correo)) {
                    JOptionPane.showMessageDialog(null, "El usuario con el correo: " + correo + " ha sido eliminado");
                } else {
                    JOptionPane.showMessageDialog(null, "Error en la consulta");
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, "Se produjo un error: " + e.getMessage());
            }
        } else {
            JOptionPane.showMessageDialog(null, "Campo correo vacío");
        }
    }

    // Método para cargar datos en la tabla
    public void CargarDatos(Map<String, Usuario> data) {
        DefaultTableModel tableModel = (DefaultTableModel) view.user.getModel();
        tableModel.setRowCount(0); // Limpiar filas existentes

        for (Usuario a : data.values()) {
            Object[] rowData = {
                    a.getNombre(),
                    a.getApellido(),
                    a.getCorreo(),
                    a.getTipoUsuario(),
                    a.getDescripcion(),
                    a.getNombreInstticion(),
                    a.getMunicipio()
            };
            tableModel.addRow(rowData);
        }

        // Configura los anchos de columna
        SwingUtilities.invokeLater(() -> {
            view.user.getColumnModel().getColumn(0).setPreferredWidth(150);
            view.user.getColumnModel().getColumn(2).setPreferredWidth(150);
            view.user.getColumnModel().getColumn(3).setPreferredWidth(150);
            view.user.getColumnModel().getColumn(4).setPreferredWidth(150);
            view.user.getColumnModel().getColumn(5).setPreferredWidth(150);
            view.user.getColumnModel().getColumn(6).setPreferredWidth(150);
            view.user.repaint();
        });

        view.user.setVisible(true);
        view.tblUsuario.setVisible(true);
    }

    // Método para inhabilitar campos de texto
    public void CamposInhabilitados() {
        view.apellidoUser.setEditable(false);
        view.nombreUser.setEditable(false);
        view.correoUser.setEditable(false);
        view.rolUser.setEditable(false);
    }

    // Inicia el polling para actualizaciones cada 5 segundos
    public void startPolling() {
        timer = new Timer(5000, new ActionListener() { // Intervalo de 5 segundos
            @Override
            public void actionPerformed(ActionEvent e) {
                checkForUpdates();
            }
        });
        timer.start();
    }


    // Comprueba si hay actualizaciones en los datos
    private void checkForUpdates() {
        // Consulta la base de datos para obtener los datos actuales
        List<Usuario> datos = consulUser.datos();

        // Crea un mapa para los datos nuevos
        Map<String, Usuario> currentData = new HashMap<>();
        for (Usuario user : datos) {
            currentData.put(user.getCorreo(), user); // Usa el correo como clave
        }

        // Compara los datos anteriores con los nuevos
        if (isDataChanged(currentData)) {
            CargarDatos(currentData);
            previousData = currentData; // Actualiza los datos anteriores
        }
    }

    // Compara datos antiguos con los nuevos para detectar cambios
    private boolean isDataChanged(Map<String, Usuario> newData) {
        if (previousData.size() != newData.size()) {
            return true; // El tamaño de los datos ha cambiado, lo que indica un cambio
        }

        // Compara cada registro para detectar cambios
        for (Map.Entry<String, Usuario> entry : newData.entrySet()) {
            Usuario newUser = entry.getValue();
            Usuario oldUser = previousData.get(entry.getKey());

            if (oldUser == null || !oldUser.equals(newUser)) {
                return true; // Hay cambios en el registro
            }
        }

        return false; // No hay cambios
    }

    // Limpia los campos de texto
    private void limpiarCampos() {
        view.rolUser.setText("");
        view.correoUser.setText("");
        view.nombreUser.setText("");
        view.apellidoUser.setText("");
    }

    private void BotonInicio() {
        ControladoInicio control = new ControladoInicio(ins, user, m);
        control.inicio();// Llama al controlador de inicio
        view.dispose();// Cierra la vista actual
    }
    // Métodos para abrir diferentes vistas
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

    public void vistaRegistrarEmision() {
        Emision emisionView = new Emision();
        EmisionModelo mod = new EmisionModelo();
        ConsultasEmision consul = new ConsultasEmision();
        EmisionControlador controlador = new EmisionControlador(mod, consul, emisionView, ins, m, user);
        controlador.iniciar();
        emisionView.setVisible(true);
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

    public void vistaActualizarInstitucion() {
        ConsultasInstitucion consul = new ConsultasInstitucion();
        RegistrarInstitucion viewIns = new RegistrarInstitucion();
        InstitucionModelo mod = new InstitucionModelo();
        InstitucionControlador control = new InstitucionControlador(ins, m, viewIns, consul, mod, user);
        viewIns.setVisible(true);
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
    // Configura los listeners para los botones de la vista
    public void Listeners() {
        this.view.editarButton.addActionListener(this::actionPerformed);
        this.view.guardarCambiosButton.addActionListener(this::actionPerformed);
        this.view.buscarButton.addActionListener(this::actionPerformed);
        this.view.eliminarButton.addActionListener(this::actionPerformed);
        this.view.inicioButton.addActionListener(this::actionPerformed);
        this.view.Calcular.addActionListener(this::actionPerformed);
        this.view.perfil.addActionListener(this::actionPerformed);
        this.view.RegistrarEmisión.addActionListener(this::actionPerformed);
        this.view.Informes.addActionListener(this::actionPerformed);
        this.view.RegistrarInstitucion.addActionListener(this::actionPerformed);
        this.view.Reducir.addActionListener(this::actionPerformed);
        view.rolUser.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                actualizar2();
            }// Actualiza el texto al insertar

            @Override
            public void removeUpdate(DocumentEvent e) {
                actualizar2();
            } // Actualiza el texto al eliminar

            @Override
            public void changedUpdate(DocumentEvent e) {
                actualizar2();
            }// Actualiza el texto al cambiar
        });


    }

}
