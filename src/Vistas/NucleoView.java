package Vistas;

import javax.swing.*;

public class NucleoView extends JFrame{
    public JTextField nombreInstitucion;
    public JTextField sede;
    public JTextField nucleotxt;
    public JButton cancelarButton;
    public JButton guardarButton;
    public JPanel Principal;

    public NucleoView(){
        setTitle("Registrar Nucleo");
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setContentPane(Principal);

    }
}
