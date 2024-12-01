package Controlador;

import Modelo.Conexion;
import Modelo.Consultas.*;
import Modelo.modelo.*;
import Vistas.*;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PerfilCOntrolador {

    private final Usuario mod;
    private final Perfil view;
    private final InstitucionModelo ins;
    private final Municipio m;
    private final ConsultaUsuario cons;
    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");

    public PerfilCOntrolador(Usuario mod, Perfil view,
                             InstitucionModelo ins, Municipio m, ConsultaUsuario cons) {
        this.mod = mod;
        this.view = view;
        this.ins = ins;
        this.m = m;
        this.cons = cons;
        listeners();
    }

    public void Iniciar() {

        switch (mod.getTipoUsuario()) {
            case "Administrador":
                view.setTitle("Perfil");
                Load();
                view.setLocationRelativeTo(null);
                view.setVisible(true);
                view.VerPerfiles.setVisible(true);
                view.verInstitucion.setVisible(false);
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
            case "Superadmin":
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
            case "Invitado":
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


        GraficoPrincipal.addActionListener(e -> vistaGraficoPrincipal());
        GraficosCompararInstitucion.addActionListener(e -> vistaCompararInstituciones());
        GraficoHistorico.addActionListener(e -> vistaGraficoHistorico());
    }

    private void actionPerformed(ActionEvent event) {
        if (event.getSource() == view.editarButton) {
            hablitarCampos();

        } else {
            desahabiltarCampos();
        }
        String pass = new String(view.newPass.getPassword());
        String Npass = new String(view.confirNewPass.getPassword());
        String correo = view.correo.getText();
        int id = mod.getIdUsuario();
        System.out.println(id);
        if (!pass.isEmpty() && !Npass.isEmpty()) {
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
                    } else {
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
        if(event.getSource() == view.verInstitucion){
            vistaVerInstitucion();
        }
    }

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
        //view.apellido.setEditable(true);
        //view.nombre.setEditable(true);
        //view.Sede.setEditable(true);
        //view.Uni.setEditable(true);
        //view.rol.setEditable(true);
        view.correo.setEditable(true);
        view.newPass.setEditable(true);
        view.confirNewPass.setEditable(true);
    }

    private void desahabiltarCampos() {
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
        ControladoInicio control = new ControladoInicio(ins, mod, m);
        control.inicio();
        view.dispose();
    }

    private void vistaRegistrarEmision() {
        Emision emisionView = new Emision();
        EmisionModelo EmisionModelo = new EmisionModelo();
        ConsultasEmision consul = new ConsultasEmision();
        EmisionControlador controlador = new EmisionControlador(EmisionModelo, consul, emisionView, ins, m, mod);
        controlador.iniciar();
        emisionView.setVisible(true);
        view.dispose();
    }

    private void vistaCalcular() {
        Conexion con = new Conexion();
        Vistas.Calcular view2 = new Calcular();
        CalcularModelo mod2 = new CalcularModelo();
        CalcularConsultas consul = new CalcularConsultas(con);
        ConsultaUsuario consultaUsuario = new ConsultaUsuario(con);
        CalcularControlador controlador = new CalcularControlador(mod2, consul, view2, ins, consultaUsuario, mod, m);
        controlador.iniciar();
        view2.setVisible(true);
        view.dispose();
    }

    private void vistaInforme() {
        Conexion con = new Conexion();
        ConsultaInforme consul = new ConsultaInforme(con);
        ModeloInforme modInfo = new ModeloInforme();
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
        GraficorModelo modGraf = new GraficorModelo();
        InstitucionModelo modelo = new InstitucionModelo();
        GraficoControlador contro = new GraficoControlador(modGraf, consul, viewGraf, modelo, m, mod, ins);
        contro.iniciar();
        viewGraf.setVisible(true);
        view.dispose();
    }

    private void vistaCompararInstituciones() {
        CompararOtrarInstituciones comIns = new CompararOtrarInstituciones();
        GraficoComparar viewGraf = new GraficoComparar();
        Conexion conn = new Conexion();
        GraficoCompararConsultas consultas = new GraficoCompararConsultas(conn);
        GraficoCompararModelo modGraf = new GraficoCompararModelo();
        ComparaInstitucion contro = new ComparaInstitucion(modGraf, consultas, comIns, viewGraf, ins, m, mod);
        contro.iniciar();
        view.dispose();
    }

    private void vistaGraficoHistorico() {
        Conexion con = new Conexion();
        TendenciaModelo modGraf = new TendenciaModelo();
        ConsultasTendencias consult = new ConsultasTendencias(con);
        GraficoTendencia viewGraf = new GraficoTendencia();
        TendenciaControlador control = new TendenciaControlador(modGraf, consult, viewGraf, m, ins, mod);
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
        Conexion con = new Conexion();
        ConsultasInstitucion consul = new ConsultasInstitucion();
        VerDatosInstitucion view2 = new VerDatosInstitucion();
        InstitucionModelo mod2 = new InstitucionModelo();
        InstitucionControlador control = new InstitucionControlador(ins, m, view2, consul, mod2, mod);
        view2.setVisible(true);
        control.iniciar();
        view.dispose();


    }

    private void vistaVerPerfiles() {
        Conexion con = new Conexion();
        VerPerfiles verPerfiles = new VerPerfiles();
        ConsultaUsuario consul = new ConsultaUsuario(con);

        VerPerfilesControlador verControl = new VerPerfilesControlador(mod, ins, m, verPerfiles, consul);
        verControl.Iniciar();
        view.dispose();
    }

    private void vistaVerInstitucion(){
        Conexion con = new Conexion();
        VerInstituciones verInstituciones = new VerInstituciones();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(con);
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();
        InstitucionModelo institucionModelo = new InstitucionModelo();
        ContraladorVerInstituciones contraladorVerInstituciones = new ContraladorVerInstituciones(consultasInstitucion,consultaNucleo,
                institucionModelo,verInstituciones,mod,m);
        contraladorVerInstituciones.iniciar();
        //view.dispose();
    }


    private void listeners() {
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
