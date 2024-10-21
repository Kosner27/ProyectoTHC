package Vistas;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RecordarContrasena extends JFrame{
    public JTextField textField1;
    public  JButton recordarButton;
    public JButton inicoButton;
    private JPanel main;
    public JPasswordField passN;
    public JPasswordField passC;
    public JButton verPassN;
    public JButton verPassC;
    public JButton verificarCorreoButton;

    public RecordarContrasena() {
        setTitle("Recordar Constraseña");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(800, 200);
        setLocationRelativeTo(null);
        setContentPane(main);
        verPassC.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                passN.setEchoChar((char) 0);

            }
        });


        verPassC.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                passN.setEchoChar('*');
            }
        });

        verPassN.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                passC.setEchoChar((char) 0);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                passC.setEchoChar('*');
            }
        });
    }
}
