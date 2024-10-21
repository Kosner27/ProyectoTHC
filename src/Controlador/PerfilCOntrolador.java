package Controlador;

import Modelo.Conexion;
import Modelo.Consultas.*;
import Modelo.modelo.*;
import Vistas.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PerfilCOntrolador {

    private Usuario mod;
    private Perfil view;
    private InstitucionModelo ins;
    private Municipio m;
    private ConsultaUsuario cons;
    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");

    public PerfilCOntrolador(Usuario mod, Perfil view,
                             InstitucionModelo ins, Municipio m, ConsultaUsuario cons) {
        this.mod = mod;
        this.view = view;
        this.ins=ins;
        this.m=m;
        this.cons=cons;
        listeners();
    }

    public void Iniciar(){

        switch (mod.getTipoUsuario()) {
            case "Administrador":
                view.setTitle("Perfil");
                Load();
                view.setLocationRelativeTo(null);
                view.setVisible(true);
                view.VerPerfiles.setVisible(false);
                view.setLocationRelativeTo(null);
                view.Graficos.add(GraficoPrincipal);
                view.Graficos.add(GraficosCompararInstitucion);
                view.Graficos.add(GraficoHistorico);
                view.setVisible(true);
                view.setLocationRelativeTo(null);
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
                break;
            case "SuperAdmin":
                view.setTitle("Perfil");
                Load();
                view.setLocationRelativeTo(null);
                view.Graficos.add(GraficoPrincipal);
                view.Graficos.add(GraficosCompararInstitucion);
                view.Graficos.add(GraficoHistorico);
                view.setVisible(true);
                view.setLocationRelativeTo(null);
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
private void actionPerformed(ActionEvent event){
        if(event.getSource()==view.editarButton){
            hablitarCampos();

        }else{
            desahabiltarCampos();
        }
        String pass = new String(view.newPass.getPassword());
        String Npass = new String(view.confirNewPass.getPassword());
        String correo = view.correo.getText();
        int id = mod.getIdUsuario();
        System.out.println(id);
        if(!pass.isEmpty() && !Npass.isEmpty()) {
            if (pass.equals(Npass)) {
               /* if(cons.ExisteUsuario(correo)==0) {*/
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
                        }else{
                            JOptionPane.showMessageDialog(null, "Error en la actualizacion");
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "El correo no está en un formato valido");
                    }
               /* } else {
                    JOptionPane.showMessageDialog(null,"El correo ya se encuentra registrado");
                }*/
            } else {
                JOptionPane.showMessageDialog(null, "Para actualizar la contraseña deben coincidir en ambos campos");
            }
        }
        if(event.getSource()==view.inicioButton){
            BotonInicio();
        }
        if(event.getSource()==view.RegistrarEmisión){
            vistaRegistrarEmision();
        }if(event.getSource()==view.Calcular){
            vistaCalcular();
        }if(event.getSource()==view.Informes){
                vistaInforme();
        }if(event.getSource()==view.Reducir){
                vistaReducir();
        }if(event.getSource()==view.RegistrarInstitucion){
           vistaActualizarInstitucion();
    }if(event.getSource()==view.VerPerfiles){
            vistaVerPerfiles();
    }

}
    public void Load(){
        llenarFormulario();
        desahabiltarCampos();

    }
    public void llenarFormulario(){
        view.apellido.setText(mod.getApellido());
        view.nombre.setText(mod.getNombre());
        view.correo.setText(mod.getCorreo());
        view.Sede.setText( m.getNombreM());
        view.Uni.setText(ins.getNombreInstitucion());
        view.rol.setText(mod.getTipoUsuario());
    }
    public void hablitarCampos(){
        //view.apellido.setEditable(true);
        //view.nombre.setEditable(true);
        //view.Sede.setEditable(true);
        //view.Uni.setEditable(true);
        //view.rol.setEditable(true);
        view.correo.setEditable(true);
        view.newPass.setEditable(true);
        view.confirNewPass.setEditable(true);
    }
    public void desahabiltarCampos(){
        view.Sede.setEditable(false);
        view.apellido.setEditable(false);
        view.nombre.setEditable(false);
        view.correo.setEditable(false);
        view.Uni.setEditable(false);
        view.rol.setEditable(false);
        view.newPass.setEditable(false);
        view.confirNewPass.setEditable(false);
    }
    public void BotonInicio (){
        ControladoInicio control = new ControladoInicio(ins,mod, m);
        control.inicio();
        view.dispose();
    }
    public void vistaRegistrarEmision(){
        Emision emisionView = new Emision();
        EmisionModelo EmisionModelo = new EmisionModelo();
        ConsultasEmision consul = new ConsultasEmision();
        EmisionControlador controlador = new EmisionControlador(EmisionModelo,consul,emisionView,ins,m,mod);
        controlador.iniciar();
        emisionView.setVisible(true);
        view.dispose();
    }
    public void vistaCalcular(){
        Conexion con = new Conexion();
        Vistas.Calcular view2 = new Calcular();
        CalcularModelo mod2 = new CalcularModelo();
        CalcularConsultas consul = new CalcularConsultas(con);
        ConsultaUsuario consultaUsuario= new ConsultaUsuario(con);
        CalcularControlador controlador = new CalcularControlador(mod2,consul,view2,ins,consultaUsuario,mod,m);
        controlador.iniciar();
        view2.setVisible(true);
        view.dispose();
    }
    public void vistaInforme(){
        Conexion con = new Conexion();
        ConsultaInforme consul = new ConsultaInforme(con);
        ModeloInforme modInfo = new ModeloInforme();
        Informe viewInfo = new Informe();
        ControladorInforme contro = new ControladorInforme(viewInfo,modInfo,consul,m,ins,mod);
        contro.iniciar();
        viewInfo.setVisible(true);
        view.dispose();
    }
    public void vistaGraficoPrincipal(){
        Conexion con = new Conexion();
        GraficoConsulta consul = new GraficoConsulta(con);
        Vistas.Graficos viewGraf = new Graficos();
        GraficorModelo modGraf = new GraficorModelo();
        InstitucionModelo modelo = new InstitucionModelo();
        GraficoControlador contro = new GraficoControlador(modGraf,consul, viewGraf,modelo,m,mod,ins);
        contro.iniciar();
        viewGraf.setVisible(true);
        view.dispose();
    }
    public void vistaCompararInstituciones(){
        CompararOtrarInstituciones comIns = new CompararOtrarInstituciones();
        GraficoComparar viewGraf = new GraficoComparar();
        Conexion conn = new Conexion();
        GraficoCompararConsultas consultas = new GraficoCompararConsultas(conn);
        GraficoCompararModelo modGraf = new GraficoCompararModelo();
        ComparaInstitucion contro = new ComparaInstitucion(modGraf,consultas,comIns,viewGraf,ins,m,mod);
        contro.iniciar();
        view.dispose();
    }
    public void vistaGraficoHistorico(){
        Conexion con = new Conexion();
        TendenciaModelo modGraf = new TendenciaModelo();
        ConsultasTendencias consult = new ConsultasTendencias(con);
        GraficoTendencia viewGraf= new GraficoTendencia();
        TendenciaControlador control = new TendenciaControlador(modGraf,consult,viewGraf,m,ins,mod);
        viewGraf.setVisible(true);
        control.iniciar();
        view.dispose();
    }
    public void vistaReducir(){
        Conexion con = new Conexion();
        Reducir2 vista = new Reducir2();
        GraficoConsulta consul = new GraficoConsulta(con);
        ControladorReducir redu = new ControladorReducir(ins,consul, vista,m,mod);
        redu.Iniciar();
        view.dispose();
    }
    public void vistaActualizarInstitucion(){
        Conexion con = new Conexion();
        ConsultasInstitucion consul = new ConsultasInstitucion();
        RegistrarInstitucion view2 = new RegistrarInstitucion();
        InstitucionModelo mod2 = new InstitucionModelo();
        InstitucionControlador control = new InstitucionControlador(ins, m,view2, consul,mod2,mod);
        view2.setVisible(true);
        control.iniciar();
        view.dispose();



    }
    public void vistaVerPerfiles(){
        Conexion con = new Conexion();
        VerPerfiles verPerfiles = new VerPerfiles();
        ConsultaUsuario consul = new ConsultaUsuario(con);

        VerPerfilesControlador verControl = new VerPerfilesControlador(mod,ins, m,verPerfiles,consul);
        verControl.Iniciar();
        view.dispose();
    }
    public void listeners(){
        this.view.editarButton.addActionListener(this::actionPerformed);
        this.view.guardarCambiosButton.addActionListener(this::actionPerformed);
        this.view.verPassC.addActionListener(this::actionPerformed);
        this.view.verPassN.addActionListener(this::actionPerformed);
        this.view.inicioButton.addActionListener(this::actionPerformed);
        this.view.RegistrarEmisión.addActionListener(this::actionPerformed);
        this.view.Calcular.addActionListener(this::actionPerformed);
        this.view.Informes.addActionListener(this::actionPerformed);
        this.view.Reducir.addActionListener(this::actionPerformed);
        this.view.RegistrarInstitucion.addActionListener(this::actionPerformed);
        this.view.VerPerfiles.addActionListener(this::actionPerformed);

    }
}
