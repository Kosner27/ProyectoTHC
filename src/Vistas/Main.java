package Vistas;



import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

public class Main extends JFrame {
    private JPanel Principal;
    public JButton iniciarSesionButton;
    public JButton registrarseButton;
    private JPanel titulo;
    public JButton iniciarComoInvitadoButton;

    public Main(){
        setTitle("Inicio");
        setVisible(true);
        setSize(500, 500);
        setLocationRelativeTo(null);
        setContentPane(Principal);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

    }
}
