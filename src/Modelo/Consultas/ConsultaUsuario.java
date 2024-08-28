package Modelo.Consultas;
import Modelo.Conexion;
import Modelo.modelo.InstitucionModelo;
import Modelo.modelo.Rol;
import Modelo.modelo.UsuarioModel;
import Modelo.modelo.municipio;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConsultaUsuario {
    private  Connection conn;

    public ConsultaUsuario(Conexion conn) {
        this.conn = conn.getConection();
    }

    public boolean insersartUsuario(String municipio, UsuarioModel usuarioModel){
        String sql = "call InsertarUsuario(?,?,?,?,?,?,?)";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,usuarioModel.getNombre());
            ps.setString(2,usuarioModel.getApellido());
            ps.setString(3, usuarioModel.getCorreo());
            ps.setString(4, usuarioModel.getContrasena());
            ps.setString(5, usuarioModel.getIdInstitucion());
            ps.setString(6, usuarioModel.getTipoUsuario());
            ps.setString(7, municipio);
            ps.execute();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }


    }
    public boolean updatetUsuario(UsuarioModel usuarioModel){
        String sql = "Call ActualizarUsuario(?,?,?)";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,usuarioModel.getCorreo());
            ps.setString(2,usuarioModel.getContrasena());
            ps.setInt(3, usuarioModel.getIdUsuario());
            ps.execute();
            return true;
        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }


    }
    public int ExisteUsuario(String usuario){

        ResultSet st = null;
        String sql = "Select count(idUsuario) from usuario where Correo = ?";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setString(1,usuario);
                st=ps.executeQuery();
                if(st.next()){
                    return st.getInt(1);
                }{
                    return 1;
            }

            }catch (SQLException ex ){
            Logger.getLogger(UsuarioModel.class.getName()).log(Level.SEVERE,null,ex);
            return 1;
        }

    }

    public boolean esEmail(String correo){

        Pattern pattern = Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                +"[A-Za-z0-9-]+(\\.[A-Za-z{2,}$])");
        Matcher mather = pattern.matcher(correo);
        return  mather.find();
    }


    public boolean LogIn(UsuarioModel usr,InstitucionModelo ins, municipio m){

        ResultSet st = null;
        String sql = "Select u.idUsuario, u.Correo, u.Constrasena, u.idRol, r.tipoUsuario,u.Nombre, u.Apellido,i.NombreInstitucion, m.NombreMunicipio from usuario u inner join Roles r on r.idRol = u.idRol inner join institucion i on i.idInstitucionAuto = u.idInstitucion inner join municipio m on m.idMunicipio = i.idMunicipio where u.Correo = ? ";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,usr.getCorreo());
            st=ps.executeQuery();
            if(st.next()){
                if(usr.getContrasena().equals(st.getString(3))){
                    usr.setIdUsuario(st.getInt("idUsuario"));
                    usr.setCorreo(st.getString(2));
                    usr.setContrasena(st.getString(3));
                     usr.setTipoUsuario(st.getString(5));
                     usr.setNombre(st.getString(6));
                     usr.setApellido(st.getString(7));
                     ins.setNombreInstitucion(st.getString(8));
                     m.setNombreM(st.getString(9));
                    return true;
                }else {
                    return false;
                }

            }{
                return false;
            }

        }catch (SQLException ex ){
            Logger.getLogger(UsuarioModel.class.getName()).log(Level.SEVERE,null,ex);
            return false;
        }

    }

}
