package formulas.dao;

import formulas.to.TOFormula;
import formulas.to.TOInsumo;
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
public class DAOFormulas {
    private DataSource ds;

    public DAOFormulas() throws NamingException {
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
    
    private TOInsumo construirInsumo(ResultSet rs) throws SQLException {
        TOInsumo to=new TOInsumo();
        to.setIdEmpaque(rs.getInt("idProducto"));
        to.setCantidad(rs.getDouble("cantidad"));
        to.setPorcentVariacion(rs.getDouble("porcentVariacion"));
        to.setCostoUnitarioPromedio(rs.getDouble("costoUnitarioPromedio"));
        return to;
    }
    
    public ArrayList<TOInsumo> obtenerInsumos(int idFormula) throws SQLException {
        ArrayList<TOInsumo> insumos=new ArrayList<TOInsumo>();
        String strSQL="SELECT * FROM formulasInsumos WHERE idFormula="+idFormula;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                insumos.add(construirInsumo(rs));
            }
        } finally {
            st.close();
            cn.close();
        }
        return insumos;
    }
    
    public void eliminarInsumo(int idFormula, int idEmpaque) throws SQLException {
        String strSQL="DELETE FROM formulasInsumos "
                    + "WHERE idFormula="+idFormula+" AND idProducto="+idEmpaque;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate(strSQL);
        } finally {
            st.close();
            cn.close();
        } 
    }
    
    public void modificarInsumo(int idFormula, TOInsumo to) throws SQLException {
        String strSQL="UPDATE formulasInsumos "
                    + "SET cantidad="+to.getCantidad()+", porcentVariacion="+to.getPorcentVariacion()+", costoUnitarioPromedio="+to.getCostoUnitarioPromedio()+" "
                    + "WHERE idFormula="+idFormula+" AND idProducto="+to.getIdEmpaque();
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate(strSQL);
        } finally {
            st.close();
            cn.close();
        } 
    }
    
    public double agregarInsumo(int idFormula, int idEmpresa, TOInsumo to) throws SQLException {
        double costoPromedio=0;
        String strSQL="SELECT costoUnitarioPromedio FROM formulasInsumos WHERE idFormula="+idFormula+" AND idProducto="+to.getIdEmpaque();
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("begin Transaction");
            
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                costoPromedio=rs.getDouble("costoUnitarioPromedio");
            }
            strSQL="INSERT INTO formulasInsumos (idFormula, idProducto, cantidad, porcentVariacion, costoUnitarioPromedio, costoUnitario) "
                + "VALUES ("+idFormula+", "+to.getIdEmpaque()+", "+to.getCantidad()+", "+to.getPorcentVariacion()+", "+costoPromedio+", 0.00)";
            st.executeUpdate(strSQL);
            
            st.executeUpdate("commit Transaction");
        } catch (SQLException e) {
            st.executeUpdate("rollback Transaction");
            throw(e);
        } finally {
            st.close();
            cn.close();
        }
        return costoPromedio;
    }
    
    private TOFormula construirFormula(ResultSet rs) throws SQLException {
        TOFormula to=new TOFormula();
        to.setIdFormula(rs.getInt("idFormula"));
        to.setIdEmpresa(rs.getInt("idEmpresa"));
        to.setIdEmpaque(rs.getInt("idEmpaque"));
        to.setMerma(rs.getDouble("porcentMerma"));
        to.setCostoPromedio(rs.getDouble("costoUnitarioPromedio"));
        to.setObservaciones(rs.getString("observaciones"));
        return to;
    }
    
    public TOFormula obtenerFormula(int idEmpaque) throws SQLException {
        TOFormula to=new TOFormula();
        String strSQL="SELECT * FROM formulas WHERE idEmpaque="+idEmpaque;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                to=construirFormula(rs);
            }
        } finally {
            st.close();
            cn.close();
        }
        return to;
    }
    
    public void modificarFormula(TOFormula to) throws SQLException {
        String strSQL="UPDATE formulas "
                    + "SET porcentMerma="+to.getMerma()+", costoUnitarioPromedio="+to.getCostoPromedio()+", observaciones='"+to.getObservaciones()+"' "
                    + "WHERE idFormula="+to.getIdFormula();
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate(strSQL);
        } finally {
            st.close();
            cn.close();
        } 
    }
    
    public int agregarFormula(TOFormula to) throws SQLException {
        int idFormula=0;
        String strSQL="INSERT INTO formulas (idEmpresa, idEmpaque, porcentMerma, costoUnitarioPromedio, observaciones) "
                + "VALUES ("+to.getIdEmpresa()+", "+to.getIdEmpaque()+", "+to.getMerma()+", "+to.getCostoPromedio()+", '"+to.getObservaciones()+"')";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("begin Transaction");
            
            st.executeUpdate(strSQL);
            ResultSet rs=st.executeQuery("SELECT @@IDENTITY AS idFormula");
            if(rs.next()) {
                idFormula=rs.getInt("idFormula");
            }
            st.executeUpdate("commit Transaction");
        } catch(SQLException e) {
            st.executeUpdate("rollback Transaction");
            throw(e);
        } finally {
            st.close();
            cn.close();
        }
        return idFormula;
    }
}
