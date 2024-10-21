package Vistas;



import javax.swing.*;

public class Main extends JFrame {
    private JPanel Principal;
    public JButton iniciarSesionButton;
    public JButton registrarseButton;
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
