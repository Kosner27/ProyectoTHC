package Vistas;

import javax.swing.*;

public class RegistrarInstitucion  extends JFrame{
    public JComboBox departamento;
    public JComboBox municipio;
    public JTextField nit;
    public JTextField nombreInstitucion;
    public JButton guardarButton;
    public JButton cancelarButton;
    public JTextField hectareas;
    public JPanel Main;


    public RegistrarInstitucion(){
        setTitle("Registrar Institución");
        setVisible(true);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(500, 600);
        setLocationRelativeTo(null);
        setContentPane(Main);

    }
}
