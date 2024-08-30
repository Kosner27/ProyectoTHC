package Controlador;

import Modelo.modelo.InstitucionModelo;
import Modelo.modelo.UsuarioModel;
import Modelo.modelo.municipio;
import Vistas.Inicio;


public class ControladoInicio  {
    public UsuarioModel user;
    public InstitucionModelo ins;
    public municipio m;
Inicio init = new Inicio();
    public ControladoInicio(InstitucionModelo ins, UsuarioModel user, municipio m) {
        this.ins = ins;
        this.user = user;
        this.m = m;
        this.init.perfil.addActionListener(this::actionPerformed);
    }
    public void inicio(){
        if(user.getTipoUsuario().equals("Docente")){
            System.out.println("Estoy en docente");
            init.setVisible(true);
            init.RegistrarInstitucion.setVisible(false);
            VentanasDocente();
        }else {

            Inicio ini = new Inicio();
            ini.setVisible(true);
            ini.dispose();
        }
    }
    public void VentanasDocente(){

    }
}
