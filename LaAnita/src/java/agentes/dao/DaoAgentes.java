/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes.dao;

import agentes.dominio.Agentes;
import contactos.dominio.Telefono;
import contribuyentes.Contribuyente;
import direccion.dominio.Asentamiento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
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
 * @author Anita
 */
public class DaoAgentes {

    private DataSource ds = null;

    public DaoAgentes() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            UsuarioSesion usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");

            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/" + usuarioSesion.getJndi());
        } catch (NamingException ex) {
            try {
                throw (ex);
            } catch (NamingException ex1) {
                Logger.getLogger(DaoAgentes.class.getName()).log(Level.SEVERE, null, ex1);
            }
        }
    }

    public ArrayList<Agentes> listaAgentes() throws SQLException {
        ArrayList<Agentes> listagentes = new ArrayList<Agentes>();
//        String slq = "select * from agentes a \n"
//                + "inner join contribuyentes c\n"
//                + "on c.idContribuyente = a.idContribuyente \n"
//                + "inner join direcciones dc\n"
//                + "on dc.idDireccion = c.idDireccion\n"
//                + "inner join direcciones d \n"
//                + "on d.idDireccion = a.idDireccion\n"
//                + "inner join cedis cd\n"
//                + "on cd.idCedis = a.idCedis\n"
//                + "inner join contribuyentesRfc cR\n"
//                + "on cR.idRfc = c.idRfc\n";
        String sql ="SELECT * FROM agentes";
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            Agentes agentes = new Agentes();
            agentes.setIdAgente(rs.getInt("idAgente"));
            agentes.setAgente(rs.getString("agente"));
            agentes.getContribuyente().setIdContribuyente(rs.getInt("idContribuyente"));
            agentes.getDireccionAgente().setIdDireccion(rs.getInt("idDireccion"));
            agentes.getMiniCedis().setIdCedis(rs.getInt("idCedis"));
//            agentes.setIdAgente(rs.getInt("idAgente"));
//            agentes.setAgente(rs.getString("agente"));
//            agentes.getContribuyente().setIdContribuyente(rs.getInt("idContribuyente"));
//            agentes.getMiniCedis().setIdCedis(rs.getInt("idCedis"));
//            agentes.getContribuyente().setContribuyente(rs.getString("contribuyente"));
//            agentes.getContribuyente().setIdRfc(rs.getInt("idRfc"));
//            agentes.getContribuyente().getDireccion().setIdDireccion(rs.getInt("idDireccion"));
//            agentes.getContribuyente().getDireccion().setCalle(rs.getString("calle"));
//            agentes.getContribuyente().getDireccion().setNumeroExterior(rs.getString("numeroExterior"));
//            agentes.getContribuyente().getDireccion().setNumeroInterior(rs.getString("numeroInterior"));
//            agentes.getContribuyente().getDireccion().setColonia(rs.getString("colonia"));
//            agentes.getContribuyente().getDireccion().setLocalidad(rs.getString("localidad"));
//            agentes.getContribuyente().getDireccion().setReferencia(rs.getString("referencia"));
//            agentes.getContribuyente().getDireccion().setMunicipio(rs.getString("municipio"));
//            agentes.getContribuyente().getDireccion().setEstado(rs.getString("estado"));
//            agentes.getContribuyente().getDireccion().getPais().setIdPais(rs.getInt("idPais"));
//            agentes.getContribuyente().getDireccion().setCodigoPostal(rs.getString("codigoPostal"));
//            agentes.getContribuyente().getDireccion().setNumeroLocalizacion("");
//            agentes.getDireccionAgente().setIdDireccion(rs.getInt(22));
//            agentes.getDireccionAgente().setCalle(rs.getString(23));
//            agentes.getDireccionAgente().setNumeroExterior(rs.getString(24));
//            agentes.getDireccionAgente().setNumeroInterior(rs.getString(25));
//            agentes.getDireccionAgente().setColonia(rs.getString(26));
//            agentes.getDireccionAgente().setLocalidad(rs.getString(27));
//            agentes.getDireccionAgente().setReferencia(rs.getString(28));
//            agentes.getDireccionAgente().setMunicipio(rs.getString(29));
//            agentes.getDireccionAgente().setEstado(rs.getString(30));
//            agentes.getDireccionAgente().getPais().setIdPais(rs.getInt(31));
//            agentes.getDireccionAgente().setCodigoPostal(rs.getString(32));
//            agentes.getDireccionAgente().setNumeroLocalizacion("");
//            agentes.getMiniCedis().setCedis(rs.getString("cedis"));
//            agentes.getContribuyente().setRfc(rs.getString("rfc"));
//            agentes.getContribuyente().setCurp(rs.getString("curp"));
//            agentes.getContacto().setIdContacto(rs.getInt("idContacto"));
//            agentes.getContacto().setCorreo(rs.getString("correo"));
            listagentes.add(agentes);
        }
        return listagentes;
    }

    public boolean guardarAgentes(Agentes agente) throws SQLException {
        boolean x = false;
        Connection cn;
        Statement st = null;
        cn = ds.getConnection();
        st = cn.createStatement();
        try {
            ResultSet rs;
//            int idPais = 0;
            int idDireccionContribuyente = 0;
            int idDireccionAgente = 0;
            int idRfc = 0;
            int idContribuyente = 0;
            int idAgente = 0;
            int idContacto = 0;
            st.executeUpdate("begin transaction");

            if (agente.getContribuyente().getRfc() != "") {
                String sqlContribuyenteRfc = "INSERT INTO contribuyentesRfc (rfc, curp) VALUES ('" + agente.getContribuyente().getRfc().toUpperCase() + "', '" + agente.getContribuyente().getCurp().toUpperCase() + "')";
                st.executeUpdate(sqlContribuyenteRfc);
                rs = st.executeQuery("SELECT @@IDENTITY AS idContribuyenteRfc");
                if (rs.next()) {
                    idRfc = rs.getInt("idContribuyenteRfc");
                }
            }
            if (agente.getDireccionAgente().getCalle() != "") {
                String sqlDireccionAgente = "INSERT INTO direcciones (calle, numeroExterior, numeroInterior, colonia, localidad, referencia, municipio, estado, idPais, codigoPostal, numeroLocalizacion)VALUES('" + agente.getDireccionAgente().getCalle() + "', '" + agente.getDireccionAgente().getNumeroExterior() + "','" + agente.getDireccionAgente().getNumeroInterior() + "','" + agente.getDireccionAgente().getColonia() + "','" + agente.getDireccionAgente().getLocalidad() + "','" + agente.getDireccionAgente().getReferencia() + "','" + agente.getDireccionAgente().getMunicipio() + "','" + agente.getDireccionAgente().getEstado() + "','" + agente.getDireccionAgente().getPais().getIdPais() + "','" + agente.getDireccionAgente().getCodigoPostal() + "','0')";
                st.executeUpdate(sqlDireccionAgente);
                rs = st.executeQuery("SELECT @@IDENTITY AS idDireccionAgente");
                if (rs.next()) {
                    idDireccionAgente = rs.getInt("idDireccionAgente");
                }
            }
            if (agente.getContribuyente().getDireccion().getCalle() != "") {
                String sqlDireccionContribuyente = "INSERT INTO direcciones (calle, numeroExterior, numeroInterior, colonia, localidad, referencia, municipio, estado, idPais, codigoPostal,numeroLocalizacion)VALUES('" + agente.getContribuyente().getDireccion().getCalle() + "', '" + agente.getContribuyente().getDireccion().getNumeroExterior() + "','" + agente.getContribuyente().getDireccion().getNumeroInterior() + "','" + agente.getContribuyente().getDireccion().getColonia() + "','" + agente.getContribuyente().getDireccion().getLocalidad() + "','" + agente.getContribuyente().getDireccion().getReferencia() + "','" + agente.getContribuyente().getDireccion().getMunicipio() + "','" + agente.getContribuyente().getDireccion().getEstado() + "','" + agente.getContribuyente().getDireccion().getPais().getIdPais() + "','" + agente.getContribuyente().getDireccion().getCodigoPostal() + "','0')";
                st.executeUpdate(sqlDireccionContribuyente);
                rs = st.executeQuery("SELECT @@IDENTITY AS idDireccionContribuyente");
                if (rs.next()) {
                    idDireccionContribuyente = rs.getInt("idDireccionContribuyente");
                }
                String sqlContribuyente = "INSERT INTO contribuyentes (contribuyente, idRfc, idDireccion) values('" + agente.getContribuyente().getContribuyente() + "','" + idRfc + "','" + idDireccionContribuyente + "')";
                st.executeUpdate(sqlContribuyente);
                rs = st.executeQuery("SELECT @@IDENTITY AS idContribuyente");
                if (rs.next()) {
                    idContribuyente = rs.getInt("idContribuyente");
                }
            }

            String sqlAgentes = "INSERT INTO agentes (agente, idContribuyente, idDireccion, idCedis) VALUES('" + agente.getAgente() + "','" + idContribuyente + "','" + idDireccionAgente + "','" + agente.getMiniCedis().getIdCedis() + "')";
            st.executeUpdate(sqlAgentes);
            rs = st.executeQuery("SELECT @@IDENTITY AS idAgente");
            if (rs.next()) {
                idAgente = rs.getInt("idAgente");
            }
            String sqlContactos = "INSERT INTO contactos(contacto ,puesto, correo, idTipo, idPadre) VALUES('" + agente.getAgente() + "','Agente','" + agente.getContacto().getCorreo() + "','3','" + idAgente + "')";
            st.executeUpdate(sqlContactos);
            rs = st.executeQuery("SELECT @@IDENTITY AS idContacto");
            if (rs.next()) {
                idContacto = rs.getInt("idContacto");
            }
            x = true;
            PreparedStatement ps = null;
            if (agente.getContacto().getTelefonos().size() > 0) {
                for (Telefono telefonos : agente.getContacto().getTelefonos()) {
                    int idTipo = 0;
                    if (telefonos.getTipo().isCelular() == false) {
                        idTipo = 2;
                    } else {
                        idTipo = 1;
                    }
                    String sql = "INSERT INTO telefonos (lada, telefono, extension, idTipo, idContacto) VALUES ("
                            + "'" + telefonos.getLada() + "','" + telefonos.getTelefono() + "','" + telefonos.getExtension() + "','" + idTipo + "','" + idContacto + "') ";
                    ps = cn.prepareStatement(sql);
                    ps.executeUpdate();
                    ps.close();
                }
            }
            st.executeUpdate("commit transaction");
        } catch (SQLException ex) {
            System.err.println(ex);
            st.executeUpdate("rollback transaction");
            throw (ex);
        } finally {
            cn.close();
            st.close();
        }
        return x;
    }

    public void actualizarAgente(Agentes agente, Contribuyente contribuyente) throws SQLException {
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        String sqlContribuyente = "UPDATE contribuyentes set contribuyente = '" + contribuyente.getContribuyente() + "' WHERE idContribuyente = " + contribuyente.getIdContribuyente();
        String sqlContribuyenteRfc = "UPDATE contribuyentesRfc set  curp='" + contribuyente.getCurp().toUpperCase() + "' WHERE idRfc = " + contribuyente.getIdRfc();
        String sql = "UPDATE agentes set agente='" + agente.getAgente() + "', idCedis ='" + agente.getMiniCedis().getIdCedis() + "' WHERE idAgente=" + agente.getIdAgente();
        try {
            st.executeUpdate("begin transaction");
            st.executeUpdate(sql);
            st.executeUpdate(sqlContribuyente);
            st.executeUpdate(sqlContribuyenteRfc);
            st.executeUpdate("commit transaction");
        } catch (SQLException ex) {
            st.executeUpdate("rollback transaction");
            throw (ex);
        } finally {

            cn.close();
            st.close();
        }

    }

    public Agentes dameAgentes(int idAgente) throws SQLException {
        Agentes agente = new Agentes();
        Connection cn = null;
        String sql = "SELECT idAgente, agente FROM agentes";
        try {
            cn = ds.getConnection();
            Statement st = cn.createStatement();
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                agente.setIdAgente(rs.getInt("idAgente"));
                agente.setAgente(rs.getString("agente"));
            }
        } finally {
            cn.close();
        }
        return agente;
    }

    public boolean guardarAgentesConContribuyente(Agentes agente) throws SQLException {
        boolean x = false;
        Connection cn;
        Statement st = null;
        cn = ds.getConnection();
        st = cn.createStatement();
        try {
            ResultSet rs;
            int idDireccionAgente = 0;
            int idAgente = 0;
            int idContacto = 0;
            st.executeUpdate("begin transaction");
            String sqlDireccionContribuyente = "UPDATE direcciones SET calle='" + agente.getContribuyente().getDireccion().getCalle().trim() + "', numeroExterior='" + agente.getContribuyente().getDireccion().getNumeroExterior().trim() + "', numeroInterior='" + agente.getContribuyente().getDireccion().getNumeroInterior().trim() + "', referencia='" + agente.getContribuyente().getDireccion().getReferencia().trim() + "', idPais=" + agente.getContribuyente().getDireccion().getPais().getIdPais() + ", codigoPostal='" + agente.getContribuyente().getDireccion().getCodigoPostal().trim() + "', estado='" + agente.getContribuyente().getDireccion().getEstado().trim() + "', municipio='" + agente.getContribuyente().getDireccion().getMunicipio().trim() + "', localidad='" + agente.getContribuyente().getDireccion().getLocalidad().trim() + "', colonia='" + agente.getContribuyente().getDireccion().getColonia().trim() + "', numeroLocalizacion='" + agente.getContribuyente().getDireccion().getNumeroLocalizacion().trim() + "' WHERE idDireccion=" + agente.getContribuyente().getDireccion().getIdDireccion();
            st.executeUpdate(sqlDireccionContribuyente);
            String sqlDireccionAgente = "INSERT INTO direcciones (calle, numeroExterior, numeroInterior, colonia, localidad, referencia, municipio, estado, idPais, codigoPostal, numeroLocalizacion)VALUES('" + agente.getDireccionAgente().getCalle() + "', '" + agente.getDireccionAgente().getNumeroExterior() + "','" + agente.getDireccionAgente().getNumeroInterior() + "','" + agente.getDireccionAgente().getColonia() + "','" + agente.getDireccionAgente().getLocalidad() + "','" + agente.getDireccionAgente().getReferencia() + "','" + agente.getDireccionAgente().getMunicipio() + "','" + agente.getDireccionAgente().getEstado() + "','" + agente.getDireccionAgente().getPais().getIdPais() + "','" + agente.getDireccionAgente().getCodigoPostal() + "','0')";
            st.executeUpdate(sqlDireccionAgente);
            rs = st.executeQuery("SELECT @@IDENTITY AS idDireccionAgente");
            if (rs.next()) {
                idDireccionAgente = rs.getInt("idDireccionAgente");
            }
            String sqlAgentes = "INSERT INTO agentes (agente, idContribuyente, idDireccion, idCedis) VALUES('" + agente.getAgente() + "','" + agente.getContribuyente().getIdContribuyente() + "','" + idDireccionAgente + "','" + agente.getMiniCedis().getIdCedis() + "')";
            st.executeUpdate(sqlAgentes);
            rs = st.executeQuery("SELECT @@IDENTITY AS idAgente");
            if (rs.next()) {
                idAgente = rs.getInt("idAgente");
            }
            String sqlContactos = "INSERT INTO contactos(contacto ,puesto, correo, idTipo, idPadre) VALUES('" + agente.getAgente() + "','Agente','" + agente.getContacto().getCorreo() + "','3','" + idAgente + "')";
            st.executeUpdate(sqlContactos);
            rs = st.executeQuery("SELECT @@IDENTITY AS idContacto");
            if (rs.next()) {
                idContacto = rs.getInt("idContacto");
            }
            x = true;
            PreparedStatement ps = null;
            if (agente.getContacto().getTelefonos().size() > 0) {
                for (Telefono telefonos : agente.getContacto().getTelefonos()) {
                    int idTipo = 0;
                    if (telefonos.getTipo().isCelular() == false) {
                        idTipo = 2;
                    } else {
                        idTipo = 1;
                    }
                    String sql = "INSERT INTO telefonos (lada, telefono, extension, idTipo, idContacto) VALUES ("
                            + "'" + telefonos.getLada() + "','" + telefonos.getTelefono() + "','" + telefonos.getExtension() + "','" + idTipo + "','" + idContacto + "') ";
                    ps = cn.prepareStatement(sql);
                    ps.executeUpdate();
                    ps.close();
                }
            }
            st.executeUpdate("commit transaction");
        } catch (SQLException ex) {
            System.err.println(ex);
            st.executeUpdate("rollback transaction");
            throw (ex);
        } finally {
            cn.close();
            st.close();
        }
        return x;
    }
}
