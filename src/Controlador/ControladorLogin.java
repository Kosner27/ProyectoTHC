package Controlador;

import Modelo.Consultas.ConsultaUsuario;
import Modelo.modelo.*;
import Vistas.Calcular;
import Vistas.Inicio;
import Vistas.LogIn;
import Vistas.Perfil;
import com.itextpdf.text.pdf.PdfOutline;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class ControladorLogin {
    private final UsuarioModel mod;
    private final LogIn view;
    private final ConsultaUsuario consul;


    public ControladorLogin(UsuarioModel mod, LogIn view, ConsultaUsuario consul) {
        this.mod = mod;
        this.view = view;
        this.consul = consul;
        this.view.iniciarSesionButton.addActionListener(this::actionPerformed);
    }
public void Iniciar(){
    view.setTitle("Login");
    view.setLocationRelativeTo(null);
}
    private void actionPerformed(ActionEvent event) {
            if(event.getSource()==view.iniciarSesionButton){

                InstitucionModelo ins = new InstitucionModelo();
                municipio m = new municipio();
                String pass = new String(view.Contrasena.getPassword());
                String Usuario = view.Correo.getText();

                if(!Usuario.isEmpty() && !pass.isEmpty()){
                    String nuevPas = HASH.sha1(pass);
                    mod.setCorreo(Usuario);
                    mod.setContrasena(nuevPas);

                        if(consul.LogIn(mod,ins,m)){
                            ControladoInicio controladoInicio = new ControladoInicio(ins,mod,m);
                            controladoInicio.inicio();
                            view.dispose();
                            JOptionPane.showMessageDialog(null, "Datos correcto");
                        }else{
                            JOptionPane.showMessageDialog(null, "Datos Incorrectos");
                        }
                }else{
                    JOptionPane.showMessageDialog(null, "Debe ingresar sus credenciales");
                }

            }

    }
}
