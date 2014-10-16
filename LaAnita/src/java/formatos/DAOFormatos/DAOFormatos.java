/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatos.DAOFormatos;

import formatos.dominio.ClientesFormato;
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
 * @author Usuario
 */
public class DAOFormatos {

    int idUsuario;
    private DataSource ds = null;

    public DAOFormatos() throws NamingException {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            UsuarioSesion usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");
            this.idUsuario = usuarioSesion.getUsuario().getId();

            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/" + usuarioSesion.getJndi());
        } catch (NamingException ex) {
            throw (ex);
        }
    }

    public void guardarFormato(ClientesFormato clientesFormatos) throws SQLException {
        String sql = "INSERT INTO clientesFormato (formato, idGrupoCte) VALUES ('" + clientesFormatos.getFormato() + "', '" + clientesFormatos.getClientesGrupos().getIdGrupoCte() + "')";
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            st.executeUpdate(sql);
        } finally {
            cn.close();
        }
    }

    public ArrayList<ClientesFormato> dameFormatos(int idGrupoClte) throws SQLException {
        ArrayList<ClientesFormato> lstFormatos = null;
        String sql = "SELECT * FROM  clientesFormato WHERE idGrupoCte  = '" + idGrupoClte + "'";
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs = st.executeQuery(sql);
            lstFormatos = new ArrayList<ClientesFormato>();
            while (rs.next()) {
                ClientesFormato formatos = new ClientesFormato();
                formatos.setIdFormato(rs.getInt("idFormato"));
                formatos.setFormato(rs.getString("formato"));
                formatos.getClientesGrupos().setIdGrupoCte(rs.getInt("idGrupoCte"));
                lstFormatos.add(formatos);
            }
        } finally {
            cn.close();
        }
        return lstFormatos;
    }

    public ClientesFormato obtenerClientesFormato(int id) throws SQLException {
        ClientesFormato clientes = new ClientesFormato();
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        String sql = "SELECT * FROM clientesFormato WHERE idFormato = " + id;
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            clientes.setFormato(rs.getString("formato"));
            clientes.setIdFormato(rs.getInt("idFormato"));
            clientes.getClientesGrupos().setIdGrupoCte(rs.getInt("idGrupoCte"));
        }
        return clientes;
    }

    public void actulizarFormato(ClientesFormato clientesFormatos) throws SQLException {
        String sql = "UPDATE clientesFormato set formato = '" + clientesFormatos.getFormato() + "' WHERE idFormato = '" + clientesFormatos.getIdFormato() + "'";
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            st.executeUpdate(sql);
        } finally {
            cn.close();
        }
    }
    
    
//    
//    public void guardarFormato(ClientesFormato formato) throws SQLException {
//        Connection cn = ds.getConnection();
//        String sql = "INSERT INTO clientesFormatos (formato) VALUES('" + formato.getFormato() + "')";
//        Statement st = cn.createStatement();
//        try {
//            st.executeUpdate(sql);
//        } finally {
//            cn.close();
//        }
//    }
    
     public void actualizar(ClientesFormato formato) throws SQLException {
        String sql = "UPDATE clientesFormatos set formato = '" + formato.getFormato() + "' WHERE idFormato ='" + formato.getIdFormato() + "'";
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            st.executeUpdate(sql);
        } finally {
            cn.close();
        }
    }
    
}
