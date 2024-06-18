package Modelo;

public class GraficoCompararModelo {
    private String NombreInstitucion;
    private String Nombrefuente;
    private String Alcance;
    private Double total;

    public GraficoCompararModelo(){

    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(Double total) {
        this.total = total;
    }

    public String getAlcance() {
        return Alcance;
    }

    public void setAlcance(String alcance) {
        Alcance = alcance;
    }

    public String getNombrefuente() {
        return Nombrefuente;
    }

    public void setNombrefuente(String nombrefuente) {
        Nombrefuente = nombrefuente;
    }

    public String getNombreInstitucion() {
        return NombreInstitucion;
    }

    public void setNombreInstitucion(String nombreInstitucion) {
        NombreInstitucion = nombreInstitucion;
    }
}
