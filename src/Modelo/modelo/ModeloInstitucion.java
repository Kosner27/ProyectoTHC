package Modelo.modelo;

public class ModeloInstitucion {
    private String Nit;
    private String NombreInstitucion;
    private String departamento;
    private String municipio;
    private String nucleo;
    private int hectareas;
    private int hectareasNucleo;

    public ModeloInstitucion() {
    }

    public String getNucleo() {
        return nucleo;
    }

    public void setNucleo(String nucleo) {
        this.nucleo = nucleo;
    }

    public int getHectareasNucleo() {
        return hectareasNucleo;
    }

    public void setHectareasNucleo(int hectareasNucleo) {
        this.hectareasNucleo = hectareasNucleo;
    }

    public String getNombreInstitucion() {
        return NombreInstitucion;
    }

    public void setNombreInstitucion(String nombreInstitucion) {
        NombreInstitucion = nombreInstitucion;
    }

    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }

    public String getNit() {
        return Nit;
    }

    public void setNit(String nit) {
        Nit = nit;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public int getHectareas() {
        return hectareas;
    }

    public void setHectareas(int hectareas) {
        this.hectareas = hectareas;
    }
}
