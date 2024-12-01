package Controlador;

import Modelo.Conexion;

import Modelo.Consultas.*;

import Modelo.modelo.*;
import Vistas.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;



public class ControladoInicio  {
    public Usuario user;
    public InstitucionModelo ins;
    public Municipio m;
    Inicio init = new Inicio();
    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");

    public ControladoInicio(InstitucionModelo ins, Usuario user, Municipio m) {
        this.ins = ins;
        this.user = user;
        this.m = m;
        Listeners();
        System.out.println(user.getTipoUsuario()+"hola");
    }

    public void inicio(){
        init.Graficos.add(GraficoPrincipal);
        init.Graficos.add(GraficosCompararInstitucion);
        init.Graficos.add(GraficoHistorico);

        String usuario = user.getTipoUsuario();
        System.out.println(usuario);
        switch (usuario){
            case "Invitado" :
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
            case "Administrador" :
                Inicio ini = new Inicio();
                ini.setVisible(true);
                init.VerPerfiles.setVisible(true);
                init.verInstitucion.setVisible(false);
                ini.setSize(3800,600);
                ini.dispose();
                break;
            case "Superadmin":
                ini = new Inicio();
                ini.setVisible(true);
                ini.setSize(3800,600);
                ini.dispose();
                break;
            default:
                JOptionPane.showMessageDialog(null, "Usuario no definido en el sistema");
                break;

        }


            GraficoPrincipal.addActionListener(e -> vistaGraficoPrincipal());
            GraficosCompararInstitucion.addActionListener(e -> vistaCompararInstituciones());
            GraficoHistorico.addActionListener(e -> vistaGraficoHistorico());
    }



    public void actionPerformed(ActionEvent e){
            if(e.getSource() == init.perfil ){
                vistaPerfil();
            }if(e.getSource()==init.Calcular){
                vistaCalcular();
            }if(e.getSource()== init.RegistrarEmision){
                vistaRegistrarEmision();
            }if(e.getSource()== init.Informes){
                vistaInforme();
            }if(e.getSource()==init.RegistrarInstitucion){
                vistaActualizarInstitucion();
            }if(e.getSource()== init.VerPerfiles){
                vistaVerPerfiles();
            }if(e.getSource()==init.Reducir){
                vistaReducir();
            }if(e.getSource()== init.verInstitucion){
                vistaVerInstitucion();
            }if(e.getSource() == init.CerraSesion){
                cerrarSession();
        }

    }
        private void vistaPerfil(){
                Conexion conn = new Conexion();
                Perfil per = new Perfil();
                ConsultaUsuario cons = new ConsultaUsuario(conn);
                PerfilCOntrolador control = new PerfilCOntrolador(user, per, ins, m, cons);
                control.Iniciar();
                per.setVisible(true);
                init.dispose();
            }
        private void vistaCalcular(){
                Conexion con = new Conexion();
                Vistas.Calcular view = new Calcular();
                CalcularModelo mod = new CalcularModelo();
                CalcularConsultas consul = new CalcularConsultas(con);
                ConsultaUsuario consultaUsuario= new ConsultaUsuario(con);
                CalcularControlador controlador = new CalcularControlador(mod,consul,view,ins,consultaUsuario,user,m);
                controlador.iniciar();
                view.setVisible(true);
                init.dispose();
            }
        private void vistaRegistrarEmision(){
                Emision emisionView = new Emision();
                EmisionModelo mod = new EmisionModelo();
                ConsultasEmision consul = new ConsultasEmision();
                EmisionControlador controlador = new EmisionControlador(mod,consul,emisionView,ins,m,user);
                controlador.iniciar();
                emisionView.setVisible(true);
                init.dispose();
            }
        private void vistaInforme(){
                Conexion con = new Conexion();
                ConsultaInforme consul = new ConsultaInforme(con);
                ModeloInforme mod = new ModeloInforme();
                Informe view = new Informe();
                ControladorInforme contro = new ControladorInforme(view,mod,consul,m,ins,user);
                contro.iniciar();
                view.setVisible(true);
                init.dispose();
            }
        private void vistaGraficoPrincipal(){
                Conexion con = new Conexion();
                GraficoConsulta consul = new GraficoConsulta(con);
                Vistas.Graficos view = new Graficos();
                GraficorModelo mod = new GraficorModelo();
                InstitucionModelo modelo = new InstitucionModelo();
                GraficoControlador contro = new GraficoControlador(mod,consul, view,modelo,m,user,ins);
                contro.iniciar();
                view.Graficos.setVisible(true);
                init.dispose();
            }
        private void vistaCompararInstituciones(){
                CompararOtrarInstituciones comIns = new CompararOtrarInstituciones();
                GraficoComparar view = new GraficoComparar();
                Conexion conn = new Conexion();
                GraficoCompararConsultas consultas = new GraficoCompararConsultas(conn);
                GraficoCompararModelo mod = new GraficoCompararModelo();
                ComparaInstitucion contro = new ComparaInstitucion(mod,consultas,comIns,view,ins,m,user);
                contro.iniciar();
                init.dispose();
            }
        private void vistaGraficoHistorico(){
                Conexion con = new Conexion();
                TendenciaModelo mod = new TendenciaModelo();
                ConsultasTendencias consult = new ConsultasTendencias(con);
                GraficoTendencia view = new GraficoTendencia();
                TendenciaControlador control = new TendenciaControlador(mod,consult,view,m,ins,user);
                view.setVisible(true);
                control.iniciar();
                init.dispose();
            }
        private void vistaActualizarInstitucion(){
                Conexion con = new Conexion();
                ConsultasInstitucion consul = new ConsultasInstitucion();
                VerDatosInstitucion view = new VerDatosInstitucion();
                InstitucionModelo mod = new InstitucionModelo();
                InstitucionControlador control = new InstitucionControlador(ins, m,view, consul,mod,user);
                view.setVisible(true);
                control.iniciar();
                init.dispose();



            }
        private void vistaVerPerfiles(){
                    Conexion con = new Conexion();
                    VerPerfiles verPerfiles = new VerPerfiles();
                    ConsultaUsuario consul = new ConsultaUsuario(con);

                    VerPerfilesControlador verControl = new VerPerfilesControlador(user,ins, m,verPerfiles,consul);
                    verControl.Iniciar();
                    init.dispose();
            }
        private void vistaReducir(){
                Conexion con = new Conexion();
                Reducir2 vista = new Reducir2();
                GraficoConsulta consul = new GraficoConsulta(con);
                ControladorReducir redu = new ControladorReducir(ins,consul, vista,m,user);
                redu.Iniciar();
                init.dispose();
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
                //init.dispose();
            }
        private void Listeners(){
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
        private void cerrarSession(){
            Main view = new Main();
            Usuario user1 = new Usuario();
            ControladorMain controladorMain = new ControladorMain(view,user1);
            controladorMain.Iniciar();
            init.dispose();
            JOptionPane.showMessageDialog(init, "Sesion cerrada");
        }
}
