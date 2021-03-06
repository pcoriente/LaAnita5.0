package empresas.dao;

import direccion.dao.DAODireccion;
import empresas.dominio.Empresa;
import empresas.to.TOEmpresa;
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

public class DAOEmpresas {
    private  DataSource ds=null;

    public DAOEmpresas() throws NamingException {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            UsuarioSesion usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");

            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/"+usuarioSesion.getJndi());
        } catch (NamingException ex) {
            throw (ex);
        }
    }

    public ArrayList<TOEmpresa> obtenerEmpresa() throws SQLException {
        ArrayList<TOEmpresa> lista = new ArrayList<TOEmpresa>();
        ResultSet rs;
        Connection cn = ds.getConnection();
        try {
            String stringSQL = "SELECT * FROM empresasGrupo";
            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(stringSQL);
            while (rs.next()) {
                lista.add(construir(rs));
            }
        } finally {
            cn.close();
        }
        return lista;
    }

    private TOEmpresa construir(ResultSet rs) throws SQLException {
        TOEmpresa to = new TOEmpresa();

        to.setIdEmpresa(rs.getInt("idEmpresa"));
        to.setCodigoEmpresa(rs.getInt("codigoEmpresa"));
        to.setEmpresa(rs.getString("empresa"));
        to.setNombreComercial(rs.getString("nombreComercial"));
        to.setRfc(rs.getString("rfc"));
        to.setTelefono(rs.getString("telefono"));
        to.setFax(rs.getString("fax"));
        to.seteMail(rs.getString("eMail"));
        to.setRepresentanteLegal(rs.getString("representanteLegal"));
        to.setIdDireccion(rs.getInt("idDireccion"));
        return to;
    }

    //DAVID
     public ArrayList<Empresa> obtenerComboEmpresa() throws SQLException, NamingException {
        ArrayList<Empresa> listaCombo = new ArrayList<Empresa>();
        ResultSet rs;
        Connection cn = ds.getConnection();
        try {
            String stringSQL = "SELECT * FROM empresasGrupo";
            Statement sentencia = cn.createStatement();
            rs = sentencia.executeQuery(stringSQL);
            while (rs.next()) {
                listaCombo.add(construirCombo(rs));
            }
        } finally {
            cn.close();
        }
        return listaCombo;
    }
     
     public Empresa obtenerEmpresaConverter(int idEmpresa) throws SQLException, NamingException {
        Empresa to = null;
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs = st.executeQuery("SELECT * FROM empresasGrupo WHERE idEmpresa=" + idEmpresa);
            if (rs.next()) {
                to = construirCombo(rs);
            }
        } finally {
            cn.close();
        }
        return to;
    }

    private Empresa construirCombo(ResultSet rs) throws SQLException, NamingException {
        Empresa to = new Empresa();
        DAODireccion daoD = new DAODireccion();

        to.setIdEmpresa(rs.getInt("idEmpresa"));
        to.setCodigoEmpresa(rs.getInt("codigoEmpresa"));
        to.setEmpresa(rs.getString("empresa"));
        to.setNombreComercial(rs.getString("nombreComercial"));
        to.setRfc(rs.getString("rfc"));
        to.setTelefono(rs.getString("telefono"));
        to.setFax(rs.getString("fax"));
        to.seteMail(rs.getString("eMail"));
        to.setRepresentanteLegal(rs.getString("representanteLegal"));
      //  to.setIdDireccion(rs.getInt("idDireccion"));
        to.setDireccion(daoD.obtenerDireccion(rs.getInt("idDireccion")));
        return to;
    }
//FIN DAVID
    
    public int ultimoEmpresa() throws SQLException {
        int ultimo = 0;
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs = st.executeQuery("SELECT MAX(codigoEmpresa) as ultimo FROM empresasGrupo");
            if (rs.next()) {
                ultimo = rs.getInt("ultimo");
            }
        } finally {
            cn.close();
        }
        return ultimo;
    }

    public TOEmpresa obtenerEmpresa(int idEmpresa) throws SQLException {
        TOEmpresa to = null;
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs = st.executeQuery("SELECT * FROM empresasGrupo WHERE idEmpresa=" + idEmpresa);
            if (rs.next()) {
                to = construir(rs);
            }
        } finally {
            cn.close();
        }
        return to;
    }

    //MODELO DE CONEXION CON SQL
    public int agregar(int codigo, String strEmpresa, String nombreComercial, String rfc, String telefono, String fax, String correo, String representanteLegal, int idDireccion) throws SQLException {
        int idEmpresa = 0;
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            st.executeUpdate("begin Transaction");
            st.executeUpdate("INSERT INTO empresasGrupo (codigoEmpresa, empresa, nombreComercial, rfc, telefono, fax, eMail, representanteLegal, idDireccion) "
                    + "VALUES(" + codigo + ", '" + strEmpresa + "', '" + nombreComercial + "', '" + rfc + "', '" + telefono + "', '" + fax + "', '" + correo + "', '" + representanteLegal + "', " + idDireccion + ")");
            ResultSet rs = st.executeQuery("SELECT MAX(idEmpresa) AS idEmpresa FROM empresasGrupo");
            if (rs.next()) {
                idEmpresa = rs.getInt("idEmpresa");
            }
            st.executeUpdate("commit Transaction");
        } catch (SQLException ex) {
            st.executeUpdate("rollback Transaction");
            throw (ex);
        } finally {
            cn.close();
        }
        return idEmpresa;
    }

    public void modificar(int idEmpresa, String strEmpresa, String nombreComercial, String rfc, String telefono, String fax, String correo, String representanteLegal, int idDireccion) throws SQLException {
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            st.executeUpdate("UPDATE empresasGrupo SET empresa='" + strEmpresa + "', nombreComercial='" + nombreComercial + "', rfc='" + rfc + "', telefono='" + telefono + "', fax='" + fax + "', eMail='" + correo + "', representanteLegal='" + representanteLegal + "', idDireccion=" + idDireccion + " "
                    + "WHERE idEmpresa=" + idEmpresa);
        } finally {
            cn.close();
        }
    }
}
