package Modelo.modelo;

public class ModeloNucleo {
    private String idInstitucion;
    private String NombreNucleo;
    private String NombreIns;
    private String municipio;
    private String Departamento;
    private int hectareas;

    public ModeloNucleo() {

    }

    public String getDepartamento() {
        return Departamento;
    }

    public int getHectareas() {
        return hectareas;
    }

    public void setHectareas(int hectareas) {
        this.hectareas = hectareas;
    }

    public void setDepartamento(String departamento) {
        Departamento = departamento;
    }

    public String getNombreIns() {
        return NombreIns;
    }

    public void setNombreIns(String nombreIns) {
        NombreIns = nombreIns;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getIdInstitucion() {
        return idInstitucion;
    }

    public void setIdInstitucion(String idInstitucion) {
        this.idInstitucion = idInstitucion;
    }

    public String getNombreNucleo() {
        return NombreNucleo;
    }

    public void setNombreNucleo(String nombreNucleo) {
        NombreNucleo = nombreNucleo;
    }
}
