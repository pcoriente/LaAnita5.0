/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientesTienda.DAOClientesTienda;

import clientesListas.dominio.ClientesListas;
import clientesTienda.dominio.ClienteTienda;
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
public class DAOClientesTienda {

    private DataSource ds;
    private Connection cn;

    public DAOClientesTienda() throws NamingException {
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
//
//    public ArrayList<ClienteTienda> listaClientesTienda() throws SQLException {
//        ArrayList<ClienteTienda> lst = new ArrayList<ClienteTienda>();
//        String sql = "select * from clientesTiendas ct\n"
//                + "inner join direcciones dr\n"
//                + "on ct.idDireccion = dr.idDireccion\n"
//                + "inner join clientesFormato cf\n"
//                + "on ct.idFormato = cf.idFormato\n"
//                + "inner join clientesGrupos cg\n"
//                + "on ct.idGrupoCte = cg.idGrupoCte "
//                + "inner join rutas rut "
//                + "on rut.idRuta = ct.idRuta";
//        Connection cn = ds.getConnection();
//        Statement st = cn.createStatement();
//        try {
//            ResultSet rs = st.executeQuery(sql);
//            while (rs.next()) {
//                ClienteTienda ct = new ClienteTienda();
//                ct.setIdClienteTienda(rs.getInt("idGrupoCte"));
//                ct.setCodigoCliente(rs.getInt("codigoCliente"));
//                ct.setCodigoTienda(rs.getInt("codigoTienda"));
//                ct.setNombre(rs.getString("nombre"));
//                ct.getDireccion().setIdDireccion(rs.getInt("idDireccion"));
//                ct.getDireccion().setCalle(rs.getString("calle"));
//                ct.getDireccion().setNumeroExterior(rs.getString("numeroExterior"));
//                ct.getDireccion().setNumeroInterior(rs.getString("numeroInterior"));
//                ct.getDireccion().setColonia(rs.getString("colonia"));
//                ct.getDireccion().setLocalidad(rs.getString("localidad"));
//                ct.getDireccion().setReferencia(rs.getString("referencia"));
//                ct.getDireccion().setMunicipio(rs.getString("municipio"));
//                ct.getDireccion().setEstado(rs.getString("estado"));
//                ct.getDireccion().getPais().setIdPais(rs.getInt("idPais"));
//                ct.getDireccion().setCodigoPostal("codigoPostal");
//                ct.getDireccion().setNumeroLocalizacion(rs.getString("numeroLocalizacion"));
//                ct.getFormatos().setIdFormato(rs.getInt("idFormato"));
//                ct.getFormatos().setFormato(rs.getString("formato"));
//                ct.getFormatos().getClientesGrupo().setIdGrupoCte(rs.getInt("idGrupo"));
//                ct.getRuta().setIdRuta(rs.getInt("idRuta"));
//                ct.getRuta().setRuta(rs.getString("ruta"));
//                lst.add(ct);
//            }
//        } finally {
//            cn.close();
//        }
//        return lst;
//    }
//
//    public void guardarClientesTienda(ClienteTienda clienteTienda) throws SQLException {
//        Connection cn = ds.getConnection();
//        Statement st = cn.createStatement();
//        ResultSet rs = null;
//        int idDireccion = 0;
//        String sqlInsertarDireccion = "INSERT INTO direcciones"
//                + " (calle, numeroExterior, numeroInterior, colonia, localidad, referencia, municipio, estado , idPais, codigoPostal, numeroLocalizacion) "
//                + " VALUES('" + clienteTienda.getDireccion().getCalle() + "', '" + clienteTienda.getDireccion().getNumeroExterior() + "', '" + clienteTienda.getDireccion().getNumeroInterior() + "', "
//                + "'" + clienteTienda.getDireccion().getColonia() + "', '" + clienteTienda.getDireccion().getLocalidad() + "', "
//                + "'" + clienteTienda.getDireccion().getReferencia() + "', '" + clienteTienda.getDireccion().getMunicipio() + "', '" + clienteTienda.getDireccion().getEstado() + "', '" + clienteTienda.getDireccion().getPais().getIdPais() + "', "
//                + "'" + clienteTienda.getDireccion().getCodigoPostal() + "', '" + clienteTienda.getDireccion().getNumeroLocalizacion() + "')";
//        try {
//            st.executeUpdate("begin transaction");
//            st.executeUpdate(sqlInsertarDireccion);
//            rs = st.executeQuery("SELECT @@IDENTITY AS idDireccion");
//            while (rs.next()) {
//                idDireccion = rs.getInt("idDireccion");
//            }
//            String sqlInsertarClienteTienda = "INSERT INTO clientesTiendas (codigoTienda, nombre, idDireccion, idFormato, idRuta, codigoCliente) "
//                    + " VALUES ('" + clienteTienda.getCodigoCliente() + "', '" + clienteTienda.getNombre() + "', '" + idDireccion + "', '" + clienteTienda.getFormatos().getIdFormato() + "', '" + clienteTienda.getRuta().getIdRuta() + "', '" + clienteTienda.getCodigoCliente() + "')";
//            st.executeUpdate(sqlInsertarClienteTienda);
//            st.executeUpdate("commit transaction");
//        } catch (SQLException e) {
//            st.executeUpdate("rollback transaction");
//        } finally {
//            cn.close();
//        }
//    }
//
//    public void actualizarClientesTienda(ClienteTienda clientes) throws SQLException {
//        Connection cn = ds.getConnection();
//        Statement st = cn.createStatement();
//        String sql = "UPDATE clientesTiendas set codigoTienda='" + clientes.getCodigoTienda() + "', nombre = '" + clientes.getNombre() + "', idFormato ='" + clientes.getFormatos().getIdFormato() + "', idRuta ='" + clientes.getRuta().getIdRuta() + "', codigoCliente = '" + clientes.getCodigoCliente() + "' WHERE idGrupoCte ='"+clientes.getIdClienteTienda()+"'";
//        try {
//            st.executeUpdate(sql);
//        } finally {
//            cn.close();
//        }
//    }

    
}
