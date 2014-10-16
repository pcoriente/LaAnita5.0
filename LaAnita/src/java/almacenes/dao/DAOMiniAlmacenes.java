package almacenes.dao;

import almacenes.dominio.MiniAlmacen;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpSession;
import javax.sql.DataSource;
import usuarios.dominio.UsuarioSesion;

/**
 *
 * @author jesc
 */
public class DAOMiniAlmacenes {
    private DataSource ds;

    public DAOMiniAlmacenes() throws NamingException {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            UsuarioSesion usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");

            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/" + usuarioSesion.getJndi());
        } catch (NamingException ex) {
            throw (ex);
        }
    }
    
    public MiniAlmacen obtenerAlmacen(int idAlmacen) throws SQLException {
        MiniAlmacen mini = null;
        Connection cn = ds.getConnection();
        String strSQL = "select idAlmacen,  almacen " +
                        "from almacenes " +
                        "where idAlmacen="+idAlmacen;
        try {
            Statement sentencia = cn.createStatement();
            ResultSet rs = sentencia.executeQuery(strSQL);
            if (rs.next()) {
                mini=construir(rs);
            }
        } finally {
            cn.close();
        }
        return mini;
    }
    
    public ArrayList<MiniAlmacen> obtenerAlmacenes(int idEmpresa, int idCedis) throws SQLException {
        ArrayList<MiniAlmacen> lista = new ArrayList<MiniAlmacen>();

        Connection cn = ds.getConnection();
        String strSQL = "select idAlmacen, almacen " +
                        "from almacenes " +
                        "where idEmpresa="+idEmpresa+" and idCedis="+idCedis+" " +
                        "order by almacen";
        try {
            Statement sentencia = cn.createStatement();
            ResultSet rs = sentencia.executeQuery(strSQL);
            while (rs.next()) {
                lista.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return lista;
    }
    
    private MiniAlmacen construir(ResultSet rs) throws SQLException {
        MiniAlmacen mini=new MiniAlmacen();
        mini.setIdAlmacen(rs.getInt("idAlmacen"));
        mini.setAlmacen(rs.getString("almacen"));
//        mini.setIdEmpresa(rs.getInt("idEmpresa"));
//        mini.setIdCedis(rs.getInt("idCedis"));
        return mini;
    } 
}
