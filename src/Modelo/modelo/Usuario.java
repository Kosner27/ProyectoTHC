package Modelo.modelo;

public class Usuario {
    private String Nombre;
    private String Apellido;
    private String Correo;
    private String contrasena;
    private String idInstitucion;
    private String TipoUsuario;
    private Integer idUsuario;
    private String descripcion;
    private String nombreInstticion;
    private String municipio;

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }

    public String getNombreInstticion() {
        return nombreInstticion;
    }

    public void setNombreInstticion(String nombreInstticion) {
        this.nombreInstticion = nombreInstticion;
    }

    public Usuario() {
    }

    public String getApellido() {
        return Apellido;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public void setApellido(String apellido) {
        Apellido = apellido;
    }

    public String getNombre() {
        return Nombre;
    }

    public void setNombre(String nombre) {
        Nombre = nombre;
    }

    public String getCorreo() {
        return Correo;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setCorreo(String correo) {
        Correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getIdInstitucion() {
        return idInstitucion;
    }

    public void setIdInstitucion(String idInstitucion) {
        this.idInstitucion = idInstitucion;
    }

    public String getTipoUsuario() {
        return TipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        TipoUsuario = tipoUsuario;
    }
}
