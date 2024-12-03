package Modelo.modelo;

public class GraficorModeloInstitucion extends ModeloInstitucion {
    public String Alcance;
    public Double Total;

    public GraficorModeloInstitucion() {
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
