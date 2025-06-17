package Controlador;


import Modelo.Conexion;
import Modelo.Consultas.*;
import Modelo.modelo.*;
import Vistas.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class ControladorInstitucion implements ActionListener {
    private final ModeloUsuario modeloUsuario;
    private final ModeloInstitucion modeloInstitucion;
    private final ModeloMunicipio modeloMunicipio;
    private final VerDatosInstitucion view;
    private final ConsultasInstitucion consultasInstitucion;
    private final ModeloInstitucion mod;
    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");

    public ControladorInstitucion(ModeloInstitucion modeloInstitucion, ModeloMunicipio modeloMunicipio,
                                  VerDatosInstitucion view, ConsultasInstitucion consultasInstitucion,
                                  ModeloInstitucion mod, ModeloUsuario modeloUsuario) {
        this.view = view;
        this.modeloMunicipio = modeloMunicipio;
        this.modeloInstitucion = modeloInstitucion;
        this.consultasInstitucion = consultasInstitucion;
        this.mod = mod;
        this.modeloUsuario = modeloUsuario;
        Listeners();
    }
    /**
     * Método para inicializar la vista dependiendo del tipo de modeloUsuario.
     * Se ajustan los elementos visibles y las acciones de acuerdo al tipo de modeloUsuario (Administrador, Superadmin, Invitado).
     */
    public void iniciar() {
        switch (modeloUsuario.getTipoUsuario()) {
            case "Administrador":
                System.out.println("Estoy en docente");
                view.setTitle("Registro de Institucion");
                view.setLocationRelativeTo(null);
                view.setVisible(true);
                view.VerPerfiles.setVisible(true);
                view.verInstitucion.setVisible(false);
                view.setLocationRelativeTo(null);
                view.Graficos.add(GraficoPrincipal);
                view.Graficos.add(GraficosCompararInstitucion);
                view.Graficos.add(GraficoHistorico);
                CargarDatos();
                CamposInhabilitados();
                view.setVisible(true);
                view.setLocationRelativeTo(null);
                GraficoPrincipal.addActionListener(_ -> vistaGraficoPrincipal());
                GraficosCompararInstitucion.addActionListener(_ -> vistaCompararInstituciones());
                GraficoHistorico.addActionListener(_ -> vistaGraficoHistorico());
                break;
            case "Superadmin":
                view.setTitle("Registro de Institucion");
                view.setLocationRelativeTo(null);
                view.Graficos.add(GraficoPrincipal);
                view.Graficos.add(GraficosCompararInstitucion);
                view.Graficos.add(GraficoHistorico);
                CargarDatos();
                CamposInhabilitados();
                view.setVisible(true);
                view.setLocationRelativeTo(null);
                GraficoPrincipal.addActionListener(_ -> vistaGraficoPrincipal());
                GraficosCompararInstitucion.addActionListener(_ -> vistaCompararInstituciones());
                GraficoHistorico.addActionListener(_ -> vistaGraficoHistorico());
                break;
            default:
                JOptionPane.showMessageDialog(null, "ModeloUsuario no definido en el sistema");
                break;

        }
    }

    /**
     * Maneja los eventos de acción generados por los botones y componentes de la vista.
     * Este método se ejecuta cuando un modeloUsuario interactúa con los botones y componentes de la interfaz gráfica.
     * En función del botón que se haya presionado, se ejecuta el método correspondiente para gestionar la acción.
     *
     * @param e El evento de acción que contiene información sobre el componente que generó el evento.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.editarButton) {
            view.hectareas.setEditable(true);
            view.nit.setEditable(true);
        }
        String hectareas = view.hectareas.getText();
        if (e.getSource() == view.guardarCambios) {
            if (!hectareas.isEmpty()) {
                Integer h = Integer.parseInt(view.hectareas.getText());
                String nit = view.nit.getText();
                if (consultasInstitucion.ActualizarInstitucion(view.nombre.getText(), view.municipio.getText(), h, nit)) {
                    JOptionPane.showMessageDialog(null, "institucion actualizada correctamente");
                    view.hectareas.setEditable(false);
                } else {
                    JOptionPane.showMessageDialog(null, "Error en la consulta ");
                }
            } else {
                JOptionPane.showMessageDialog(null, " Por favor ingresar la cantidad de hectareas\n " +
                        "por metro cuadrado que tiene la universidad");
            }

        }
        if (e.getSource() == view.inicioButton) {
            BotonInicio();
        }
        if (e.getSource() == view.Reducir) {
            vistaReducir();
        }
        if (e.getSource() == view.perfil) {
            vistaPerfil();
        }
        if (e.getSource() == view.Calcular) {
            vistaCalcular();
        }
        if (e.getSource() == view.RegistrarEmision) {
            vistaRegistrarEmision();
        }
        if (e.getSource() == view.Informes) {
            vistaInforme();
        }
        if (e.getSource() == view.VerPerfiles) {
            vistaVerPerfiles();
        }
        if(e.getSource() == view.verInstitucion){
            vistaVerInstitucion();
        }

    }

    /**
     * Carga y muestra los datos de la institución y el modeloMunicipio en los campos correspondientes de la vista.
     *
     * Este método obtiene el nombre de la institución y el modeloMunicipio desde el modelo actual y luego consulta
     * los datos asociados a estos parámetros desde una base de datos o fuente de datos utilizando el objeto
     * `consultasInstitucion`. Los datos recuperados se muestran en los campos de la vista correspondientes a
     * la institución, como el nombre, el NIT, el departamento y el modeloMunicipio.
     *
     * El proceso incluye:
     * 1. Obtener el nombre de la institución y el modeloMunicipio desde el modelo.
     * 2. Consultar los datos utilizando la función `CargarDatos` de `consultasInstitucion`.
     * 3. Mostrar los datos obtenidos en los campos de la vista correspondientes.
     */
    public void CargarDatos() {
        // Obtener el nombre de la institución y el modeloMunicipio desde los modelos
        String NombreInstitucion = modeloInstitucion.getNombreInstitucion();
        String Municipio = modeloMunicipio.getNombreM();

        // Mostrar el modeloMunicipio en la consola para depuración
        System.out.println(Municipio);

        // Consultar los datos de la institución y el modeloMunicipio
        List<ModeloInstitucion> modelos = consultasInstitucion.CargarDatos(NombreInstitucion, Municipio);

        // Iterar sobre los resultados y cargar los datos en la vista
        for (ModeloInstitucion i : modelos) {
            // Establecer los valores recuperados en los campos de la vista
            view.nombre.setText(i.getNombreInstitucion());
            view.nit.setText(i.getNit());
            view.departamento.setText(i.getDepartamento());
            view.municipio.setText(i.getMunicipio());
        }
    }


    /**
     * Deshabilita la edición de los campos de texto en la vista.
     *
     * Este método se utiliza para hacer que los campos de texto relacionados con la institución (nombre, NIT, modeloMunicipio,
     * departamento, hectáreas) sean de solo lectura, es decir, que no puedan ser modificados por el modeloUsuario. Esto es útil
     * cuando se desea mostrar información de forma estática, sin permitir su edición.
     *
     * Los campos afectados son:
     * - `view.nombre`: Campo para el nombre de la institución.
     * - `view.nit`: Campo para el NIT de la institución.
     * - `view.modeloMunicipio`: Campo para el modeloMunicipio de la institución.
     * - `view.departamento`: Campo para el departamento de la institución.
     * - `view.hectareas`: Campo para el número de hectáreas.
     */
    public void CamposInhabilitados() {
        // Deshabilitar la edición de los campos de texto
        view.nombre.setEditable(false);
        view.nit.setEditable(false);
        view.municipio.setEditable(false);
        view.departamento.setEditable(false);
        view.hectareas.setEditable(false);
    }

    /**
     * Navega a la vista inicial de la aplicación.
     * Pasos:
     * 1. Crea el controlador de inicio con las dependencias necesarias.
     * 2. Llama al método que inicia la vista de inicio.
     * 3. Cierra la vista actual.
     */
    public void BotonInicio() {
        ControladorPestaniaPrincipal control = new ControladorPestaniaPrincipal(modeloInstitucion, modeloUsuario, modeloMunicipio);
        control.inicio();
        view.dispose();
    }
    /**
     * Inicia la vista para aplicar estrategias de reducción de emisiones.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador para manejar las estrategias de reducción.
     * 3. Cierra la vista actual.
     */
    public void vistaReducir() {
        Conexion con = new Conexion();
        Reducir2 vista = new Reducir2();
        GraficoConsulta consul = new GraficoConsulta(con);
        ControladorReducir redu = new ControladorReducir(modeloInstitucion, consul, vista, modeloMunicipio, modeloUsuario);
        redu.Iniciar();
        view.dispose();
    }
    /**
     * Inicia la vista para mostrar gráficos principales.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado para gestionar los gráficos.
     * 3. Muestra la vista de gráficos y cierra la vista actual.
     */
    public void vistaGraficoPrincipal() {
        Conexion con = new Conexion();
        GraficoConsulta consul = new GraficoConsulta(con);
        Vistas.Graficos view2 = new Graficos();
        GraficorModeloInstitucion mod = new GraficorModeloInstitucion();
        ModeloInstitucion modelo = new ModeloInstitucion();
        ControladorGrafico contro = new ControladorGrafico(mod, consul, view2, modelo, modeloMunicipio, modeloUsuario, modeloInstitucion);
        contro.iniciar();
        view2.Graficos.setVisible(true);
        view.dispose();
    }
    /**
     * Inicia la vista para comparar instituciones mediante gráficos.
     * Pasos:
     * 1. Configura la vista, modelo y consultas necesarios.
     * 2. Inicializa el controlador para manejar la comparación de instituciones.
     * 3. Cierra la vista actual.
     */
    public void vistaCompararInstituciones() {
        CompararOtrarInstituciones comIns = new CompararOtrarInstituciones();
        GraficoComparar view2 = new GraficoComparar();
        Conexion conn = new Conexion();
        GraficoCompararConsultas consultas = new GraficoCompararConsultas(conn);
        ModeloGraficoComparar mod = new ModeloGraficoComparar();
        ControladorCompararInstitucion contro = new ControladorCompararInstitucion(mod, consultas, comIns, view2, modeloInstitucion, modeloMunicipio, modeloUsuario);
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
    public void vistaGraficoHistorico() {
        Conexion con = new Conexion();
        ModeloTendencia mod = new ModeloTendencia();
        ConsultasTendencias consult = new ConsultasTendencias(con);
        GraficoTendencia view2 = new GraficoTendencia();
        ControladorTendencia control = new ControladorTendencia(mod, consult, view2, modeloMunicipio, modeloInstitucion, modeloUsuario);
        view2.setVisible(true);
        control.iniciar();
        view.dispose();
    }
    /**
     * Inicia la vista del perfil del modeloUsuario.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado con los datos del modeloUsuario.
     * 3. Muestra la vista del perfil y cierra la vista actual.
     */
    public void vistaPerfil() {
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
    public void vistaCalcular() {
        Conexion con = new Conexion();
        Vistas.Calcular view2 = new Calcular();
        ModeloEmisionCalcular mod = new ModeloEmisionCalcular();
        CalcularConsultas consul = new CalcularConsultas(con);
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        ControladorCalcular controlador = new ControladorCalcular(mod, consul, view2, modeloInstitucion, consultaUsuario, modeloUsuario, modeloMunicipio);
        controlador.iniciar();
        view.setVisible(true);
        view.dispose();
    }
    /**
     * Inicia la vista de informes.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado para manejar la lógica de los informes.
     * 3. Muestra la vista de informes y cierra la vista actual.
     */
    public void vistaRegistrarEmision() {
        Emision emisionView = new Emision();
        ModeloEmision mod = new ModeloEmision();
        ConsultasEmision consul = new ConsultasEmision();
        ControladorEmision controlador = new ControladorEmision(mod, consul, emisionView, modeloInstitucion, modeloMunicipio, modeloUsuario);
        controlador.iniciar();
        emisionView.setVisible(true);
        view.dispose();
    }
    /**
     * Inicia la vista de informes.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado para manejar la lógica de los informes.
     * 3. Muestra la vista de informes y cierra la vista actual.
     */
    public void vistaInforme() {
        Conexion con = new Conexion();
        ModeloInstitucion mod2 = new ModeloInstitucion();
        ConsultaInforme consul = new ConsultaInforme(con);
        ModeloEmisionInforme mod = new ModeloEmisionInforme();
        Informe view2 = new Informe();
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        ControladorInforme contro = new ControladorInforme(view2, mod, consul, modeloMunicipio, modeloInstitucion, modeloUsuario);
        contro.iniciar();
        view2.setVisible(true);
        view.dispose();
    }
    /**
     * Inicia la vista para visualizar perfiles de usuarios.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista y consultas.
     * 2. Inicializa el controlador para manejar la lógica de visualización de perfiles.
     * 3. Cierra la vista actual.
     */
    public void vistaVerPerfiles() {
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
     * Método que inicializa los listeners para los botones y componentes de la vista.
     * Asocia eventos de acción a los botones y otros componentes interactivos.
     */
    public void Listeners() {
        this.view.inicioButton.addActionListener(this);
        this.view.editarButton.addActionListener(this);
        this.view.guardarCambios.addActionListener(this);
        this.view.Reducir.addActionListener(this);
        this.view.perfil.addActionListener(this);
        this.view.Calcular.addActionListener(this);
        this.view.RegistrarEmision.addActionListener(this);
        this.view.Informes.addActionListener(this);
        this.view.VerPerfiles.addActionListener(this);
        this.view.verInstitucion.addActionListener(this);
    }
}
