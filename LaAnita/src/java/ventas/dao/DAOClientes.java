package ventas.dao;

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
import ventas.dominio.MiniCliente;
import ventas.to.TOCliente;

/**
 *
 * @author jesc
 */
public class DAOClientes {
    private int idCedis;
    private DataSource ds = null;

    public DAOClientes() throws NamingException {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            UsuarioSesion usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");

            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/" + usuarioSesion.getJndi());
            this.idCedis=usuarioSesion.getUsuario().getIdCedis();
        } catch (NamingException ex) {
            throw (ex);
        }
    }
    
    public ArrayList<TOCliente> obtenerClientes(int idClienteGpo, int idFormato) throws SQLException {
        ArrayList<TOCliente> tos=new ArrayList<TOCliente>();
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        String strSQL="SELECT C.* " +
                      "FROM clientes C " +
                      "INNER JOIN agentes A ON A.idAgente=C.idAgente " +
                      "WHERE C.idGrupoCte="+idClienteGpo+" AND C.idFormato="+idFormato+" AND A.idCedis="+this.idCedis;
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                tos.add(this.construir(rs));
            }
        } finally {
            cn.close();
        }
        return tos;
    }
    
    public TOCliente obtenerCliente(int idCliente) throws SQLException {
        TOCliente to=null;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        String strSQL="SELECT * FROM clientes WHERE idCliente="+idCliente;
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
    
    private TOCliente construir(ResultSet rs) throws SQLException {
        TOCliente to=new TOCliente();
        to.setCodigoCliente(rs.getInt("codigoCliente"));
        to.setCodigoTienda(rs.getInt("codigoTienda"));
        to.setDesctoComercial(rs.getDouble("desctoComercial"));
        to.setDiasBloqueo(rs.getInt("diasBloqueo"));
        to.setDiasCredito(rs.getInt("diasCredito"));
        to.setFechaAlta(new java.util.Date(rs.getDate("fechaAlta").getTime()));
        to.setIdAgente(rs.getInt("idAgente"));
        to.setIdCliente(rs.getInt("idCliente"));
        to.setIdContribuyente(rs.getInt("idContribuyente"));
        to.setIdDireccion(rs.getInt("idDireccion"));
        to.setIdFormato(rs.getInt("idFormato"));
        to.setIdGrupoCte(rs.getInt("idGrupoCte"));
        to.setIdImpuestoZona(rs.getInt("idImpuestoZona"));
        to.setIdRuta(rs.getInt("idRuta"));
        to.setLimiteCredito(rs.getDouble("limiteCredito"));
        to.setNombreComercial(rs.getString("nombreComercial"));
        return to;
    }
    
    public MiniCliente convertirMiniCliente(TOCliente to) {
        MiniCliente mini=new MiniCliente();
        mini.setCodigoTienda(to.getCodigoTienda());
        mini.setIdCliente(to.getIdCliente());
        mini.setNombreComercial(to.getNombreComercial());
        mini.setIdGrupoCte(to.getIdGrupoCte());
        mini.setIdFormato(to.getIdFormato());
        return mini;
    }
}
