package producto2.dao;

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
import producto2.dominio.Empaque;
import producto2.dominio.SubProducto;
import producto2.to.TOProducto;
import producto2.to.TOProductoCombo;
import usuarios.dominio.UsuarioSesion;

/**
 *
 * @author jesc
 */
public class DAOProductosBuscar {
    private DataSource ds;
    
    public DAOProductosBuscar() throws NamingException {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            UsuarioSesion usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");
            
            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/"+usuarioSesion.getJndi());
        } catch (NamingException ex) {
            throw(ex);
        }
    }
    
//    public ArrayList<TOProducto> obtenerCombo(int idProducto) throws SQLException {
//        ArrayList<TOProducto> productos=new ArrayList<TOProducto>();
//        String strSQL=""
//                + "SELECT e.idEmpaque, e.cod_pro, e.idProducto, e.piezas, e.dun14, e.peso, e.volumen"
//                + "     , u.idUnidad as idUnidadEmpaque, u.unidad as unidadEmpaque, u.abreviatura as abreviaturaEmpaque"
//                + "     , isnull(se.idEmpaque, 0) as idSubEmpaque, se.piezas as piezasSubEmpaque"
//                + "     , su.idUnidad as idUnidadSubEmpaque, su.unidad as unidadSubEmpaque, su.abreviatura as abreviaturaSubEmpaque "
//                + "FROM empaquesCombos c "
//                + "INNER JOIN empaques e ON e.idEmpaque=c.idSubEmpaque "
//                + "INNER JOIN empaquesUnidades u ON u.idUnidad=e.idUnidadEmpaque "
//                + "LEFT JOIN empaques se ON se.idEmpaque=e.idSubEmpaque "
//                + "LEFT JOIN empaquesUnidades su ON su.idUnidad=se.idUnidadEmpaque "
//                + "WHERE c.idEmpaque="+idProducto;
//        Connection cn=ds.getConnection();
//        Statement st=cn.createStatement();
//        try {
//            ResultSet rs=st.executeQuery(strSQL);
//            while(rs.next()) {
//                productos.add(this.construir(rs));
//            }
//        } finally {
//            cn.close();
//        }
//        return productos;
//    }
    
    public ArrayList<TOProducto> obtenerProductosClasificacion(int idGrupo, int idSubGrupo) throws SQLException {
        ArrayList<TOProducto> productos=new ArrayList<TOProducto>();
        String strSQL=sqlEmpaque()+" "+
            "INNER JOIN productos p on p.idProducto=e.idProducto " +
            "LEFT JOIN productosPartes pp on pp.idParte=p.idParte " +
            "WHERE p.idGrupo="+idGrupo+" OR p.idSubGrupo="+idSubGrupo+" " +
            "ORDER BY pp.parte, p.descripcion";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                productos.add(this.construir(rs));
            }
        } finally {
            cn.close();
        }
        return productos;
    }
    
    public ArrayList<TOProducto> obtenerProductosDescripcion(String descripcion) throws SQLException {
        ArrayList<TOProducto> productos=new ArrayList<TOProducto>();
        String strSQL=sqlEmpaque()+" "+
            "INNER JOIN productos p on p.idProducto=e.idProducto " +
            "LEFT JOIN productosPartes pp on pp.idParte=p.idParte " +
            "WHERE p.descripcion like '%"+descripcion+"%' "+
            "ORDER BY pp.parte, p.descripcion";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                productos.add(this.construir(rs));
            }
        } finally {
            cn.close();
        }
        return productos;
    }
    
    public ArrayList<TOProducto> obtenerProductosParte(int idParte) throws SQLException {
        ArrayList<TOProducto> productos=new ArrayList<TOProducto>();
        String strSQL=sqlEmpaque()+" "+
            "INNER JOIN productos p on p.idProducto=e.idProducto " +
            "WHERE p.idParte="+idParte+" "+
            "ORDER BY p.descripcion";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                productos.add(this.construir(rs));
            }
        } finally {
            cn.close();
        }
        return productos;
    }
    
    public ArrayList<TOProducto> obtenerProductos(int idArticulo) throws SQLException {
        ArrayList<TOProducto> productos=new ArrayList<TOProducto>();
        String strSQL=sqlEmpaque()+ " WHERE e.idProducto="+idArticulo+" ORDER BY cod_pro";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                productos.add(this.construir(rs));
            }
        } finally {
            cn.close();
        }
        return productos;
    }
    
    public TOProducto obtenerProductoSku(String sku) throws SQLException {
        TOProducto to=null;
        String strSQL=sqlEmpaque()+ " WHERE e.cod_pro='"+sku+"'";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                to=this.construir(rs);
            }
        } finally {
            cn.close();
        }
        return to;
    }
    
    public TOProducto obtenerProducto(int idProducto) throws SQLException {
        TOProducto to=null;
        String strSQL=sqlEmpaque()+ " WHERE e.idEmpaque="+idProducto;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                to=this.construir(rs);
            }
        } finally {
            cn.close();
        }
        return to;
    }
    
//    public TOProducto construir(ResultSet rs) throws SQLException {
//        TOProducto to=new TOProducto();
//        to.setIdProducto(rs.getInt("idEmpaque"));
//        to.setCod_pro(rs.getString("cod_pro"));
//        to.setIdArticulo(rs.getInt("idProducto"));
//        to.setPiezas(rs.getInt("piezas"));
//        Empaque empaque=new Empaque(rs.getInt("idUnidadEmpaque"), rs.getString("unidadEmpaque"), rs.getString("abreviaturaEmpaque"));
//        to.setEmpaque(empaque);
//        SubProducto sub=new SubProducto(rs.getInt("idSubEmpaque"), rs.getInt("piezasSubEmpaque"), new Empaque(rs.getInt("idUnidadSubEmpaque"), rs.getString("unidadSubEmpaque"), rs.getString("abreviaturaSubEmpaque")));
//        to.setSubProducto(sub);
//        to.setDun14(rs.getString("dun14"));
//        to.setPeso(rs.getDouble("peso"));
//        to.setVolumen(rs.getDouble("volumen"));
//        return to;
//    }
    
    public TOProducto construir(ResultSet rs) throws SQLException {
        TOProducto to=new TOProducto();
        to.setIdProducto(rs.getInt("idEmpaque"));
        to.setCod_pro(rs.getString("cod_pro"));
        to.setIdArticulo(rs.getInt("idProducto"));
        to.setPiezas(rs.getInt("piezas"));
        Empaque empaque=new Empaque(rs.getInt("idUnidadEmpaque"), rs.getString("unidadEmpaque"), rs.getString("abreviaturaEmpaque"));
        to.setEmpaque(empaque);
        SubProducto sub=new SubProducto(rs.getInt("idSubEmpaque"));
        to.setSubProducto(sub);
        to.setDun14(rs.getString("dun14"));
        to.setPeso(rs.getDouble("peso"));
        to.setVolumen(rs.getDouble("volumen"));
        return to;
    }
    
//    private String sqlEmpaque() {
//        String strSQL=""
//                + "SELECT e.idEmpaque, e.cod_pro, e.idProducto, e.piezas, e.dun14, e.peso, e.volumen"
//                + "     , u.idUnidad as idUnidadEmpaque, u.unidad as unidadEmpaque, u.abreviatura as abreviaturaEmpaque"
//                + "     , isnull(se.idEmpaque, 0) as idSubEmpaque, se.piezas as piezasSubEmpaque"
//                + "     , su.idUnidad as idUnidadSubEmpaque, su.unidad as unidadSubEmpaque, su.abreviatura as abreviaturaSubEmpaque "
//                + "FROM empaques e "
//                + "INNER JOIN empaquesUnidades u ON u.idUnidad=e.idUnidadEmpaque "
//                + "LEFT JOIN empaques se ON se.idEmpaque=e.idSubEmpaque "
//                + "LEFT JOIN empaquesUnidades su ON su.idUnidad=se.idUnidadEmpaque";
//        return strSQL;
//    }
    
     private String sqlEmpaque() {
        String strSQL=""
                + "SELECT e.idEmpaque, e.cod_pro, e.idProducto, e.piezas, e.idSubEmpaque, e.dun14, e.peso, e.volumen"
                + "     , u.idUnidad as idUnidadEmpaque, u.unidad as unidadEmpaque, u.abreviatura as abreviaturaEmpaque "
                + "FROM empaques e "
                + "INNER JOIN empaquesUnidades u ON u.idUnidad=e.idUnidadEmpaque";
        return strSQL;
    }
}
