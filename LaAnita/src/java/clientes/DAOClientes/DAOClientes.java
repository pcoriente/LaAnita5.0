/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clientes.DAOClientes;

import clientes.dominio.Cliente;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
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
 * @author Usuario
 */
public class DAOClientes {

    private DataSource ds = null;

    public DAOClientes() {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            UsuarioSesion usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");

            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/" + usuarioSesion.getJndi());
        } catch (NamingException ex) {
            Logger.getLogger(cliente.dao2.DAOClientes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ArrayList<Cliente> lstClientes() throws SQLException {
        ArrayList<Cliente> lstClientes = new ArrayList<Cliente>();
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        String sql = " SELECT top(40) *  from clientes cl \n"
                + "INNER JOIN direcciones dir \n"
                + "on dir.idDireccion= cl.idDireccion  \n"
                + "inner join contribuyentes contr\n"
                + "on cl.idContribuyente = contr.idContribuyente \n"
                + "inner join contribuyentesRfc contrRfc \n"
                + "on contr.idRfc = contrRfc.idRfc";
        try {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                Cliente cliente = new Cliente();
                cliente.setIdCliente(rs.getInt("idCliente"));
                cliente.setCodigoCliente(rs.getInt("codigoCliente"));
                cliente.setCodigoTienda(rs.getInt("codigoTienda"));
                cliente.setNombreComercial(rs.getString("nombreComercial"));
                cliente.getAgente().setIdAgente(rs.getInt("idAgente"));
                cliente.getClientesGrupos().setIdGrupoCte(rs.getInt("idGrupoCte"));
                cliente.getClientesFormato().setIdFormato(rs.getInt("idFormato"));
                cliente.getContribuyente().setIdContribuyente(rs.getInt("idContribuyente"));
                cliente.getImpuestoZona().setIdZona(rs.getInt("idImpuestoZona"));
                cliente.getDireccion().setIdDireccion(rs.getInt("idDireccion"));
                cliente.getDireccion().setCalle(rs.getString("calle"));
                cliente.getDireccion().setNumeroExterior(rs.getString("numeroExterior"));
                cliente.getDireccion().setNumeroInterior(rs.getString("numeroInterior"));
                cliente.getDireccion().setColonia(rs.getString("colonia"));
                cliente.getDireccion().setLocalidad(rs.getString("localidad"));
                cliente.getDireccion().setReferencia(rs.getString("referencia"));
                cliente.getDireccion().setMunicipio(rs.getString("municipio"));
                cliente.getDireccion().setEstado(rs.getString("estado"));
                cliente.getDireccion().getPais().setIdPais(rs.getInt("idPais"));
                cliente.getDireccion().setCodigoPostal(rs.getString("codigoPostal"));
                cliente.getRuta().setIdRuta(rs.getInt("idRuta"));
                cliente.setFechaAlta(rs.getDate("fechaAlta"));
                cliente.setDiasCredito(rs.getInt("diasCredito"));
                cliente.setLimiteCredito(rs.getFloat("limiteCredito"));
                cliente.setDescuentoComercial(rs.getFloat("desctoComercial"));
                cliente.setDiasBloqueo(rs.getInt("diasBloqueo"));
                cliente.getContribuyente().setRfc(rs.getString("rfc"));

                lstClientes.add(cliente);
            }
        } finally {
            cn.close();
        }
        return lstClientes;
    }

    public Cliente dameInformacionCliente(String rfc) throws SQLException {
        Cliente cliente = new Cliente();
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        String sql = "SELECT *  from clientes cl\n"
                + "INNER JOIN contribuyentes cnt\n"
                + "ON cl.idContribuyente = cnt.idContribuyente\n"
                + "INNER JOIN impuestosZonas iz\n"
                + "ON iz.idZona = cl.idImpuestoZona\n"
                + "INNER JOIN contribuyentesRfc cr \n"
                + "ON cr.idRfc = cnt.idRfc "
                + "inner join direcciones d \n"
                + "on d.idDireccion = cnt.idDireccion "
                + " WHERE rfc ='" + rfc + "'";
        try {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                cliente.setIdCliente(rs.getInt("idcliente"));
                cliente.getContribuyente().setIdContribuyente(rs.getInt("idContribuyente"));
                cliente.getContribuyente().setContribuyente(rs.getString("contribuyente"));
                cliente.setCodigoCliente(rs.getInt("codigoCliente"));
                cliente.getImpuestoZona().setIdZona(rs.getInt("idImpuestoZona"));
                cliente.setDiasCredito(rs.getInt("diasCredito"));
                cliente.setLimiteCredito(rs.getFloat("limiteCredito"));
                cliente.setDescuentoComercial(rs.getFloat("desctoComercial"));
//               cliente.setDescuentoProntoPago(rs.getDouble("desctoProntoPago"));
                cliente.setDiasBloqueo(rs.getInt("diasBloqueo"));
                cliente.getContribuyente().setIdRfc(rs.getInt("idRfc"));
                cliente.getContribuyente().setRfc(rs.getString("rfc"));
                cliente.getContribuyente().setCurp(rs.getString("curp"));
                cliente.getContribuyente().getDireccion().setIdDireccion(rs.getInt("idDireccion"));
                cliente.getContribuyente().getDireccion().setCalle(rs.getString("calle"));
                cliente.getContribuyente().getDireccion().setNumeroExterior(rs.getString("numeroExterior"));
                cliente.getContribuyente().getDireccion().setNumeroInterior(rs.getString("numeroInterior"));
                cliente.getContribuyente().getDireccion().setColonia(rs.getString("colonia"));
                cliente.getContribuyente().getDireccion().setLocalidad(rs.getString("localidad"));
                cliente.getContribuyente().getDireccion().setReferencia(rs.getString("referencia"));
                cliente.getContribuyente().getDireccion().setMunicipio(rs.getString("municipio"));
                cliente.getContribuyente().getDireccion().setEstado(rs.getString("estado"));
                cliente.getContribuyente().getDireccion().getPais().setIdPais(rs.getInt("idPais"));
                cliente.getContribuyente().getDireccion().setCodigoPostal(rs.getString("codigoPostal"));
            }
        } finally {
            cn.close();
        }
        return cliente;
    }

    public void guardarCliente(Cliente cliente) throws SQLException {
        Statement st = null;
        ResultSet rs;
        int idContribuyente = 0;
        Connection cn = ds.getConnection();
        String sqlTraeridContribuyente = "SELECT idContribuyente from contribuyentes c "
                + " inner join contribuyentesRfc cr "
                + "on c.idRfc = cr.idRfc WHERE rfc='" + cliente.getContribuyente().getRfc() + "'";
        try {
            st = cn.createStatement();
            st.executeUpdate("begin transaction");
            rs = st.executeQuery(sqlTraeridContribuyente);
            while (rs.next()) {
                idContribuyente = rs.getInt("idContribuyente");
            }
            String sqlClientes = "INSERT INTO clientes (codigoCliente, codigoTienda, nombreComercial, idAgente, idGrupoCte, idFormato, idContribuyente, idImpuestoZona, idDireccion, idRuta, fechaAlta, diasCredito, limiteCredito, desctoComercial, diasBloqueo) "
                    + "VALUES ('" + cliente.getCodigoCliente() + "', '" + cliente.getCodigoTienda() + "','" + cliente.getNombreComercial() + "','" + cliente.getAgente().getIdAgente() + "', '" + cliente.getClientesGrupos().getIdGrupoCte() + "', '" + cliente.getClientesFormato().getIdFormato() + "', '" + idContribuyente + "', '" + cliente.getImpuestoZona().getIdZona() + "','" + cliente.getDireccion().getIdDireccion() + "', '" + cliente.getRuta().getIdRuta() + "',GETDATE(), '" + cliente.getDiasCredito() + "', '" + cliente.getLimiteCredito() + "', '" + cliente.getDescuentoComercial() + "', '" + cliente.getDiasBloqueo() + "' )";
            st.executeUpdate(sqlClientes);
            st.executeUpdate("commit transaction");
        } catch (SQLException ex) {
            st.executeUpdate("rollback transaction");
            throw ex;
        } finally {
            cn.close();
        }
    }

    public void actualizarClientes(Cliente cliente) throws SQLException {
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        int idContribuyente = 0;
        String sqlDameContribuyente = "SELECT idContribuyente FROM contribuyentes cn \n"
                + "inner join contribuyentesRfc cr \n"
                + "on cn.idRfc = cr.idRfc where cr.rfc='" + cliente.getContribuyente().getRfc() + "'";
        ResultSet rs = st.executeQuery(sqlDameContribuyente);
        while (rs.next()) {
            idContribuyente = rs.getInt("idContribuyente");
        }
        String sqlActualizar = "UPDATE clientes SET codigoCliente = '" + cliente.getCodigoCliente() + "', idImpuestoZona = '" + cliente.getImpuestoZona().getIdZona() + "', diasCredito = '" + cliente.getDiasCredito() + "', "
                + "limiteCredito = '" + cliente.getLimiteCredito() + "', desctoComercial = '" + cliente.getDescuentoComercial() + "',diasBloqueo = '" + cliente.getDiasBloqueo() + "', nombreComercial = '" + cliente.getNombreComercial() + "', idAgente = ' " + cliente.getAgente().getIdAgente() + "'  "
                + ", idContribuyente = '" + idContribuyente + "', codigoTienda='"+cliente.getCodigoTienda()+"', idGrupoCte = '"+cliente.getClientesGrupos().getIdGrupoCte()+"', idFormato ='"+cliente.getClientesFormato().getIdFormato()+"' "
                + "WHERE idCliente = '" + cliente.getIdCliente() + "'";
        try {
            st.executeUpdate(sqlActualizar);
        } finally {
            cn.close();
        }
    }
}
