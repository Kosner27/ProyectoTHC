package Controlador;
import Modelo.modelo.Usuario;
import Vistas.Loader;
import Vistas.Main;

import java.util.logging.Level;
import java.util.logging.Logger;

public class Prinicipal {
    public static void main(String[] args) {

        Runnable nRun = ()->{
            Loader load = new Loader();
            load.setVisible(true);
            load.setLocationRelativeTo(null);
            try{
                Thread.sleep(5000);
            }catch (Exception ex){
                Logger.getLogger(Prinicipal.class.getName()).log(Level.SEVERE,null, ex);
            }
            load.dispose();
            Main inicio = new Main();
            Usuario mod = new Usuario();
            ControladorMain main = new ControladorMain(inicio, mod);
            main.Iniciar();

        };
            Thread loader = new Thread(nRun);
            loader.start();


    }
}
