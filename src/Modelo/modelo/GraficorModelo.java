package Modelo.modelo;

public class GraficorModelo extends InstitucionModelo {
    public String Alcance;
    public Double Total;

    public GraficorModelo() {
        super();
    }

    public String getAlcance() {
        return Alcance;
    }

    public void setAlcance(String alcance) {
        Alcance = alcance;
    }

    public Double getTotal() {
        return Total;
    }

    public void setTotal(Double total) {
        Total = total;
    }
}
