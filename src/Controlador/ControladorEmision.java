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

public class ControladorEmision implements ActionListener {
    private final ModeloEmision modeloEmision;
    private final ConsultasEmision consultasEmision;
    private final Emision view;
    private TableRowSorter<DefaultTableModel> sorter;
    public static Diccionario factorEmision = new Diccionario();
    public ModeloInstitucion modeloInstitucion;
    public ModeloMunicipio modeloMunicipio;
    public ModeloUsuario modeloUsuario;
    private Timer timer;
    private Map<String, ModeloEmision> previousData;
    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");

    public ControladorEmision(ModeloEmision modeloEmision, ConsultasEmision consultasEmision, Emision view, ModeloInstitucion modeloInstitucion,
                              ModeloMunicipio modeloMunicipio, ModeloUsuario modeloUsuario) {
        this.modeloEmision = modeloEmision;
        this.consultasEmision = consultasEmision;
        this.view = view;

        this.modeloMunicipio = modeloMunicipio;
        this.modeloInstitucion = modeloInstitucion;
        this.modeloUsuario = modeloUsuario;
        Listeners();

    }

    // Método que actualiza el texto de un campo de texto (JTextField) convirtiéndolo a mayúsculas
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

    /**
     * Método que actualiza el valor en el campo de texto `Factor` dependiendo de la clave ingresada en `nombre`.
     * Realiza una búsqueda para encontrar el valor correspondiente y lo muestra en el campo de texto.
     */
    private void actualizar() {
        // Obtiene el texto ingresado en el campo `nombre`, eliminando los espacios extra
        String Nombre = view.nombre.getText().trim();

        // Si el nombre no está vacío, realiza la búsqueda del valor asociado
        if (!Nombre.isEmpty()) {
            // Llama al método de búsqueda de factor de emisión con el nombre proporcionado
            Double valor = factorEmision.buscarClave(Nombre);

            // Si se encuentra un valor, se muestra en el campo `Factor`
            if (valor != null) {
                view.Factor.setText(valor.toString());
            } else {
                // Si no se encuentra el valor, se limpia el campo `Factor`
                view.Factor.setText("");
            }
        }
    }

    /**
     * Método que inicializa la vista, configurando los gráficos y cargando los datos de emisiones.
     * Dependiendo del tipo de modeloUsuario, se establecen visibilidad y acciones para los gráficos.
     */
    public void iniciar() {
        // Agrega los gráficos a la vista
        view.Graficos.add(GraficoPrincipal);
        view.Graficos.add(GraficosCompararInstitucion);
        view.Graficos.add(GraficoHistorico);

        // Carga los datos de emisiones en un mapa para su posterior uso
        Map<String, ModeloEmision> datosMap = new HashMap<>();
        List<ModeloEmision> datos = consultasEmision.dato();

        // Llena el mapa con los datos de emisiones, usando el nombre de la fuente como clave
        for (ModeloEmision emi : datos) {
            datosMap.put(emi.getNombreFuente(), emi);
        }

        // Carga las emisiones existentes
        loadEmisionesExistentes(datosMap);

        // Inicia el sondeo para actualizar datos en tiempo real
        startPolling();

        // Configura el modelo y el sorter de la tabla de emisiones
        DefaultTableModel tableModel = (DefaultTableModel) view.Emisiones.getModel();
        sorter = new TableRowSorter<>(tableModel);
        view.Emisiones.setRowSorter(sorter);

        // Configura las acciones dependiendo del tipo de modeloUsuario
        switch (modeloUsuario.getTipoUsuario()) {
            case "Administrador":
                // Si el modeloUsuario es un Administrador, oculta ciertos elementos y configura las acciones para los gráficos
                view.VerPerfiles.setVisible(false);
                view.setTitle("Registrar Emisión");
                view.verInstitucion.setVisible(false);
                view.setLocationRelativeTo(null);
                GraficoPrincipal.addActionListener(e -> vistaGraficoPrincipal());
                GraficosCompararInstitucion.addActionListener(e -> vistaCompararInstituciones());
                GraficoHistorico.addActionListener(e -> vistaGraficoHistorico());
                break;

            case "Superadmin":
                // Si el modeloUsuario es un Superadmin, configura las acciones para los gráficos
                view.setTitle("Registrar Emisión");
                view.setLocationRelativeTo(null);
                GraficoPrincipal.addActionListener(e -> vistaGraficoPrincipal());
                GraficosCompararInstitucion.addActionListener(e -> vistaCompararInstituciones());
                GraficoHistorico.addActionListener(e -> vistaGraficoHistorico());
                break;

            default:
                // Si el tipo de modeloUsuario no es reconocido, muestra un mensaje de error
                JOptionPane.showMessageDialog(null, "ModeloUsuario no definido en el sistema");
                break;
        }

        // Configura acciones para cada gráfico
        GraficoPrincipal.addActionListener(e -> vistaGraficoPrincipal());
        GraficosCompararInstitucion.addActionListener(e -> vistaCompararInstituciones());
        GraficoHistorico.addActionListener(e -> vistaGraficoHistorico());
    }

    /**
     * Este método maneja las acciones de los eventos generados por los componentes de la interfaz gráfica,
     * como botones y otros elementos. Dependiendo de la acción, realiza distintas tareas como guardar registros,
     * navegar entre vistas, buscar, editar, eliminar, y más.
     *
     * @param e El evento generado por un componente de la interfaz.
     */
    public void actionPerformed(ActionEvent e) {
        // Obtiene el nombre de la emisión ingresada por el modeloUsuario
        String EmisionNombre = view.nombre.getText();

        // Acción cuando se hace clic en el botón 'guardarButton'
        if (e.getSource() == view.guardarButton) {
            // Verifica si la emisión ya existe en la base de datos
            if (consultasEmision.ExisteEmision(EmisionNombre) == 0) {
                // Establece los valores del modelo 'mod' con la información ingresada por el modeloUsuario
                modeloEmision.setTipoFuente(view.Fuente.getSelectedItem().toString());
                modeloEmision.setNombreFuente(view.nombre.getText());
                modeloEmision.setEstadoFuente(view.Estado.getSelectedItem().toString());
                modeloEmision.setFactorEmision(Double.parseDouble(view.Factor.getText()));
                modeloEmision.setUnidadMedidad(view.Unidad.getSelectedItem().toString());
                modeloEmision.setAlcance(view.Alcance.getSelectedItem().toString());

                // Verifica si todos los campos están completos
                if (!view.Fuente.getSelectedItem().toString().isEmpty() && !view.Unidad.getSelectedItem().toString().isEmpty()
                        && !view.Alcance.getSelectedItem().toString().isEmpty() && !view.Factor.getText().isEmpty()
                        && !view.nombre.getText().isEmpty()) {

                    // Prepara los datos para agregar una nueva fila a la tabla de emisiones
                    String[] rows = {view.Fuente.getSelectedItem().toString(), view.Estado.getSelectedItem().toString(),
                            view.nombre.getText(), view.Unidad.getSelectedItem().toString(),
                            view.Factor.getText(), view.Alcance.getSelectedItem().toString()};

                    // Agrega la fila a la tabla de emisiones
                    DefaultTableModel tableModel = (DefaultTableModel) view.Emisiones.getModel();
                    tableModel.addRow(rows);
                    view.Emisiones.setModel(tableModel);
                    view.Emisiones.setVisible(true);
                    view.Contenedor.setVisible(true);

                    // Configura el tamaño de las columnas de la tabla y redibuja la interfaz
                    SwingUtilities.invokeLater(() -> {
                        view.Emisiones.getColumnModel().getColumn(0).setPreferredWidth(150);
                        view.Emisiones.getColumnModel().getColumn(2).setPreferredWidth(150);
                        view.Emisiones.getColumnModel().getColumn(3).setPreferredWidth(150);
                        view.Emisiones.getColumnModel().getColumn(4).setPreferredWidth(150);
                        view.Emisiones.repaint();
                    });

                    // Intenta registrar la nueva emisión
                    if (consultasEmision.registrarEmision(modeloEmision)) {
                        JOptionPane.showMessageDialog(null, "Registro guardado");
                        Limpiar();  // Limpia los campos
                    } else {
                        JOptionPane.showMessageDialog(null, "Error");
                        Limpiar();  // Limpia los campos en caso de error
                    }
                } else {
                    // Si alguno de los campos está vacío, muestra un mensaje de advertencia
                    JOptionPane.showMessageDialog(null, "Por favor, llene todos los campos.");
                }
            } else {
                // Si la emisión ya existe, muestra un mensaje de advertencia
                JOptionPane.showMessageDialog(null, "La fuente de emisión ya ha sido insertada, por favor revisa la tabla\n" +
                        "y mira que tu emision que desea registrar no esté allí");
                Limpiar();  // Limpia los campos
            }
        }

        // Acción cuando se hace clic en el botón 'inicioButton'
        if (e.getSource() == view.inicioButton) {
            BotonInicio();
        }

        // Acción cuando se hace clic en el botón 'perfil'
        if (e.getSource() == view.perfil) {
            vistaPerfil();
        }

        // Acción cuando se hace clic en el botón 'Calcular'
        if (e.getSource() == view.Calcular) {
            vistaCalcular();
        }

        // Acción cuando se hace clic en el botón 'Informes'
        if (e.getSource() == view.Informes) {
            vistaInforme();
        }

        // Acción cuando se hace clic en el botón 'buscarButton'
        if (e.getSource() == view.buscarButton) {
            Buscar();
        }

        // Acción cuando se hace clic en el botón 'editar'
        if (e.getSource() == view.editar) {
            // Habilita la edición del campo 'Factor' y limpia su contenido
            view.Factor.setFocusable(true);
            view.Factor.setText("");
            desactivarCampos();
        }

        // Acción cuando se hace clic en el botón 'guardarCambiosButton'
        if (e.getSource() == view.guardarCambiosButton) {
            guardarCambios();
        }

        // Acción cuando se hace clic en el botón 'eliminarButton1'
        if (e.getSource() == view.eliminarButton1) {
            eliminar();
        }

        // Acción cuando se hace clic en el botón 'Reducir'
        if (e.getSource() == view.Reducir) {
            vistaReducir();
        }

        // Acción cuando se hace clic en el botón 'RegistrarInstitucion'
        if (e.getSource() == view.RegistrarInstitucion) {
            vistaActualizarInstitucion();
        }

        // Acción cuando se hace clic en el botón 'VerPerfiles'
        if (e.getSource() == view.VerPerfiles) {
            vistaVerPerfiles();
        }

        // Acción cuando se hace clic en el botón 'verInstitucion'
        if (e.getSource() == view.verInstitucion) {
            vistaVerInstitucion();
        }

        // Acción cuando se hace clic en el botón 'limpiarCamposButton'
        if (e.getSource() == view.limpiarCamposButton) {
            Limpiar();  // Limpia los campos
        }
    }

    /**
     * Este método limpia todos los campos del formulario de entrada.
     */
    private void Limpiar() {
        // Restablece los valores predeterminados de los campos de selección y texto
        view.Fuente.setSelectedIndex(0);
        view.Estado.setSelectedIndex(0);
        view.Unidad.setSelectedIndex(0);
        view.Alcance.setSelectedIndex(0);
        view.Factor.setText(null);
        view.nombre.setText(null);
    }


    /**
     * Este método carga las emisiones existentes desde un mapa de datos y las muestra en la tabla de la vista.
     * Limpia la tabla y agrega las filas correspondientes con la información de las emisiones.
     *
     * @param data Un mapa que contiene las emisiones donde la clave es el nombre de la fuente y el valor es el modelo de la emisión (ModeloEmision).
     */
    private void loadEmisionesExistentes(Map<String, ModeloEmision> data) {
        // Obtiene el modelo de la tabla de emisiones
        DefaultTableModel tableModel = (DefaultTableModel) view.Emisiones.getModel();

        // Limpia las filas existentes en la tabla
        tableModel.setRowCount(0);

        // Itera sobre las emisiones y agrega cada una como una nueva fila en la tabla
        for (ModeloEmision dato : data.values()) {
            Object[] rowData = {
                    dato.getTipoFuente(),            // Tipo de la fuente de emisión
                    dato.getEstadoFuente(),          // Estado de la fuente de emisión
                    dato.getNombreFuente().trim(),   // Nombre de la fuente de emisión (con espacios en blanco eliminados)
                    dato.getUnidadMedidad(),         // Unidad de medida asociada a la emisión
                    dato.getFactorEmision(),         // Factor de emisión
                    dato.getAlcance()                // Alcance de la emisión
            };
            // Agrega la fila a la tabla
            tableModel.addRow(rowData);
        }

        // Después de modificar el modelo de la tabla, ajusta el ancho de las columnas para mejorar la presentación
        SwingUtilities.invokeLater(() -> {
            // Configura el ancho de las columnas en la tabla
            view.Emisiones.getColumnModel().getColumn(0).setPreferredWidth(150);
            view.Emisiones.getColumnModel().getColumn(2).setPreferredWidth(150);
            view.Emisiones.getColumnModel().getColumn(3).setPreferredWidth(150);
            view.Emisiones.getColumnModel().getColumn(4).setPreferredWidth(150);
            view.Emisiones.repaint();  // Redibuja la tabla para reflejar los cambios
        });

        // Actualiza la tabla en la vista y la hace visible junto con su contenedor
        view.Emisiones.setModel(tableModel);
        view.Emisiones.setVisible(true);
        view.Contenedor.setVisible(true);
    }
    /**
     * Navega a la vista inicial de la aplicación.
     * Pasos:
     * 1. Crea el controlador de inicio con las dependencias necesarias.
     * 2. Llama al método que inicia la vista de inicio.
     * 3. Cierra la vista actual.
     */
    private void BotonInicio() {
        ControladorPestaniaPrincipal control = new ControladorPestaniaPrincipal(modeloInstitucion, modeloUsuario, modeloMunicipio);
        control.inicio();
        view.dispose();
    }
    /**
     * Inicia la vista del perfil del modeloUsuario.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado con los datos del modeloUsuario.
     * 3. Muestra la vista del perfil y cierra la vista actual.
     */
    private void vistaPerfil() {
        Conexion conn = new Conexion();
        Perfil per = new Perfil();
        ConsultaUsuario cons = new ConsultaUsuario(conn);
        ControladorPerfil control = new ControladorPerfil(modeloUsuario, per, modeloInstitucion, modeloMunicipio, cons);
        control.Iniciar();
        per.setVisible(true);
        view.dispose();
    }
    /**
     * Este método se encarga de inicializar y mostrar la vista de cálculo de la aplicación.
     * Crea las instancias necesarias de las clases para manejar el modelo, las consultas y el controlador
     * que gestionará la lógica del cálculo. Posteriormente, muestra la interfaz de modeloUsuario para realizar el cálculo
     * y cierra la vista actual de la aplicación.
     */
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
    /**
     * Inicia la vista de informes.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado para manejar la lógica de los informes.
     * 3. Muestra la vista de informes y cierra la vista actual.
     */
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
    /**
     * Inicia la vista para mostrar gráficos principales.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado para gestionar los gráficos.
     * 3. Muestra la vista de gráficos y cierra la vista actual.
     */
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
    /**
     * Inicia la vista para comparar instituciones mediante gráficos.
     * Pasos:
     * 1. Configura la vista, modelo y consultas necesarios.
     * 2. Inicializa el controlador para manejar la comparación de instituciones.
     * 3. Cierra la vista actual.
     */
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
    /**
     * Inicia la vista para mostrar tendencias históricas de datos mediante gráficos.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador para gestionar las tendencias históricas.
     * 3. Muestra la vista de gráficos y cierra la vista actual.
     */
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
    /**
     * Inicia la vista para aplicar estrategias de reducción de emisiones.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador para manejar las estrategias de reducción.
     * 3. Cierra la vista actual.
     */
    private void vistaReducir() {
        Conexion con = new Conexion();
        Reducir2 vista = new Reducir2();
        GraficoConsulta consul = new GraficoConsulta(con);
        ControladorReducir redu = new ControladorReducir(modeloInstitucion, consul, vista, modeloMunicipio, modeloUsuario);
        redu.Iniciar();
        view.dispose();
    }
    /**
     * Inicia la vista para visualizar instituciones.
     * Pasos:
     * 1. Crea las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado para manejar la lógica.
     * 3. Opcionalmente, cierra la vista actual (comentado en este caso).
     */
    private void vistaActualizarInstitucion() {
        Conexion con = new Conexion();
        ConsultasInstitucion consul = new ConsultasInstitucion();
        VerDatosInstitucion view2 = new VerDatosInstitucion();
        ModeloInstitucion mod = new ModeloInstitucion();
        ControladorInstitucion control = new ControladorInstitucion(modeloInstitucion, modeloMunicipio, view2, consul, mod, modeloUsuario);
        view2.setVisible(true);
        control.iniciar();
        view.dispose();


    }
    /**
     * Inicia la vista para visualizar perfiles de usuarios.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista y consultas.
     * 2. Inicializa el controlador para manejar la lógica de visualización de perfiles.
     * 3. Cierra la vista actual.
     */
    private void vistaVerPerfiles() {
        Conexion con = new Conexion();
        VerPerfiles verPerfiles = new VerPerfiles();
        ConsultaUsuario consul = new ConsultaUsuario(con);

        ControladorVerPerfiles verControl = new ControladorVerPerfiles(modeloUsuario, modeloInstitucion, modeloMunicipio, verPerfiles, consul);
        verControl.Iniciar();
        view.dispose();
    }
    /**
     * Inicia la vista para visualizar instituciones.
     * Pasos:
     * 1. Crea las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado para manejar la lógica.
     * 3. Opcionalmente, cierra la vista actual (comentado en este caso).
     */
    private void vistaVerInstitucion() {
        Conexion con = new Conexion();
        VerInstituciones verInstituciones = new VerInstituciones();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(con);
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();
        ModeloInstitucion modeloInstitucion = new ModeloInstitucion();
        ControladorVerInstituciones contraladorVerInstituciones = new ControladorVerInstituciones(consultasInstitucion, consultaNucleo,
                modeloInstitucion, verInstituciones, modeloUsuario, modeloMunicipio);
        contraladorVerInstituciones.iniciar();
        view.dispose();
    }

    /**
     * Este método busca una emisión en la base de datos utilizando el nombre de la emisión.
     * Si el campo de nombre no está vacío, realiza la búsqueda y llena los campos del formulario con los datos encontrados.
     * Si el campo está vacío, muestra un mensaje de advertencia.
     */
    private void Buscar() {
        // Obtiene el texto del campo de nombre
        String nombre = view.nombre.getText();

        // Si el nombre no está vacío, realiza la búsqueda
        if (!nombre.isEmpty()) {
            // Realiza la consulta en la base de datos con el nombre ingresado
            List<ModeloEmision> dato = consultasEmision.buscarEmision(nombre);
            System.out.println(nombre);

            // Si encuentra datos, llena los campos con la información de la emisión
            for (ModeloEmision a : dato) {
                view.Alcance.setSelectedItem(a.getAlcance());
                view.Fuente.setSelectedItem(a.getTipoFuente());
                view.Estado.setSelectedItem(a.getEstadoFuente());
                view.Unidad.setSelectedItem(a.getUnidadMedidad());
                view.nombre.setText(a.getNombreFuente());
                view.Factor.setText(a.getFactorEmision().toString());
            }

        } else {
            // Si el campo de nombre está vacío, muestra un mensaje de advertencia
            JOptionPane.showMessageDialog(view, "Para buscar por favor llene el campo del nombre de emisión");
        }
    }

    /**
     * Este método desactiva (pone en estado no editable) los campos del formulario de emisión.
     * Es útil para evitar que el modeloUsuario modifique ciertos campos cuando está editando.
     */
    private void desactivarCampos() {
        view.Alcance.setEnabled(false);
        view.Fuente.setEnabled(false);
        view.Estado.setEnabled(false);
        view.Unidad.setEnabled(false);
        view.nombre.setEnabled(false);
    }

    /**
     * Este método guarda los cambios realizados en el factor de emisión.
     * Si el campo de factor contiene un valor válido, actualiza la base de datos con el nuevo valor.
     * Si el campo está vacío o contiene un valor no válido, muestra un mensaje de advertencia.
     */
    private void guardarCambios() {
        // Obtiene el valor del campo de factor de emisión y lo convierte a Double
        Double facto = Double.parseDouble(view.Factor.getText());

        // Verifica si el valor de facto no es NaN (No es un número inválido)
        if (!facto.isNaN()) {
            // Obtiene el nombre de la emisión y actualiza el factor en la base de datos
            String a = view.nombre.getText();
            if (consultasEmision.ActualizarFactoreEmision(a.trim(), facto)) {
                // Si la actualización es exitosa, muestra un mensaje y limpia los campos
                JOptionPane.showMessageDialog(null, "Registro Actualizado");
                Limpiar();
            } else {
                // Si ocurre un error al actualizar, muestra un mensaje de error
                JOptionPane.showMessageDialog(null, "ERROR");
            }
        } else {
            // Si el campo de factor está vacío o es inválido, muestra un mensaje de advertencia
            JOptionPane.showMessageDialog(null, "Campo Vacio");
        }
    }

    /**
     * Este método elimina una fuente de emisión de la base de datos utilizando el nombre de la emisión.
     * Si el nombre de la fuente está vacío, muestra un mensaje de advertencia.
     * Si la eliminación es exitosa, muestra un mensaje confirmando la eliminación.
     */
    private void eliminar() {
        // Obtiene el texto del campo de nombre de la fuente de emisión
        String nombre = view.nombre.getText();

        // Verifica que el nombre no esté vacío antes de intentar eliminar la fuente
        if (!nombre.isEmpty()) {
            // Intenta eliminar la fuente de emisión utilizando el nombre ingresado
            if (consultasEmision.eliminarFuente(nombre)) {
                // Si la eliminación es exitosa, muestra un mensaje confirmando la acción
                JOptionPane.showMessageDialog(null, "La fuente de emisión llamada " + nombre + "\n fue eliminada");
            } else {
                // Si ocurre un error en la consulta, muestra un mensaje de error
                JOptionPane.showMessageDialog(null, "ERROR EN LA CONSULTA");
            }
        } else {
            // Si el campo de nombre está vacío, muestra un mensaje de advertencia
            JOptionPane.showMessageDialog(null, "El nombre de la fuente está vacío");
        }
    }


    /**
     * Este método inicia un proceso de sondeo (polling) periódico para verificar si hay actualizaciones en los datos.
     * El sondeo se ejecuta cada 5 segundos.
     */
    private void startPolling() {
        // Crea un Timer que ejecuta el método checkForUpdates cada 5000 milisegundos (5 segundos)
        timer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                checkForUpdates();  // Llama al método checkForUpdates para verificar actualizaciones
            }
        });
        timer.start();  // Inicia el Timer
    }

    /**
     * Este método consulta la base de datos para obtener los datos actuales y compara esos datos con los datos anteriores.
     * Si los datos han cambiado, actualiza la vista con la nueva información.
     */
    private void checkForUpdates() {
        // Consulta la base de datos para obtener la lista actual de emisiones
        List<ModeloEmision> datos = consultasEmision.dato();

        // Crea un mapa que contiene los datos nuevos, usando el nombre de la fuente como clave
        Map<String, ModeloEmision> currentData = new HashMap<>();
        for (ModeloEmision emi : datos) {
            currentData.put(emi.getNombreFuente(), emi); // Usa el nombre de la fuente como clave
        }

        // Compara los datos nuevos con los anteriores para ver si ha habido cambios
        if (isDataChanged(currentData)) {
            loadEmisionesExistentes(currentData);  // Si hay cambios, actualiza la vista con los nuevos datos
            previousData = currentData;  // Actualiza los datos anteriores con los nuevos datos
        }
    }

    /**
     * Este método compara los datos actuales con los datos anteriores para determinar si ha habido algún cambio.
     *
     * @param newData Mapa que contiene los datos nuevos a comparar con los anteriores.
     * @return true si los datos han cambiado, de lo contrario, false.
     */
    private boolean isDataChanged(Map<String, ModeloEmision> newData) {
        // Si previousData es null, consideramos que es el primer sondeo, por lo que los datos han cambiado
        if (previousData == null) {
            return true;
        }

        // Si el tamaño de los datos ha cambiado, significa que ha habido un cambio
        if (previousData.size() != newData.size()) {
            return true;
        }

        // Compara cada registro en newData con los registros correspondientes en previousData
        for (Map.Entry<String, ModeloEmision> entry : newData.entrySet()) {
            ModeloEmision newEmi = entry.getValue();
            ModeloEmision oldEmi = previousData.get(entry.getKey());

            // Si los registros no coinciden, significa que ha habido un cambio
            if (oldEmi == null || !oldEmi.equals(newEmi)) {
                return true;
            }
        }

        // Si no hay cambios, retorna false
        return false;
    }
    /**
     * Método que inicializa los listeners para los botones y componentes de la vista.
     * Asocia eventos de acción a los botones y otros componentes interactivos.
     */

    public void Listeners() {
        this.view.guardarButton.addActionListener(this);
        this.view.inicioButton.addActionListener(this);
        this.view.buscarButton.addActionListener(this);
        this.view.editar.addActionListener(this);
        this.view.eliminarButton1.addActionListener(this);
        this.view.perfil.addActionListener(this);
        this.view.Calcular.addActionListener(this);
        this.view.Informes.addActionListener(this);
        this.view.guardarCambiosButton.addActionListener(this);
        this.view.Reducir.addActionListener(this);
        this.view.RegistrarInstitucion.addActionListener(this);
        this.view.VerPerfiles.addActionListener(this);
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
        this.view.VerPerfiles.addActionListener(this);
        this.view.verInstitucion.addActionListener(this);
        this.view.limpiarCamposButton.addActionListener(this);
    }


}
