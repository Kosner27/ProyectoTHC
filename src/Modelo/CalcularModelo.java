package Modelo;



public class CalcularModelo extends EmisionModelo {
public Double CantidadConsumidad;
public Double Total1;
public int anioBase;
    public CalcularModelo(){
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
}
