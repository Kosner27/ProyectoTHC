package Modelo;

public class EmisionModelo {
 public String NombreFuente;
 public String TipoFuente;
 public String EstadoFuente;
 public String Alcance;
 public Double  FactorEmision;
 public String UnidadMedidad;

    public EmisionModelo(){

    }
    public String getNombreFuente() {
        return NombreFuente;
    }

    public void setNombreFuente(String nombreFuente) {
        NombreFuente = nombreFuente;
    }

    public String getTipoFuente() {
        return TipoFuente;
    }

    public void setTipoFuente(String tipoFuente) {
        TipoFuente = tipoFuente;
    }



    public String getEstadoFuente() {
        return EstadoFuente;
    }

    public void setEstadoFuente(String estadoFuente) {
        EstadoFuente = estadoFuente;
    }

    public String getAlcance() {
        return Alcance;
    }

    public void setAlcance(String alcance) {
        Alcance = alcance;
    }

    public Double getFactorEmision() {
        return FactorEmision;
    }

    public void setFactorEmision(Double factorEmision) {
        FactorEmision = factorEmision;
    }

    public String getUnidadMedidad() {
        return UnidadMedidad;
    }

    public void setUnidadMedidad(String unidadMedidad) {
        UnidadMedidad = unidadMedidad;
    }
}

