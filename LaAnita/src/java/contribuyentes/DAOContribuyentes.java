package contribuyentes;

import direccion.dominio.Direccion;
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
 * @author jsolis
 */
public class DAOContribuyentes {

    private DataSource ds;

    public DAOContribuyentes() throws NamingException {
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

    public int grabarRFC(String rfc) throws SQLException {
        int idRfc = 0;
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            st.executeUpdate("begin transaction");
            st.executeUpdate("INSERT INTO contribuyentesRfc (rfc, curp) values ('" + rfc + "', '')");
            ResultSet rs = st.executeQuery("SELECT @@IDENTITY AS idRfc");
            if (rs.next()) {
                idRfc = rs.getInt("idRfc");
            }
            st.executeUpdate("commit transaction");
        } catch (SQLException e) {
            st.executeUpdate("rollback transaction");
            throw (e);
        } finally {
            cn.close();
        }
        return idRfc;
    }

    public int obtenerRfc(String rfc) throws SQLException {
        int idRfc = 0;
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            String strSQL = "SELECT idRfc FROM contribuyentesRfc WHERE rfc='" + rfc + "'";
            ResultSet rs = st.executeQuery(strSQL);
            if (rs.next()) {
                idRfc = rs.getInt("idRfc");
            }
        } finally {
            cn.close();
        }
        return idRfc;
    }

    public ArrayList<Contribuyente> obtenerContribuyentesCliente() throws SQLException {
        ArrayList<Contribuyente> cs = new ArrayList<Contribuyente>();
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        String strSQL = "SELECT c.idContribuyente, contribuyente, cr.idRfc, cr.rfc, c.idDireccion, cr.curp "
                + "FROM contribuyentes c "
                + "inner join contribuyentesRfc cr on cr.idRfc=c.idRfc "
                + "WHERE c.idContribuyente IN (SELECT DISTINCT idContribuyente FROM clientes)";
        try {
            ResultSet rs = st.executeQuery(strSQL);
            while (rs.next()) {
                cs.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return cs;
    }

    public ArrayList<Contribuyente> obtenerContribuyentes(String cadena) throws SQLException {
        ArrayList<Contribuyente> cs = new ArrayList<Contribuyente>();
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        String strSQL = "Select c.idContribuyente, contribuyente, cr.idRfc, cr.rfc, c.idDireccion, cr.curp "
                + "from contribuyentes c "
                + "inner join contribuyentesRfc cr on cr.idRfc=c.idRfc "
                + "where c.contribuyente like '%" + cadena + "%'";
        try {
            ResultSet rs = st.executeQuery(strSQL);
            while (rs.next()) {
                cs.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return cs;
    }

    public Contribuyente obtenerContribuyente(int idContribuyente) throws SQLException {
        Contribuyente to = null;
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        String strSQL = "Select c.idContribuyente, c.contribuyente, cr.idRfc, cr.rfc, c.idDireccion, cr.curp "
                + "from contribuyentes c "
                + "inner join contribuyentesRfc cr on cr.idRfc=c.idRfc "
                + "where c.idContribuyente=" + idContribuyente;
        try {
            ResultSet rs = st.executeQuery(strSQL);
            if (rs.next()) {
                to = construir(rs);
            }
        } finally {
            cn.close();
        }
        return to;
    }

    public ArrayList<Contribuyente> obtenerContribuyentesRFC(String rfc) throws SQLException {
        ArrayList<Contribuyente> cs = new ArrayList<Contribuyente>();
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        String strSQL = "Select c.idContribuyente, contribuyente, cr.idRfc, cr.rfc, c.idDireccion, cr.curp "
                + "from contribuyentes c "
                + "inner join contribuyentesRfc cr on cr.idRfc=c.idRfc "
                + "where cr.rfc='" + rfc + "'";
        try {
            ResultSet rs = st.executeQuery(strSQL);
            while (rs.next()) {
                cs.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return cs;
    }

    private Contribuyente construir(ResultSet rs) throws SQLException {
        Contribuyente contribuyente = new Contribuyente();
        contribuyente.setIdContribuyente(rs.getInt("idContribuyente"));
        contribuyente.setContribuyente(rs.getString("contribuyente"));
        contribuyente.setIdRfc(rs.getInt("idRfc"));
        contribuyente.setRfc(rs.getString("rfc"));
        contribuyente.setCurp(rs.getString("curp"));
        contribuyente.setDireccion(new Direccion());
        contribuyente.getDireccion().setIdDireccion(rs.getInt("idDireccion"));
        return contribuyente;
    }

    public void actualizarContribuyente(Contribuyente contribuyente) throws SQLException {
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        String sql = "UPDATE contribuyentes set contribuyente = '" + contribuyente.getContribuyente() + "' WHERE idContribuyente = " + contribuyente.getIdContribuyente();
        try {
            st.executeUpdate(sql);
        } finally {
            cn.close();
            st.close();
        }
    }

    public void actualizarContribuyenteRfc(Contribuyente contribuyente) throws SQLException {
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        String sql = "UPDATE contribuyentesRfc set  curp='" + contribuyente.getCurp().toUpperCase() + "' WHERE idRfc = " + contribuyente.getIdRfc();
        try {
            st.executeUpdate(sql);
        } finally {
            cn.close();
            st.close();
        }
    }

    public ArrayList<Contribuyente> dameContribuyentes() throws SQLException {
        ArrayList<Contribuyente> lstContribuyente = new ArrayList<Contribuyente>();
        String sql = "SELECT * FROM contribuyentes";
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            Contribuyente contribuyente = new Contribuyente();
            contribuyente.setIdContribuyente(rs.getInt("idContribuyente"));
            contribuyente.setContribuyente(rs.getString("contribuyente"));
            lstContribuyente.add(contribuyente);
        }
        return lstContribuyente;
    }

    public void guardarContribuyente(Contribuyente contribuyente, Direccion direccion) throws SQLException {
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        ResultSet rs = null;
        int idDireccion = 0;
        int idRfc = 0;
        st.executeUpdate("begin transaction");
        try {
            String sqlDireccion = "INSERT INTO direcciones (calle, numeroExterior, numeroInterior, colonia, localidad, referencia, municipio, estado, idPais, codigoPostal, numeroLocalizacion)"
                    + "     VALUES ('" + direccion.getCalle() + "', '" + direccion.getNumeroExterior() + "','" + direccion.getNumeroInterior() + "', '" + direccion.getColonia() + "', '" + direccion.getLocalidad() + "', '" + direccion.getReferencia() + "', '" + direccion.getMunicipio() + "', '" + direccion.getEstado() + "', '" + direccion.getPais().getIdPais() + "', '" + direccion.getCodigoPostal() + "', '" + direccion.getNumeroLocalizacion() + "')";
            st.executeUpdate(sqlDireccion);
            rs = st.executeQuery("SELECT @@IDENTITY AS idDireccion");
            if (rs.next()) {
                idDireccion = rs.getInt("idDireccion");
            }
            String sqlContribuyenteRfc = "INSERT INTO contribuyentesRfc (rfc, curp) VALUES ('" + contribuyente.getRfc().toUpperCase() + "', '" + contribuyente.getCurp() + "')";
            st.executeUpdate(sqlContribuyenteRfc);
            rs = st.executeQuery("SELECT @@IDENTITY AS idRfc");
            if (rs.next()) {
                idRfc = rs.getInt("idRfc");
            }
            String sqlContribuyente = "INSERT INTO contribuyentes (contribuyente, idRfc, idDireccion) VALUES ('" + contribuyente.getContribuyente() + "','" + idRfc + "', '" + idDireccion + "' )";
            st.executeUpdate(sqlContribuyente);
            st.executeUpdate("commit transaction");
        } catch (SQLException ex) {
            Message.Mensajes.mensajeError(ex.getMessage());
            st.executeUpdate("rollback transaction");
            throw ex;
        } finally {
        }
    }

    public Contribuyente buscarContribuyente(String rfc) throws SQLException {
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        Contribuyente contribuyente = new Contribuyente();
        String sql = "select * from contribuyentes cr\n"
                + "inner join contribuyentesRfc crR\n"
                + "on cr.idContribuyente = crR.idRfc\n"
                + "inner join direcciones dir \n"
                + "on cr.idDireccion = dir.idDireccion\n"
                + "where crR.rfc ='" + rfc + "';";
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            contribuyente.setContribuyente(rs.getString("contribuyente"));
            contribuyente.setRfc(rs.getString("rfc"));
            contribuyente.getDireccion().setIdDireccion(rs.getInt("idDireccion"));
            contribuyente.getDireccion().setCalle(rs.getString("calle"));
            contribuyente.getDireccion().setNumeroExterior(rs.getString("numeroExterior"));
            contribuyente.getDireccion().setNumeroInterior(rs.getString("numeroInterior"));
            contribuyente.getDireccion().setColonia(rs.getString("colonia"));
            contribuyente.getDireccion().setLocalidad(rs.getString("localidad"));
            contribuyente.getDireccion().setReferencia(rs.getString("referencia"));
            contribuyente.getDireccion().setMunicipio(rs.getString("municipio"));
            contribuyente.getDireccion().setEstado(rs.getString("estado"));
            contribuyente.getDireccion().getPais().setIdPais(rs.getInt("idPais"));
            contribuyente.getDireccion().setCodigoPostal(rs.getString("codigoPostal"));
        }
        return contribuyente;
    }

    public ArrayList<Contribuyente> dameRfcContribuyente(String query) throws SQLException {
        ArrayList<Contribuyente> lstContribuyente = new ArrayList<Contribuyente>();
        String slqVerificarContribuyente = "select rfc from contribuyentes cntr \n"
                + "inner join contribuyentesRfc rfc \n"
                + "on cntr.idRfc = rfc.idRfc WHERE rfc like '%" + query + "%'";
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs = st.executeQuery(slqVerificarContribuyente);
            while (rs.next()) {
                Contribuyente contribuyente = new Contribuyente();
                contribuyente.setRfc(rs.getString("rfc"));
                lstContribuyente.add(contribuyente);
            }
        } finally {
            cn.close();
        }
        return lstContribuyente;
    }

    public boolean verificarContribuyente(String rfc) throws SQLException {
        boolean ok = false;
        Contribuyente contribuyente = new Contribuyente();
        String sqlVerificar = "SELECT * FROM contribuyentes con "
                + "inner join contribuyentesRfc crRfc "
                + "on con.idRfc = crRfc.idRfc "
                + "where crRfc.rfc='" + rfc.trim() + "';";
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs = st.executeQuery(rfc);
            if (rs.getRow() > 0) {
                ok = true;
            }
        } finally {
            cn.close();
        }
        return ok;
    }
}
