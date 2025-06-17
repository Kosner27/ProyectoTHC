package Controlador;

import Modelo.Conexion;
import Modelo.Consultas.*;
import Modelo.modelo.*;
import Vistas.*;

import javax.swing.*;
import java.awt.event.ActionEvent;

/**
 * ControladorPerfil es la clase encargada de gestionar las acciones y lógica asociadas a la vista de perfil de un modeloUsuario.
 * Dependiendo del tipo de modeloUsuario (Administrador, Superadmin, Invitado), configura las opciones y funcionalidades
 * que estarán disponibles para el modeloUsuario en la interfaz gráfica, como la posibilidad de editar su perfil, ver gráficos,
 * comparar instituciones, y gestionar emisiones, entre otros.
 * <p>
 * Además, gestiona la interacción entre la vista, el modelo y la base de datos, realizando actualizaciones y acciones de acuerdo
 * a las entradas del modeloUsuario.
 */
public class ControladorPerfil {

    // Atributos
    private final ModeloUsuario mod;  // Modelo del modeloUsuario
    private final Perfil view;  // Vista de perfil del modeloUsuario
    private final ModeloInstitucion ins;  // Modelo de la institución asociada al modeloUsuario
    private final ModeloMunicipio m;  // Modelo del modeloMunicipio
    private final ConsultaUsuario cons;  // Consultas relacionadas con el modeloUsuario
    private final JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");  // Menú para comparar instituciones
    private final JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");  // Menú para ver gráficos por alcance
    private final JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");  // Menú para ver gráfico histórico

    /**
     * Constructor de la clase ControladorPerfil.
     *
     * @param mod  Modelo de modeloUsuario
     * @param view Vista del perfil
     * @param ins  Modelo de la institución
     * @param m    Modelo del modeloMunicipio
     * @param cons Consultas relacionadas con el modeloUsuario
     */
    public ControladorPerfil(ModeloUsuario mod, Perfil view,
                             ModeloInstitucion ins, ModeloMunicipio m, ConsultaUsuario cons) {
        this.mod = mod;
        this.view = view;
        this.ins = ins;
        this.m = m;
        this.cons = cons;
        listeners();  // Registra los listeners de los eventos
    }

    /**
     * Inicializa la vista de perfil dependiendo del tipo de modeloUsuario.
     * Configura las opciones disponibles para cada tipo de modeloUsuario y agrega los listeners correspondientes.
     */
    public void Iniciar() {

        // Dependiendo del tipo de modeloUsuario, se configurarán las opciones y funcionalidades disponibles en la vista.
        switch (mod.getTipoUsuario()) {
            case "Administrador":
                // Configuración para el administrador
                view.setTitle("Perfil");
                Load();
                view.setLocationRelativeTo(null);
                view.setVisible(true);
                view.VerPerfiles.setVisible(true);
                view.verInstitucion.setVisible(false);
                view.Graficos.add(GraficoPrincipal);
                view.Graficos.add(GraficosCompararInstitucion);
                view.Graficos.add(GraficoHistorico);
                view.setVisible(true);
                view.setLocationRelativeTo(null);
                GraficoPrincipal.addActionListener(_ -> vistaGraficoPrincipal());
                GraficosCompararInstitucion.addActionListener(_ -> vistaCompararInstituciones());
                GraficoHistorico.addActionListener(_ -> vistaGraficoHistorico());
                break;

            case "Superadmin":
                // Configuración para el superadmin
                view.setTitle("Perfil");
                Load();
                view.setLocationRelativeTo(null);
                view.Graficos.add(GraficoPrincipal);
                view.Graficos.add(GraficosCompararInstitucion);
                view.Graficos.add(GraficoHistorico);
                view.setVisible(true);
                view.setLocationRelativeTo(null);
                GraficoPrincipal.addActionListener(_ -> vistaGraficoPrincipal());
                GraficosCompararInstitucion.addActionListener(_ -> vistaCompararInstituciones());
                GraficoHistorico.addActionListener(_ -> vistaGraficoHistorico());
                break;

            case "Invitado":
                // Configuración para el invitado
                view.setVisible(true);
                view.setTitle("Perfil");
                System.out.println(ins.getNombreInstitucion() + " Vista Perfil");
                view.RegistrarInstitucion.setVisible(false);
                view.VerPerfiles.setVisible(false);
                view.RegistrarEmision.setVisible(false);
                view.Calcular.setVisible(false);
                view.verInstitucion.setVisible(false);
                view.perfil.setVisible(true);
                view.setLocationRelativeTo(null);
                Load();
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
     * Método que maneja las acciones de los botones y otros componentes en la vista de perfil.
     *
     * @param event Evento generado por los componentes de la vista.
     */
    private void actionPerformed(ActionEvent event) {
        // Lógica para habilitar/deshabilitar campos según la acción del modeloUsuario
        if (event.getSource() == view.editarButton) {
            hablitarCampos();
        } else {
            desahabiltarCampos();
        }

        // Actualización de los datos de modeloUsuario si se modifican
        String pass = new String(view.newPass.getPassword());
        String Npass = new String(view.confirNewPass.getPassword());
        String correo = view.correo.getText();
        int id = mod.getIdUsuario();
        if (!pass.isEmpty() && !Npass.isEmpty()) {
            if (pass.equals(Npass)) {
                if (cons.esEmail(correo)) {
                    String pasCifrada = HASH.sha1(Npass);
                    mod.setCorreo(correo);
                    mod.setContrasena(pasCifrada);
                    mod.setIdUsuario(id);
                    if (event.getSource() == view.guardarCambiosButton) {
                        cons.updatetUsuario(mod);
                        JOptionPane.showMessageDialog(null, "Datos Actualizados Exitosamente");
                        view.correo.setText(mod.getCorreo());
                        view.newPass.setText("");
                        view.confirNewPass.setText("");
                        desahabiltarCampos();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error en la actualizacion");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "El correo no está en un formato valido");
                }
            } else {
                JOptionPane.showMessageDialog(null, "Para actualizar la contraseña deben coincidir en ambos campos");
            }
        }

        // Llamadas a otros métodos según la acción realizada por el modeloUsuario
        if (event.getSource() == view.inicioButton) {
            BotonInicio();
        }
        if (event.getSource() == view.RegistrarEmision) {
            vistaRegistrarEmision();
        }
        if (event.getSource() == view.Calcular) {
            vistaCalcular();
        }
        if (event.getSource() == view.Informes) {
            vistaInforme();
        }
        if (event.getSource() == view.Reducir) {
            vistaReducir();
        }
        if (event.getSource() == view.RegistrarInstitucion) {
            vistaActualizarInstitucion();
        }
        if (event.getSource() == view.VerPerfiles) {
            vistaVerPerfiles();
        }
        if (event.getSource() == view.verInstitucion) {
            vistaVerInstitucion();
        }
    }

    // Otros métodos relacionados con la configuración y actualización de la vista de perfil.
    private void Load() {
        llenarFormulario();
        desahabiltarCampos();
    }

    private void llenarFormulario() {
        view.apellido.setText(mod.getApellido());
        view.nombre.setText(mod.getNombre());
        view.correo.setText(mod.getCorreo());
        view.Sede.setText(m.getNombreM());
        view.Uni.setText(ins.getNombreInstitucion());
        view.rol.setText(mod.getTipoUsuario());
    }

    private void hablitarCampos() {
        // Habilita la edición de los campos de correo y contraseña
        view.correo.setEditable(true);
        view.newPass.setEditable(true);
        view.confirNewPass.setEditable(true);
    }

    private void desahabiltarCampos() {
        // Deshabilita la edición de todos los campos
        view.Sede.setEditable(false);
        view.apellido.setEditable(false);
        view.nombre.setEditable(false);
        view.correo.setEditable(false);
        view.Uni.setEditable(false);
        view.rol.setEditable(false);
        view.newPass.setEditable(false);
        view.confirNewPass.setEditable(false);
    }

    private void BotonInicio() {
        ControladorPestaniaPrincipal control = new ControladorPestaniaPrincipal(ins, mod, m);
        control.inicio();
        view.dispose();
    }

    private void vistaRegistrarEmision() {
        Emision emisionView = new Emision();
        ModeloEmision ModeloEmision = new ModeloEmision();
        ConsultasEmision consul = new ConsultasEmision();
        ControladorEmision controlador = new ControladorEmision(ModeloEmision, consul, emisionView, ins, m, mod);
        controlador.iniciar();
        emisionView.setVisible(true);
        view.dispose();
    }

    private void vistaCalcular() {
        Conexion con = new Conexion();
        Calcular view2 = new Calcular();
        ModeloEmisionCalcular mod2 = new ModeloEmisionCalcular();
        CalcularConsultas consul = new CalcularConsultas(con);
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        ControladorCalcular controlador = new ControladorCalcular(mod2, consul, view2, ins, consultaUsuario, mod, m);
        controlador.iniciar();
        view2.setVisible(true);
        view.dispose();
    }

    private void vistaInforme() {
        Conexion con = new Conexion();
        ConsultaInforme consul = new ConsultaInforme(con);
        ModeloEmisionInforme modInfo = new ModeloEmisionInforme();
        Informe viewInfo = new Informe();
        ControladorInforme contro = new ControladorInforme(viewInfo, modInfo, consul, m, ins, mod);
        contro.iniciar();
        viewInfo.setVisible(true);
        view.dispose();
    }

    private void vistaGraficoPrincipal() {
        Conexion con = new Conexion();
        GraficoConsulta consul = new GraficoConsulta(con);
        Vistas.Graficos viewGraf = new Graficos();
        GraficorModeloInstitucion modGraf = new GraficorModeloInstitucion();
        ModeloInstitucion modelo = new ModeloInstitucion();
        ControladorGrafico contro = new ControladorGrafico(modGraf, consul, viewGraf, modelo, m, mod, ins);
        contro.iniciar();
        viewGraf.setVisible(true);
        view.dispose();
    }

    private void vistaCompararInstituciones() {
        CompararOtrarInstituciones comIns = new CompararOtrarInstituciones();
        GraficoComparar viewGraf = new GraficoComparar();
        Conexion conn = new Conexion();
        GraficoCompararConsultas consultas = new GraficoCompararConsultas(conn);
        ModeloGraficoComparar modGraf = new ModeloGraficoComparar();
        ControladorCompararInstitucion contro = new ControladorCompararInstitucion(modGraf, consultas, comIns, viewGraf, ins, m, mod);
        contro.iniciar();
        view.dispose();
    }

    private void vistaGraficoHistorico() {
        Conexion con = new Conexion();
        ModeloTendencia modGraf = new ModeloTendencia();
        ConsultasTendencias consult = new ConsultasTendencias(con);
        GraficoTendencia viewGraf = new GraficoTendencia();
        ControladorTendencia control = new ControladorTendencia(modGraf, consult, viewGraf, m, ins, mod);
        viewGraf.setVisible(true);
        control.iniciar();
        view.dispose();
    }

    private void vistaReducir() {
        Conexion con = new Conexion();
        Reducir2 vista = new Reducir2();
        GraficoConsulta consul = new GraficoConsulta(con);
        ControladorReducir redu = new ControladorReducir(ins, consul, vista, m, mod);
        redu.Iniciar();
        view.dispose();
    }

    private void vistaActualizarInstitucion() {
        ConsultasInstitucion consul = new ConsultasInstitucion();
        VerDatosInstitucion view2 = new VerDatosInstitucion();
        ModeloInstitucion mod2 = new ModeloInstitucion();
        ControladorInstitucion control = new ControladorInstitucion(ins, m, view2, consul, mod2, mod);
        view2.setVisible(true);
        control.iniciar();
        view.dispose();
    }

    private void vistaVerPerfiles() {
        Conexion con = new Conexion();
        VerPerfiles verPerfiles = new VerPerfiles();
        ConsultaUsuario consul = new ConsultaUsuario(con);
        ControladorVerPerfiles verControl = new ControladorVerPerfiles(mod, ins, m, verPerfiles, consul);
        verControl.Iniciar();
        view.dispose();
    }

    private void vistaVerInstitucion() {
        Conexion con = new Conexion();
        VerInstituciones verInstituciones = new VerInstituciones();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(con);
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();
        ModeloInstitucion modeloInstitucion = new ModeloInstitucion();
        ControladorVerInstituciones contraladorVerInstituciones = new ControladorVerInstituciones(consultasInstitucion, consultaNucleo, modeloInstitucion, verInstituciones, mod, m);
        contraladorVerInstituciones.iniciar();
        view.dispose();
    }

    /**
     * Registra los listeners para los componentes de la vista.
     */
    private void listeners() {
        // Asocia eventos de los botones con los métodos correspondientes
        this.view.editarButton.addActionListener(this::actionPerformed);
        this.view.guardarCambiosButton.addActionListener(this::actionPerformed);
        this.view.verPassC.addActionListener(this::actionPerformed);
        this.view.verPassN.addActionListener(this::actionPerformed);
        this.view.inicioButton.addActionListener(this::actionPerformed);
        this.view.RegistrarEmision.addActionListener(this::actionPerformed);
        this.view.Calcular.addActionListener(this::actionPerformed);
        this.view.Informes.addActionListener(this::actionPerformed);
        this.view.Reducir.addActionListener(this::actionPerformed);
        this.view.RegistrarInstitucion.addActionListener(this::actionPerformed);
        this.view.VerPerfiles.addActionListener(this::actionPerformed);
        this.view.verInstitucion.addActionListener(this::actionPerformed);
    }
}
