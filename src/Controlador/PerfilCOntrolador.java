package Controlador;

import Modelo.Conexion;
import Modelo.Consultas.ConsultaUsuario;
import Modelo.modelo.HASH;
import Modelo.modelo.InstitucionModelo;
import Modelo.modelo.UsuarioModel;
import Modelo.modelo.municipio;
import Vistas.Inicio;
import Vistas.Perfil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PerfilCOntrolador {

    private UsuarioModel mod;
    private Perfil view;
    private InstitucionModelo ins;
    private municipio m;
    private ConsultaUsuario cons;

    public PerfilCOntrolador(UsuarioModel mod, Perfil view, InstitucionModelo ins,municipio m, ConsultaUsuario cons) {
        this.mod = mod;
        this.view = view;
        this.ins=ins;
        this.m=m;
        this.cons=cons;
        this.view.editarButton.addActionListener(this::actionPerformed);
        this.view.guardarCambiosButton.addActionListener(this::actionPerformed);
        this.view.verPassC.addActionListener(this::actionPerformed);
        this.view.verPassN.addActionListener(this::actionPerformed);
        this.view.inicioButton.addActionListener(this::actionPerformed);
    }

    public PerfilCOntrolador(){

    }


    public void Iniciar(){
        view.setTitle("Perfil");
        view.setLocationRelativeTo(null);
        view.setVisible(true);
        Load();
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
            Inicio in = new Inicio(mod, ins, m);
            in.setVisible(true);
            view.dispose();
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

    public void ActualizarDatos(){

    }

}
