package Controlador;

import Modelo.Conexion;
import Modelo.Consultas.*;
import Modelo.modelo.*;
import Vistas.*;
import javax.swing.*;
import java.awt.event.ActionEvent;

public class ControladorPestaniaPrincipal {
    public ModeloUsuario user;  // ModeloUsuario actual que está iniciando sesión.
    public ModeloInstitucion ins;  // Modelo de la institución asociada al modeloUsuario.
    public ModeloMunicipio m;  // ModeloMunicipio asociado al modeloUsuario.
    Inicio init = new Inicio();  // Vista de inicio (pantalla principal).
    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");  // Opción de menú para comparar instituciones.
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");  // Opción de menú para ver gráficos principales.
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");  // Opción de menú para ver gráficos históricos.

    /**
     * Constructor del controlador de la vista de inicio.
     * @param ins Modelo de la institución asociada al modeloUsuario.
     * @param user ModeloUsuario actual.
     * @param m ModeloMunicipio asociado al modeloUsuario.
     */
    public ControladorPestaniaPrincipal(ModeloInstitucion ins, ModeloUsuario user, ModeloMunicipio m) {
        this.ins = ins;
        this.user = user;
        this.m = m;
        Listeners();  // Inicializa los listeners para la interacción con la interfaz.
        System.out.println(user.getTipoUsuario() + "hola");
    }

    /**
     * Inicializa la vista según el tipo de modeloUsuario y agrega las opciones de gráficos al menú.
     */
    public void inicio() {
        init.Graficos.add(GraficoPrincipal);  // Agrega la opción de gráfico principal al menú.
        init.Graficos.add(GraficosCompararInstitucion);  // Agrega la opción para comparar instituciones al menú.
        init.Graficos.add(GraficoHistorico);  // Agrega la opción de gráfico histórico al menú.

        String usuario = user.getTipoUsuario();  // Obtiene el tipo de modeloUsuario.
        System.out.println(usuario);

        // Configura la interfaz según el tipo de modeloUsuario (Invitado, Administrador, Superadmin).
        switch (usuario) {
            case "Invitado":
                System.out.println("Estoy en docente");
                init.setVisible(true);
                init.RegistrarInstitucion.setVisible(false);
                init.VerPerfiles.setVisible(false);
                init.RegistrarEmision.setVisible(false);
                init.Calcular.setVisible(false);
                init.verInstitucion.setVisible(false);
                init.perfil.setVisible(true);
                init.setLocationRelativeTo(null);
                break;
            case "Administrador":
                Inicio ini = new Inicio();
                ini.setVisible(true);
                init.VerPerfiles.setVisible(true);
                init.verInstitucion.setVisible(false);
                ini.setSize(3800, 600);
                ini.dispose();
                break;
            case "Superadmin":
                ini = new Inicio();
                ini.setVisible(true);
                ini.setSize(3800, 600);
                ini.dispose();
                break;
            default:
                JOptionPane.showMessageDialog(null, "ModeloUsuario no definido en el sistema");
                break;
        }

        // Asigna las acciones correspondientes a cada opción de menú.
        GraficoPrincipal.addActionListener(e -> vistaGraficoPrincipal());
        GraficosCompararInstitucion.addActionListener(e -> vistaCompararInstituciones());
        GraficoHistorico.addActionListener(e -> vistaGraficoHistorico());
    }

    /**
     * Maneja las acciones de los botones de la vista de inicio.
     * @param e Evento de acción.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == init.perfil) {
            vistaPerfil();
        } else if (e.getSource() == init.Calcular) {
            vistaCalcular();
        } else if (e.getSource() == init.RegistrarEmision) {
            vistaRegistrarEmision();
        } else if (e.getSource() == init.Informes) {
            vistaInforme();
        } else if (e.getSource() == init.RegistrarInstitucion) {
            vistaActualizarInstitucion();
        } else if (e.getSource() == init.VerPerfiles) {
            vistaVerPerfiles();
        } else if (e.getSource() == init.Reducir) {
            vistaReducir();
        } else if (e.getSource() == init.verInstitucion) {
            vistaVerInstitucion();
        } else if (e.getSource() == init.CerraSesion) {
            cerrarSession();
        }
    }

    // Métodos para manejar las vistas asociadas a los diferentes botones de la interfaz

    /**
     * Abre la vista de perfil del modeloUsuario.
     */
    private void vistaPerfil() {
        Conexion conn = new Conexion();
        Perfil per = new Perfil();
        ConsultaUsuario cons = new ConsultaUsuario(conn);
        ControladorPerfil control = new ControladorPerfil(user, per, ins, m, cons);
        control.Iniciar();
        per.setVisible(true);
        init.dispose();
    }

    /**
     * Abre la vista para realizar cálculos relacionados con las emisiones.
     */
    private void vistaCalcular() {
        Conexion con = new Conexion();
        Vistas.Calcular view = new Calcular();
        ModeloEmisionCalcular mod = new ModeloEmisionCalcular();
        CalcularConsultas consul = new CalcularConsultas(con);
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        ControladorCalcular controlador = new ControladorCalcular(mod, consul, view, ins, consultaUsuario, user, m);
        controlador.iniciar();
        view.setVisible(true);
        init.dispose();
    }

    /**
     * Abre la vista para registrar emisiones.
     */
    private void vistaRegistrarEmision() {
        Emision emisionView = new Emision();
        ModeloEmision mod = new ModeloEmision();
        ConsultasEmision consul = new ConsultasEmision();
        ControladorEmision controlador = new ControladorEmision(mod, consul, emisionView, ins, m, user);
        controlador.iniciar();
        emisionView.setVisible(true);
        init.dispose();
    }

    /**
     * Abre la vista de informes.
     */
    private void vistaInforme() {
        Conexion con = new Conexion();
        ConsultaInforme consul = new ConsultaInforme(con);
        ModeloEmisionInforme mod = new ModeloEmisionInforme();
        Informe view = new Informe();
        ControladorInforme contro = new ControladorInforme(view, mod, consul, m, ins, user);
        contro.iniciar();
        view.setVisible(true);
        init.dispose();
    }

    /**
     * Abre la vista de gráficos principales relacionados con la huella de carbono.
     */
    private void vistaGraficoPrincipal() {
        Conexion con = new Conexion();
        GraficoConsulta consul = new GraficoConsulta(con);
        Vistas.Graficos view = new Graficos();
        GraficorModeloInstitucion mod = new GraficorModeloInstitucion();
        ModeloInstitucion modelo = new ModeloInstitucion();
        ControladorGrafico contro = new ControladorGrafico(mod, consul, view, modelo, m, user, ins);
        contro.iniciar();
        view.Graficos.setVisible(true);
        init.dispose();
    }

    /**
     * Abre la vista para comparar instituciones.
     */
    private void vistaCompararInstituciones() {
        CompararOtrarInstituciones comIns = new CompararOtrarInstituciones();
        GraficoComparar view = new GraficoComparar();
        Conexion conn = new Conexion();
        GraficoCompararConsultas consultas = new GraficoCompararConsultas(conn);
        ModeloGraficoComparar mod = new ModeloGraficoComparar();
        ControladorCompararInstitucion contro = new ControladorCompararInstitucion(mod, consultas, comIns, view, ins, m, user);
        contro.iniciar();
        init.dispose();
    }

    /**
     * Abre la vista de gráficos históricos relacionados con la huella de carbono.
     */
    private void vistaGraficoHistorico() {
        Conexion con = new Conexion();
        ModeloTendencia mod = new ModeloTendencia();
        ConsultasTendencias consult = new ConsultasTendencias(con);
        GraficoTendencia view = new GraficoTendencia();
        ControladorTendencia control = new ControladorTendencia(mod, consult, view, m, ins, user);
        view.setVisible(true);
        control.iniciar();
        init.dispose();
    }

    /**
     * Abre la vista para actualizar los datos de la institución.
     */
    private void vistaActualizarInstitucion() {
        Conexion con = new Conexion();
        ConsultasInstitucion consul = new ConsultasInstitucion();
        VerDatosInstitucion view = new VerDatosInstitucion();
        ModeloInstitucion mod = new ModeloInstitucion();
        ControladorInstitucion control = new ControladorInstitucion(ins, m, view, consul, mod, user);
        view.setVisible(true);
        control.iniciar();
        init.dispose();
    }

    /**
     * Abre la vista para ver los perfiles de modeloUsuario.
     */
    private void vistaVerPerfiles() {
        Conexion con = new Conexion();
        VerPerfiles verPerfiles = new VerPerfiles();
        ConsultaUsuario consul = new ConsultaUsuario(con);

        ControladorVerPerfiles verControl = new ControladorVerPerfiles(user, ins, m, verPerfiles, consul);
        verControl.Iniciar();
        init.dispose();
    }

    /**
     * Abre la vista para reducir las emisiones.
     */
    private void vistaReducir() {
        Conexion con = new Conexion();
        Reducir2 vista = new Reducir2();
        GraficoConsulta consul = new GraficoConsulta(con);
        ControladorReducir redu = new ControladorReducir(ins, consul, vista, m, user);
        redu.Iniciar();
        init.dispose();
    }

    /**
     * Abre la vista para ver las instituciones asociadas al modeloUsuario.
     */
    private void vistaVerInstitucion() {
        Conexion con = new Conexion();
        VerInstituciones verInstituciones = new VerInstituciones();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(con);
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();
        ModeloInstitucion modeloInstitucion = new ModeloInstitucion();
        ControladorVerInstituciones contraladorVerInstituciones = new ControladorVerInstituciones(
                consultasInstitucion, consultaNucleo, modeloInstitucion, verInstituciones, user, m
        );
        contraladorVerInstituciones.iniciar();
    }

    /**
     * Inicializa los listeners para los botones de la vista de inicio.
     */
    private void Listeners() {
        this.init.perfil.addActionListener(this::actionPerformed);
        this.init.Calcular.addActionListener(this::actionPerformed);
        this.init.RegistrarEmision.addActionListener(this::actionPerformed);
        this.init.Informes.addActionListener(this::actionPerformed);
        this.init.RegistrarInstitucion.addActionListener(this::actionPerformed);
        this.init.VerPerfiles.addActionListener(this::actionPerformed);
        this.init.Reducir.addActionListener(this::actionPerformed);
        this.init.verInstitucion.addActionListener(this::actionPerformed);
        this.init.CerraSesion.addActionListener(this::actionPerformed);
    }

    /**
     * Cierra la sesión del modeloUsuario y regresa a la pantalla principal.
     */
    private void cerrarSession() {
        Main view = new Main();
        ModeloUsuario user1 = new ModeloUsuario();
        ControladorMain controladorMain = new ControladorMain(view, user1);
        controladorMain.Iniciar();
        init.dispose();
        JOptionPane.showMessageDialog(init, "Sesion cerrada");
    }
}
