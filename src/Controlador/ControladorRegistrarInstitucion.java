package Controlador;

import Modelo.Conexion;
import Modelo.Consultas.ConsultasInstitucion;
import Modelo.modelo.ModeloInstitucion;
import Modelo.modelo.ModeloMunicipio;
import Vistas.RegistrarInstitucion;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.*;

/**
 * El controlador para la vista de registro de institución, maneja las acciones y la interacción
 * entre la vista, el modelo y las consultas relacionadas con la base de datos.
 * Este controlador se encarga de manejar el registro de nuevas instituciones en el sistema,
 * permitiendo a los usuarios ingresar información relevante como el nombre de la institución,
 * el NIT, el departamento, el modeloMunicipio y las hectáreas. También se encarga de gestionar la
 * carga de departamentos y municipios desde la base de datos.
 */
public class ControladorRegistrarInstitucion {

    // Atributos
    private final RegistrarInstitucion registrarInstitucion;  // Vista para registrar institución
    private final ModeloInstitucion modeloInstitucion;  // Modelo de institución
    private final ConsultasInstitucion consultasInstitucion;  // Consultas para instituciones en la base de datos
    private final ModeloMunicipio modeloMunicipio1;  // Modelo de modeloMunicipio

    /**
     * Constructor de la clase ControladorRegistrarInstitucion.
     *
     * @param registrarInstitucion Vista para registrar institución
     * @param modeloInstitucion Modelo que maneja los datos de la institución
     * @param consultasInstitucion Consultas relacionadas con la institución
     * @param modeloMunicipio1 Modelo de modeloMunicipio
     */
    public ControladorRegistrarInstitucion(RegistrarInstitucion registrarInstitucion,
                                           ModeloInstitucion modeloInstitucion,
                                           ConsultasInstitucion consultasInstitucion,
                                           ModeloMunicipio modeloMunicipio1) {
        this.registrarInstitucion = registrarInstitucion;
        this.modeloInstitucion = modeloInstitucion;
        this.consultasInstitucion = consultasInstitucion;
        this.modeloMunicipio1 = modeloMunicipio1;
        listener();  // Registra los listeners para los eventos
    }

    /**
     * Inicializa la vista de registro de institución, establece el título de la ventana y
     * carga los departamentos y municipios para que el modeloUsuario los seleccione.
     */
    public void inicio() {
        registrarInstitucion.setTitle("Registrar Institucion");
        registrarInstitucion.setLocationRelativeTo(null);  // Centra la ventana
        CargarDepartamento();  // Carga los departamentos
        cargarMunicipio();  // Carga los municipios según el departamento
    }

    /**
     * Maneja las acciones realizadas por el modeloUsuario en la interfaz gráfica.
     *
     * @param e Evento generado por la acción del modeloUsuario.
     */
    private void actionPerformed(ActionEvent e) {
        if (e.getSource() == registrarInstitucion.guardarButton) {
            guardar();  // Llama al método para guardar la institución
        }
        if (e.getSource() == registrarInstitucion.cancelarButton) {
            registrarInstitucion.dispose();  // Cierra la ventana
        }
    }

    /**
     * Registra los listeners de los botones y componentes en la vista.
     */
    private void listener() {
        registrarInstitucion.guardarButton.addActionListener(this::actionPerformed);
        registrarInstitucion.departamento.addActionListener(_ -> cargarMunicipio());  // Recarga los municipios cuando cambia el departamento
        registrarInstitucion.cancelarButton.addActionListener(this::actionPerformed);
    }

    /**
     * Guarda la nueva institución en la base de datos si los campos obligatorios están llenos.
     * Muestra un mensaje de éxito o error al modeloUsuario.
     */
    private void guardar() {
        String nombreInst = registrarInstitucion.nombreInstitucion.getText();  // Obtiene el nombre de la institución
        String departamento = String.valueOf(registrarInstitucion.departamento.getSelectedItem());  // Obtiene el departamento seleccionado
        String municipio = String.valueOf(registrarInstitucion.municipio.getSelectedItem());  // Obtiene el modeloMunicipio seleccionado
        String nit = registrarInstitucion.nit.getText();  // Obtiene el NIT de la institución
        int hectareas = Integer.parseInt(registrarInstitucion.hectareas.getText());  // Obtiene las hectáreas
        // Asigna los valores al modelo de institución y modeloMunicipio
        modeloInstitucion.setNombreInstitucion(nombreInst);
        modeloInstitucion.setNit(nit);
        modeloInstitucion.setHectareas(hectareas);
        modeloMunicipio1.setNombreM(municipio);


        // Valida que los campos obligatorios no estén vacíos
        if (!nombreInst.isEmpty() && !municipio.isEmpty() && !nit.isEmpty() && !departamento.isEmpty()) {
            // Intenta insertar la institución en la base de datos
            if (consultasInstitucion.InsertarInstitucion(modeloInstitucion, modeloMunicipio1,departamento)) {
                JOptionPane.showMessageDialog(registrarInstitucion,
                        "La Insitución " + nombreInst + " con NIT " + nit + " \nUbicada en el departamento de " + departamento +
                                " En el municipio de " + municipio + " \n ha sido registrada de manera exitosa");
            } else {
                JOptionPane.showMessageDialog(registrarInstitucion, "Error en la consulta");
            }
        } else {
            JOptionPane.showMessageDialog(registrarInstitucion,
                    "POR FAVOR LLENAR COMO MINIMO LOS CAMPOS DE NIT, \nNOMBRE INSTITUCIÓN y SELECCIONAR EL DEPARTAMENTO Y EL MUNICIPIO");
        }
    }

    /**
     * Carga los departamentos desde la base de datos y los agrega al JComboBox correspondiente en la vista.
     */
    private void CargarDepartamento() {
        try (Connection conn = new Conexion().getConection();  // Conexión a la base de datos
             Statement stmt = conn.createStatement()) {

            // Ejecuta la consulta para obtener los departamentos
            ResultSet rst = stmt.executeQuery("CALL SeleccionarDepartamento()");
            registrarInstitucion.departamento.removeAllItems();  // Limpia el JComboBox de departamentos
            registrarInstitucion.departamento.addItem("");  // Agrega un ítem vacío para indicar que no se ha seleccionado nada

            // Agrega los departamentos al JComboBox
            while (rst.next()) {
                registrarInstitucion.departamento.addItem(rst.getString("NombreDepartamento"));
            }

            // Si hay departamentos cargados, selecciona el primero por defecto
            if (registrarInstitucion.departamento.getItemCount() > 0) {
                registrarInstitucion.departamento.setSelectedIndex(0);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar departamentos: " + e.getMessage());
        }
    }

    /**
     * Carga los municipios correspondientes al departamento seleccionado en el JComboBox de municipios.
     */
    private void cargarMunicipio() {
        String departamentoSeleccionado = (String) registrarInstitucion.departamento.getSelectedItem();
        if (departamentoSeleccionado == null || departamentoSeleccionado.isEmpty()) {
            return;  // Si no se ha seleccionado un departamento, no se cargan municipios
        }

        try (Connection conn = new Conexion().getConection();  // Conexión a la base de datos
             CallableStatement stmt = conn.prepareCall("CALL BuscarMunicipio(?)")) {

            stmt.setString(1, departamentoSeleccionado);  // Establece el departamento como parámetro para la consulta
            ResultSet rs = stmt.executeQuery();  // Ejecuta la consulta

            // Limpia el JComboBox de municipios y agrega un ítem vacío
            registrarInstitucion.municipio.removeAllItems();
            registrarInstitucion.municipio.addItem("");

            // Agrega los municipios al JComboBox
            while (rs.next()) {
                registrarInstitucion.municipio.addItem(rs.getString("NombreMunicipio"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error al cargar municipios: " + e.getMessage());
        }
    }
}
