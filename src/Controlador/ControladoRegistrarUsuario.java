package Controlador;

import Modelo.Conexion;
import Modelo.modelo.HASH;
import Modelo.modelo.Municipio;
import Modelo.modelo.Usuario;
import Vistas.Main;
import Vistas.RegistrarUsuario;
import Modelo.Consultas.ConsultaUsuario;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.sql.*;

/**
 * Controlador para la funcionalidad de registro de usuarios.
 */
public class ControladoRegistrarUsuario {
    private final Usuario mod; // Modelo de usuario
    private final RegistrarUsuario view; // Vista para registrar usuario
    private final ConsultaUsuario consul; // Consulta de usuarios

    /**
     * Constructor del controlador.
     * @param mod Modelo de usuario
     * @param view Vista para registrar usuario
     * @param consul Consultas relacionadas a usuarios
     */
    public ControladoRegistrarUsuario(Usuario mod, RegistrarUsuario view, ConsultaUsuario consul) {
        this.mod = mod;
        this.view = view;
        this.consul = consul;
        // Agrega listeners para los eventos de los componentes de la vista
        this.view.Registrar.addActionListener(this::actionPerformed);
        this.view.departamento.addActionListener(e -> cargarMunicipio());
        this.view.municipio.addActionListener(e -> cargarInstitucion());
        this.view.inicio.addActionListener(this::actionPerformed);

    }
    /**
     * Inicializa la vista y carga los datos necesarios.
     */
    public void iniciar() {
        view.setTitle("Registrar Institucion");
        view.setLocationRelativeTo(null);
        CargarDepartamento();
        cargarMunicipio();
        cargarInstitucion();
    }
    /**
     * Maneja los eventos de los componentes de la vista.
     * @param e Evento de acción
     */
    private void actionPerformed(ActionEvent e) {

        if (e.getSource() == view.Registrar) {

            registrarUsuario();
        }
        if (e.getSource() == view.inicio) {
          abrirInicio();
        }
    }
    /**
     * Abre la vista principal.
     */
    private void abrirInicio(){
        Main inicio = new Main();
        Usuario user = new Usuario();
        ControladorMain contro = new ControladorMain(inicio, user);
        contro.Iniciar();
        view.dispose();
    }

    /**
     * Limpia los campos de la vista.
     */
    private void Limpiar() {
        view.Nombre.setText(null);
        view.Apellido.setText(null);
        view.Correo.setText(null);
        view.Contrasena.setText(null);
        view.NewPass.setText(null);
        view.departamento.setSelectedIndex(0);
        view.municipio.setSelectedIndex(0);
        view.Institucion.setSelectedIndex(0);
    }
    /**
     * Carga los departamentos desde la base de datos en el JComboBox correspondiente.
     */
    private void CargarDepartamento() {
        try (Connection conn = new Conexion().getConection();
             Statement stmt = conn.createStatement()) {

            ResultSet rst = stmt.executeQuery("CALL SeleccionarDepartamento()");
            view.departamento.removeAllItems();
            view.departamento.addItem("");
            while (rst.next()) {
                view.departamento.addItem(rst.getString("NombreDepartamento"));
            }

            if (view.departamento.getItemCount() > 0) {
                view.departamento.setSelectedIndex(0);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Error al cargar departamentos: " + e.getMessage());
        }
    }

    /**
     * Carga los municipios de acuerdo al departamento seleccionado.
     */
    private void cargarMunicipio() {
        String departamentoSeleccionado = (String) view.departamento.getSelectedItem();
        if (departamentoSeleccionado == null || departamentoSeleccionado.isEmpty()) {
            return;
        }

        try (Connection conn = new Conexion().getConection();
             CallableStatement stmt = conn.prepareCall("CALL BuscarMunicipio(?)")) {

            stmt.setString(1, departamentoSeleccionado);
            ResultSet rs = stmt.executeQuery();
            view.municipio.removeAllItems();
            view.municipio.addItem("");

            while (rs.next()) {
                view.municipio.addItem(rs.getString("NombreMunicipio"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Error al cargar municipios: " + e.getMessage());
        }
    }
    /**
     * Carga las instituciones de acuerdo al municipio seleccionado.
     */
    private void cargarInstitucion() {
        String municipioSeleccionado = (String) view.municipio.getSelectedItem();
        if (municipioSeleccionado == null || municipioSeleccionado.isEmpty()) {
            return;
        }

        try (Connection conn = new Conexion().getConection();
             CallableStatement stmt = conn.prepareCall("CALL BuscarInstitucion2(?)")) {

            stmt.setString(1, municipioSeleccionado);
            ResultSet rs = stmt.executeQuery();
            view.Institucion.removeAllItems();
            view.Institucion.addItem(" ");

            while (rs.next()) {
                view.Institucion.addItem(rs.getString("NombreInstitucion"));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Error al cargar instituciones: " + e.getMessage());
        }
    }
    /**
     * Registra un nuevo usuario en la base de datos.
     */
    private void registrarUsuario() {
        String nombre = view.Nombre.getText();
        String apellido = view.Apellido.getText();
        String correo = view.Correo.getText();
        String contrasena = new String(view.Contrasena.getPassword());
        String nuevaContrasena = new String(view.NewPass.getPassword());
        String departamento = view.departamento.getSelectedItem().toString();
        String municipio = view.municipio.getSelectedItem().toString();
        String institucion = view.Institucion.getSelectedItem().toString();
        String rol = "Administrador";

        if (validarCampos(nombre, apellido, correo, departamento, municipio, institucion, contrasena, nuevaContrasena)) {
            Municipio m = new Municipio();
            m.setNombreM(municipio);
            mod.setCorreo(correo);
            if (contrasena.equals(nuevaContrasena) && consul.ExisteUsuario(correo) == 0) {
                if (consul.esEmail(mod.getCorreo())) {
                    String contrasenaCifrada = HASH.sha1(contrasena);
                    mod.setNombre(nombre);
                    mod.setApellido(apellido);
                    mod.setCorreo(correo);
                    mod.setContrasena(contrasenaCifrada);
                    mod.setIdInstitucion(institucion);
                    mod.setTipoUsuario(rol);

                    if (consul.insertarUsuario(m, mod)) {
                        JOptionPane.showMessageDialog(null, "Usuario registrado: " + nombre);
                        Limpiar();
                    } else {
                        JOptionPane.showMessageDialog(null, "Error al registrar usuario");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "El correo no tiene un formato válido");
                }
            } else {
                JOptionPane.showMessageDialog(null, "El correo ya ha sido registrado o las contraseñas no coinciden");
            }
        } else {
            JOptionPane.showMessageDialog(null, "Complete todos los campos");
        }
    }
    /**
     * Validar que todos los campos requeridos estén completos.
     * @return true si todos los campos son válidos, false de lo contrario.
     */
    private boolean validarCampos(String nombre, String apellido, String correo, String departamento, String municipio, String institucion, String contrasena, String nuevaContrasena) {
        return !nombre.isEmpty() && !apellido.isEmpty() && !correo.isEmpty() &&
                !departamento.isEmpty() && !municipio.isEmpty() &&
                !institucion.isEmpty() && !contrasena.isEmpty()
                && !nuevaContrasena.isEmpty();
    }

}
