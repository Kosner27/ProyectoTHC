package Controlador;

import Modelo.Conexion;
import Modelo.Consultas.*;
import Modelo.modelo.*;
import Vistas.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.event.ActionEvent;
import java.util.List;

public class ControladorVerInstituciones {
    public ConsultasInstitucion consultasInstitucion;
    public ConsultaNucleo consultaNucleo;
    public ModeloInstitucion modeloInstitucion;
    public VerInstituciones viewVerInstitucion;
    public ModeloUsuario user;
    public ModeloMunicipio modeloMunicipio;
    public Conexion conn = new Conexion();

    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");

    public ControladorVerInstituciones(ConsultasInstitucion consultasInstitucion, ConsultaNucleo consultaNucleo,
                                       ModeloInstitucion modeloInstitucion,
                                       VerInstituciones viewVerInstitucion, ModeloUsuario user, ModeloMunicipio modeloMunicipio) {
        this.consultasInstitucion = consultasInstitucion;
        this.consultaNucleo = consultaNucleo;
        this.modeloInstitucion = modeloInstitucion;
        this.viewVerInstitucion = viewVerInstitucion;
        this.modeloMunicipio = modeloMunicipio;
        this.user = user;
        listeners();

    }

    public void iniciar() {
        switch (user.getTipoUsuario()){
            case "Superadmin":
                viewVerInstitucion.setTitle("Ver Instituciones");
                viewVerInstitucion.setLocationRelativeTo(null);
                deshabilitarEdicionTabla();
                CargarInstituciones();
                CamposInhabilitados();
                viewVerInstitucion.Graficos.add(GraficoPrincipal);
                viewVerInstitucion.Graficos.add(GraficosCompararInstitucion);
                viewVerInstitucion.Graficos.add(GraficoHistorico);
                GraficoPrincipal.addActionListener(e -> vistaGraficoPrincipal());
                GraficoHistorico.addActionListener(e -> vistaGraficoHistorico());
                GraficosCompararInstitucion.addActionListener(e -> vistaCompararInstituciones());
                viewVerInstitucion.limpiarCamposButton.setEnabled(false);
                viewVerInstitucion.nombreInstitucion.setFocusable(true);
                viewVerInstitucion.actualizarTablaButton.setEnabled(false);
                viewVerInstitucion.actualizarTablaButton.addActionListener(this::actionPerformed);
                break;
            default:
                JOptionPane.showMessageDialog(viewVerInstitucion,"USUARIO SIN EL NIVEL DE PRIVILEGIO REQUERIDO");
                break;
        }



    }

    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == viewVerInstitucion.buscarPorNombreButton) {
            BuscarPorNit();
        }
        if (e.getSource() == viewVerInstitucion.limpiarCamposButton) {
            limpiar();
            CargarInstituciones();
            CamposInhabilitados();
            viewVerInstitucion.nombreInstitucion.setFocusable(true);
            viewVerInstitucion.nombreInstitucion.setEnabled(true);

        }
        if (e.getSource() == viewVerInstitucion.editarButton) {
            CamposHabilitados();
        }
        if (e.getSource() == viewVerInstitucion.guardarButton) {
            guardar();
            viewVerInstitucion.limpiarCamposButton.setEnabled(true);

        }
        if(e.getSource()==viewVerInstitucion.insertarButton){
            registrasInstitucion();


        }
        if(e.getSource()== viewVerInstitucion.eliminarButton){
            eliminar();
        }
        if(e.getSource() == viewVerInstitucion.registrarNucleoButton){
            registrarNucleo();

        }
        if(e.getSource() == viewVerInstitucion.actualizarTablaButton){
            ActulizarTabla();

        }
    }

    public void vistaGraficoPrincipal() {
        Conexion con = new Conexion();
        GraficoConsulta consul = new GraficoConsulta(con);
        Vistas.Graficos viewGraf = new Graficos();
        GraficorModeloInstitucion mod = new GraficorModeloInstitucion();
        ModeloInstitucion modelo = new ModeloInstitucion();
        ControladorGrafico contro = new ControladorGrafico(mod, consul, viewGraf, modelo, modeloMunicipio, user, modeloInstitucion);
        contro.iniciar();
        viewGraf.setVisible(true);
        viewVerInstitucion.dispose();
    }

    public void vistaGraficoHistorico() {
        Conexion con = new Conexion();
        ModeloTendencia mod = new ModeloTendencia();
        ConsultasTendencias consult = new ConsultasTendencias(con);
        GraficoTendencia viewGraf = new GraficoTendencia();
        ControladorTendencia control = new ControladorTendencia(mod, consult, viewGraf, modeloMunicipio, modeloInstitucion, user);
        viewGraf.setVisible(true);
        control.iniciar();
        viewVerInstitucion.dispose();
    }

    public void vistaCompararInstituciones() {
        CompararOtrarInstituciones comIns = new CompararOtrarInstituciones();
        GraficoComparar viewGraf = new GraficoComparar();
        Conexion conn = new Conexion();
        GraficoCompararConsultas consultas = new GraficoCompararConsultas(conn);
        ModeloGraficoComparar mod = new ModeloGraficoComparar();
        ControladorCompararInstitucion contro = new ControladorCompararInstitucion(mod, consultas, comIns, viewGraf, modeloInstitucion, modeloMunicipio, user);
        contro.iniciar();
        viewVerInstitucion.dispose();
    }

    // Deshabilitar la edición de celdas y selección de filas
    private void deshabilitarEdicionTabla() {
        DefaultTableModel tableModel = (DefaultTableModel) viewVerInstitucion.instituciontbl.getModel();

        // Deshabilitar la edición en la tabla
        viewVerInstitucion.instituciontbl.setDefaultEditor(Object.class, null);

        // Deshabilitar la selección de celdas
        viewVerInstitucion.instituciontbl.setCellSelectionEnabled(false);

        // Deshabilitar la selección de filas para que no se puedan editar las celdas
        viewVerInstitucion.instituciontbl.setRowSelectionAllowed(false);
        viewVerInstitucion.instituciontbl.setColumnSelectionAllowed(false);

        // Opcional: cambiar el color de fondo de la tabla para hacerla visualmente "bloqueada"
        viewVerInstitucion.instituciontbl.setBackground(new java.awt.Color(240, 240, 240));
    }

    // Cargar las instituciones en la tabla
    private void CargarInstituciones() {
        DefaultTableModel tableModel;
        tableModel = new DefaultTableModel() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;  // Asegurar que las celdas no sean editables
            }
        };
        tableModel.setRowCount(0);
        tableModel = (DefaultTableModel) viewVerInstitucion.instituciontbl.getModel();
        List<ModeloInstitucion> datos = consultasInstitucion.LlenarTablas();

        for (ModeloInstitucion dato : datos) {
            Object[] rowData = {
                    dato.getNombreInstitucion(),
                    dato.getNit(),
                    dato.getDepartamento(),
                    dato.getMunicipio(),
                    dato.getNucleo(),
                    dato.getHectareas(),
                    dato.getHectareasNucleo()
            };
            tableModel.addRow(rowData);
        }

        // Establecer el tamaño de las columnas
        viewVerInstitucion.instituciontbl.getColumnModel().getColumn(0).setPreferredWidth(150);
        viewVerInstitucion.instituciontbl.getColumnModel().getColumn(1).setPreferredWidth(150);
        viewVerInstitucion.instituciontbl.getColumnModel().getColumn(2).setPreferredWidth(150);
        viewVerInstitucion.instituciontbl.getColumnModel().getColumn(3).setPreferredWidth(150);
        viewVerInstitucion.instituciontbl.getColumnModel().getColumn(4).setPreferredWidth(150);
        viewVerInstitucion.instituciontbl.getColumnModel().getColumn(5).setPreferredWidth(150);
        viewVerInstitucion.instituciontbl.repaint();
        viewVerInstitucion.instituciontbl.setModel(tableModel);
        viewVerInstitucion.instituciontbl.setVisible(true);
        viewVerInstitucion.Institucion.setVisible(true);
    }



    // Métodos para buscar, guardar, limpiar y actualizar datos

    /**
     * Realiza la búsqueda de instituciones por nombre (NIT).
     */
    public void BuscarPorNit() {
        // Obtener el modelo actual de la tabla
        DefaultTableModel tableModel = (DefaultTableModel) viewVerInstitucion.instituciontbl.getModel();

        // Limpiar cualquier dato existente en la tabla
        tableModel.setRowCount(0);

        // Obtener el NIT ingresado por el modeloUsuario
        String nombreBuscado = viewVerInstitucion.nombreInstitucion.getText().trim().toUpperCase();

        if (nombreBuscado.isEmpty()) {
            JOptionPane.showMessageDialog(viewVerInstitucion, "Por favor ingrese un nombre de la institucion.");
            return;
        }

        // Buscar instituciones con el NIT proporcionado
        List<ModeloInstitucion> institucionesEncontradas = consultasInstitucion.BuscarPorNIT(nombreBuscado);

        if (institucionesEncontradas.isEmpty()) {
            JOptionPane.showMessageDialog(viewVerInstitucion, "No se encontraron instituciones con el nombre  proporcionado.");
        } else {
            // Agregar los resultados encontrados al modelo de la tabla
            for (ModeloInstitucion institucion : institucionesEncontradas) {
                Object[] rowData = {
                        institucion.getNombreInstitucion(),
                        institucion.getNit(),
                        institucion.getDepartamento(),
                        institucion.getMunicipio(),
                        institucion.getNucleo(),
                        institucion.getHectareas(),
                        institucion.getHectareasNucleo()
                };
                tableModel.addRow(rowData);
            }
        }

        // Refrescar la tabla para mostrar los datos nuevos
        viewVerInstitucion.instituciontbl.repaint();
        viewVerInstitucion.instituciontbl.setModel(tableModel);
        viewVerInstitucion.instituciontbl.setVisible(true);
        viewVerInstitucion.Institucion.setVisible(true);
        viewVerInstitucion.limpiarCamposButton.setEnabled(true);
    }


    /**
     * Deshabilita los campos de edición de los datos de la institución.
     */
    private void CamposInhabilitados() {
        viewVerInstitucion.campus.setEditable(false);
        viewVerInstitucion.departamento.setEditable(false);
        viewVerInstitucion.municipio.setEditable(false);
        viewVerInstitucion.nit.setEditable(false);
        viewVerInstitucion.hectareas.setEditable(false);
    }

    private void CamposHabilitados() {
        viewVerInstitucion.campus.setEditable(true);
        viewVerInstitucion.departamento.setEditable(false);
        viewVerInstitucion.municipio.setEditable(false);
        //viewVerInstitucion.nombreInstitucion.setEditable(false);
        viewVerInstitucion.hectareas.setEditable(true);
        viewVerInstitucion.nit.setEditable(true);
    }

    private void limpiar() {
        viewVerInstitucion.campus.setText(" ");
        viewVerInstitucion.departamento.setText(" ");
        viewVerInstitucion.municipio.setText(" ");
        viewVerInstitucion.nombreInstitucion.setText(" ");
        viewVerInstitucion.hectareas.setText(" ");
        viewVerInstitucion.nit.setText(" ");
        DefaultTableModel tableModel = (DefaultTableModel) viewVerInstitucion.instituciontbl.getModel();

        // Limpiar cualquier dato existente en la tabla
        tableModel.setRowCount(0);
    }

    private void llenarFormularioDesdeTabla() {
        // Obtener la fila seleccionada
        int selectedRow = viewVerInstitucion.instituciontbl.getSelectedRow();

        if (selectedRow != -1) { // Verifica que se haya seleccionado una fila
            // Obtener el modelo de la tabla
            DefaultTableModel tableModel = (DefaultTableModel) viewVerInstitucion.instituciontbl.getModel();

            // Obtener los valores de la fila seleccionada
            String nombreInstitucion = tableModel.getValueAt(selectedRow, 0).toString();
            String nit = tableModel.getValueAt(selectedRow, 1).toString();
            String departamento = tableModel.getValueAt(selectedRow, 2).toString();
            String municipio = tableModel.getValueAt(selectedRow, 3).toString();
            String campues = tableModel.getValueAt(selectedRow, 4).toString();
            String hectareas = tableModel.getValueAt(selectedRow, 5).toString();

            // Llenar los campos del formulario con los valores obtenidos
            viewVerInstitucion.nombreInstitucion.setText(nombreInstitucion);
            viewVerInstitucion.nit.setText(nit);
            viewVerInstitucion.departamento.setText(departamento);
            viewVerInstitucion.municipio.setText(municipio);
            viewVerInstitucion.campus.setText(campues);
            viewVerInstitucion.hectareas.setText(hectareas);
        }
    }

    private void listeners() {
        this.viewVerInstitucion.buscarPorNombreButton.addActionListener(this::actionPerformed);
        this.viewVerInstitucion.instituciontbl.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        // Agregar un MouseListener para manejar el doble clic en la tabla
        viewVerInstitucion.instituciontbl.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (evt.getClickCount() == 1) { // Verifica que sea un doble clic
                    llenarFormularioDesdeTabla();
                }
            }
        });
        this.viewVerInstitucion.limpiarCamposButton.addActionListener(this::actionPerformed);
        this.viewVerInstitucion.editarButton.addActionListener(this::actionPerformed);
        this.viewVerInstitucion.guardarButton.addActionListener(this::actionPerformed);
        this.viewVerInstitucion.insertarButton.addActionListener(this::actionPerformed);
        this.viewVerInstitucion.eliminarButton.addActionListener(this::actionPerformed);
        this.viewVerInstitucion.registrarNucleoButton.addActionListener(this::actionPerformed);
    }
    // Métodos para guardar y eliminar

    /**
     * Guarda los cambios realizados en una institución.
     */
    private void guardar() {
        String nit = viewVerInstitucion.nit.getText();
        int hectareas = Integer.parseInt(viewVerInstitucion.hectareas.getText());
        String campus = viewVerInstitucion.campus.getText();
        String nombreInstitucion = viewVerInstitucion.nombreInstitucion.getText();
        String municipio = viewVerInstitucion.municipio.getText();
        if (!nit.isEmpty() && campus.isEmpty()) {
            if (consultasInstitucion.ActualizarInstitucion(nombreInstitucion, municipio, hectareas, nit)) {
                JOptionPane.showMessageDialog(viewVerInstitucion, "Institución " + nombreInstitucion + " Actualizada correctamente");

            } else {
                JOptionPane.showMessageDialog(viewVerInstitucion, "ERROR EN LA ACTUALIZACIÓN");
            }
        }
        if(!nit.isEmpty() && !campus.isEmpty()){
            if(consultasInstitucion.ActualizarInstitucionConNucleo(nombreInstitucion, municipio, hectareas, campus)
            && consultasInstitucion.ActualizarInstitucionNitNucleo(nombreInstitucion, municipio, nit)
            ){
                JOptionPane.showMessageDialog(viewVerInstitucion, "Institución " + nombreInstitucion + " Actualizada correctamente");

            }else{
                JOptionPane.showMessageDialog(viewVerInstitucion, "ERROR EN LA ACTUALIZACIÓN");
            }
        }
    }

    private void registrasInstitucion(){
        RegistrarInstitucion view = new RegistrarInstitucion();
        ControladorRegistrarInstitucion controladorRegistrarInstitucion = new ControladorRegistrarInstitucion(view, modeloInstitucion,consultasInstitucion, modeloMunicipio);
        controladorRegistrarInstitucion.inicio();
    }

    private void eliminar(){
        String nit = viewVerInstitucion.nit.getText();
        String campus = viewVerInstitucion.campus.getText();
        String nombreInstitucion = viewVerInstitucion.nombreInstitucion.getText();
        String municipio = viewVerInstitucion.municipio.getText();
        if(!nit.isEmpty()){
            if(campus.isEmpty()){
                if(consultasInstitucion.eliminarInstiucion(nombreInstitucion,municipio)){
                    JOptionPane.showMessageDialog(viewVerInstitucion, "Institución " + nombreInstitucion + " eliminada correctamente");
                }else{
                    JOptionPane.showMessageDialog(viewVerInstitucion, "ERROR EN LA CONSULTA ");
                }
            }if(!campus.isEmpty()){
                System.out.println(campus);
                    if(consultasInstitucion.eliminarNucleo(campus)){
                        JOptionPane.showMessageDialog(viewVerInstitucion, "El campus " + campus + " eliminada correctamente");

                    }
            }
        }else{
            JOptionPane.showMessageDialog(viewVerInstitucion.Main, "Por favor seleccione una institución y/0 nucleo antes de eliminar");
        }
    }

    private void registrarNucleo() {
        String nit = viewVerInstitucion.nit.getText();
        String nombreInstitucion = viewVerInstitucion.nombreInstitucion.getText();
        String municipio = viewVerInstitucion.municipio.getText();

        // Si nit está vacío, no hacemos nada más y mostramos el mensaje
        if (nit.isEmpty()) {
            JOptionPane.showMessageDialog(viewVerInstitucion.Main,
                    "Para registrar un campus y/o modeloNucleo debes seleccionar una institución primero");
            return; // Detenemos la ejecución aquí, sin crear la vista
        }

        // Si nit no está vacío, procedemos con la creación de la vista y el controlador
        NucleoView view = new NucleoView();
        ModeloNucleo modeloNucleo = new ModeloNucleo();
        ControladorNucleo controladorNucleo = new ControladorNucleo(view, consultaNucleo, this.modeloMunicipio, modeloInstitucion, modeloNucleo);

        // Establecemos los datos y mostramos la vista
        modeloInstitucion.setNombreInstitucion(nombreInstitucion);
        this.modeloMunicipio.setNombreM(municipio);
        controladorNucleo.establecerDatos(nombreInstitucion, municipio);
        controladorNucleo.inicio(); // Aquí se muestra la vista

        // Al finalizar, no se debería mostrar la vista si no se pasa la validación
    }

    private void ActulizarTabla(){
        DefaultTableModel tableModel = (DefaultTableModel) viewVerInstitucion.instituciontbl.getModel();

        // Limpiar cualquier dato existente en la tabla
        tableModel.setRowCount(0);

    }
}