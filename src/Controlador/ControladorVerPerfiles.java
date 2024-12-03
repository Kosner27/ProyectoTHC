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


public class ControladorVerPerfiles {
    // Variables de instancia

    public ModeloUsuario modeloUsuario;
    public ModeloInstitucion modeloInstitucion;
    public ModeloMunicipio modeloMunicipio;
    public VerPerfiles view;
    public ConsultaUsuario consultaUsuario;

    // Elementos del menú para gráficas
    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");

    // Constructor
    public ControladorVerPerfiles(ModeloUsuario modeloUsuario, ModeloInstitucion modeloInstitucion,
                                  ModeloMunicipio modeloMunicipio, VerPerfiles view, ConsultaUsuario consultaUsuario) {
        this.modeloUsuario = modeloUsuario;
        this.modeloInstitucion = modeloInstitucion;
        this.modeloMunicipio = modeloMunicipio;
        this.view = view;
        this.consultaUsuario = consultaUsuario;
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
        switch (modeloUsuario.getTipoUsuario()) {
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
        GraficoPrincipal.addActionListener(_ -> vistaGraficoPrincipal());
        GraficosCompararInstitucion.addActionListener(_ -> vistaCompararInstituciones());
        GraficoHistorico.addActionListener(_ -> vistaGraficoHistorico());
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
             eliminar(); // Elimina el modeloUsuario
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
           if (modeloUsuario.getTipoUsuario().equals("Superadmin")) {
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


    // Método para guardar cambios en los privilegios del modeloUsuario


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
        List<ModeloUsuario> datos = consultaUsuario.datos();

        for (ModeloUsuario a : datos) {
            Object[] rowData = {
                    a.getNombre(),
                    a.getApellido(),
                    a.getCorreo(),
                    a.getTipoUsuario(),
                    a.getDescripcion(),
                    a.getNombreInstticion(),
                    a.getMunicipio()
            };
            tableModel.addRow(rowData); // Agregar una fila por cada modeloUsuario
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
        String municipio = this.modeloMunicipio.getNombreM();
        String institucion = modeloInstitucion.getNombreInstitucion();
        DefaultTableModel tableModel;
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Asegurar que las celdas no sean editables
            }
        };
        tableModel.setRowCount(0);
        tableModel = (DefaultTableModel) view.user.getModel();
        List<ModeloUsuario> datos = consultaUsuario.datosAdministradorDeSede(municipio, institucion);

        for (ModeloUsuario a : datos) {
            Object[] rowData = {
                    a.getNombre(),
                    a.getApellido(),
                    a.getCorreo(),
                    a.getTipoUsuario(),
                    a.getDescripcion(),
                    a.getNombreInstticion(),
                    a.getMunicipio()
            };
            tableModel.addRow(rowData); // Agregar una fila por cada modeloUsuario
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
        List<ModeloUsuario> datos = consultaUsuario.BuscarUsuario(correo);
        if (datos.isEmpty()) {
            JOptionPane.showMessageDialog(view, "No se encontraron usuarios con el correo ingresado.");
        } else {
            // Agregar los resultados encontrados al modelo de la tabla
            for (ModeloUsuario a : datos) {
                Object[] rowData = {
                        a.getNombre(),
                        a.getApellido(),
                        a.getCorreo(),
                        a.getTipoUsuario(),
                        a.getDescripcion(),
                        a.getNombreInstticion(),
                        a.getMunicipio()
                };
                tableModel.addRow(rowData); // Agregar una fila por cada modeloUsuario
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
        ControladorPestaniaPrincipal control = new ControladorPestaniaPrincipal(modeloInstitucion, modeloUsuario, modeloMunicipio);
        control.inicio();// Llama al controlador de inicio
        view.dispose();// Cierra la vista actual
    }

    // Métodos para abrir diferentes vistas
    private void vistaPerfil() {
        Conexion conn = new Conexion();
        Perfil per = new Perfil();
        ConsultaUsuario cons = new ConsultaUsuario(conn);
        ControladorPerfil control = new ControladorPerfil(modeloUsuario, per, modeloInstitucion, modeloMunicipio, cons);
        control.Iniciar();
        per.setVisible(true);
        view.dispose();
    }

    private void vistaCalcular() {
        Conexion con = new Conexion();
        Vistas.Calcular viewCal = new Calcular();
        ModeloEmisionCalcular mod = new ModeloEmisionCalcular();
        CalcularConsultas consul = new CalcularConsultas(con);
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        ControladorCalcular controlador = new ControladorCalcular(mod, consul, viewCal, modeloInstitucion, consultaUsuario, modeloUsuario, modeloMunicipio);
        controlador.iniciar();
        viewCal.setVisible(true);
        view.dispose();
    }

    private void vistaRegistrarEmision() {
        Emision emisionView = new Emision();
        ModeloEmision mod = new ModeloEmision();
        ConsultasEmision consul = new ConsultasEmision();
        ControladorEmision controlador = new ControladorEmision(mod, consul, emisionView, modeloInstitucion, modeloMunicipio, modeloUsuario);
        controlador.iniciar();
        emisionView.setVisible(true);
        view.dispose();
    }

    private void vistaInforme() {
        Conexion con = new Conexion();
        ConsultaInforme consul = new ConsultaInforme(con);
        ModeloEmisionInforme mod = new ModeloEmisionInforme();
        Informe viewInfo = new Informe();
        ControladorInforme contro = new ControladorInforme(viewInfo, mod, consul, modeloMunicipio, modeloInstitucion, modeloUsuario);
        contro.iniciar();
        viewInfo.setVisible(true);
        view.dispose();
    }

    private void vistaGraficoPrincipal() {
        Conexion con = new Conexion();
        GraficoConsulta consul = new GraficoConsulta(con);
        Vistas.Graficos viewGraf = new Graficos();
        GraficorModeloInstitucion mod = new GraficorModeloInstitucion();
        ModeloInstitucion modelo = new ModeloInstitucion();
        ControladorGrafico contro = new ControladorGrafico(mod, consul, viewGraf, modelo, modeloMunicipio, modeloUsuario, modeloInstitucion);
        contro.iniciar();
        viewGraf.setVisible(true);
        view.dispose();
    }

    private void vistaCompararInstituciones() {
        CompararOtrarInstituciones comIns = new CompararOtrarInstituciones();
        GraficoComparar viewGraf = new GraficoComparar();
        Conexion conn = new Conexion();
        GraficoCompararConsultas consultas = new GraficoCompararConsultas(conn);
        ModeloGraficoComparar mod = new ModeloGraficoComparar();
        ControladorCompararInstitucion contro = new ControladorCompararInstitucion(mod, consultas, comIns, viewGraf, modeloInstitucion, modeloMunicipio, modeloUsuario);
        contro.iniciar();
        view.dispose();
    }

    private void vistaGraficoHistorico() {
        Conexion con = new Conexion();
        ModeloTendencia mod = new ModeloTendencia();
        ConsultasTendencias consult = new ConsultasTendencias(con);
        GraficoTendencia viewGraf = new GraficoTendencia();
        ControladorTendencia control = new ControladorTendencia(mod, consult, viewGraf, modeloMunicipio, modeloInstitucion, modeloUsuario);
        viewGraf.setVisible(true);
        control.iniciar();
        view.dispose();
    }

    private void vistaActualizarInstitucion() {
        ConsultasInstitucion consul = new ConsultasInstitucion();
        VerDatosInstitucion viewIns = new VerDatosInstitucion();
        ModeloInstitucion mod = new ModeloInstitucion();
        ControladorInstitucion control = new ControladorInstitucion(modeloInstitucion, modeloMunicipio, viewIns, consul, mod, modeloUsuario);
        viewIns.setVisible(true);
        control.iniciar();
        view.dispose();

    }

    private void vistaReducir() {
        Conexion con = new Conexion();
        Reducir2 vista = new Reducir2();
        GraficoConsulta consul = new GraficoConsulta(con);
        ControladorReducir redu = new ControladorReducir(modeloInstitucion, consul, vista, modeloMunicipio, modeloUsuario);
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
            // Validación y búsqueda del modeloUsuario
            if (consultaUsuario.esEmail(Correo)) {
                if (consultaUsuario.ExisteUsuario(Correo) > 0) {
                    List<ModeloUsuario> datos = consultaUsuario.BuscarUsuario(Correo);
                    for (ModeloUsuario mod : datos) {
                        view.nombreUser.setText(mod.getNombre());
                        view.apellidoUser.setText(mod.getApellido());
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "El modeloUsuario no existe");
                }
            } else {
                JOptionPane.showMessageDialog(null, "No es un correo válido");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Campo Correo vacío");
        }
    }

    private void guardarCambios(){
        String correo = view.correoUser.getText(); // Obtener el correo del modeloUsuario a actualizar
        String rol = view.rolUser.getText();

        if (!correo.isEmpty() && !rol.isEmpty()) {
            int privilegio;
            // Determinar el privilegio basado en el rol
            if (rol.equals("Administrador")) {
                privilegio = 9;
            } else if (rol.equals("Superadmin")) {
                privilegio = 10;
            } else {
                JOptionPane.showMessageDialog(null, "ModeloRol no válido");
                return; // Salir del método si el rol no es válido
            }

            // Llamar a EditarPrivilegios con el correo del modeloUsuario que quieres actualizar
            if (consultaUsuario.EditarPrivilegios(correo, privilegio)) {
                JOptionPane.showMessageDialog(null, "Privilegios actualizados para el modeloUsuario con el correo " + correo);
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
                if (consultaUsuario.eliminarUsuario(correo)) {
                    JOptionPane.showMessageDialog(null, "El modeloUsuario con el correo: " + correo + " ha sido eliminado");
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
        ModeloInstitucion modeloInstitucion = new ModeloInstitucion();
        ControladorVerInstituciones contraladorVerInstituciones = new ControladorVerInstituciones(consultasInstitucion, consultaNucleo,
                modeloInstitucion, verInstituciones, modeloUsuario, modeloMunicipio);
        contraladorVerInstituciones.iniciar();
        //view.dispose();
    }

    private void registrarUsuario(){
        Conexion conexion = new Conexion();
        RegistrarUsuario registrarUsuario = new RegistrarUsuario();
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(conexion);
        ModeloUsuario modeloUsuario = new ModeloUsuario();
        ControladorRegistrarUsuario controladorRegistrarUsuario = new ControladorRegistrarUsuario(modeloUsuario, registrarUsuario, consultaUsuario);
        controladorRegistrarUsuario.iniciar();
    }


}
