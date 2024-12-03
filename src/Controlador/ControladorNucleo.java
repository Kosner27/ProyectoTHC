package Controlador;

import Modelo.Consultas.ConsultaNucleo;
import Modelo.modelo.ModeloInstitucion;
import Modelo.modelo.ModeloMunicipio;
import Modelo.modelo.ModeloNucleo;
import Vistas.NucleoView;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.event.ActionEvent;

/**
 * Controlador para gestionar la vista de registro de núcleo.
 * Este controlador permite realizar el registro de un núcleo y valida la existencia de registros duplicados.
 */
public class ControladorNucleo {
    private final NucleoView view;            // Vista de registro de núcleo
    private final ModeloNucleo modeloNucleo;              // Modelo de núcleo
    private final ConsultaNucleo consultaNucleo; // Consultas relacionadas con el núcleo
    private final ModeloMunicipio modeloMunicipio;                // Modelo de modeloMunicipio
    private final ModeloInstitucion modeloInstitucion; // Modelo de institución

    /**
     * Constructor del controlador que inicializa la vista y los modelos.
     * Configura los listeners para los botones y un filtro de documentos para los campos de texto.
     * @param view Vista de registro de núcleo.
     * @param consultaNucleo Consulta para interactuar con la base de datos de núcleos.
     * @param modeloMunicipio Modelo de modeloMunicipio.
     * @param modeloInstitucion Modelo de institución.
     * @param modeloNucleo Modelo de núcleo.
     */
    public ControladorNucleo(NucleoView view, ConsultaNucleo consultaNucleo,
                             ModeloMunicipio modeloMunicipio, ModeloInstitucion modeloInstitucion, ModeloNucleo modeloNucleo) {
        this.view = view;
        this.modeloNucleo = modeloNucleo;
        this.consultaNucleo = consultaNucleo;
        this.modeloMunicipio = modeloMunicipio;
        this.modeloInstitucion = modeloInstitucion;
        listeners();  // Configura los listeners de los botones
        configurarDocumentFilter();  // Configura el filtro para los campos de texto
    }

    /**
     * Configura un filtro que convierte el texto ingresado en mayúsculas antes de insertarlo o reemplazarlo.
     */
    private void configurarDocumentFilter() {
        ((AbstractDocument) view.nucleotxt.getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) throws BadLocationException {
                // Convierte a mayúsculas antes de insertar
                if (string != null) {
                    super.insertString(fb, offset, string.toUpperCase(), attr);
                } else {
                    super.insertString(fb, offset, null, attr);
                }
            }

            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) throws BadLocationException {
                // Convierte a mayúsculas antes de reemplazar
                super.replace(fb, offset, length, text != null ? text.toUpperCase() : null, attrs);
            }

            @Override
            public void remove(FilterBypass fb, int offset, int length) throws BadLocationException {
                // Solo llama al método remove
                super.remove(fb, offset, length);
            }
        });
    }

    /**
     * Inicia la vista de registro de núcleo y configura los campos de texto con los datos de la institución y modeloMunicipio.
     */
    public void inicio() {
        view.setTitle("Registrar ModeloNucleo");
        view.setLocationRelativeTo(null);
        view.sede.setText(modeloMunicipio.getNombreM());  // Establece el nombre del modeloMunicipio en la vista
        view.nombreInstitucion.setText(modeloInstitucion.getNombreInstitucion());  // Establece el nombre de la institución
        view.setVisible(true);  // Hace visible la vista
    }

    /**
     * Maneja los eventos generados por los botones de la vista de registro de núcleo.
     * @param e Evento generado por la interacción del modeloUsuario.
     */
    private void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.guardarButton) {
            guardarButton();  // Llama al método para guardar el núcleo
        }
        if (e.getSource() == view.cancelarButton) {
            view.dispose();  // Cierra la vista de registro
        }
    }

    /**
     * Establece los datos de la institución y modeloMunicipio en los campos correspondientes de la vista.
     * @param nombreInstitucion Nombre de la institución.
     * @param nombreMunicipio Nombre del modeloMunicipio.
     */
    public void establecerDatos(String nombreInstitucion, String nombreMunicipio) {
        System.out.println(nombreInstitucion);  // Imprime el nombre de la institución en la consola
        view.nombreInstitucion.setText(nombreInstitucion);  // Establece el nombre de la institución en la vista
        view.nombreInstitucion.setEditable(false);  // Hace que el campo no sea editable
        view.sede.setText(nombreMunicipio);  // Establece el nombre del modeloMunicipio en la vista
        view.sede.setEditable(false);  // Hace que el campo no sea editable
    }

    /**
     * Guarda un nuevo núcleo en la base de datos si no existe previamente.
     * Valida que los campos no estén vacíos y que el núcleo no esté ya registrado.
     */
    private void guardarButton() {
        String nucleo1 = view.nucleotxt.getText();  // Obtiene el nombre del núcleo desde el campo de texto
        int hectares = Integer.parseInt(view.hectareas.getText());  // Obtiene el valor de hectáreas desde el campo de texto

        if (!nucleo1.isEmpty()) {
            modeloNucleo.setNombreNucleo(nucleo1);  // Asigna el nombre del núcleo al modelo
            modeloNucleo.setHectareas(hectares);  // Asigna el valor de hectáreas al modelo
            // Verifica si el núcleo ya existe en la base de datos
            if (consultaNucleo.ExisteNucleo(modeloNucleo) < 1) {
                // Si no existe, registra el núcleo
                if (consultaNucleo.RegistrarNucleo(modeloNucleo, modeloMunicipio, modeloInstitucion)) {
                    JOptionPane.showMessageDialog(view, "El modeloNucleo " + nucleo1 + " fue registrado correctamente");
                    view.dispose();  // Cierra la vista de registro de núcleo
                    System.out.println(modeloNucleo.getNombreNucleo());  // Imprime el nombre del núcleo en la consola
                } else {
                    JOptionPane.showMessageDialog(view, "ERROR");  // Muestra un mensaje de error
                }
            } else {
                JOptionPane.showMessageDialog(view, "EL modeloNucleo ya esta registrado en la base de datos");  // Muestra un mensaje si el núcleo ya existe
            }
        }
    }

    /**
     * Configura los listeners para los botones de la vista de registro de núcleo.
     */
    private void listeners() {
        this.view.guardarButton.addActionListener(this::actionPerformed);  // Asocia el botón guardar al evento
        this.view.cancelarButton.addActionListener(this::actionPerformed);  // Asocia el botón cancelar al evento
    }
}
