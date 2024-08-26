package Modelo.Consultas;
import Modelo.Conexion;
import Modelo.modelo.Rol;
import Modelo.modelo.UsuarioModel;

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

    public boolean insersartUsuario(String municipio, UsuarioModel usuarioModel, Rol rol){
        String sql = "call InsertarUsuario(?,?,?,?,?,?,?)";
        try(PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,usuarioModel.getNombre());
            ps.setString(2,usuarioModel.getApellido());
            ps.setString(3, usuarioModel.getCorreo());
            ps.setString(4, usuarioModel.getContrasena());
            ps.setString(5, usuarioModel.getIdInstitucion());
            ps.setString(6, rol.getTipoUsuario());
            ps.setString(7, municipio);
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
}
