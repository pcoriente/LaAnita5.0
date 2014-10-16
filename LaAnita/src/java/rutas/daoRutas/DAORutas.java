/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rutas.daoRutas;

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
import rutas.dominio.Ruta;
import usuarios.dominio.UsuarioSesion;

/**
 *
 * @author Usuario
 */
public class DAORutas {

    private DataSource ds = null;

    public DAORutas() throws NamingException {
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

    public ArrayList<Ruta> dameListaRutas() throws SQLException {
        ArrayList<Ruta> lstRuta = new ArrayList<Ruta>();
        String sql = "SELECT * FROM rutas";
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        ResultSet rs = st.executeQuery(sql);
        while (rs.next()) {
            Ruta ruta = new Ruta();
            ruta.setIdRuta(rs.getInt("idRuta"));
            ruta.setRuta(rs.getString("ruta"));
            lstRuta.add(ruta);
        }
        return lstRuta;
    }

    public void guardar(Ruta ruta) throws SQLException {
        String sql = "INSERT INTO rutas (ruta) VALUES('" + ruta.getRuta() + "')";
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        st.executeUpdate(sql);
    }

    public Ruta dameRuta(int id) throws SQLException {
        Ruta ruta = new Ruta();
        String sql = "SELECT * FROM rutas WHERE idRuta = '" + id + "'";
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        try {
            ResultSet rs = st.executeQuery(sql);
            while (rs.next()) {
                ruta.setIdRuta(rs.getInt("idRuta"));
                ruta.setRuta(rs.getString("ruta"));
            }
        } finally {
            cn.close();
        }
        return ruta;
    }

    public void actualizarRutas(Ruta ruta) throws SQLException {
        Connection cn = ds.getConnection();
        Statement st = cn.createStatement();
        String sql = "UPDATE rutas SET  ruta = '" + ruta.getRuta() + "' WHERE idRuta = '" + ruta.getIdRuta() + "'";
        try {
            st.execute(sql);
        } finally {
            cn.close();
        }
    }

}
