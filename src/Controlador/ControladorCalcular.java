package Controlador;

import Modelo.Conexion;
import Modelo.Consultas.*;
import Modelo.modelo.*;
import Vistas.*;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import java.util.Objects;

public class ControladorCalcular implements ActionListener {
    private final ModeloEmisionCalcular modeloCalcular;
    private final CalcularConsultas calcularConsultas;
    private final Calcular view;
    private final ModeloInstitucion modeloInstitucion;
    private final ModeloUsuario user;
    private final ModeloMunicipio modeloMunicipio;

    JMenuItem GraficosCompararInstitucion = new JMenuItem("comparar con otras instituciones");
    JMenuItem GraficoPrincipal = new JMenuItem("Ver graficos por alcance y fuente");
    JMenuItem GraficoHistorico = new JMenuItem("Ver grafico historico de la huella de carbono");

    public ControladorCalcular(ModeloEmisionCalcular modeloCalcular, CalcularConsultas calcularConsultas,
                               Calcular view, ModeloInstitucion modeloInstitucion, ConsultaUsuario consultaUsuario,
                               ModeloUsuario user, ModeloMunicipio modeloMunicipio) {
        this.modeloInstitucion = modeloInstitucion;
        this.calcularConsultas = calcularConsultas;
        this.view = view;
        this.modeloMunicipio = modeloMunicipio;
        this.user = user;
        this.modeloCalcular = modeloCalcular;

        inciarListeners();
    }


    /**
     * Método que inicializa los listeners para los botones y componentes de la vista.
     * Asocia eventos de acción a los botones y otros componentes interactivos.
     */
    private void inciarListeners() {
        // Asignar ActionListeners a los botones de la vista
        this.view.guardarCalculoButton.addActionListener(this); // Botón para guardar cálculo
        this.view.inicioButton.addActionListener(this); // Botón de inicio
        this.view.fuente.addActionListener(this::comboBoxActionPerformed); // ComboBox de fuentes
        this.view.perfil.addActionListener(this); // Botón para ver perfiles
        this.view.RegistrarEmision.addActionListener(this); // Botón para registrar emisión
        this.view.Informes.addActionListener(this); // Botón para ver informes
        this.view.Reducir.addActionListener(this); // Botón para reducir
        this.view.RegistrarInstitucion.addActionListener(this); // Botón para registrar institución
        this.view.actualizarButton.addActionListener(this); // Botón para actualizar datos
        this.view.VerPerfiles.addActionListener(this); // Botón para ver perfiles
        this.view.verInstitucion.addActionListener(this); // Botón para ver institución

        // Listener para el checkbox "siCheckBox", muestra y oculta ciertos elementos en la vista
        this.view.siCheckBox.addActionListener((ActionEvent _) -> {
            if (view.siCheckBox.isSelected()) {
                view.Registro.setVisible(true);
                view.siRegistro.setVisible(true);
                view.noRegistro.setVisible(true);
                view.noCalculo.setEnabled(false);
            }
            if (!view.siCheckBox.isSelected()) {
                view.Registro.setVisible(false);
                view.siRegistro.setVisible(false);
                view.noRegistro.setVisible(false);
                view.noCalculo.setEnabled(true);
            }
        });

        // Listener para el checkbox "siRegistro", muestra u oculta elementos adicionales según la selección
        this.view.siRegistro.addActionListener((ActionEvent _) -> {
            if (view.siRegistro.isSelected()) {
                view.noCalculo.setEnabled(false);
                view.hayNucleo.setVisible(true);
                view.comboNucleo.setVisible(true);
                view.noRegistro.setEnabled(false);
                view.nucleo.setVisible(false);
                view.Nucleo.setVisible(false);
                view.actualizarButton.setVisible(false);
            }
            if (!view.siRegistro.isSelected()) {
                view.noCalculo.setEnabled(true);
                view.hayNucleo.setVisible(false);
                view.comboNucleo.setVisible(false);
                view.noRegistro.setEnabled(true);
            }
        });

        // Listener para el checkbox "noRegistro", muestra u oculta la vista de núcleo
        this.view.noRegistro.addActionListener((ActionEvent _) -> {
            if (view.noRegistro.isSelected()) {
                view.hayNucleo.setVisible(false);
                view.comboNucleo.setVisible(false);
                view.siRegistro.setEnabled(false);
                view.noRegistro.setEnabled(true);
                view.nucleo.setVisible(true);
                view.Nucleo.setVisible(true);
                view.actualizarButton.setVisible(true);
                // Crea e inicializa el controlador de núcleo para el "Superadmin"
                Conexion conn = new Conexion();
                NucleoView nucleoView = new NucleoView();
                ModeloNucleo modeloNucleo = new ModeloNucleo();
                ConsultaNucleo consultaNucleo = new ConsultaNucleo(conn);
                if (user.getTipoUsuario().equals("Superadmin")) {
                    modeloMunicipio.setNombreM(Objects.requireNonNull(view.municipio.getSelectedItem()).toString());
                    modeloInstitucion.setNombreInstitucion(Objects.requireNonNull(view.comboInstitucion.getSelectedItem()).toString());
                    ControladorNucleo controladorNucleo = new ControladorNucleo(nucleoView, consultaNucleo, modeloMunicipio, modeloInstitucion, modeloNucleo);
                    controladorNucleo.establecerDatos(modeloInstitucion.getNombreInstitucion(), modeloMunicipio.getNombreM());
                    controladorNucleo.inicio();
                } else {
                    ControladorNucleo controladorNucleo = new ControladorNucleo(nucleoView, consultaNucleo, modeloMunicipio, modeloInstitucion, modeloNucleo);
                    controladorNucleo.establecerDatos(modeloInstitucion.getNombreInstitucion(), modeloMunicipio.getNombreM());
                    controladorNucleo.inicio();
                }
            }
            if (!view.noRegistro.isSelected()) {
                view.siRegistro.setEnabled(true);
                view.actualizarButton.setVisible(false);
                view.nucleo.setVisible(false);
                view.Nucleo.setVisible(false);
            }
        });

        // Listener para el checkbox "noCalculo", deshabilita o habilita el checkbox "siCheckBox"
        this.view.noCalculo.addActionListener(_ -> {
            if (view.noCalculo.isSelected()) {
                view.siCheckBox.setEnabled(false);
            }
            if (!view.noCalculo.isSelected()) {
                view.siCheckBox.setEnabled(true);
            }
        });

        // Listener para el combo "comboInstitucion", carga el combo de municipios cuando se cambia la institución
        this.view.comboInstitucion.addActionListener(_ -> {
            if (view.comboInstitucion.getItemCount() > 0) {
                view.municipio.removeAllItems(); // Limpiar el combo de modeloMunicipio
                llenarComoboMunicipio(); // Llenar el combo de modeloMunicipio con nuevos datos
                cargarComboNucleo();
            }
        });

        this.view.LImpiarButton.addActionListener(this);
    }

    /**
     * Método para inicializar la vista dependiendo del tipo de modeloUsuario.
     * Se ajustan los elementos visibles y las acciones de acuerdo al tipo de modeloUsuario (Administrador, Superadmin, Invitado).
     */
    public void iniciar() {
        switch (user.getTipoUsuario()) {
            case "Administrador":
                // Configuración específica para el modeloUsuario "Administrador"
                view.VerPerfiles.setVisible(false);
                view.Graficos.add(GraficoPrincipal);
                view.Graficos.add(GraficosCompararInstitucion);
                view.Graficos.add(GraficoHistorico);
                view.comboInstitucion.setVisible(false);
                view.municipio.setVisible(false);
                cargarNucleoExistentes();
                cargarInstitucion();
                cargarFuentesPorNombre();
                calcular();
                view.LImpiarButton.setVisible(false);
                view.sede.setText(modeloMunicipio.getNombreM());
                view.setTitle("Calcular Emision");
                view.setLocationRelativeTo(null);
                view.verInstitucion.setVisible(false);
                GraficoPrincipal.addActionListener(_ -> vistaGraficoPrincipal());
                GraficosCompararInstitucion.addActionListener(_ -> vistaCompararInstituciones());
                GraficoHistorico.addActionListener(_ -> vistaGraficoHistorico());
                break;
            case "Superadmin":
                // Configuración específica para el modeloUsuario "Superadmin"
                view.Graficos.add(GraficoPrincipal);
                view.Graficos.add(GraficosCompararInstitucion);
                view.Graficos.add(GraficoHistorico);
                view.Institucion.setVisible(false);
                view.sede.setVisible(false);
                llenarComboInstitution();
                cargarComboNucleo();
                cargarFuentesPorNombre();
                calcular();
                view.sede.setText(modeloMunicipio.getNombreM());
                view.setTitle("Calcular Emision");
                view.setLocationRelativeTo(null);
                GraficoPrincipal.addActionListener(_ -> vistaGraficoPrincipal());
                GraficosCompararInstitucion.addActionListener(_ -> vistaCompararInstituciones());
                GraficoHistorico.addActionListener(_ -> vistaGraficoHistorico());
                break;
            case "Invitado":
                view.dispose();
                break;
            default:
                // Mensaje de error si el tipo de modeloUsuario no está definido
                JOptionPane.showMessageDialog(null, "ModeloUsuario no definido en el sistema");
                break;
        }
    }

    /**
     * Método que actualiza el total de emisiones de acuerdo con los cambios en la tabla.
     * Se activa cuando se actualiza la columna de carga ambiental en la tabla de emisiones.
     */
    private void calcular() {
        // Añadir un TableModelListener para detectar cambios en la tabla de emisiones
        view.Emisiones.getModel().addTableModelListener(e -> {
            int columnaCargaAmbiental = 3; // Índice de la columna "Carga Ambiental"
            if (e.getColumn() == columnaCargaAmbiental && e.getType() == TableModelEvent.UPDATE) {
                // Si se ha actualizado la columna de carga ambiental, recalcular el total
                int fila = e.getFirstRow();
                String cargaAmbiental = view.Emisiones.getValueAt(fila, columnaCargaAmbiental).toString();
                // Actualizar la vista con el nuevo total
                double total = modeloCalcular.actualizarTotal((DefaultTableModel) view.Emisiones.getModel());
                view.total.setText(String.valueOf(total));

                System.out.println("Carga ambiental en fila " + fila + ": " + cargaAmbiental);
            }
        });
    }

    /**
     * Método que se ejecuta cuando se selecciona un elemento en el JComboBox de fuentes.
     * Se encarga de obtener y procesar las emisiones relacionadas con la fuente seleccionada,
     * y actualizar la tabla de emisiones en la vista.
     *
     * @param e El evento de acción generado por la interacción con el JComboBox.
     */
    private void comboBoxActionPerformed(ActionEvent e) {
        // Obtener el JComboBox que generó el evento
        JComboBox<String> comboBox = (JComboBox<String>) e.getSource();

        // Obtener el elemento seleccionado en el comboBox
        String selectedItem = (String) comboBox.getSelectedItem();

        // Verificar si el item seleccionado no es null ni vacío
        if (selectedItem != null && !selectedItem.isEmpty()) {
            // Obtener la fuente seleccionada en el comboBox de fuentes
            String fuenteSeleccionada = Objects.requireNonNull(view.fuente.getSelectedItem()).toString();

            // Llamar al modelo para obtener las emisiones procesadas según la fuente seleccionada
            List<ModeloEmision> emisiones = modeloCalcular.obtenerEmisionesProcesadas(fuenteSeleccionada, view.Emisiones);

            // Actualizar la tabla de emisiones con los datos obtenidos del modelo
            actualizarTablaEmisiones(emisiones);

            // Configurar la tabla de emisiones (puede incluir aspectos como el formato, orden, etc.)
            configurarTabla();
        }
    }


    /**
     * Actualiza la tabla de emisiones en la vista con la información proporcionada en la lista de emisiones.
     * Cada emisión se agrega como una nueva fila en la tabla, con los datos correspondientes.
     *
     * @param emisiones La lista de objetos {@link ModeloEmision} que contienen los datos de las emisiones
     *                  que se van a mostrar en la tabla.
     */
    private void actualizarTablaEmisiones(List<ModeloEmision> emisiones) {
        // Obtener el modelo de la tabla de emisiones desde la vista
        DefaultTableModel tableModel = (DefaultTableModel) view.Emisiones.getModel();

        // Recorrer la lista de emisiones y agregar una fila por cada emisión
        for (ModeloEmision emision : emisiones) {
            // Crear un arreglo con los datos de la emisión para la fila
            Object[] rowData = {
                    emision.getNombreFuente(),  // Nombre de la fuente
                    emision.getEstadoFuente(),  // Estado de la fuente
                    emision.getAlcance(),       // Alcance de la emisión
                    "",                         // Campo vacío para ser llenado más tarde o configurado
                    emision.getUnidadMedidad(), // Unidad de medida
                    emision.getFactorEmision()  // Factor de emisión
            };

            // Agregar la fila a la tabla
            tableModel.addRow(rowData);
        }

        // Deshabilitar el combo de fuente, ya que ya no se requiere cambiar la fuente
        view.fuente.setEnabled(false);

        // Hacer el combo de año no editable
        view.anio.setEditable(false);

        // Actualizar la tabla de emisiones en la vista con el modelo actualizado
        view.Emisiones.setModel(tableModel);

        // Hacer visibles tanto la tabla de emisiones como el contenedor que la contiene
        view.Emisiones.setVisible(true);
        view.Contenedor.setVisible(true);
    }

    /**
     * Configura la tabla de emisiones ajustando el ancho de las columnas.
     * El método establece el ancho preferido de cada columna de la tabla de emisiones a 150 píxeles,
     * siempre y cuando la tabla tenga más de 4 columnas.
     */
    private void configurarTabla() {
        // Ejecutar el código en el hilo de la interfaz gráfica (para evitar conflictos con el hilo principal)
        SwingUtilities.invokeLater(() -> {
            // Obtener el modelo de columnas de la tabla de emisiones
            TableColumnModel columnModel = view.Emisiones.getColumnModel();

            // Verificar si la tabla tiene más de 4 columnas
            if (columnModel.getColumnCount() > 4) {
                // Si la tabla tiene más de 4 columnas, se establece el ancho preferido de todas las columnas a 150 píxeles
                for (int i = 0; i < columnModel.getColumnCount(); i++) {
                    columnModel.getColumn(i).setPreferredWidth(150);
                }
            } else {
                // Si la tabla tiene 4 o menos columnas, se imprime un mensaje indicando que no tiene suficientes columnas
                System.out.println("La tabla no tiene suficientes columnas");
            }

            // Forzar un repintado de la tabla para reflejar los cambios visuales
            view.Emisiones.repaint();
        });
    }


    @Override
    /**
     * Maneja los eventos de acción generados por los botones y componentes de la vista.
     * Este método se ejecuta cuando un modeloUsuario interactúa con los botones y componentes de la interfaz gráfica.
     * En función del botón que se haya presionado, se ejecuta el método correspondiente para gestionar la acción.
     *
     * @param e El evento de acción que contiene información sobre el componente que generó el evento.
     */
    public void actionPerformed(ActionEvent e) {
        // Verificar si el evento proviene del botón 'guardarCalculoButton' y ejecutar el método correspondiente
        if (e.getSource() == view.guardarCalculoButton) {
            InsertarCalculo();
        }
        // Verificar si el evento proviene del botón 'inicioButton' y ejecutar el método correspondiente
        if (e.getSource() == view.inicioButton) {
            BotonInicio();
        }
        // Verificar si el evento proviene del componente 'perfil' y ejecutar el método correspondiente
        if (e.getSource() == view.perfil) {
            vistaPerfil();
        }
        // Verificar si el evento proviene del botón 'RegistrarEmision' y ejecutar el método correspondiente
        if (e.getSource() == view.RegistrarEmision) {
            vistaRegistrarEmision();
        }
        // Verificar si el evento proviene del botón 'Informes' y ejecutar el método correspondiente
        if (e.getSource() == view.Informes) {
            vistaInforme();
        }
        // Verificar si el evento proviene del botón 'Reducir' y ejecutar el método correspondiente
        if (e.getSource() == view.Reducir) {
            vistaReducir();
        }
        // Verificar si el evento proviene del botón 'RegistrarInstitucion' y ejecutar el método correspondiente
        if (e.getSource() == view.RegistrarInstitucion) {
            vistaActualizarInstitucion();
        }
        // Verificar si el evento proviene del botón 'VerPerfiles' y ejecutar el método correspondiente
        if (e.getSource() == view.VerPerfiles) {
            vistaVerPerfiles();
        }
        // Verificar si el evento proviene del botón 'actualizarButton' y ejecutar el método correspondiente
        if (e.getSource() == view.actualizarButton) {
            actualizarButton();
        }
        // Verificar si el evento proviene del botón 'verInstitucion' y ejecutar el método correspondiente
        if (e.getSource() == view.verInstitucion) {
            vistaVerInstitution();
        }
        if(e.getSource()== view.LImpiarButton){
            limpiar();
        }
    }


    /**
     * Carga el nombre de la institución en el componente correspondiente de la vista.
     * Obtiene el nombre de la institución y el modeloMunicipio desde los modelos correspondientes
     * y realiza una consulta para obtener el nombre de la institución en la base de datos.
     * Luego, muestra el nombre de la institución en el campo de texto de la vista y desactiva la edición.
     */
    private void cargarInstitucion() {
        // Obtener el nombre de la institución y el nombre del modeloMunicipio desde los modelos correspondientes
        String nombre = modeloInstitucion.getNombreInstitucion();
        String Nombremunicipio = modeloMunicipio.getNombreM();

        // Consultar el nombre de la institución en la base de datos
        String n = calcularConsultas.Institucion(nombre, Nombremunicipio);

        // Mostrar el nombre de la institución en el campo de texto y deshabilitar la edición
        view.Institucion.setText(n);
        view.Institucion.setEditable(false);
    }

    /**
     * Carga los nombres de las fuentes de emisión disponibles en la base de datos
     * en un JComboBox de la vista. La lista de fuentes se obtiene mediante una consulta
     * a la base de datos que selecciona los nombres de las fuentes de emisión, agrupadas
     * por el campo "NombreFuente". Los nombres de las fuentes se añaden al JComboBox
     * y se establece la selección por defecto del primer elemento, si es que hay elementos disponibles.
     */
    private void cargarFuentesPorNombre() {
        // Establecer la conexión a la base de datos
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();

        // Consulta SQL para obtener los nombres de las fuentes de emisión
        String sql = "select NombreFuente from emision group by NombreFuente";

        // Limpiar los elementos existentes en el JComboBox
        view.fuente.removeAllItems();

        // Verificar que la conexión a la base de datos es válida
        if (conn != null) {
            try {
                // Crear un Statement y ejecutar la consulta SQL
                Statement stmt = conn.createStatement();
                ResultSet rst = stmt.executeQuery(sql);

                // Limpiar los elementos del JComboBox antes de añadir los nuevos
                view.fuente.removeAllItems();
                view.fuente.addItem(""); // Añadir un elemento vacío como primer ítem

                // Iterar a través de los resultados de la consulta y añadir los nombres de las fuentes al JComboBox
                while (rst.next()) {
                    String nombre = rst.getString("NombreFuente");
                    view.fuente.addItem(nombre);
                }

                // Si el JComboBox contiene elementos, seleccionar el primero
                if (view.fuente.getItemCount() > 0) {
                    view.fuente.setSelectedIndex(0);
                } else {
                    // Manejar el caso en el que no hay elementos en el JComboBox
                    System.out.println("El JComboBox fuente está vacío");
                }

                // Cerrar los recursos de la consulta
                rst.close();
                stmt.close();
            } catch (SQLException e) {
                // Manejo de excepciones si ocurre un error durante la consulta
                e.printStackTrace();
            }
        }
    }


    /**
     * Inserta los cálculos de emisiones en la base de datos. Dependiendo del tipo de modeloUsuario (Superadmin o no),
     * el cálculo se guarda de diferentes maneras. Si el modeloUsuario es Superadmin, se pueden registrar los cálculos
     * para una institución y modeloMunicipio específicos, sin tener en cuenta un núcleo. Si se selecciona un núcleo,
     * se registra el cálculo para ese núcleo. El método también maneja el registro de emisiones para instituciones
     * sin núcleo o con núcleo, dependiendo de las selecciones en la vista.
     *
     * @throws NumberFormatException Si hay un error al intentar convertir los datos en el formato esperado.
     * @throws Exception             Si ocurre un error inesperado durante el proceso.
     */
    private void InsertarCalculo() {
        // Hacer que el campo de fuente sea editable nuevamente
        view.fuente.setEditable(true);
        System.out.println("Botón guardarCalculoButton presionado"); // Mensaje de depuración

        try {
            // Obtener los valores del año base y el nombre de la fuente desde la vista
            modeloCalcular.setAnioBase(Integer.parseInt(view.anio.getText()));
            modeloCalcular.setNombreFuente(Objects.requireNonNull(view.fuente.getSelectedItem()).toString());

            // Obtener el modelo de la tabla de emisiones y la última fila
            DefaultTableModel model = (DefaultTableModel) view.Emisiones.getModel();
            int lastRow = model.getRowCount() - 1; // Obtener el índice de la última fila

            // Obtener los valores de la última fila (carga ambiental y resultado)
            String cargaAmbientalStr = model.getValueAt(lastRow, 3).toString();
            String resultadoStr = model.getValueAt(lastRow, 6).toString();

            // Comprobación para el tipo de modeloUsuario
            if (user.getTipoUsuario().equals("Superadmin")) {
                // Si los valores no están vacíos y no se ha marcado la opción de cálculo
                if (!cargaAmbientalStr.isEmpty() && !resultadoStr.isEmpty() && view.noCalculo.isSelected()) {
                    double cargaAmbiental = Double.parseDouble(cargaAmbientalStr);
                    double total1 = Double.parseDouble(resultadoStr);

                    // Asignar los valores al modelo de cálculo
                    modeloCalcular.setCantidadConsumidad(cargaAmbiental);
                    modeloCalcular.setTotal1(total1);

                    // Establecer los valores de la institución y modeloMunicipio
                    modeloInstitucion.setNombreInstitucion(String.valueOf(view.comboInstitucion.getSelectedItem()));
                    modeloMunicipio.setNombreM(Objects.requireNonNull(view.municipio.getSelectedItem()).toString());

                    // Registrar la carga ambiental para la institución sin núcleo
                    if (calcularConsultas.registrarCargaAmbientaInstitucionSinNucleo(modeloCalcular, modeloInstitucion, modeloMunicipio)) {
                        JOptionPane.showMessageDialog(view, "Registro guardado");
                        view.fuente.setEnabled(true);
                        view.anio.setEditable(true);
                    } else {
                        JOptionPane.showMessageDialog(view, "Error al guardar el registro");
                    }
                }
            } else {
                // Caso para usuarios que no son Superadmin (registro de carga ambiental sin núcleo)
                if (!cargaAmbientalStr.isEmpty() && !resultadoStr.isEmpty() && view.noCalculo.isSelected()) {
                    double cargaAmbiental = Double.parseDouble(cargaAmbientalStr);
                    double total1 = Double.parseDouble(resultadoStr);

                    // Asignar los valores al modelo de cálculo
                    modeloCalcular.setCantidadConsumidad(cargaAmbiental);
                    modeloCalcular.setTotal1(total1);

                    // Registrar la carga ambiental para la institución sin núcleo
                    if (calcularConsultas.registrarCargaAmbientaInstitucionSinNucleo(modeloCalcular, modeloInstitucion, modeloMunicipio)) {
                        JOptionPane.showMessageDialog(view, "Registro guardado");
                        view.fuente.setEnabled(true);
                        view.anio.setEditable(true);
                    } else {
                        JOptionPane.showMessageDialog(view, "Error al guardar el registro");
                    }
                }
            }

            // Comprobación para si se ha seleccionado "Sí" en el checkbox y "No registro" está seleccionado
            if (view.siCheckBox.isSelected() && view.noRegistro.isSelected()) {
                Conexion conn = new Conexion();
                ConsultaNucleo consultaNucleo = new ConsultaNucleo(conn);
                ModeloNucleo modeloNucleo = new ModeloNucleo();
                double cargaAmbiental = Double.parseDouble(cargaAmbientalStr);
                double total1 = Double.parseDouble(resultadoStr);

                // Asignar los valores al modelo de cálculo
                modeloCalcular.setCantidadConsumidad(cargaAmbiental);
                modeloCalcular.setTotal1(total1);

                // Establecer el nombre del núcleo
                modeloNucleo.setNombreNucleo(view.nucleo.getText());

                // Registrar el cálculo para un núcleo
                if (consultaNucleo.InsertarCalculoConNucleo(modeloCalcular, modeloNucleo)) {
                    JOptionPane.showMessageDialog(view, "Se registro el cálculo para el núcleo " + modeloNucleo.getNombreNucleo() +
                            " fue exitosa");
                    view.fuente.setEnabled(true);
                    view.anio.setEditable(true);
                    view.nucleo.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(view, "ERROR");
                }
            }

            // Comprobación para si se ha seleccionado "Sí" en el checkbox y "Sí registro" está seleccionado
            if (view.siCheckBox.isSelected() && view.siRegistro.isSelected()) {
                Conexion conn = new Conexion();
                ConsultaNucleo consultaNucleo = new ConsultaNucleo(conn);
                ModeloNucleo modeloNucleo = new ModeloNucleo();
                double cargaAmbiental = Double.parseDouble(cargaAmbientalStr);
                double total1 = Double.parseDouble(resultadoStr);

                // Asignar los valores al modelo de cálculo
                modeloCalcular.setCantidadConsumidad(cargaAmbiental);
                modeloCalcular.setTotal1(total1);

                // Establecer el nombre del núcleo seleccionado
                modeloNucleo.setNombreNucleo(Objects.requireNonNull(view.comboNucleo.getSelectedItem()).toString());

                // Registrar el cálculo para el núcleo seleccionado
                if (consultaNucleo.InsertarCalculoConNucleo(modeloCalcular, modeloNucleo)) {
                    JOptionPane.showMessageDialog(view, "Se registro el cálculo para el núcleo " + modeloNucleo.getNombreNucleo() +
                            " fue exitosa");
                    view.fuente.setEnabled(true);
                    view.anio.setEditable(true);
                    view.nucleo.setEnabled(false);
                } else {
                    JOptionPane.showMessageDialog(null, "ERROR");
                }
            }
        } catch (NumberFormatException ex) {
            // Manejo de error si los datos de entrada no tienen el formato adecuado
            JOptionPane.showMessageDialog(view, "Error en el formato de los datos");
            ex.printStackTrace(); // Mensaje de depuración
        } catch (Exception ex) {
            // Manejo de excepciones inesperadas
            JOptionPane.showMessageDialog(view, "Ocurrió un error inesperado");
            ex.printStackTrace(); // Mensaje de depuración
        }
    }


    /**
     * Actualiza el campo de texto relacionado con el núcleo en la vista utilizando el último registro obtenido de la base de datos.
     * Este método utiliza las clases de conexión a la base de datos para consultar el registro más reciente relacionado con un núcleo.
     * Pasos:
     * 1. Establece una conexión con la base de datos utilizando la clase `Conexion`.
     * 2. Utiliza la clase `ConsultaNucleo` para realizar la consulta que obtiene el último registro relacionado con un núcleo.
     * 3. Actualiza el campo de texto correspondiente en la vista (`view.modeloNucleo`) con el valor del último registro obtenido.
     * 4. Imprime en la consola el valor del último registro para propósitos de depuración.
     */
    private void actualizarButton() {
        // Crear una instancia de conexión a la base de datos
        Conexion conn = new Conexion();

        // Crear una instancia de consulta para núcleos, utilizando la conexión establecida
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conn);

        // Obtener el último registro del núcleo desde la base de datos
        String ultimoRegistro = consultaNucleo.UltimoRegistro();

        // Imprimir el último registro en la consola (para depuración)
        System.out.println(ultimoRegistro);

        // Actualizar el campo de texto del núcleo en la vista con el último registro obtenido
        view.nucleo.setText(ultimoRegistro);
    }


    /**
     * Carga los núcleos existentes asociados a una institución en un componente de selección (combo box) de la interfaz gráfica.
     * Pasos:
     * 1. Limpia el contenido del combo box para evitar duplicados o valores previos.
     * 2. Crea una conexión a la base de datos utilizando la clase `Conexion`.
     * 3. Utiliza la clase `ConsultaNucleo` para obtener una lista de núcleos asociados a la institución actual.
     * 4. Agrega los nombres de los núcleos recuperados al combo box para que el modeloUsuario pueda seleccionarlos.
     * Nota: Este método depende de un modelo (`modeloInstitucion`) que contiene el nombre de la institución actual.
     */
    private void cargarNucleoExistentes() {
        // Limpia el combo box antes de cargar nuevos valores
        view.comboNucleo.removeAllItems();
        view.comboNucleo.addItem(" "); // Agrega un valor vacío al inicio como opción predeterminada

        // Crear una instancia de conexión a la base de datos
        Conexion conexion = new Conexion();

        // Crear una instancia de consulta para núcleos, utilizando la conexión establecida
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conexion);

        // Obtener el nombre de la institución desde el modelo
        String nombreInstitucion = modeloInstitucion.getNombreInstitucion();
        List<String> nucleos = consultaNucleo.cargarNucleos(nombreInstitucion);
        // Agregar los núcleos recuperados al combo box
        for (String nucleo : nucleos) {
            view.comboNucleo.addItem(nucleo);
        }

        // Llamar al método que carga los núcleos asociados a la institución


        // Nota: Este método podría beneficiarse de un manejo de errores para gestionar fallas en la consulta o conexión
    }

    private void cargarComboNucleo() {
        view.comboNucleo.removeAllItems();
        view.comboNucleo.addItem(" "); // Agrega un valor vacío al inicio como opción predeterminada

        // Crear una instancia de conexión a la base de datos
        Conexion conexion = new Conexion();

        // Crear una instancia de consulta para núcleos, utilizando la conexión establecida
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(conexion);

        // Obtener el nombre de la institución desde el modelo
        String nombreInstitucion = ((String) view.comboInstitucion.getSelectedItem()).toString();
        List<String> nucleos = consultaNucleo.cargarNucleos(nombreInstitucion);
        // Agregar los núcleos recuperados al combo box
        for (String nucleo : nucleos) {
            view.comboNucleo.addItem(nucleo);
        }
    }
    /**
     * Inicia la vista para visualizar instituciones.
     * Pasos:
     * 1. Crea las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado para manejar la lógica.
     * 3. Opcionalmente, cierra la vista actual (comentado en este caso).
     */
    private void vistaVerInstitution() {
        Conexion con = new Conexion();
        VerInstituciones verInstituciones = new VerInstituciones();
        ConsultaNucleo consultaNucleo = new ConsultaNucleo(con);
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();
        ModeloInstitucion modeloInstitucion = new ModeloInstitucion();
        ControladorVerInstituciones contraladorVerInstituciones = new ControladorVerInstituciones(consultasInstitucion, consultaNucleo,
                modeloInstitucion, verInstituciones, user, modeloMunicipio);
        contraladorVerInstituciones.iniciar();
        view.dispose();
    }

    /**
     * Llena el combo box de instituciones con datos obtenidos desde el modelo.
     * Pasos:
     * 1. Limpia el combo box y agrega un elemento vacío.
     * 2. Recupera la lista de instituciones desde el modelo.
     * 3. Agrega cada institución al combo box.
     * 4. Selecciona el primer elemento, si está disponible.
     */
    private void llenarComboInstitution() {
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();
        // Obtener la lista de instituciones desde el Modelo
        List<String> instituciones = consultasInstitucion.obtenerInstituciones();

        // Limpiar el comboBox y agregar un item vacío
        view.comboInstitucion.removeAllItems();
        view.comboInstitucion.addItem("");  // Añadimos un item vacío como indicativo

        // Llenar el comboBox con las instituciones obtenidas del Modelo
        for (String institucion : instituciones) {
            view.comboInstitucion.addItem(institucion);
        }

        // Si el comboBox tiene elementos, seleccionamos el primero
        if (view.comboInstitucion.getItemCount() > 0) {
            view.comboInstitucion.setSelectedIndex(0); // Seleccionamos el primer elemento
        }
    }

    /**
     * Llena el combo box de municipios en función de la institución seleccionada.
     * Pasos:
     * 1. Verifica si hay una institución seleccionada.
     * 2. Obtiene la lista de municipios relacionados desde el modelo.
     * 3. Limpia y actualiza el combo box de municipios con los datos obtenidos.
     * 4. Selecciona el primer elemento disponible.
     */
    private void llenarComoboMunicipio() {
        // Obtener la institución seleccionada desde la vista
        String nombreInstitucion = (String) view.comboInstitucion.getSelectedItem();
        ConsultasInstitucion consultasInstitucion = new ConsultasInstitucion();

        // Verificar que la institución no esté vacía
        if (nombreInstitucion != null && !nombreInstitucion.isEmpty()) {
            // Obtener la lista de municipios desde el Modelo
            List<String> municipios = consultasInstitucion.obtenerMunicipios(nombreInstitucion);

            // Limpiar el JComboBox de municipios y agregar un item vacío
            view.municipio.removeAllItems();
            view.municipio.addItem("");  // Añadir un item vacío como indicativo

            // Llenar el JComboBox con los municipios obtenidos del Modelo
            for (String municipio : municipios) {
                view.municipio.addItem(municipio);
            }

            // Si el JComboBox tiene elementos, seleccionamos el primero
            if (view.municipio.getItemCount() > 0) {
                view.municipio.setSelectedIndex(0); // Seleccionamos el primer elemento
            }
        } else {
            // Si la institución está vacía, limpiar el JComboBox de municipios
            view.municipio.removeAllItems();
        }
    }

    /**
     * Navega a la vista inicial de la aplicación.
     * Pasos:
     * 1. Crea el controlador de inicio con las dependencias necesarias.
     * 2. Llama al método que inicia la vista de inicio.
     * 3. Cierra la vista actual.
     */
    private void BotonInicio() {
        ControladorPestaniaPrincipal control = new ControladorPestaniaPrincipal(modeloInstitucion, user, modeloMunicipio);
        control.inicio();
        view.dispose();
    }

    /**
     * Inicia la vista del perfil del modeloUsuario.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado con los datos del modeloUsuario.
     * 3. Muestra la vista del perfil y cierra la vista actual.
     */
    private void vistaPerfil() {
        Conexion conn = new Conexion();
        Perfil per = new Perfil();
        ConsultaUsuario cons = new ConsultaUsuario(conn);
        ControladorPerfil control = new ControladorPerfil(user, per, modeloInstitucion, modeloMunicipio, cons);
        control.Iniciar();
        per.setVisible(true);
        view.dispose();
    }

    /**
     * Inicia la vista para registrar emisiones.
     * Pasos:
     * 1. Configura la vista, modelo y consultas necesarios para registrar emisiones.
     * 2. Inicializa el controlador asociado.
     * 3. Muestra la vista de registro de emisiones y cierra la vista actual.
     */
    private void vistaRegistrarEmision() {
        Emision emisionView = new Emision();
        ModeloEmision mod = new ModeloEmision();
        ConsultasEmision consul = new ConsultasEmision();
        ControladorEmision controlador = new ControladorEmision(mod, consul, emisionView, modeloInstitucion, modeloMunicipio, user);
        controlador.iniciar();
        emisionView.setVisible(true);
        view.dispose();
    }

    /**
     * Inicia la vista de informes.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado para manejar la lógica de los informes.
     * 3. Muestra la vista de informes y cierra la vista actual.
     */
    private void vistaInforme() {
        Conexion con = new Conexion();
        ConsultaInforme consul = new ConsultaInforme(con);
        ModeloEmisionInforme mod = new ModeloEmisionInforme();
        Informe viewInfo = new Informe();
        ControladorInforme contro = new ControladorInforme(viewInfo, mod, consul, modeloMunicipio, modeloInstitucion, user);
        contro.iniciar();
        viewInfo.setVisible(true);
        view.dispose();
    }

    /**
     * Inicia la vista para mostrar gráficos principales.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador asociado para gestionar los gráficos.
     * 3. Muestra la vista de gráficos y cierra la vista actual.
     */
    private void vistaGraficoPrincipal() {
        Conexion con = new Conexion();
        GraficoConsulta consul = new GraficoConsulta(con);
        Vistas.Graficos viewGraf = new Graficos();
        GraficorModeloInstitucion mod = new GraficorModeloInstitucion();
        ModeloInstitucion modelo = new ModeloInstitucion();
        ControladorGrafico contro = new ControladorGrafico(mod, consul, viewGraf, modelo, modeloMunicipio, user, modeloInstitucion);
        contro.iniciar();
        viewGraf.setVisible(true);
        view.dispose();
    }

    /**
     * Inicia la vista para comparar instituciones mediante gráficos.
     * Pasos:
     * 1. Configura la vista, modelo y consultas necesarios.
     * 2. Inicializa el controlador para manejar la comparación de instituciones.
     * 3. Cierra la vista actual.
     */
    private void vistaCompararInstituciones() {
        CompararOtrarInstituciones comIns = new CompararOtrarInstituciones();
        GraficoComparar viewGraf = new GraficoComparar();
        Conexion conn = new Conexion();
        GraficoCompararConsultas consultas = new GraficoCompararConsultas(conn);
        ModeloGraficoComparar mod = new ModeloGraficoComparar();
        ControladorCompararInstitucion contro = new ControladorCompararInstitucion(mod, consultas, comIns, viewGraf, modeloInstitucion, modeloMunicipio, user);
        contro.iniciar();
        view.dispose();
    }

    /**
     * Inicia la vista para mostrar tendencias históricas de datos mediante gráficos.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador para gestionar las tendencias históricas.
     * 3. Muestra la vista de gráficos y cierra la vista actual.
     */
    private void vistaGraficoHistorico() {
        Conexion con = new Conexion();
        ModeloTendencia mod = new ModeloTendencia();
        ConsultasTendencias consult = new ConsultasTendencias(con);
        GraficoTendencia viewGraf = new GraficoTendencia();
        ControladorTendencia control = new ControladorTendencia(mod, consult, viewGraf, modeloMunicipio, modeloInstitucion, user);
        viewGraf.setVisible(true);
        control.iniciar();
        view.dispose();
    }

    /**
     * Inicia la vista para aplicar estrategias de reducción de emisiones.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista, modelo y consultas.
     * 2. Inicializa el controlador para manejar las estrategias de reducción.
     * 3. Cierra la vista actual.
     */
    private void vistaReducir() {
        Conexion con = new Conexion();
        Reducir2 vista = new Reducir2();
        GraficoConsulta consul = new GraficoConsulta(con);
        ControladorReducir redu = new ControladorReducir(modeloInstitucion, consul, vista, modeloMunicipio, user);
        redu.Iniciar();
        view.dispose();
    }

    /**
     * Inicia la vista para actualizar los datos de una institución.
     * Pasos:
     * 1. Configura las dependencias necesarias: vista, modelo y consultas.
     * 2. Inicializa el controlador para gestionar la lógica de actualización.
     * 3. Muestra la vista de actualización y cierra la vista actual.
     */
    private void vistaActualizarInstitucion() {
        ConsultasInstitucion consul = new ConsultasInstitucion();
        VerDatosInstitucion view2 = new VerDatosInstitucion();
        ModeloInstitucion mod = new ModeloInstitucion();
        ControladorInstitucion control = new ControladorInstitucion(modeloInstitucion, modeloMunicipio, view2, consul, mod, user);
        view2.setVisible(true);
        control.iniciar();
        view.dispose();


    }

    /**
     * Inicia la vista para visualizar perfiles de usuarios.
     * Pasos:
     * 1. Configura las dependencias necesarias: conexión, vista y consultas.
     * 2. Inicializa el controlador para manejar la lógica de visualización de perfiles.
     * 3. Cierra la vista actual.
     */
    private void vistaVerPerfiles() {
        Conexion con = new Conexion();
        VerPerfiles verPerfiles = new VerPerfiles();
        ConsultaUsuario consul = new ConsultaUsuario(con);

        ControladorVerPerfiles verControl = new ControladorVerPerfiles(user, modeloInstitucion, modeloMunicipio, verPerfiles, consul);
        verControl.Iniciar();
        view.dispose();
    }

    private void limpiar(){
        view.siCheckBox.setSelected(false);
        view.comboInstitucion.setSelectedIndex(0);
        view.noCalculo.setSelected(false);
        view.noRegistro.setSelected(false);
        view.nucleo.setText(" ");
        view.comboNucleo.setSelectedIndex(0);
        view.fuente.setSelectedIndex(0);
        view.anio.setText(" ");
        view.total.setText(" ");
        view.comboNucleo.setVisible(false);
        view.nucleo.setText(" ");
        view.nucleo.setVisible(false);
        view.Nucleo.setVisible(false);
        view.noCalculo.setEnabled(true);
        view.siCheckBox.setEnabled(true);
        view.siRegistro.setSelected(false);
        view.noRegistro.setEnabled(true);
        view.siRegistro.setEnabled(true);
        view.siRegistro.setVisible(false);
        view.noRegistro.setVisible(false);
        view.Nucleo.setVisible(false);
        view.hayNucleo.setVisible(false);
        view.Registro.setVisible(false);
        view.actualizarButton.setVisible(false);
    }
}
