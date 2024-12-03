package Modelo.modelo;

public class ModeloRol {
    private String tipoUsuario;
    private String Descripcion;

    public ModeloRol() {
    }

    public ModeloRol(String tipoUsuario, String descripcion) {
        this.tipoUsuario = tipoUsuario;
        Descripcion = descripcion;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getDescripcion() {
        return Descripcion;
    }

    public void setDescripcion(String descripcion) {
        Descripcion = descripcion;
    }
}
