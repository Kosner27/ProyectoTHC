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
                init.RegistrarEmisión.setVisible(false);
                init.Calcular.setVisible(false);
                init.perfil.setVisible(true);
                init.setLocationRelativeTo(null);
                break;
            case "Administrador" :
                Inicio ini = new Inicio();
                ini.setVisible(true);
                init.VerPerfiles.setVisible(false);
                ini.setSize(3800,600);
                ini.dispose();
                break;
            case "SuperAdmin":
                ini = new Inicio();
                ini.setVisible(true);
                ini.setSize(3800,600);
                ini.dispose();
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



    public void actionPerformed(ActionEvent e){
            if(e.getSource() == init.perfil ){
                vistaPerfil();
            }if(e.getSource()==init.Calcular){
                vistaCalcular();
            }if(e.getSource()== init.RegistrarEmisión){
                vistaRegistrarEmision();
            }if(e.getSource()== init.Informes){
                vistaInforme();
            }if(e.getSource()==init.RegistrarInstitucion){
                vistaActualizarInstitucion();
            }if(e.getSource()== init.VerPerfiles){
                vistaVerPerfiles();
            }if(e.getSource()==init.Reducir){
                vistaReducir();
        }

    }
        public void vistaPerfil(){
            Conexion conn = new Conexion();
            Perfil per = new Perfil();
            ConsultaUsuario cons = new ConsultaUsuario(conn);
            PerfilCOntrolador control = new PerfilCOntrolador(user, per, ins, m, cons);
            control.Iniciar();
            per.setVisible(true);
            init.dispose();
        }
        public void vistaCalcular(){
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
        public void vistaRegistrarEmision(){
            Emision emisionView = new Emision();
            EmisionModelo mod = new EmisionModelo();
            ConsultasEmision consul = new ConsultasEmision();
            EmisionControlador controlador = new EmisionControlador(mod,consul,emisionView,ins,m,user);
            controlador.iniciar();
            emisionView.setVisible(true);
            init.dispose();
        }
        public void vistaInforme(){
            Conexion con = new Conexion();
            InstitucionModelo mod2 = new InstitucionModelo();
            ConsultaInforme consul = new ConsultaInforme(con);
            ModeloInforme mod = new ModeloInforme();
            Informe view = new Informe();
            ConsultaUsuario consultaUsuario= new ConsultaUsuario(con);
            ControladorInforme contro = new ControladorInforme(view,mod,consul,m,ins,user);
            contro.iniciar();
            view.setVisible(true);
            init.dispose();
        }
        public void vistaGraficoPrincipal(){
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
        public void vistaCompararInstituciones(){
            CompararOtrarInstituciones comIns = new CompararOtrarInstituciones();
            GraficoComparar view = new GraficoComparar();
            Conexion conn = new Conexion();
            GraficoCompararConsultas consultas = new GraficoCompararConsultas(conn);
            GraficoCompararModelo mod = new GraficoCompararModelo();
            ComparaInstitucion contro = new ComparaInstitucion(mod,consultas,comIns,view,ins,m,user);
            contro.iniciar();
            init.dispose();
        }
        public void vistaGraficoHistorico(){
            Conexion con = new Conexion();
            TendenciaModelo mod = new TendenciaModelo();
            ConsultasTendencias consult = new ConsultasTendencias(con);
            GraficoTendencia view = new GraficoTendencia();
            TendenciaControlador control = new TendenciaControlador(mod,consult,view,m,ins,user);
            view.setVisible(true);
            control.iniciar();
            init.dispose();
        }
        public void vistaActualizarInstitucion(){
            Conexion con = new Conexion();
            ConsultasInstitucion consul = new ConsultasInstitucion();
            RegistrarInstitucion view = new RegistrarInstitucion();
            InstitucionModelo mod = new InstitucionModelo();
            InstitucionControlador control = new InstitucionControlador(ins, m,view, consul,mod,user);
            view.setVisible(true);
            control.iniciar();
            init.dispose();



        }
        public void vistaVerPerfiles(){
                Conexion con = new Conexion();
                VerPerfiles verPerfiles = new VerPerfiles();
                ConsultaUsuario consul = new ConsultaUsuario(con);

                VerPerfilesControlador verControl = new VerPerfilesControlador(user,ins, m,verPerfiles,consul);
                verControl.Iniciar();
                init.dispose();
        }
        public void vistaReducir(){
            Conexion con = new Conexion();
            Reducir2 vista = new Reducir2();
            GraficoConsulta consul = new GraficoConsulta(con);
            ControladorReducir redu = new ControladorReducir(ins,consul, vista,m,user);
            redu.Iniciar();
            init.dispose();
        }
        public void Listeners(){
            this.init.perfil.addActionListener(this::actionPerformed);
            this.init.Calcular.addActionListener(this::actionPerformed);
            this.init.RegistrarEmisión.addActionListener(this::actionPerformed);
            this.init.Informes.addActionListener(this::actionPerformed);
            this.init.RegistrarInstitucion.addActionListener(this::actionPerformed);
            this.init.VerPerfiles.addActionListener(this::actionPerformed);
            this.init.Reducir.addActionListener(this::actionPerformed);
        }
}
