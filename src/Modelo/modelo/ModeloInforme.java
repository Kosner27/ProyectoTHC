package Modelo.modelo;

public class ModeloInforme extends EmisionModelo {
    private Double CantidadConsumidad;
    private Double CargaAmnbiental;
    private Integer AnioBase;
    private String UnidadMedidad;
    private Double FactorEmision;
    private String Municipio;
    private String Departamento;
    private String nit;

    public ModeloInforme() {
        super();
    }

    public Integer getAnioBase() {
        return AnioBase;
    }

    public void setAnioBase(Integer anioBase) {
        AnioBase = anioBase;
    }

    public Double getCargaAmnbiental() {
        return CargaAmnbiental;
    }

    public void setCargaAmnbiental(Double cargaAmnbiental) {
        CargaAmnbiental = cargaAmnbiental;
    }

    public Double getCantidadConsumidad() {
        return CantidadConsumidad;
    }

    public void setCantidadConsumidad(Double cantidadConsumidad) {
        CantidadConsumidad = cantidadConsumidad;
    }

    @Override
    public String getUnidadMedidad() {
        return UnidadMedidad;
    }

    @Override
    public void setUnidadMedidad(String unidadMedidad) {
        UnidadMedidad = unidadMedidad;
    }

    @Override
    public Double getFactorEmision() {
        return FactorEmision;
    }

    @Override
    public void setFactorEmision(Double factorEmision) {
        FactorEmision = factorEmision;
    }

    public String getMunicipio() {
        return Municipio;
    }

    public void setMunicipio(String municipio) {
        Municipio = municipio;
    }

    public String getDepartamento() {
        return Departamento;
    }

    public void setDepartamento(String departamento) {
        Departamento = departamento;
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }
}
