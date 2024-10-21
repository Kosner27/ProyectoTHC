package Modelo.Consultas;

import Modelo.Conexion;
import Modelo.modelo.InstitucionModelo;
import Modelo.modelo.Usuario;
import Modelo.modelo.Municipio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsultaUsuario {
    private Connection conn;

    public ConsultaUsuario(Conexion conn) {
        this.conn = conn.getConection();
    }

    public boolean insertarUsuario(Municipio municipio, Usuario usuario) {
        String sql = "call InsertarUsuario(?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getContrasena());
            ps.setString(5, usuario.getIdInstitucion());
            ps.setString(6, municipio.getNombreM());
            ps.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


    }

    public boolean insersartUsuarioDefault(String municipio, Usuario usuario) {
        String sql = "call InsertarUsuarioDefault(?,?,?,?,?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getNombre());
            ps.setString(2, usuario.getApellido());
            ps.setString(3, usuario.getCorreo());
            ps.setString(4, usuario.getContrasena());
            ps.setString(5, usuario.getIdInstitucion());
            ps.setString(6, usuario.getTipoUsuario());
            ps.setString(7, municipio);
            ps.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


    }

    /*public int PrimerRegistro(String Institucion , String Municipio){
        String sql = "Select count(u.idUsuario) from usuario u inner join institucion i on u.idInstitucion = i.IdInstitucionAuto  inner join municipio m on i.idMunicipio = m.idMunicipio where u.Descripcion = 'Creado por Default'  and i.NombreInstitucion = ? and m.NombreMunicipio = ?";
        ResultSet st = null;
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,Institucion);
            ps.setString(2,Municipio);
            st=ps.executeQuery();
            if(st.next()){
                return st.getInt(1);
            }{
                return 1;
            }
        }catch (SQLException ex){
            ex.printStackTrace();
            return 0;
        }
    }*/
  /*  public int Acceso(String correo){
        String sql = "Select count(Correo) from usuario where AccesoUsuario = 1 and Correo = ?";
        ResultSet st = null;
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,correo);
            st=ps.executeQuery();
            if(st.next()){
                return st.getInt(1);
            }{
                return 1;
            }
        }catch (SQLException ex){
            Logger.getLogger(UsuarioModel.class.getName()).log(Level.SEVERE,null,ex);
            return 0;
        }
    }*/
    public boolean updatetUsuario(Usuario usuario) {
        String sql = "Call ActualizarUsuario(?,?,?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getCorreo());
            ps.setString(2, usuario.getContrasena());
            ps.setInt(3, usuario.getIdUsuario());
            ps.execute();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }


    }

    public int ExisteUsuario(String usuario) {

        ResultSet st = null;
        String sql = "Select count(idUsuario) from usuario where Correo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario);
            st = ps.executeQuery();
            if (st.next()) {
                return st.getInt(1);
            }
            {
                return 1;
            }

        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
            return 1;
        }

    }

    public boolean esEmail(String correo) {

        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                + "[A-Za-z0-9-]+(\\.[A-Za-z{2,}$])");
        Matcher mather = pattern.matcher(correo);
        return mather.find();
    }

    public List<Usuario> datos() {
        Conexion conexion = new Conexion();
        List<Usuario> Datos = new ArrayList<>();
        Connection conn = conexion.getConection();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("Select u.Nombre,u.Apellido,u.Correo,r.tipoUsuario,r.descripcion, i.NombreInstitucion, m.NombreMunicipio from usuario u inner join roles r on u.idRol = r.idRol inner join institucion i on i.idInstitucionAuto = u.idInstitucion inner join  municipioinstitiucion mi on i.idInstitucionAuto = mi.IdInstitucion inner join municipio m on mi.idMuncipio = m.idMunicipio ");
            rs = ps.executeQuery();
            while (rs.next()) {
                Usuario user = new Usuario();
                user.setNombre(rs.getString("Nombre"));
                user.setApellido(rs.getString("Apellido"));
                user.setCorreo(rs.getString("Correo"));
                user.setTipoUsuario(rs.getString("tipoUsuario"));
                user.setDescripcion(rs.getString("descripcion"));
                user.setNombreInstticion(rs.getString("NombreInstitucion"));
                user.setMunicipio(rs.getString("NombreMunicipio"));
                Datos.add(user);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();

            }
        }
        return Datos;

    }

    public List<Usuario> BuscarUsuario(String correo) {
        Conexion conexion = new Conexion();
        Connection conn = conexion.getConection();
        List<Usuario> mod = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            ps = conn.prepareStatement("select u.Nombre,U.Apellido,r.tipoUsuario from usuario u inner join roles r on  u.idRol = r.idRol where Correo = ?");
            ps.setString(1, correo);
            rs = ps.executeQuery();
            if (rs.next()) {
                Usuario user = new Usuario();
                user.setNombre(rs.getString("Nombre"));
                user.setApellido(rs.getString("Apellido"));
                user.setTipoUsuario(rs.getString("tipoUsuario"));
                mod.add(user);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();

        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
            } catch (SQLException e) {
                e.printStackTrace();

            }
        }
        return mod;
    }

    public boolean EditarPrivilegios(String correo, int privilegio) {
        String sql = "UPDATE usuario SET idRol = ? WHERE Correo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, privilegio);
            ps.setString(2, correo); // Aquí se utiliza el correo proporcionado
            ps.executeUpdate(); // Cambié a executeUpdate() para actualizar filas
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean LogIn(Usuario usr, InstitucionModelo ins, Municipio m) {

        ResultSet st = null;
        String sql = "Select u.idUsuario, u.Correo, u.Constrasena, u.idRol, r.tipoUsuario,u.Nombre, u.Apellido,i.NombreInstitucion, m.NombreMunicipio from usuario u inner join Roles r on r.idRol = u.idRol inner join institucion i on i.idInstitucionAuto = u.idInstitucion inner join  municipioinstitiucion mi \n" +
                "on i.idInstitucionAuto = mi.IdInstitucion inner join municipio m on m.idMunicipio = mi.idMuncipio where u.Correo = ? ";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usr.getCorreo());
            st = ps.executeQuery();
            if (st.next()) {
                if (usr.getContrasena().equals(st.getString(3))) {
                    usr.setIdUsuario(st.getInt("idUsuario"));
                    usr.setCorreo(st.getString(2));
                    usr.setContrasena(st.getString(3));
                    usr.setTipoUsuario(st.getString(5));
                    usr.setNombre(st.getString(6));
                    usr.setApellido(st.getString(7));
                    ins.setNombreInstitucion(st.getString(8));
                    m.setNombreM(st.getString(9));
                    return true;
                } else {
                    return false;
                }

            }
            {
                return false;
            }

        } catch (SQLException ex) {
            Logger.getLogger(Usuario.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }

    }

    public boolean eliminarUsuario(String Correo) {
        String sql = "Delete from usuario where Correo = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, Correo);
            ps.execute();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean actualizarContrasena(String Correo, String Contrasena) {
        String sql = "Update usuario set Constrasena = ? where Correo = ? ";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, Contrasena);
            ps.setString(2, Correo);
            ps.execute();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

}
