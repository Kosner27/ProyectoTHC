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
        switch (user.getTipoUsuario()) {
            case "Superadmin":
                CargarDatos();
                CamposInhabilitados();
                deshabilitarEdicionTabla();
                break;
            case "Administrador":
                CargarDatosAdministradorSede();
                CamposInhabilitados();
                deshabilitarEdicionTabla();
                view.verInstitucion.setVisible(false);
                break;

        }

        ;// Inhabilita los campos de entrada (excepto correo)
        // Asignar el ActionListener para el botón de búsqueda
        //view.buscarButton.addActionListener(e -> buscarUsuario());
        // Asigna los ActionListeners a los botones
        GraficoPrincipal.addActionListener(e -> vistaGraficoPrincipal());
        GraficosCompararInstitucion.addActionListener(e -> vistaCompararInstituciones());
        GraficoHistorico.addActionListener(e -> vistaGraficoHistorico());
        view.limpiarCamposButton.setEnabled(false);
    }

    // Maneja las acciones de los botones
    private void actionPerformed(ActionEvent e) {

        if (e.getSource() == view.buscarButton) {
            buscarPorCorreo();
        }
        if (e.getSource() == view.editarButton) {
            // Permite editar los campos de rol
            editarPrivilegios();
            view.limpiarCamposButton.setEnabled(true);
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
        if (e.getSource() == view.RegistrarEmision) {
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
        if(e.getSource() == view.limpiarCamposButton){
            limpiarCampos();
            view.correoUser.setFocusable(true);

        }
       if(e.getSource()== view.actualizarTablaButton){
           if(user.getTipoUsuario().equals("Superadmin")){
               actualizarButton();
                CargarDatos();
           }else{
               actualizarButton();
               CargarDatosAdministradorSede();
           }

       }
       if(e.getSource() == view.verInstitucion){
           vistaVerInstitucion();
       }
        if(e.getSource() == view.registrarUsuarioButton){
            registrarUsuario();
        }
    }


    // Método para guardar cambios en los privilegios del usuario


    // Método para cargar datos en la tabla
    private void CargarDatos() {
        DefaultTableModel tableModel;
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Asegurar que las celdas no sean editables
            }
        };
        tableModel.setRowCount(0);
        tableModel = (DefaultTableModel) view.user.getModel();
        List<Usuario> datos = consulUser.datos();

        for (Usuario a : datos) {
            Object[] rowData = {
                    a.getNombre(),
                    a.getApellido(),
                    a.getCorreo(),
                    a.getTipoUsuario(),
                    a.getDescripcion(),
                    a.getNombreInstticion(),
                    a.getMunicipio()
            };
            tableModel.addRow(rowData); // Agregar una fila por cada usuario
        }

        // Configura los anchos de columna

        view.user.getColumnModel().getColumn(0).setPreferredWidth(150);
        view.user.getColumnModel().getColumn(2).setPreferredWidth(150);
        view.user.getColumnModel().getColumn(3).setPreferredWidth(150);
        view.user.getColumnModel().getColumn(4).setPreferredWidth(150);
        view.user.getColumnModel().getColumn(5).setPreferredWidth(150);
        view.user.getColumnModel().getColumn(6).setPreferredWidth(150);
        view.user.repaint();
        view.user.setModel(tableModel);
        view.user.setVisible(true);
        view.tblUsuario.setVisible(true);
    }

    private void CargarDatosAdministradorSede() {
        String municipio = m.getNombreM();
        String institucion = ins.getNombreInstitucion();
        DefaultTableModel tableModel;
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Asegurar que las celdas no sean editables
            }
        };
        tableModel.setRowCount(0);
        tableModel = (DefaultTableModel) view.user.getModel();
        List<Usuario> datos = consulUser.datosAdministradorDeSede(municipio, institucion);

        for (Usuario a : datos) {
            Object[] rowData = {
                    a.getNombre(),
                    a.getApellido(),
                    a.getCorreo(),
                    a.getTipoUsuario(),
                    a.getDescripcion(),
                    a.getNombreInstticion(),
                    a.getMunicipio()
            };
            tableModel.addRow(rowData); // Agregar una fila por cada usuario
        }

        // Configura los anchos de columna

        view.user.getColumnModel().getColumn(0).setPreferredWidth(150);
        view.user.getColumnModel().getColumn(2).setPreferredWidth(150);
        view.user.getColumnModel().getColumn(3).setPreferredWidth(150);
        view.user.getColumnModel().getColumn(4).setPreferredWidth(150);
        view.user.getColumnModel().getColumn(5).setPreferredWidth(150);
        view.user.getColumnModel().getColumn(6).setPreferredWidth(150);
        view.user.repaint();
        view.user.setModel(tableModel);
        view.user.setVisible(true);
        view.tblUsuario.setVisible(true);
    }


    private void llenarFormularioDesdeTabla() {
        // Obtener la fila seleccionada
        int selectedRow = view.user.getSelectedRow();

        if (selectedRow != -1) { // Verifica que se haya seleccionado una fila
            // Obtener el modelo de la tabla
            DefaultTableModel tableModel = (DefaultTableModel) view.user.getModel();

            // Obtener los valores de la fila seleccionada
            String nombreUsuario = tableModel.getValueAt(selectedRow, 0).toString();
            String apellidoUsuario = tableModel.getValueAt(selectedRow, 1).toString();
            String correo = tableModel.getValueAt(selectedRow, 2).toString();
            String rol = tableModel.getValueAt(selectedRow, 3).toString();
            String institucion = tableModel.getValueAt(selectedRow, 4).toString();
            String sede = tableModel.getValueAt(selectedRow, 5).toString();

            // Llenar los campos del formulario con los valores obtenidos
            view.nombreUser.setText(nombreUsuario);
            view.apellidoUser.setText(apellidoUsuario);
            view.correoUser.setText(correo);
            view.rolUser.setText(rol);

        }
    }

    // Método para inhabilitar campos de texto
    private void CamposInhabilitados() {
        view.apellidoUser.setEditable(false);
        view.nombreUser.setEditable(false);
        view.rolUser.setEditable(false);

    }

    private void buscarPorCorreo() {
        DefaultTableModel tableModel = (DefaultTableModel) view.user.getModel();
        tableModel.setRowCount(0);
        String correo = view.correoUser.getText().trim().toUpperCase();
        if (correo.isEmpty()) {
            JOptionPane.showMessageDialog(view, "Por favor ingrese un nombre de la institucion.");
            return;
        }
        List<Usuario> datos = consulUser.BuscarUsuario(correo);
        if (datos.isEmpty()) {
            JOptionPane.showMessageDialog(view, "No se encontraron usuarios con el correo ingresado.");
        } else {
            // Agregar los resultados encontrados al modelo de la tabla
            for (Usuario a : datos) {
                Object[] rowData = {
                        a.getNombre(),
                        a.getApellido(),
                        a.getCorreo(),
                        a.getTipoUsuario(),
                        a.getDescripcion(),
                        a.getNombreInstticion(),
                        a.getMunicipio()
                };
                tableModel.addRow(rowData); // Agregar una fila por cada usuario
            }

        }
        view.user.repaint();
        view.user.setModel(tableModel);
        view.user.setVisible(true);
        view.tblUsuario.setVisible(true);


    }

    // Limpia los campos de texto
    private void limpiarCampos() {
        view.rolUser.setText("");
        view.correoUser.setText("");
        view.nombreUser.setText("");
        view.apellidoUser.setText("");
        view.correoUser.setEditable(true);
    }

    private void BotonInicio() {
        ControladoInicio control = new ControladoInicio(ins, user, m);
        control.inicio();// Llama al controlador de inicio
        view.dispose();// Cierra la vista actual
    }

    // Métodos para abrir diferentes vistas
    private void vistaPerfil() {
        Conexion conn = new Conexion();
        Perfil per = new Perfil();
        ConsultaUsuario cons = new ConsultaUsuario(conn);
        PerfilCOntrolador control = new PerfilCOntrolador(user, per, ins, m, cons);
        control.Iniciar();
        per.setVisible(true);
        view.dispose();
    }

    private void vistaCalcular() {
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

    private void vistaRegistrarEmision() {
        Emision emisionView = new Emision();
        EmisionModelo mod = new EmisionModelo();
        ConsultasEmision consul = new ConsultasEmision();
        EmisionControlador controlador = new EmisionControlador(mod, consul, emisionView, ins, m, user);
        controlador.iniciar();
        emisionView.setVisible(true);
        view.dispose();
    }

    private void vistaInforme() {
        Conexion con = new Conexion();
        ConsultaInforme consul = new ConsultaInforme(con);
        ModeloInforme mod = new ModeloInforme();
        Informe viewInfo = new Informe();
        ControladorInforme contro = new ControladorInforme(viewInfo, mod, consul, m, ins, user);
        contro.iniciar();
        viewInfo.setVisible(true);
        view.dispose();
    }

    private void vistaGraficoPrincipal() {
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

    private void vistaCompararInstituciones() {
        CompararOtrarInstituciones comIns = new CompararOtrarInstituciones();
        GraficoComparar viewGraf = new GraficoComparar();
        Conexion conn = new Conexion();
        GraficoCompararConsultas consultas = new GraficoCompararConsultas(conn);
        GraficoCompararModelo mod = new GraficoCompararModelo();
        ComparaInstitucion contro = new ComparaInstitucion(mod, consultas, comIns, viewGraf, ins, m, user);
        contro.iniciar();
        view.dispose();
    }

    private void vistaGraficoHistorico() {
        Conexion con = new Conexion();
        TendenciaModelo mod = new TendenciaModelo();
        ConsultasTendencias consult = new ConsultasTendencias(con);
        GraficoTendencia viewGraf = new GraficoTendencia();
        TendenciaControlador control = new TendenciaControlador(mod, consult, viewGraf, m, ins, user);
        viewGraf.setVisible(true);
        control.iniciar();
        view.dispose();
    }

    private void vistaActualizarInstitucion() {
        ConsultasInstitucion consul = new ConsultasInstitucion();
        VerDatosInstitucion viewIns = new VerDatosInstitucion();
        InstitucionModelo mod = new InstitucionModelo();
        InstitucionControlador control = new InstitucionControlador(ins, m, viewIns, consul, mod, user);
        viewIns.setVisible(true);
        control.iniciar();
        view.dispose();

    }

    private void vistaReducir() {
        Conexion con = new Conexion();
        Reducir2 vista = new Reducir2();
        GraficoConsulta consul = new GraficoConsulta(con);
        ControladorReducir redu = new ControladorReducir(ins, consul, vista, m, user);
        redu.Iniciar();
        view.dispose();
    }

    private void Listeners() {
        this.view.editarButton.addActionListener(this::actionPerformed);
        this.view.guardarCambiosButton.addActionListener(this::actionPerformed);
        this.view.buscarButton.addActionListener(this::actionPerformed);
        this.view.eliminarButton.addActionListener(this::actionPerformed);
        this.view.inicioButton.addActionListener(this::actionPerformed);
        this.view.Calcular.addActionListener(this::actionPerformed);
        this.view.perfil.addActionListener(this::actionPerformed);
        this.view.RegistrarEmision.addActionListener(this::actionPerformed);
        this.view.Informes.addActionListener(this::actionPerformed);
        this.view.RegistrarInstitucion.addActionListener(this::actionPerformed);
        this.view.Reducir.addActionListener(this::actionPerformed);
        this.view.limpiarCamposButton.addActionListener(this::actionPerformed);
        this.view.user.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.view.actualizarTablaButton.addActionListener(this::actionPerformed);
        this.view.verInstitucion.addActionListener(this::actionPerformed);
        this.view.user.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 1) { // Verifica que sea un doble clic
                    llenarFormularioDesdeTabla();
                }
            }
        });

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
        this.view.registrarUsuarioButton.addActionListener(this::actionPerformed);
    }

    private void deshabilitarEdicionTabla() {
        DefaultTableModel tableModel = (DefaultTableModel) view.user.getModel();

        // Deshabilitar la edición en la tabla
        view.user.setDefaultEditor(Object.class, null);

        // Deshabilitar la selección de celdas
        view.user.setCellSelectionEnabled(false);

        // Deshabilitar la selección de filas para que no se puedan editar las celdas
        view.user.setRowSelectionAllowed(false);
        view.user.setColumnSelectionAllowed(false);

        // Opcional: cambiar el color de fondo de la tabla para hacerla visualmente "bloqueada"
        view.user.setBackground(new java.awt.Color(240, 240, 240));
    }

    private void editarPrivilegios() {
        String Correo = view.correoUser.getText();
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

    private void guardarCambios(){
        String correo = view.correoUser.getText(); // Obtener el correo del usuario a actualizar
        String rol = view.rolUser.getText();

        if (!correo.isEmpty() && !rol.isEmpty()) {
            int privilegio;
            // Determinar el privilegio basado en el rol
            if (rol.equals("Administrador")) {
                privilegio = 9;
            } else if (rol.equals("Superadmin")) {
                privilegio = 10;
            } else {
                JOptionPane.showMessageDialog(null, "Rol no válido");
                return; // Salir del método si el rol no es válido
            }

            // Llamar a EditarPrivilegios con el correo del usuario que quieres actualizar
            if (consulUser.EditarPrivilegios(correo, privilegio)) {
                JOptionPane.showMessageDialog(null, "Privilegios actualizados para el usuario con el correo " + correo);
                CamposInhabilitados(); // Inhabilita campos
            } else {
                JOptionPane.showMessageDialog(null, "Error en la consulta");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Faltan datos para actualizar");
        }
    }

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

    private void actualizarButton(){
        DefaultTableModel tableModel = (DefaultTableModel) view.user.getModel();

        // Limpiar cualquier dato existente en la tabla
        tableModel.setRowCount(0);

    }

    private void vistaVerInstitucion(){
        Conexion con = new Conexion();
        VerInstituciones verInstituciones = new VerInstituciones();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(con);
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();
        InstitucionModelo institucionModelo = new InstitucionModelo();
        ContraladorVerInstituciones contraladorVerInstituciones = new ContraladorVerInstituciones(consultasInstitucion,consultaNucleo,
                institucionModelo,verInstituciones,user,m);
        contraladorVerInstituciones.iniciar();
        //view.dispose();
    }

    private void registrarUsuario(){
        Conexion conexion = new Conexion();
        RegistrarUsuario registrarUsuario = new RegistrarUsuario();
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(conexion);
        Usuario usuario = new Usuario();
        ControladoRegistrarUsuario controladoRegistrarUsuario = new ControladoRegistrarUsuario(usuario, registrarUsuario, consultaUsuario);
        controladoRegistrarUsuario.iniciar();
    }


}
