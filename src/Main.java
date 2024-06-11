import Controlador.InstitucionControlador;
import Modelo.ConsultasInstitucion;
import Modelo.InstitucionModelo;
import Vistas.Inicio;
import Vistas.RegistrarInstitucion;

public class Main {


    public static void main(String[]args){
        InstitucionModelo mod = new InstitucionModelo();
        ConsultasInstitucion consul = new ConsultasInstitucion();
        RegistrarInstitucion view = new RegistrarInstitucion();
        InstitucionControlador controlador = new InstitucionControlador(mod, consul,view);
        controlador.iniciar();
        view.setVisible(true);
        Inicio in = new Inicio();
        in.setVisible(true);
}
}