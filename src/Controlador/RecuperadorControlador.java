package Controlador;

import Modelo.Consultas.ConsultaUsuario;
import Modelo.modelo.HASH;
import Modelo.modelo.Usuario;
import Vistas.Main;
import Vistas.RecordarContrasena;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class RecuperadorControlador {
    private Usuario user;
    private ConsultaUsuario consultaUsuario;
    private RecordarContrasena contrasena;

    public RecuperadorControlador(Usuario user, ConsultaUsuario consultaUsuario, RecordarContrasena contrasena) {
        this.user = user;
        this.consultaUsuario = consultaUsuario;
        this.contrasena = contrasena;
        listeners();
    }
    public void inicio(){
        contrasena.setVisible(true);
    }
    public void listeners (){
        this.contrasena.recordarButton.addActionListener(this::actionPerformed);
        this.contrasena.verificarCorreoButton.addActionListener(this::actionPerformed);
        this.contrasena.inicoButton.addActionListener(this::actionPerformed);
    }
    public void actionPerformed(ActionEvent e ){
        String Correo = contrasena.textField1.getText();
        if(e.getSource()==contrasena.verificarCorreoButton){
            if(!Correo.isEmpty()){
                if(consultaUsuario.esEmail(Correo)){
                    if(consultaUsuario.ExisteUsuario(Correo)==1){
                        contrasena.passC.setVisible(true);
                        contrasena.passN.setVisible(true);
                        contrasena.verPassC.setVisible(true);
                        contrasena.verPassN.setVisible(true);
                    }else{
                        JOptionPane.showMessageDialog(null, " EL usuario no existe");
                    }
                }else{
                    JOptionPane.showMessageDialog(null, "El texto no está en un formato correo por ejemplo: example@ejemplo.com");
                }
            }
        }

        if(e.getSource()==contrasena.recordarButton) {
            String pass1 = new String(contrasena.passN.getPassword());
            String pass2 = new String(contrasena.passC.getPassword());
            System.out.println(pass1);
            System.out.println(pass2);
        if(pass1.equals(pass2)){
            String passCod= HASH.sha1(pass2);
            if(consultaUsuario.actualizarContrasena(Correo,passCod)){
                JOptionPane.showMessageDialog(null, "Actualizacion de la contraseña ha sido correcta");
                limpiar();
            }else{
                JOptionPane.showMessageDialog(null, " Error en la consulta");
            }
        }else{
            JOptionPane.showMessageDialog(null, " Los campos estan vacios y/o las contraseñas no coinciden");
        }
        }if(e.getSource()==contrasena.inicoButton){
            Main vista = new Main();
            vista.setVisible(true);
            contrasena.dispose();
        }
    }

    public void limpiar (){
        contrasena.textField1.setText("");
        contrasena.passC.setText("");
        contrasena.passN.setText("");
        contrasena.passC.setVisible(false);
        contrasena.passN.setVisible(false);
    }

}
