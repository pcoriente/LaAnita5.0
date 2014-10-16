/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientesBancos.DAOClientesBancos;

import java.sql.Connection;
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
import leyenda.dominio.ClientesBancos;
import usuarios.dominio.UsuarioSesion;

/**
 *
 * @author Usuario
 */
public class DAOClientesBancos {

    private DataSource ds = null;

    public DAOClientesBancos() {
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

    public void guardarClientesBancos(ClientesBancos clientesBancos) throws SQLException {
        Connection cn = ds.getConnection();
        String sql = "INSERT INTO clientesBancos (codigoCliente, idBanco, numCtaPago, medioPago) "
                + "VALUES('" + clientesBancos.getCodigoCliente() + "', '" + clientesBancos.getIdBanco() + "', '" + clientesBancos.getNumCtaPago() + "', '" + clientesBancos.getMedioPago() + "' )";
        Statement st = cn.createStatement();
        try {
            st.executeUpdate(sql);
        } finally {
            st.close();
            cn.close();
        }
    }

    public ArrayList<ClientesBancos> dameBancos(int codigoCliente) throws SQLException {
        ArrayList<ClientesBancos> lstClientesBancos = new ArrayList<ClientesBancos>();
        String sql = "SELECT * FROM clientesBancos cb "
                + "INNER JOIN bancosSat bs "
                + "on bs.idBanco = cb.idBanco "
                + "WHERE codigoCliente = '" + codigoCliente + "'";
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            ClientesBancos clientes = new ClientesBancos();
            clientes.setIdClienteBanco(rs.getInt("idClienteBanco"));
            clientes.setCodigoCliente(rs.getInt("codigoCliente"));
            clientes.getBancoLeyenda().setIdBanco(rs.getInt("idBanco"));
            clientes.setNumCtaPago(rs.getString("numCtaPago"));
            clientes.setMedioPago(rs.getString("medioPago"));
            clientes.getBancoLeyenda().setNombreCorto(rs.getString("nombreCorto"));
            lstClientesBancos.add(clientes);
        }
        return lstClientesBancos;
    }

    public ClientesBancos dameClientesBancos(int clienteBanco) throws SQLException {
        ClientesBancos clientes = new ClientesBancos();
        String slq = "SELECT * FROM clientesBancos WHERE idClienteBanco = '" + clienteBanco + "'";
        Connection c = ds.getConnection();
        Statement st = c.createStatement();
        ResultSet rs = st.executeQuery(slq);
        while (rs.next()) {
            clientes.setCodigoCliente(rs.getInt("codigoCliente"));
            clientes.setIdClienteBanco(rs.getInt("idClienteBanco"));
            clientes.getBancoLeyenda().setIdBanco(rs.getInt("idBanco"));
            clientes.setNumCtaPago(rs.getString("numCtaPago"));
            clientes.setMedioPago(rs.getString("medioPago"));
        }
        return clientes;

    }

    public void actualizarClientesBancos(ClientesBancos clientesBancos) throws SQLException {
        String sqlActualizar = "UPDATE clientesBancos SET codigoCliente= '" + clientesBancos.getCodigoCliente() + "', idBanco ='" + clientesBancos.getIdBanco() + "', numCtaPago = '" + clientesBancos.getNumCtaPago() + "', medioPago = '" + clientesBancos.getMedioPago() + "'  WHERE idClienteBanco = '" + clientesBancos.getIdClienteBanco() + "'";
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            st.executeUpdate(sqlActualizar);
        } finally {
            cn.close();
            st.close();
        }
    }
}
