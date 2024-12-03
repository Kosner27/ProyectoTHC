package Modelo.modelo;


import Modelo.Conexion;
import Modelo.Consultas.CalcularConsultas;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.List;

public class ModeloEmisionCalcular extends ModeloEmision {
    private Double CantidadConsumidad;
    private Double Total1;
    private int anioBase;

    public ModeloEmisionCalcular() {
        super();
    }

    public int getAnioBase() {
        return anioBase;
    }

    public void setAnioBase(int anioBase) {
        this.anioBase = anioBase;
    }

    public Double getCantidadConsumidad() {
        return CantidadConsumidad;
    }

    public void setCantidadConsumidad(Double cantidadConsumidad) {
        CantidadConsumidad = cantidadConsumidad;
    }

    public Double getTotal1() {
        return Total1;
    }

    public void setTotal1(Double total) {
        Total1 = total;
    }

    public double actualizarTotal(DefaultTableModel model) {
        double total = 0.0;
        int columnaCargaAmbiental = 3;
        int columnaOtroValor = 5;

        for (int row = 0; row < model.getRowCount(); row++) {
            try {
                String cargaStr = model.getValueAt(row, columnaCargaAmbiental).toString();
                String otroValorStr = model.getValueAt(row, columnaOtroValor).toString();

                if (!cargaStr.isEmpty() && !otroValorStr.isEmpty()) {
                    double carga = Double.parseDouble(cargaStr);
                    double otroValor = Double.parseDouble(otroValorStr);

                    double resultado = carga * otroValor; // Ejemplo de cálculo
                    model.setValueAt(resultado, row, 6); // Actualizar columna resultado
                    total += resultado;
                }
            } catch (NumberFormatException | NullPointerException ex) {
                System.out.println("Error en los datos de la fila " + row);
            }
        }
        return total;
    }

    public List<ModeloEmision> obtenerEmisionesProcesadas(String fuenteSeleccionada, JTable tabla) {
        DefaultTableModel tableModel = (DefaultTableModel) tabla.getModel();
        Conexion conexion  = new Conexion();
        CalcularConsultas consul = new CalcularConsultas(conexion);
        List<ModeloEmision> emisiones = consul.getEmisiones(fuenteSeleccionada);

        emisiones.removeIf(emision -> {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                String fuenteEnTabla = (String) tableModel.getValueAt(i, 0);
                if (fuenteEnTabla.equals(emision.getNombreFuente())) {
                    return true; // Si ya existe, eliminar de la lista
                }
            }
            return false;
        });

        return emisiones;
    }


}
