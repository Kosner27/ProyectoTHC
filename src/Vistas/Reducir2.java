package Vistas;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class Reducir2 extends JFrame {
    private JMenuItem RegistrarInstitucion;
    private JMenuItem RegistrarEmisión;
    private JMenuItem Calcular;
    private JMenuItem Informes;
    private JMenuItem Graficos;
    private JMenuItem MasInformacion;
    private JComboBox Institucion;
    private JComboBox anio;
    private JButton generarPlanDeAccion;
    private JTextArea introducciónLaHuellaDeTextArea;
    private JLabel Imagen;
    private JLabel Texto1;
    private JPanel PanelMain;
    private JScrollPane Contenedor;
    private JMenuBar bar;
    private JMenuItem Reducir;
    private JLabel titulo;
    private JButton inicioButton;

    public Reducir2() {
        setTitle("Reducir");
        setVisible(true);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setSize(1000, 600);
        setLocationRelativeTo(null);
        Font font = new Font("Arial", Font.PLAIN, 14);
        titulo.setFont(font);
        setContentPane(PanelMain);
        JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
        JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
        JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");
        Graficos.add(GraficoPrincipal);
        Graficos.add(GraficosCompararInstitucion);
        Graficos.add(GraficoHistorico);


        RegistrarInstitucion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new RegistrarInstitucion();
                setVisible(false);}
        });
        RegistrarInstitucion.setVisible(true);


        RegistrarEmisión.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Emision();
                setVisible(false);

            }
        });
        RegistrarEmisión.setVisible(true);
        Calcular.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Calcular();
                setVisible(false);
            }
        });
        Calcular.setVisible(true);
        MasInformacion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new MasInformacion();
                setVisible(false);
            }
        });
        MasInformacion.setVisible(true);
        Informes.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Informe();
                setVisible(false);
            }
        });
        Informes.setVisible(true);
        GraficosCompararInstitucion.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CompararOtrarInstituciones comIns = new CompararOtrarInstituciones();
                comIns.setVisible(true);
                dispose();
            }
        });

        GraficoHistorico.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                GraficoTendencia graf = new GraficoTendencia();
                graf.setVisible(true);
                dispose();
            }
        });

        GraficoPrincipal.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Vistas.Graficos graficos = new Graficos();
                graficos.setVisible(true);
                dispose();
            }
        });
        generarPlanDeAccion.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                Texto1.setVisible(true);
                if(Institucion.getSelectedItem()!="" && anio.getSelectedItem()!=""){
                    Imagen.setVisible(true);
                    introducciónLaHuellaDeTextArea.setLineWrap(true);
                    introducciónLaHuellaDeTextArea.setWrapStyleWord(true);
                    introducciónLaHuellaDeTextArea.setEditable(false);
                    Font font = new Font("Arial", Font.PLAIN, 14);
                    introducciónLaHuellaDeTextArea.setFont(font);
                    String informe = """
        Informe para Reducir la Huella de Carbono de una Institución Universitaria en Colombia

        Introducción
        La huella de carbono de una institución universitaria incluye las emisiones de gases de 
        efecto invernadero (GEI) generadas por sus actividades operativas. Este informe desglosa la huella de 
        carbono de una universidad en Colombia, clasificada por alcance, y propone una estrategia para mitigar estas 
        emisiones mediante la plantación de árboles.

        Huella de Carbono Total de la Institución Universitaria

        Para calcular la huella de carbono, se consideran las siguientes fuentes de emisiones:

        Alcance 1: Emisiones Directas
        Emisiones de vehículos universitarios.
        Emisiones de equipos de combustión en el campus.

        Alcance 2: Emisiones Indirectas por Consumo de Energía
        Emisiones de electricidad comprada y consumida en el campus.

        Alcance 3: Otras Emisiones Indirectas
        Emisiones de viajes de estudiantes y empleados.
        Emisiones de la gestión de residuos.
        Emisiones de proveedores y cadena de suministro.

        Ejemplo de Emisiones por Alcance

        Supongamos una universidad con las siguientes emisiones anuales aproximadas:

        Alcance 1: 2,000 toneladas de CO₂.
        Alcance 2: 3,000 toneladas de CO₂.
        Alcance 3: 5,000 toneladas de CO₂.

        Total: 10,000 toneladas de CO₂ al año.

        Plantación de Árboles para Mitigar la Huella de Carbono

        Dado que un árbol promedio puede absorber aproximadamente 22 kg de CO₂ al año, se puede calcular 
        la cantidad de árboles necesarios para neutralizar las emisiones.

        Cálculo de la cantidad de árboles a plantar

        Número de árboles = Emisiones Totales (toneladas de CO₂) / Absorción de CO₂ por árbol (kg)

        1 tonelada = 1000 kg, por lo tanto:

        Número de árboles = (10,000 toneladas de CO₂ * 1000 kg) / 22 kg/árbol/año

        Número de árboles = 454,545

        Para compensar las emisiones de carbono de la universidad, se necesitarían aproximadamente 454,545 árboles.

        Conclusiones y Recomendaciones

        Reducción en la Fuente:
        Implementar programas de eficiencia energética en edificios y equipos.
        Promover el uso de vehículos eléctricos o híbridos para el transporte universitario.

        Eficiencia Energética:
        Mejorar la eficiencia energética mediante la modernización de sistemas de iluminación y climatización.
        Fomentar el uso de energías renovables, como paneles solares, en el campus.

        Movilidad Sostenible:
        Promover el uso de transporte público y bicicletas entre estudiantes y empleados.
        Implementar programas de carpooling y teletrabajo para reducir desplazamientos.

        Gestión de Residuos:
        Mejorar los programas de reciclaje y compostaje en el campus.
        Reducir el uso de plásticos de un solo uso y fomentar materiales sostenibles.

        Reforestación y Conservación:
        Desarrollar proyectos de reforestación en colaboración con comunidades locales.
        Implementar programas educativos sobre la importancia de la conservación del medio ambiente.

        Implementar estas estrategias ayudará a la universidad no solo a reducir su huella de carbono, 
        sino también a promover una cultura de sostenibilidad y responsabilidad ambiental entre sus estudiantes y empleados.
        """;
                    introducciónLaHuellaDeTextArea.setText(informe);
                    introducciónLaHuellaDeTextArea.setVisible(true);
                    Contenedor.setVisible(true);
                }else{
                    JOptionPane.showMessageDialog(PanelMain, "llene todos los campos");
                }

            }
        });
        Imagen.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                if(Institucion.getSelectedItem()!="" && anio.getSelectedItem()!="" && !introducciónLaHuellaDeTextArea.getText().isEmpty()){
                    JOptionPane.showMessageDialog(PanelMain,"Se inicio la descarga del archivo pdf");
                }else {
                    JOptionPane.showMessageDialog(PanelMain, "Llene todos los campos");
                }

            }
        });
        inicioButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                new Inicio();
                setVisible(false);
            }
        });
        Inicio.getWindows();
    }
    public static void  main(String[]args ){

        new Reducir2();


    }
}
