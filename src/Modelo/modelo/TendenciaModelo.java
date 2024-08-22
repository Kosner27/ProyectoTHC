package Modelo.modelo;

public class TendenciaModelo {
    private String Nombre;
    private  Integer AnioBase;
    private Double co2;
    private String alcance;


    public TendenciaModelo(){

    }
    public Double getCo2() {
        return co2;
    }

    public String getAlcance() {
        return alcance;
    }

    public void setAlcance(String alcance) {
        this.alcance = alcance;
    }

    public void setCo2(Double co2) {
        this.co2 = co2;
    }

    public Integer getAnioBase() {
        return AnioBase;
    }

    public void setAnioBase(Integer anioBase) {
        AnioBase = anioBase;
    }


    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

}


