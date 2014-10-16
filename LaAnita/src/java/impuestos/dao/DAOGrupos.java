package impuestos.dao;

import impuestos.dominio.Impuesto;
import impuestos.dominio.ImpuestoGrupo;
import java.sql.Connection;
import java.sql.PreparedStatement;
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
 * @author JULIOS
 */
public class DAOGrupos {
    private DataSource ds;
    
    public DAOGrupos() throws NamingException {
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
    
    public ArrayList<Impuesto> eliminarImpuesto(int idGrupo, int idImpuesto) throws SQLException {
        ArrayList<Impuesto> impuestos=new ArrayList<Impuesto>();
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            st.executeUpdate("begin Transaction");
            st.executeUpdate("DELETE FROM impuestosGruposDetalle WHERE idGrupo="+idGrupo+" AND idImpuesto="+idImpuesto);
            ResultSet rs=st.executeQuery(sqlAgregados(idGrupo));
            while(rs.next()) {
                impuestos.add(new Impuesto(rs.getInt("idImpuesto"),rs.getString("impuesto"), rs.getBoolean("aplicable"), rs.getInt("modo"), rs.getBoolean("acreditable"), rs.getBoolean("acumulable")));
            }
            st.executeUpdate("commit Transaction");
        } catch (SQLException ex) {
            st.executeUpdate("rollback Transaction");
            throw (ex);
        } finally {
            cn.close();
        }
        return impuestos;
    }
    
    private String sqlAgregados(int idGrupo) {
        String strSQL="SELECT i.* "
                    + "FROM impuestosGruposDetalle gd "
                    + "INNER JOIN impuestos i ON i.idImpuesto=gd.idImpuesto "
                    + "WHERE gd.idGrupo="+idGrupo;
        return strSQL;
    }
    
    public ArrayList<Impuesto> agregarImpuesto(int idGrupo, int idImpuesto) throws SQLException {
        ArrayList<Impuesto> impuestos=new ArrayList<Impuesto>();
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            st.executeUpdate("begin Transaction");
            st.executeUpdate("INSERT INTO impuestosGruposDetalle (idGrupo, idImpuesto) VALUES ("+idGrupo+", "+idImpuesto+")");
            ResultSet rs=st.executeQuery(sqlAgregados(idGrupo));
            while(rs.next()) {
                impuestos.add(new Impuesto(rs.getInt("idImpuesto"),rs.getString("impuesto"), rs.getBoolean("aplicable"), rs.getInt("modo"), rs.getBoolean("acreditable"), rs.getBoolean("acumulable")));
            }
            st.executeUpdate("commit Transaction");
        } catch (SQLException ex) {
            st.executeUpdate("rollback Transaction");
            throw (ex);
        } finally {
            cn.close();
        }
        return impuestos;
    }
    
    public ArrayList<Impuesto> obtenerImpuestosAgregados(int idGrupo) throws SQLException {
        ArrayList<Impuesto> impuestos=new ArrayList<Impuesto>();
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(sqlAgregados(idGrupo));
            while(rs.next()) {
                impuestos.add(new Impuesto(rs.getInt("idImpuesto"),rs.getString("impuesto"), rs.getBoolean("aplicable"), rs.getInt("modo"), rs.getBoolean("acreditable"), rs.getBoolean("acumulable")));
            }
        } finally {
            cn.close();
        }
        return impuestos;
    }
    
    public ArrayList<Impuesto> obtenerImpuestosDisponibles(int idGrupo) throws SQLException {
        ArrayList<Impuesto> impuestos=new ArrayList<Impuesto>();
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT * FROM impuestos "
                    + "WHERE idImpuesto not in (SELECT idImpuesto FROM impuestosGruposDetalle WHERE idGrupo="+idGrupo+")");
            while(rs.next()) {
                impuestos.add(new Impuesto(rs.getInt("idImpuesto"),rs.getString("impuesto"), rs.getBoolean("aplicable"), rs.getInt("modo"), rs.getBoolean("acreditable"), rs.getBoolean("acumulable")));
            }
        } finally {
            cn.close();
        }
        return impuestos;
    }
    
    public void modificarDetalle(int idGrupo, ArrayList<Impuesto> impuestos) throws SQLException {
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        PreparedStatement pSt = cn.prepareStatement("INSERT INTO impuestosGruposDetalle (idGrupo, idImpuesto) VALUES (?, ?)");
        try {
            st.executeUpdate("begin Transaction");
            st.executeUpdate("DELETE FROM impuestosGruposDetalle WHERE idGrupo="+idGrupo);
            for(Impuesto i: impuestos) {
                pSt.setInt(idGrupo, 1);
                pSt.setInt(i.getIdImpuesto(), 2);
                pSt.executeUpdate();
            }
            st.executeUpdate("commit Transaction");
        } catch (SQLException ex) {
            st.executeUpdate("rollback Transaction");
            throw (ex);
        } finally {
            cn.close();
        }
    }
    
    public void modificar(ImpuestoGrupo grupo) throws SQLException {
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            st.executeUpdate("UPDATE impuestosGrupos SET grupo='"+grupo.getGrupo()+"' WHERE idGrupo="+grupo.getIdGrupo());
        } finally {
            cn.close();
        }
    }
    
    public boolean eliminar(int idGrupo) throws SQLException {
        boolean ok=false;
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            st.executeUpdate("begin Transaction");
            ResultSet rs=st.executeQuery("SELECT * FROM impuestosDetalle WHERE idGrupo="+idGrupo);
            if(!rs.next()) {
                st.executeUpdate("DELETE FROM impuestosGrupos WHERE idGrupo="+idGrupo);
                st.executeUpdate("DELETE FROM impuestosGruposDetalle WHERE idGrupo="+idGrupo);
                ok=true;
            }
            st.executeUpdate("commit Transaction");
        } catch (SQLException ex) {
            st.executeUpdate("rollback Transaction");
            throw (ex);
        } finally {
            cn.close();
        }
        return ok;
    }
    
    public int agregar(ImpuestoGrupo grupo) throws SQLException {
        int idGrupo=0;
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            st.executeUpdate("begin Transaction");
            st.executeUpdate("INSERT INTO impuestosGrupos (grupo) VALUES ('"+grupo.getGrupo()+"')");
            ResultSet rs=st.executeQuery("SELECT MAX(idGrupo) AS idGrupo FROM impuestosGrupos");
            if(rs.next()) {
                idGrupo=rs.getInt("idGrupo");
            }
            st.executeUpdate("commit Transaction");
        } catch (SQLException ex) {
            st.executeUpdate("rollback Transaction");
            throw (ex);
        } finally {
            cn.close();
        }
        return idGrupo;
    }
    
    public ImpuestoGrupo obtenerGrupo(int idGrupo) throws SQLException {
        ImpuestoGrupo impuesto=null;
        String strSQL="SELECT * FROM impuestosGrupos WHERE idGrupo="+idGrupo;
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                impuesto=new ImpuestoGrupo(rs.getInt("idGrupo"), rs.getString("grupo"));
            }
        } finally {
            cn.close();
        }
        return impuesto;
    }
    
    public ArrayList<ImpuestoGrupo> obtenerGrupos() throws SQLException {
        ArrayList<ImpuestoGrupo> impuestos=new ArrayList<ImpuestoGrupo>();
        String strSQL="SELECT * FROM impuestosGrupos";
        Connection cn=ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                impuestos.add(new ImpuestoGrupo(rs.getInt("idGrupo"), rs.getString("grupo")));
            }
        } finally {
            cn.close();
        }
        return impuestos;
    }
}
