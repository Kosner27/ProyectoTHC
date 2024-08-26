package Modelo.modelo;

import java.util.List;

public class Rol {
    private  String tipoUsuario;
    private String Descripcion;

    public Rol() {
    }

    public Rol(String tipoUsuario, String descripcion) {
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
