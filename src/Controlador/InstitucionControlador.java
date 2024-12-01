package Controlador;


import Modelo.Conexion;
import Modelo.Consultas.*;
import Modelo.modelo.*;
import Vistas.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;


public class InstitucionControlador implements ActionListener {
    private final Usuario user;
    private final InstitucionModelo ins;
    private final Municipio m;
    private final VerDatosInstitucion view;
    private final ConsultasInstitucion consul;
    private final InstitucionModelo mod;
    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");

    public InstitucionControlador(InstitucionModelo ins, Municipio m,
                                  VerDatosInstitucion view, ConsultasInstitucion consul,
                                  InstitucionModelo mod, Usuario user) {
        this.view = view;
        this.m = m;
        this.ins = ins;
        this.consul = consul;
        this.mod = mod;
        this.user = user;
        Listeners();
    }

    public void iniciar() {
        switch (user.getTipoUsuario()) {
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
                JOptionPane.showMessageDialog(null, "Usuario no definido en el sistema");
                break;

        }
    }


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
                if (consul.ActualizarInstitucion(view.nombre.getText(), view.municipio.getText(), h, nit)) {
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

    public void CargarDatos() {
        String NombreInstitucion = ins.getNombreInstitucion();
        String Municipio = m.getNombreM();
        System.out.println(Municipio);
        List<InstitucionModelo> modelos = consul.CargarDatos(NombreInstitucion, Municipio);
        for (InstitucionModelo i : modelos) {
            view.nombre.setText(i.getNombreInstitucion());
            view.nit.setText(i.getNit());
            view.departamento.setText(i.getDepartamento());
            view.municipio.setText(i.getMunicipio());
        }
    }

    public void CamposInhabilitados() {
        view.nombre.setEditable(false);
        view.nit.setEditable(false);
        view.municipio.setEditable(false);
        view.departamento.setEditable(false);
        view.hectareas.setEditable(false);
    }

    public void BotonInicio() {
        ControladoInicio control = new ControladoInicio(ins, user, m);
        control.inicio();
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

    public void vistaGraficoPrincipal() {
        Conexion con = new Conexion();
        GraficoConsulta consul = new GraficoConsulta(con);
        Vistas.Graficos view2 = new Graficos();
        GraficorModelo mod = new GraficorModelo();
        InstitucionModelo modelo = new InstitucionModelo();
        GraficoControlador contro = new GraficoControlador(mod, consul, view2, modelo, m, user, ins);
        contro.iniciar();
        view2.Graficos.setVisible(true);
        view.dispose();
    }

    public void vistaCompararInstituciones() {
        CompararOtrarInstituciones comIns = new CompararOtrarInstituciones();
        GraficoComparar view2 = new GraficoComparar();
        Conexion conn = new Conexion();
        GraficoCompararConsultas consultas = new GraficoCompararConsultas(conn);
        GraficoCompararModelo mod = new GraficoCompararModelo();
        ComparaInstitucion contro = new ComparaInstitucion(mod, consultas, comIns, view2, ins, m, user);
        contro.iniciar();
        view.dispose();
    }

    public void vistaGraficoHistorico() {
        Conexion con = new Conexion();
        TendenciaModelo mod = new TendenciaModelo();
        ConsultasTendencias consult = new ConsultasTendencias(con);
        GraficoTendencia view2 = new GraficoTendencia();
        TendenciaControlador control = new TendenciaControlador(mod, consult, view2, m, ins, user);
        view2.setVisible(true);
        control.iniciar();
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
        Vistas.Calcular view2 = new Calcular();
        CalcularModelo mod = new CalcularModelo();
        CalcularConsultas consul = new CalcularConsultas(con);
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        CalcularControlador controlador = new CalcularControlador(mod, consul, view2, ins, consultaUsuario, user, m);
        controlador.iniciar();
        view.setVisible(true);
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
        InstitucionModelo mod2 = new InstitucionModelo();
        ConsultaInforme consul = new ConsultaInforme(con);
        ModeloInforme mod = new ModeloInforme();
        Informe view2 = new Informe();
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        ControladorInforme contro = new ControladorInforme(view2, mod, consul, m, ins, user);
        contro.iniciar();
        view2.setVisible(true);
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

    private void vistaVerInstitucion() {
        Conexion con = new Conexion();
        VerInstituciones verInstituciones = new VerInstituciones();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(con);
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();
        InstitucionModelo institucionModelo = new InstitucionModelo();
        ContraladorVerInstituciones contraladorVerInstituciones = new ContraladorVerInstituciones(consultasInstitucion, consultaNucleo,
                institucionModelo, verInstituciones, user, m);
        contraladorVerInstituciones.iniciar();
        //view.dispose();
    }

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
