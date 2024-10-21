package Vistas;
import javax.swing.*;
import java.awt.event.ActionEvent;


public class Loader extends JFrame {
    public JProgressBar loader;
    public JPanel panel1;

    public Loader() {
        setUndecorated(true);
        setVisible(true);
        setSize(600, 500);
        setLocationRelativeTo(null);


        panel1.setOpaque(false);

        setContentPane(panel1);
        ProgreBar();




    }
    private void ProgreBar(){
        Timer mTimer = new Timer(43, (ActionEvent e )->{

            loader.setValue(loader.getValue()+1);
            loader.setStringPainted(true);
            loader.setString("Loading..."+ loader.getValue()+"%");


        });
        mTimer.start();
    }
}
