package entradas.dao;

import entradas.dominio.MovimientoProducto;
import entradas.to.TOMovimiento;
import entradas.to.TOMovimientoDetalle;
import impuestos.dominio.ImpuestosProducto;
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
import movimientos.dominio.Lote;
import movimientos.dominio.MovimientoTipo;
import movimientos.to.TOEntradaProducto;
import movimientos.to.TOMovimientoAlmacenProducto;
import mvEntradas.TOEntradaOficinaProducto;
import salidas.TOSalidaOficinaProducto;
import usuarios.dominio.UsuarioSesion;

/**
 *
 * @author jesc
 */
public class DAOMovimientos {
    int idUsuario;
    int idCedis;
    private DataSource ds = null;

    public DAOMovimientos() throws NamingException {
        try {
            FacesContext context = FacesContext.getCurrentInstance();
            ExternalContext externalContext = context.getExternalContext();
            HttpSession httpSession = (HttpSession) externalContext.getSession(false);
            UsuarioSesion usuarioSesion = (UsuarioSesion) httpSession.getAttribute("usuarioSesion");
            this.idUsuario=usuarioSesion.getUsuario().getId();
            this.idCedis=usuarioSesion.getUsuario().getIdCedis();

            Context cI = new InitialContext();
            ds = (DataSource) cI.lookup("java:comp/env/" + usuarioSesion.getJndi());
        } catch (NamingException ex) {
            throw (ex);
        }
    }
    
    public ArrayList<TOEntradaOficinaProducto> obtenerDetalleEntradaOficina(int idMovto) throws SQLException {
        ArrayList<TOEntradaOficinaProducto> lista=new ArrayList<TOEntradaOficinaProducto>();
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        TOEntradaOficinaProducto to;
        String strSQL="SELECT idEmpaque, cantFacturada, unitario " +
                        "FROM movimientosDetalle " +
                        "WHERE idMovto="+idMovto;
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                to=new TOEntradaOficinaProducto();
                to.setIdProducto(rs.getInt("idEmpaque"));
                to.setCantidad(rs.getDouble("cantFacturada"));
                to.setCosto(rs.getDouble("unitario"));
                lista.add(to);
            }
        } finally {
            cn.close();
        }
        return lista;
    }
    
    public void cancelarEntradaOficina(int idMovto) throws SQLException {
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        String strSQL;
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            
            strSQL="DELETE FROM movimientosDetalle where idMovto="+idMovto;
            st.executeUpdate(strSQL);
            
            strSQL="DELETE FROM movimientos WHERE idMovto="+idMovto;
            st.executeUpdate(strSQL);
            
            st.executeUpdate("COMMIT TRANSACTION");
        } catch (SQLException ex) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw ex;
        } finally {
            cn.close();
        }
    }
    
    public void grabarEntradaOficina(TOMovimiento to) throws SQLException {
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        String strSQL;
        int folio;
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            
            folio=this.obtenerMovimientoFolio(true, to.getIdAlmacen(), to.getIdTipo(), st);
            
            strSQL="UPDATE movimientos SET fecha=GETDATE(), status=1, folio="+folio+", idUsuario="+this.idUsuario+" "
                    + "WHERE idMovto="+to.getIdMovto();
            st.executeUpdate(strSQL);
            
            strSQL="DELETE FROM movimientosDetalle WHERE idMovto="+to.getIdMovto()+" AND cantFacturada=0";
            st.executeUpdate(strSQL);
            
            strSQL="UPDATE d " +
                    "SET d.unitario=e.promedioPonderado, d.fecha=GETDATE(), d.existenciaAnterior=a.existenciaOficina " +
                    "FROM movimientosDetalle d " +
                    "INNER JOIN movimientos m ON m.idMovto=d.idMovto " +
                    "INNER JOIN almacenesEmpaques a ON a.idAlmacen=m.idAlmacen AND a.idEmpaque=d.idEmpaque " +
                    "INNER JOIN empresasEmpaques e ON e.idEmpaque=d.idEmpaque " +
                    "WHERE e.idEmpresa=m.idEmpresa AND d.idMovto="+to.getIdMovto();
            st.executeUpdate(strSQL);
            
            strSQL="UPDATE a " +
                    "SET a.existenciaOficina=a.existenciaOficina+d.cantFacturada " +
                    "FROM (SELECT m.idAlmacen, d.* " +
                    "		FROM movimientosDetalle d " +
                    "		INNER JOIN movimientos m ON m.idMovto=d.idMovto " +
                    "		WHERE d.idMovto="+to.getIdMovto()+") d " +
                    "INNER JOIN almacenesEmpaques a ON a.idAlmacen=d.idAlmacen AND a.idEmpaque=d.idEmpaque";
            st.executeUpdate(strSQL);
            
            strSQL="UPDATE e " +
                    "SET e.existenciaOficina=e.existenciaOficina+d.cantFacturada " +
                    "FROM (SELECT m.idEmpresa, d.* " +
                    "		FROM movimientosDetalle d " +
                    "		INNER JOIN movimientos m ON m.idMovto=d.idMovto " +
                    "		WHERE d.idMovto="+to.getIdMovto()+") d " +
                    "INNER JOIN empresasEmpaques e ON e.idEmpresa=d.idEmpresa AND e.idEmpaque=d.idEmpaque";
            st.executeUpdate(strSQL);
            
            st.executeUpdate("COMMIT TRANSACTION");
        } catch (SQLException ex) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw ex;
        } finally {
            cn.close();
        }
    }
    
    public double actualizaEntrada(int idMovto, int idAlmacen, int idProducto, double cantidad) throws SQLException {
        Connection cn=this.ds.getConnection();
        Statement st = cn.createStatement();
        String strSQL;
        try {
            strSQL="UPDATE movimientosDetalle "
                    + "SET cantFacturada="+cantidad+" "
                    + "WHERE idMovto="+idMovto+" AND idEmpaque="+idProducto;
            st.executeUpdate(strSQL);
        } finally {
            cn.close();
        }
        return cantidad;
    }
    
    public void agregarProductoEntradaOficina(int idMovto, TOEntradaOficinaProducto to) throws SQLException {
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        String strSQL;
        try {
            strSQL="INSERT INTO movimientosDetalle (idMovto, idEmpaque, cantOrdenada, cantFacturada, cantSinCargo, cantRecibida, costo, desctoProducto1, desctoProducto2, desctoConfidencial, unitario, idImpuestoGrupo, fecha, existenciaAnterior) "
                        + "VALUES ("+idMovto+", "+to.getIdProducto()+", 0, "+to.getCantidad()+", 0, 0, 0, 0, 0, 0, 0, 0, GETDATE(), 0)";
            st.executeUpdate(strSQL);
        } finally {
            cn.close();
        }
    }
    
    public void cancelarEntradaAlmacen(int idMovto) throws SQLException {
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        String strSQL;
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            
            strSQL="DELETE FROM lotesKardex where idMovto="+idMovto;
            st.executeUpdate(strSQL);
            
            strSQL="DELETE FROM movimientosAlmacen WHERE idMovto="+idMovto;
            st.executeUpdate(strSQL);
            
            st.executeUpdate("COMMIT TRANSACTION");
        } catch (SQLException ex) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw ex;
        } finally {
            cn.close();
        }
    }
    
    public void grabarEntradaAlmacen(TOMovimiento to) throws SQLException {
        Connection cn=this.ds.getConnection();
        Statement st, st1;
        ResultSet rs, rs1;
        
        String strSQL;
        int folio;
        double saldo;
        
        st=cn.createStatement();
        st1=cn.createStatement();
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            
            folio=this.obtenerMovimientoFolio(false, to.getIdAlmacen(), to.getIdTipo(), st);
            
            strSQL="UPDATE movimientosAlmacen SET fecha=GETDATE(), status=1, folio="+folio+", idUsuario="+this.idUsuario+" "
                    + "WHERE idMovto="+to.getIdMovto();
            st.executeUpdate(strSQL);
            
            strSQL="SELECT * FROM lotesKardex "
                    + "WHERE idMovto="+to.getIdMovto()+" "
                    + "ORDER BY idEmpaque";
            rs=st.executeQuery(strSQL);
            while(rs.next()) {
                strSQL="SELECT saldo FROM lotesAlmacenes "
                        + "WHERE idAlmacen="+to.getIdAlmacen()+" AND idEmpaque="+rs.getInt("idEmpaque")+" AND lote='"+rs.getString("lote")+"'";
                rs1=st1.executeQuery(strSQL);
                if(rs1.next()) {
                    saldo=rs1.getDouble("saldo");
                    strSQL="UPDATE lotesAlmacenes "
                            + "SET cantidad=cantidad+"+rs.getDouble("cantidad")+", saldo=saldo+"+rs.getDouble("cantidad")+" "
                            + "WHERE idAlmacen="+to.getIdAlmacen()+" AND idEmpaque="+rs.getInt("idEmpaque")+" AND lote='"+rs.getString("lote")+"'";
                } else {
                    saldo=0;
                    strSQL="INSERT INTO lotesAlmacenes (idAlmacen, idEmpaque, lote, fechaCaducidad, cantidad, saldo, separados, existenciaFisica) "
                            + "VALUES ("+to.getIdAlmacen()+", "+rs.getInt("idEmpaque")+", '"+rs.getString("lote")+"', '"+rs.getDate("fecha")+"', "+rs.getDouble("cantidad")+", "+rs.getDouble("cantidad")+", 0, 0)";
                }
                st1.executeUpdate(strSQL);
                
                strSQL="UPDATE lotesKardex "
                        + "SET fecha=GETDATE(), existenciaAnterior="+saldo+" "
                        + "WHERE idMovto="+to.getIdMovto()+" AND idEmpaque="+rs.getInt("idEmpaque")+" AND lote='"+rs.getString("lote")+"'";
                st1.executeUpdate(strSQL);
            }
            st.executeUpdate("COMMIT TRANSACTION");
        } catch (SQLException ex) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw ex;
        } finally {
            cn.close();
        }
    }
    
    public ArrayList<TOMovimientoAlmacenProducto> obtenerDetalleMovimientoAlmacen(int idMovto) throws SQLException {
        ArrayList<TOMovimientoAlmacenProducto> lista=new ArrayList<TOMovimientoAlmacenProducto>();
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        TOMovimientoAlmacenProducto to;
        String strSQL="SELECT idEmpaque, SUM(cantidad) AS cantidad " +
                        "FROM lotesKardex k " +
                        "WHERE idMovto="+idMovto+" " +
                        "GROUP BY idEmpaque";
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                to=new TOMovimientoAlmacenProducto();
                to.setIdProducto(rs.getInt("idEmpaque"));
                to.setCantidad(rs.getDouble("cantidad"));
                lista.add(to);
            }
        } finally {
            cn.close();
        }
        return lista;
    }
    
    public void cancelarSalidaOficina(int idMovto) throws SQLException {
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        String strSQL;
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            
            strSQL="UPDATE e " +
                    "SET e.separados=e.separados-d.cantFacturada " +
                    "FROM (SELECT m.idAlmacen, d.* " +
                    "		FROM movimientosDetalle d " +
                    "		INNER JOIN movimientos m ON m.idMovto=d.idMovto " +
                    "		WHERE d.idMovto="+idMovto+") d " +
                    "INNER JOIN almacenesEmpaques e ON e.idAlmacen=d.idAlmacen AND e.idEmpaque=d.idEmpaque";
            st.executeUpdate(strSQL);
            
            strSQL="DELETE FROM movimientosDetalle where idMovto="+idMovto;
            st.executeUpdate(strSQL);
            
            strSQL="DELETE FROM movimientos WHERE idMovto="+idMovto;
            st.executeUpdate(strSQL);
            
            st.executeUpdate("COMMIT TRANSACTION");
        } catch (SQLException ex) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw ex;
        } finally {
            cn.close();
        }
    }
    
    public ArrayList<TOSalidaOficinaProducto> obtenerDetalleSalidaOficina(int idAlmacen, int idMovto) throws SQLException {
        ArrayList<TOSalidaOficinaProducto> lista=new ArrayList<TOSalidaOficinaProducto>();
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        TOSalidaOficinaProducto to;
        String strSQL="SELECT idEmpaque, cantFacturada, unitario " +
                        "FROM movimientosDetalle " +
                        "WHERE idMovto="+idMovto;
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                to=new TOSalidaOficinaProducto();
                to.setIdProducto(rs.getInt("idEmpaque"));
                to.setCantidad(rs.getDouble("cantFacturada"));
                to.setCosto(rs.getDouble("unitario"));
                lista.add(to);
            }
        } finally {
            cn.close();
        }
        return lista;
    }
    
    private int obtenerMovimientoFolio(boolean oficina, int idAlmacen, int idTipo, Statement st) throws SQLException {
        int folio;
        String tabla="movimientosFoliosAlmacen";
        if(oficina) {
            tabla="movimientosFolios";
        }
        ResultSet rs=st.executeQuery("SELECT folio FROM "+tabla+" WHERE idAlmacen="+idAlmacen+" AND idTipo="+idTipo);
        if(rs.next()) {
            folio=rs.getInt("folio");
            st.executeUpdate("UPDATE "+tabla+" SET folio=folio+1 WHERE idAlmacen="+idAlmacen+" AND idTipo="+idTipo);
        } else {
            folio=1;
            st.executeUpdate("INSERT INTO "+tabla+" (idAlmacen, idTipo, folio) VALUES ("+idAlmacen+", "+idTipo+", 2)");
        }
        return folio;
    }
    
    public void grabarSalidaOficina(TOMovimiento to) throws SQLException {
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        String strSQL;
        int folio;
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            
            folio=this.obtenerMovimientoFolio(true, to.getIdAlmacen(), to.getIdTipo(), st);
            
            strSQL="UPDATE movimientos SET fecha=GETDATE(), status=1, folio="+folio+", idUsuario="+this.idUsuario+" "
                    + "WHERE idMovto="+to.getIdMovto();
            st.executeUpdate(strSQL);
            
            strSQL="DELETE FROM movimientosDetalle WHERE idMovto="+to.getIdMovto()+" AND cantFacturada=0";
            st.executeUpdate(strSQL);
            
            strSQL="UPDATE d " +
                    "SET d.unitario=e.promedioPonderado, d.fecha=GETDATE(), d.existenciaAnterior=a.existenciaOficina " +
                    "FROM movimientosDetalle d " +
                    "INNER JOIN movimientos m ON m.idMovto=d.idMovto " +
                    "INNER JOIN almacenesEmpaques a ON a.idAlmacen=m.idAlmacen AND a.idEmpaque=d.idEmpaque " +
                    "INNER JOIN empresasEmpaques e ON e.idEmpaque=d.idEmpaque " +
                    "WHERE e.idEmpresa=m.idEmpresa AND d.idMovto="+to.getIdMovto();
            st.executeUpdate(strSQL);
            
            strSQL="UPDATE e " +
                    "SET e.separados=e.separados-d.cantFacturada, e.existenciaOficina=e.existenciaOficina-d.cantFacturada " +
                    "FROM (SELECT m.idAlmacen, d.* " +
                    "		FROM movimientosDetalle d " +
                    "		INNER JOIN movimientos m ON m.idMovto=d.idMovto " +
                    "		WHERE d.idMovto="+to.getIdMovto()+") d " +
                    "INNER JOIN almacenesEmpaques e ON e.idAlmacen=d.idAlmacen AND e.idEmpaque=d.idEmpaque";
            st.executeUpdate(strSQL);
            
            strSQL="UPDATE e " +
                    "SET e.existenciaOficina=e.existenciaOficina-d.cantFacturada " +
                    "FROM (SELECT m.idEmpresa, d.* " +
                    "		FROM movimientosDetalle d " +
                    "		INNER JOIN movimientos m ON m.idMovto=d.idMovto " +
                    "		WHERE d.idMovto="+to.getIdMovto()+") d " +
                    "INNER JOIN empresasEmpaques e ON e.idEmpresa=d.idEmpresa AND e.idEmpaque=d.idEmpaque";
            st.executeUpdate(strSQL);
            
            strSQL="UPDATE e " +
                    "SET e.promedioPonderado=0 " +
                    "FROM (SELECT m.idEmpresa, d.* " +
                    "		FROM movimientosDetalle d " +
                    "		INNER JOIN movimientos m ON m.idMovto=d.idMovto " +
                    "		WHERE d.idMovto="+to.getIdMovto()+") d " +
                    "INNER JOIN empresasEmpaques e ON e.idEmpresa=d.idEmpresa AND e.idEmpaque=d.idEmpaque " +
                    "WHERE e.existenciaOficina=0";
            st.executeUpdate(strSQL);
            
            st.executeUpdate("COMMIT TRANSACTION");
        } catch (SQLException ex) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw ex;
        } finally {
            cn.close();
        }
    }
    
    public void agregarProductoSalidaOficina(int idMovto, TOSalidaOficinaProducto to) throws SQLException {
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        String strSQL;
        try {
            strSQL="INSERT INTO movimientosDetalle (idMovto, idEmpaque, cantOrdenada, cantFacturada, cantSinCargo, cantRecibida, costo, desctoProducto1, desctoProducto2, desctoConfidencial, unitario, idImpuestoGrupo, fecha, existenciaAnterior) "
                        + "VALUES ("+idMovto+", "+to.getIdProducto()+", 0, "+to.getCantidad()+", 0, 0, 0, 0, 0, 0, 0, 0, GETDATE(), 0)";
            st.executeUpdate(strSQL);
        } finally {
            cn.close();
        }
    }
    
    public void liberaSalida(int idMovto, int idAlmacen, int idProducto, double cantidad) throws SQLException {
        Connection cn=this.ds.getConnection();
        Statement st = cn.createStatement();
        String strSQL;
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            
            this.liberaOficina(idAlmacen, idProducto, cantidad, st);
            
            strSQL="UPDATE movimientosDetalle "
                    + "SET cantFacturada=cantFacturada-"+cantidad+" "
                    + "WHERE idMovto="+idMovto+" AND idProducto="+idProducto;
            st.executeUpdate(strSQL);
            
            st.executeUpdate("COMMIT TRANSACTION");
        } catch (SQLException ex) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw ex;
        } finally {
            cn.close();
        }
    }
    
    private void liberaOficina(int idAlmacen, int idProducto, double cantidad, Statement st) throws SQLException {
        String strSQL="UPDATE almacenesEmpaques "
                + "SET separados=separados-"+cantidad+" "
                + "WHERE idAlmacen=" + idAlmacen + " AND idEmpaque=" + idProducto;
        st.executeUpdate(strSQL);
    }
    
    public double separaSalida(int idMovto, int idAlmacen, int idProducto, double cantidad) throws SQLException {
        Connection cn=this.ds.getConnection();
        Statement st = cn.createStatement();
        String strSQL;
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            
            cantidad=this.separaOficina(idAlmacen, idProducto, cantidad, st);
            
            strSQL="UPDATE movimientosDetalle "
                    + "SET cantFacturada=cantFacturada+"+cantidad+" "
                    + "WHERE idMovto="+idMovto+" AND idEmpaque="+idProducto;
            st.executeUpdate(strSQL);
            
            st.executeUpdate("COMMIT TRANSACTION");
        } catch (SQLException ex) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw ex;
        } finally {
            cn.close();
        }
        return cantidad;
    }
    
    private double separaOficina(int idAlmacen, int idProducto, double cantidad, Statement st) throws SQLException {
        String strSQL = "SELECT existenciaOficina-separados AS saldo "
                + "FROM almacenesEmpaques "
                + "WHERE idAlmacen=" + idAlmacen + " AND idEmpaque=" + idProducto;
        ResultSet rs = st.executeQuery(strSQL);
        if (rs.next()) {
            if(rs.getDouble("saldo") <= 0) {
                cantidad=0;
                throw new SQLException("No hay existencia para salida");
            } else if (rs.getDouble("saldo") < cantidad) {
                cantidad = rs.getDouble("saldo");
            }
        } else {
            throw new SQLException("No se encontro el producto en tabla(almacenesEmpaques)");
        }
        strSQL = "UPDATE almacenesEmpaques "
                + "SET separados=separados+" + cantidad + " "
                + "WHERE idAlmacen=" + idAlmacen + " AND idEmpaque=" + idProducto;
        st.executeUpdate(strSQL);
        
        return cantidad;
    }
    
    public int agregarMovimientoRelacionado(TOMovimiento to) throws SQLException {
        int idMovto=0;
        int idMovtoAlmacen=0;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        String strSQL;
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            strSQL="INSERT INTO movimientos (idTipo, idCedis, idEmpresa, idAlmacen, folio, idComprobante, idImpuestoZona, desctoComercial, desctoProntoPago, fecha, idUsuario, idMoneda, tipoCambio, status) "
                    + "VALUES("+to.getIdTipo()+", "+to.getIdCedis()+", "+to.getIdEmpresa()+", "+to.getIdAlmacen()+", 0, 0, 0, 0, 0, GETDATE(), "+this.idUsuario+", 1, 1, 0)";
            st.executeUpdate(strSQL);
            
            ResultSet rs=st.executeQuery("SELECT @@IDENTITY AS idMovto");
            if(rs.next()) {
                idMovto=rs.getInt("idMovto");
            }
            strSQL="INSERT INTO movimientosAlmacen (idTipo, idCedis, idEmpresa, idAlmacen, folio, idComprobante, fecha, idUsuario, status) "
                            + "VALUES ("+to.getIdTipo()+", "+to.getIdCedis()+", "+to.getIdEmpresa()+", "+to.getIdAlmacen()+", 0, 0, GETDATE(), "+this.idUsuario+", 0)";
            st.executeUpdate(strSQL);
            
            rs=st.executeQuery("SELECT @@IDENTITY AS idMovto");
            if(rs.next()) {
                idMovtoAlmacen=rs.getInt("idMovto");
            }
            strSQL="INSERT INTO movimientosRelacionados (idMovto, idMovtoAlmacen) VALUES ("+idMovto+", "+idMovtoAlmacen+")";
            st.executeUpdate(strSQL);
            
            st.executeUpdate("COMMIT TRANSACTION");
        } catch (SQLException ex) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw ex;
        } finally {
            cn.close();
        }
        return idMovto;
    }
    
    public int agregarMovimientoOficina(TOMovimiento to) throws SQLException {
        int idMovto=0;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        String strSQL;
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            strSQL="INSERT INTO movimientos (idTipo, idCedis, idEmpresa, idAlmacen, folio, idComprobante, idImpuestoZona, desctoComercial, desctoProntoPago, fecha, idUsuario, idMoneda, tipoCambio, status) "
                    + "VALUES("+to.getIdTipo()+", "+to.getIdCedis()+", "+to.getIdEmpresa()+", "+to.getIdAlmacen()+", 0, 0, 0, 0, 0, GETDATE(), "+this.idUsuario+", 1, 1, 0)";
            st.executeUpdate(strSQL);
            
            ResultSet rs=st.executeQuery("SELECT @@IDENTITY AS idMovto");
            if(rs.next()) {
                idMovto=rs.getInt("idMovto");
            }
            st.executeUpdate("COMMIT TRANSACTION");
        } catch (SQLException ex) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw ex;
        } finally {
            cn.close();
        }
        return idMovto;
    }
    
    public void cancelarSalidaAlmacen(int idMovto) throws SQLException {
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        String strSQL;
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            
            strSQL="UPDATE l " +
                    "SET l.separados=l.separados-k.cantidad " +
                    "FROM lotesKardex k " +
                    "INNER JOIN lotesAlmacenes l ON l.idAlmacen=k.idAlmacen AND l.idEmpaque=k.idEmpaque AND l.lote=k.lote " +
                    "WHERE k.idMovto="+idMovto;
            st.executeUpdate(strSQL);
            
            strSQL="DELETE FROM lotesKardex where idMovto="+idMovto;
            st.executeUpdate(strSQL);
            
            strSQL="DELETE FROM movimientosAlmacen WHERE idMovto="+idMovto;
            st.executeUpdate(strSQL);
            
            st.executeUpdate("COMMIT TRANSACTION");
        } catch (SQLException ex) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw ex;
        } finally {
            cn.close();
        }
    }
    
    public ArrayList<TOMovimiento> movimientosPendientes(boolean oficina, int entrada) throws SQLException {
        ArrayList<TOMovimiento> lista=new ArrayList<TOMovimiento>();
        String tabla="movimientosAlmacen";
        if(oficina) {
            tabla="movimientos";
        }
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        String strSQL="SELECT m.* "
                + "FROM "+tabla+" m "
                + "INNER JOIN movimientosTipos t ON t.idTipo=m.idTipo "
                + "WHERE m.idCedis="+this.idCedis+" AND m.status=0 AND t.eliminable=1 AND t.suma="+entrada+" "
                + "ORDER BY m.idAlmacen";
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                lista.add(this.construirMovimientoAlmacen(rs));
            }
        } finally {
            cn.close();
        }
        return lista;
    }
    
    private TOMovimiento construirMovimientoAlmacen(ResultSet rs) throws SQLException {
        TOMovimiento to=new TOMovimiento();
        to.setIdMovto(rs.getInt("idMovto"));
        to.setIdTipo(rs.getInt("idTipo"));
        to.setFolio(rs.getInt("folio"));
        to.setIdCedis(rs.getInt("idCedis"));
        to.setIdEmpresa(rs.getInt("idEmpresa"));
        to.setIdAlmacen(rs.getInt("idAlmacen"));
        to.setIdReferencia(rs.getInt("idComprobante"));
        java.sql.Date f=rs.getDate("fecha");
        to.setFecha(new java.util.Date(f.getTime()));
        to.setIdUsuario(rs.getInt("idUsuario"));
        to.setStatus(rs.getBoolean("status"));
        return to;
    }
    
    public void grabarSalidaAlmacen(TOMovimiento to) throws SQLException {
        Connection cn=this.ds.getConnection();
        Statement st, st1;
        ResultSet rs, rs1;
        
        String strSQL;
        int folio;
        
        st=cn.createStatement();
        st1=cn.createStatement();
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            
            folio=this.obtenerMovimientoFolio(false, to.getIdAlmacen(), to.getIdTipo(), st);
            
            strSQL="UPDATE movimientosAlmacen SET fecha=GETDATE(), status=1, folio="+folio+", idUsuario="+this.idUsuario+" "
                    + "WHERE idMovto="+to.getIdMovto();
            st.executeUpdate(strSQL);
            
            strSQL="SELECT * FROM lotesKardex "
                    + "WHERE idAlmacen="+to.getIdAlmacen()+" AND idMovto="+to.getIdMovto()+" "
                    + "ORDER BY idEmpaque";
            rs=st.executeQuery(strSQL);
            while(rs.next()) {
                strSQL="SELECT saldo, separados FROM lotesAlmacenes "
                        + "WHERE idAlmacen="+to.getIdAlmacen()+" AND idEmpaque="+rs.getInt("idEmpaque")+" AND lote='"+rs.getString("lote")+"'";
                rs1=st1.executeQuery(strSQL);
                if(rs1.next()) {
                    if(rs1.getDouble("saldo")<rs.getDouble("cantidad")) {
                        throw new SQLException("El saldo del lote es menor a la cantidad separada en kardex del movimiento");
                    } else if(rs1.getDouble("separados")<rs.getDouble("cantidad")) {
                        throw new SQLException("La cantidad separada del lote es menor a la cantidad separada en kardex del movimiento");
                    }
                } else {
                    throw new SQLException("No se encontro lote("+rs.getString("lote")+") del empaque("+rs.getInt("idEmpaque")+") en tabla lotesAlmacenes");
                }
                strSQL="UPDATE lotesKardex "
                        + "SET fecha=GETDATE(), existenciaAnterior="+rs1.getDouble("saldo")+" "
                        + "WHERE idAlmacen="+to.getIdAlmacen()+" AND idMovto="+to.getIdMovto()+" AND idEmpaque="+rs.getInt("idEmpaque")+" AND lote='"+rs.getString("lote")+"'";
                st1.executeUpdate(strSQL);
                
                strSQL="UPDATE lotesAlmacenes "
                        + "SET saldo=saldo-"+rs.getDouble("cantidad")+", separados=separados-"+rs.getDouble("cantidad")+" "
                        + "WHERE idAlmacen="+to.getIdAlmacen()+" AND idEmpaque="+rs.getInt("idEmpaque")+" AND lote='"+rs.getString("lote")+"'";
                st1.executeUpdate(strSQL);
            }
            st.executeUpdate("COMMIT TRANSACTION");
        } catch (SQLException ex) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw ex;
        } finally {
            cn.close();
        }
    }
    
    public int agregarMovimientoAlmacen(TOMovimiento to) throws SQLException {
        int idMovto=0;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            String strSQL="INSERT INTO movimientosAlmacen (idTipo, idCedis, idEmpresa, idAlmacen, folio, idComprobante, fecha, idUsuario, status) "
                            + "VALUES ("+to.getIdTipo()+", "+to.getIdCedis()+", "+to.getIdEmpresa()+", "+to.getIdAlmacen()+", 0, 0, GETDATE(), "+this.idUsuario+", 0)";
            st.executeUpdate(strSQL);
            
            ResultSet rs=st.executeQuery("SELECT @@IDENTITY AS idMovto");
            if(rs.next()) {
                idMovto=rs.getInt("idMovto");
            }
            st.executeUpdate("COMMIT TRANSACTION");
        } catch (SQLException ex) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw ex;
        } finally {
            cn.close();
        }
        return idMovto;
    }
    
    public MovimientoTipo obtenerMovimientoTipo(int idTipo) throws SQLException {
        MovimientoTipo t=null;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        String strSQL="SELECT tipo FROM movimientosTipos WHERE idTipo="+idTipo;
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                t=new MovimientoTipo(idTipo, rs.getString("tipo"));
            }
        } finally {
            cn.close();
        }
        return t;
    }
    
    public ArrayList<MovimientoTipo> obtenerMovimientosTipos(boolean suma) throws SQLException {
        ArrayList<MovimientoTipo> lst=new ArrayList<MovimientoTipo>();
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        String strSQL="SELECT idTipo, tipo FROM movimientosTipos WHERE suma="+(suma?1:0)+" AND eliminable=1";
        try {
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                lst.add(new MovimientoTipo(rs.getInt("idTipo"), rs.getString("tipo")));
            }
        } finally {
            cn.close();
        }
        return lst;
    }
    
    public void grabarTraspasoRecepcion(TOMovimiento m, ArrayList<TOEntradaProducto> detalle) throws SQLException {
        String strSQL;
        ResultSet rs;
        Statement st;
//        int idMovtoAlmacen=0;
        double existenciaAnterior, promedioPonderado, existenciaOficina, saldo;
        Connection cn=this.ds.getConnection();
        st=cn.createStatement();
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            
//            strSQL="SELECT m.idMovto " +
//                    "FROM movimientosAlmacen m " +
//                    "WHERE m.idAlmacen="+m.getIdAlmacen()+" AND m.idTipo=9 and folio=(SELECT numero FROM comprobantes WHERE idComprobante="+m.getIdReferencia()+")";
//            rs=st.executeQuery(strSQL);
//            if(rs.next()) {
//                idMovtoAlmacen=rs.getInt("idMovto");
//            } else {
//                throw new SQLException("No se encontro el movimiento de almacen con la referencia("+m.getIdReferencia()+")");
//            }
//            idMovtoAlmacen=m.getIdMovtoAlmacen();
            
            strSQL="UPDATE movimientosAlmacen SET fecha=GETDATE(), status=1 WHERE idMovto="+m.getIdMovtoAlmacen();
            st.executeUpdate(strSQL);
            
            strSQL="UPDATE movimientos SET fecha=GETDATE(), status=1 WHERE idMovto="+m.getIdMovto();
            st.executeUpdate(strSQL);
            
            for(TOEntradaProducto to: detalle) {
                strSQL="SELECT existenciaOficina FROM almacenesEmpaques "
                        + "WHERE idAlmacen="+m.getIdAlmacen()+" AND idEmpaque="+to.getIdProducto();
                rs=st.executeQuery(strSQL);
                if(rs.next()) {
                    existenciaAnterior=rs.getDouble("existenciaOficina");
                    strSQL="UPDATE almacenesEmpaques SET existenciaOficina=existenciaOficina+"+to.getCantFacturada()+" "
                            + "WHERE idAlmacen="+m.getIdAlmacen()+" AND idEmpaque="+to.getIdProducto();
                } else {
                    existenciaAnterior=0;
                    strSQL="INSERT INTO almacenesEmpaques (idAlmacen, idEmpaque, existenciaOficina, separados, existenciaAlmacen, existenciaMinima, existenciaMaxima) "
                            + "VALUES ("+m.getIdAlmacen()+", "+to.getIdProducto()+", "+to.getCantFacturada()+", 0, 0, 0, 0)";
                }
                st.executeUpdate(strSQL);
                
//                strSQL="INSERT INTO movimientosDetalle (idMovto, idEmpaque, cantOrdenada, cantFacturada, cantSinCargo, cantRecibida, costo, desctoProducto1, desctoProducto2, desctoConfidencial, unitario, idImpuestoGrupo, fecha, existenciaAnterior) "
//                        + "VALUES ("+m.getIdMovto()+", "+to.getIdProducto()+", 0, "+to.getCantFacturada()+", 0, 0, "+to.getCosto()+", 0, 0, 0, "+to.getUnitario()+", 0, GETDATE(), "+existenciaAnterior+")";
                strSQL="UPDATE movimientosDetalle "
                        + "SET cantFacturada="+to.getCantFacturada()+", existenciaAnterior="+existenciaAnterior+", fecha=GETDATE() "
                        + "WHERE idMovto="+m.getIdMovto()+" AND idEmpaque="+to.getIdProducto();
                st.executeUpdate(strSQL);
                
                strSQL="SELECT promedioPonderado, existenciaOficina FROM empresasEmpaques "
                        + "WHERE idEmpresa="+m.getIdEmpresa()+" AND idEmpaque="+to.getIdProducto();
                rs=st.executeQuery(strSQL);
                if(rs.next()) {
                    promedioPonderado=rs.getDouble("promedioPonderado");
                    existenciaOficina=rs.getDouble("existenciaOficina");
                    promedioPonderado=(promedioPonderado*existenciaOficina+to.getUnitario()*to.getCantFacturada())/(existenciaOficina+to.getCantFacturada());
                    strSQL="UPDATE empresasEmpaques "
                            + "SET promedioPonderado="+promedioPonderado+", existenciaOficina=existenciaOficina+"+to.getCantFacturada()+" "
                            + "WHERE idEmpresa="+m.getIdEmpresa()+" AND idEmpaque="+to.getIdProducto();
                } else {
                    strSQL="INSERT INTO empresasEmpaques (idEmpresa, idEmpaque, promedioPonderado, existenciaOficina, idMovtoUltimaEntrada) "
                            + "VALUES ("+m.getIdEmpresa()+", "+to.getIdProducto()+", "+to.getUnitario()+", "+to.getCantFacturada()+", 0)";
                }
                st.executeUpdate(strSQL);
                
                for(Lote l: to.getLotes()) {
                    strSQL="SELECT saldo FROM lotesAlmacenes "
                            + "WHERE idAlmacen="+m.getIdAlmacen()+" AND idEmpaque="+to.getIdProducto()+" AND lote='"+l.getLote()+"'";
                    rs=st.executeQuery(strSQL);
                    if(rs.next()) {
                        saldo=rs.getDouble("saldo");
                        strSQL="UPDATE lotesAlmacenes "
                                + "SET saldo=saldo+"+l.getSeparados()+", cantidad=cantidad+"+l.getSeparados()+" "
                                + "WHERE idAlmacen="+m.getIdAlmacen()+" AND idEmpaque="+to.getIdProducto()+" AND lote='"+l.getLote()+"'";
                    } else {
                        saldo=0;
                        strSQL="INSERT INTO lotesAlmacenes (idAlmacen, idEmpaque, lote, fechaCaducidad, cantidad, saldo, separados, existenciaFisica) "
                                + "VALUES ("+m.getIdAlmacen()+", "+to.getIdProducto()+", '"+l.getLote()+"', '"+new java.sql.Date(l.getFechaCaducidad().getTime())+"', "+l.getSeparados()+", "+l.getSeparados()+", 0, 0)";
                    }
                    st.executeUpdate(strSQL);
                    
//                    strSQL="INSERT INTO lotesKardex (idAlmacen, idMovto, idEmpaque, lote, cantidad, suma, fecha, existenciaAnterior) "
//                            + "VALUES("+m.getIdAlmacen()+", "+idMovtoAlmacen+", "+to.getIdProducto()+", '"+l.getLote()+"', "+l.getSeparados()+", 1, GETDATE(), "+saldo+")";
                    strSQL="UPDATE lotesKardex "
                            + "SET cantidad="+l.getSeparados()+", existenciaAnterior="+saldo+", fecha=GETDATE() "
                            + "WHERE idAlmacen="+m.getIdAlmacen()+" AND idMovto="+m.getIdMovtoAlmacen()+" AND idEmpaque="+to.getIdProducto()+" AND lote='"+l.getLote()+"'";
                    st.executeUpdate(strSQL);
                }
            }
            st.executeUpdate("COMMIT TRANSACTION");
        } catch (SQLException ex) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw ex;
        } finally {
            cn.close();
        }
    }
    
    public int obtenerIdMovto(int idAlmacen, int idTipo, String folio) throws SQLException {
        String strSQL;
        int idMovto=0;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            strSQL="SELECT idMovto FROM movimientos "
                    + "WHERE idAlmacen="+idAlmacen+" AND idTipo="+idTipo+" AND folio="+folio;
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                idMovto=rs.getInt("idMovto");
            } else {
                throw new SQLException("No se encotro el movimiento folio("+folio+") en el almacen("+idAlmacen+")");
            }
        } finally {
            cn.close();
        }
        return idMovto;
    }
    
//    public int obtenerIdMovtoAlmacen(int idAlmacen, int idTipo, String folio) throws SQLException {
//        String strSQL;
//        int idMovto=0;
//        Connection cn=this.ds.getConnection();
//        Statement st=cn.createStatement();
//        try {
//            strSQL="SELECT idMovto FROM movimientosAlmacen WHERE idAlmacen="+idAlmacen+" AND idTipo="+idTipo+" AND folio="+folio;
//            ResultSet rs=st.executeQuery(strSQL);
//            if(rs.next()) {
//                idMovto=rs.getInt("idMovto");
//            } else {
//                throw new SQLException("No se encotro el movimientoAlmacen folio("+folio+") en el almacen("+idAlmacen+")");
//            }
//        } finally {
//            cn.close();
//        }
//        return idMovto;
//    }
    
    public void grabarTraspasoEnvio(int idAlmacenDestino, TOMovimiento m, ArrayList<TOMovimientoDetalle> detalle) throws SQLException {
        String strSQL;
        ResultSet rs;
        Statement st, st1;
        double sumaLotes, costo;
        
        Connection cn=this.ds.getConnection();
        st=cn.createStatement();
        st1=cn.createStatement();
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            
            strSQL="UPDATE movimientosAlmacen SET fecha=GETDATE() WHERE idMovto="+m.getIdMovtoAlmacen();
            st.executeUpdate(strSQL);
            
            strSQL="UPDATE movimientos SET fecha=GETDATE(), status=1 WHERE idMovto="+m.getIdMovto();
            st.executeUpdate(strSQL);
            
            for(TOMovimientoDetalle d: detalle) {
                sumaLotes=0;
                strSQL="SELECT K.lote, K.cantidad, ISNULL(L.saldo, 0) AS saldo "
                    + "FROM lotesKardex K "
                    + "LEFT JOIN lotesAlmacenes L ON L.idAlmacen=K.idAlmacen AND L.idEmpaque=K.idEmpaque AND L.lote=K.lote "
                    + "WHERE K.idAlmacen="+m.getIdAlmacen()+" AND K.idMovto="+m.getIdMovtoAlmacen()+" AND K.idEmpaque="+d.getIdProducto();
                rs=st.executeQuery(strSQL);
                while(rs.next()) {
                    if(rs.getDouble("saldo")<rs.getDouble("cantidad")) {
                        throw new SQLException("No hay saldo o No se encontro el lote("+rs.getString("lote")+") del producto("+d.getIdProducto()+") en el almacen");
                    }
                    strSQL="UPDATE lotesKardex "
                            + "SET existenciaAnterior="+rs.getDouble("saldo")+", fecha=GETDATE() "
                            + "WHERE idAlmacen="+m.getIdAlmacen()+" AND idMovto="+m.getIdMovtoAlmacen()+" AND idEmpaque="+d.getIdProducto()+" AND lote='"+rs.getString("lote")+"'";
                    st1.executeUpdate(strSQL);
                    
                    strSQL="UPDATE lotesAlmacenes "
                            + "SET saldo=saldo-"+rs.getDouble("cantidad")+", separados=separados-"+rs.getDouble("cantidad")+" "
                            + "WHERE idAlmacen="+m.getIdAlmacen()+" AND idEmpaque="+d.getIdProducto()+" AND lote='"+rs.getString("lote")+"'";
                    st1.executeUpdate(strSQL);
                    
                    sumaLotes+=rs.getDouble("cantidad");
                }
                if(sumaLotes!=d.getCantFacturada()) {
                    throw new SQLException("Diferencia entre lotes y cantidad Facturada del producto("+d.getIdProducto()+")");
                } else if(sumaLotes > 0) {
                    strSQL="SELECT AE.existenciaOficina AS saldo, ISNULL(EE.existenciaOficina, 0) AS existencia, ISNULL(EE.promedioPonderado, 0) AS costo " +
                            "FROM almacenesEmpaques AE " +
                            "INNER JOIN almacenes A ON A.idAlmacen=AE.idAlmacen " +
                            "LEFT JOIN empresasEmpaques EE ON EE.idEmpresa=A.idEmpresa AND EE.idEmpaque=AE.idEmpaque " +
                            "WHERE AE.idAlmacen="+m.getIdAlmacen()+" AND AE.idEmpaque="+d.getIdProducto();
                    rs=st.executeQuery(strSQL);
                    if(rs.next()) {
                        if(rs.getDouble("existencia")<d.getCantFacturada()) {
                            throw new SQLException("No hay capas de costos suficientes o No se encontro el producto("+d.getIdProducto()+") en la empresa");
                        } else if(rs.getDouble("costo")<0) {
                            throw new SQLException("Costo no valido (menor que cero)");
                        }
                    } else {
                        throw new SQLException("No se encontro producto("+d.getIdProducto()+") en el almacen");
                    }
                    d.setCosto(rs.getDouble("costo"));
                    d.setUnitario(rs.getDouble("costo"));
                    costo=rs.getDouble("costo");
                    sumaLotes=rs.getDouble("existencia");

                    strSQL="UPDATE movimientosDetalle "
                        + "SET costo="+d.getCosto()+", unitario="+d.getUnitario()+", cantFacturada="+d.getCantFacturada()+", existenciaAnterior="+rs.getDouble("saldo")+", fecha=GETDATE() "
                        + "WHERE idMovto="+m.getIdMovto()+" AND idEmpaque="+d.getIdProducto();
                    st.executeUpdate(strSQL);

                    strSQL="UPDATE almacenesEmpaques "
                        + "SET existenciaOficina=existenciaOficina-"+d.getCantFacturada()+ ", separados=separados-"+d.getCantFacturada()+" "
                        + "WHERE idAlmacen="+m.getIdAlmacen()+" AND idEmpaque="+d.getIdProducto();
                    st.executeUpdate(strSQL);

                    if(d.getCantFacturada()==sumaLotes) {
                        costo=0;    // Cuando ya no hay existencia el costo promedio de la empresa se hace cero
                    }
                    strSQL="UPDATE empresasEmpaques SET existenciaOficina=existenciaOficina-"+d.getCantFacturada()+", promedioPonderado="+costo+" "
                            + "WHERE idEmpresa="+m.getIdEmpresa()+" AND idEmpaque="+d.getIdProducto();
                    st.executeUpdate(strSQL);
                }
            }
            // ----------------------- SECCION: CREAR ENLACE ENVIO-RECEPCION ------------------
            int folioRecepcion, folioRecepcionAlmacen, idComprobante;
            strSQL="SELECT folio FROM movimientosFolios WHERE idAlmacen="+m.getIdAlmacen()+" AND idTipo=9";
            rs=st.executeQuery(strSQL);
            if(rs.next()) {
                folioRecepcion=rs.getInt("folio");
                strSQL="UPDATE movimientosFolios SET folio=folio+1 WHERE idAlmacen="+m.getIdAlmacen()+" AND idTipo=9";
            } else {
                folioRecepcion=1;
                strSQL="INSERT INTO movimientosFolios (idAlmacen, idTipo, folio) VALUES ("+m.getIdAlmacen()+", 9, 2)";
            }
            st.executeUpdate(strSQL);
            
            strSQL="UPDATE comprobantes SET numero="+folioRecepcion+" "+
                   "WHERE idComprobante="+m.getIdReferencia();
            st.executeUpdate(strSQL);
            // ------------------------- SECCION: CREAR RECEPCION ---------------------
            strSQL="SELECT folio FROM movimientosFoliosAlmacen WHERE idAlmacen="+m.getIdAlmacen()+" AND idTipo=9";
            rs=st.executeQuery(strSQL);
            if(rs.next()) {
                folioRecepcionAlmacen=rs.getInt("folio");
                strSQL="UPDATE movimientosFoliosAlmacen SET folio=folio+1 WHERE idAlmacen="+m.getIdAlmacen()+" AND idTipo=9";
            } else {
                folioRecepcionAlmacen=1;
                strSQL="INSERT INTO movimientosFoliosAlmacen (idAlmacen, idTipo, folio) VALUES ("+m.getIdAlmacen()+", 9, 2)";
            }
            st.executeUpdate(strSQL);
            
            int idCedisDestino=0;
            strSQL="SELECT idCedis FROM almacenes WHERE idAlmacen="+idAlmacenDestino;
            rs=st.executeQuery(strSQL);
            if(rs.next()) {
                idCedisDestino=rs.getInt("idCedis");
            } else {
                throw new SQLException("No se encontro almacen="+idAlmacenDestino);
            }
            int idMovto=0;
            strSQL="INSERT INTO movimientos (idTipo, idCedis, idEmpresa, idAlmacen, folio, idComprobante, idImpuestoZona, desctoComercial, desctoProntoPago, fecha, idUsuario, idMoneda, tipoCambio, status) "
                + "VALUES(9, "+idCedisDestino+", "+m.getIdEmpresa()+", "+idAlmacenDestino+", "+folioRecepcion+", 0, 0, 0, 0, GETDATE(), "+this.idUsuario+", 1, 1, 0)";
            st.executeUpdate(strSQL);
            rs=st.executeQuery("SELECT @@IDENTITY AS idMovto");
            if(rs.next()) {
                idMovto=rs.getInt("idMovto");
            }
            idComprobante=0;
            // Se crea el comprobante de la recepcion, 
            strSQL="INSERT INTO comprobantes (idAlmacen, idProveedor, tipoComprobante, remision, serie, numero, idUsuario, fecha, statusOficina, statusAlmacen, propietario) "
                    + "VALUES("+m.getIdAlmacen()+", "+idAlmacenDestino+", 0, '"+idMovto+"', '', '"+m.getFolio()+"', "+this.idUsuario+", GETDATE(), 0, 0, 0)";
            st.executeUpdate(strSQL);
            rs=st.executeQuery("SELECT @@IDENTITY AS idComprobante");
            if(rs.next()) {
                idComprobante=rs.getInt("idComprobante");
            }
            strSQL="UPDATE movimientos SET idComprobante="+idComprobante+" WHERE idMovto="+idMovto;
            st.executeUpdate(strSQL);
            
            int idMovtoAlmacen=0;
            strSQL="INSERT INTO movimientosAlmacen (idTipo, idCedis, idEmpresa, idAlmacen, folio, idComprobante, fecha, idUsuario) "
                    + "VALUES (9, "+idCedisDestino+", "+m.getIdEmpresa()+", "+idAlmacenDestino+", "+folioRecepcionAlmacen+", "+idComprobante+", GETDATE(), "+this.idUsuario+")";
            st.executeUpdate(strSQL);
            rs=st.executeQuery("SELECT @@IDENTITY AS idMovtoAlmacen");
            if(rs.next()) {
                idMovtoAlmacen=rs.getInt("idMovtoAlmacen");
            }
            strSQL="INSERT INTO movimientosRelacionados (idMovto, idMovtoAlmacen) VALUES ("+idMovto+", "+idMovtoAlmacen+")";
            st.executeUpdate(strSQL);
            
            for(TOMovimientoDetalle d: detalle) {
                if(d.getCantFacturada()>0) {
                    strSQL="INSERT INTO movimientosDetalle (idMovto, idEmpaque, cantOrdenada, cantFacturada, cantSinCargo, cantRecibida, costo, desctoProducto1, desctoProducto2, desctoConfidencial, unitario, idImpuestoGrupo, fecha, existenciaAnterior) "
                            + "VALUES ("+idMovto+", "+d.getIdProducto()+", "+d.getCantFacturada()+", "+d.getCantFacturada()+", 0, 0, "+d.getCosto()+", 0, 0, 0, "+d.getUnitario()+", "+d.getIdImpuestoGrupo()+", GETDATE(), 0)";
                    st.executeUpdate(strSQL);
                    
                    strSQL="SELECT K.lote, K.cantidad "
                            + "FROM lotesKardex K "
                            + "WHERE K.idAlmacen="+m.getIdAlmacen()+" AND K.idMovto="+m.getIdMovtoAlmacen()+" AND K.idEmpaque="+d.getIdProducto()+" AND K.cantidad>0";
                    rs=st.executeQuery(strSQL);
                    while(rs.next()) {
                        strSQL="INSERT INTO lotesKardex (idAlmacen, idMovto, idEmpaque, lote, cantidad, suma, fecha, existenciaAnterior) "
                                + "VALUES("+idAlmacenDestino+", "+idMovtoAlmacen+", "+d.getIdProducto()+", '"+rs.getString("lote")+"', "+rs.getDouble("cantidad")+", 1, GETDATE(), 0)";
                        st1.executeUpdate(strSQL);
                    }
                }
            }
            st.executeUpdate("COMMIT TRANSACTION");
        } catch (SQLException ex) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw ex;
        } finally {
            cn.close();
        }
    }
    
    public boolean grabarTraspasoSolicitud(int idAlmacenSolicita, TOMovimiento solicitud, ArrayList<MovimientoProducto> productos) throws SQLException {
        boolean ok=true;
        int folio;
        int idMovto, idMovtoAlmacen;
        int idComprobante;
        String strSQL;
        ResultSet rs;
        
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            
            folio=0;
            strSQL="SELECT folio FROM movimientosFolios WHERE idAlmacen="+solicitud.getIdAlmacen()+" AND idTipo=35";
            rs=st.executeQuery(strSQL);
            if(rs.next()) {
                folio=rs.getInt("folio");
                strSQL="UPDATE movimientosFolios SET folio=folio+1 WHERE idAlmacen="+solicitud.getIdAlmacen()+" AND idTipo=35";
            } else {
                folio=1;
                strSQL="INSERT INTO movimientosFolios (idAlmacen, idTipo, folio) VALUES ("+solicitud.getIdAlmacen()+", 35, 2)";
            }
            st.executeUpdate(strSQL);
            
            idMovto=0;
            strSQL="INSERT INTO movimientos (idTipo, idCedis, idEmpresa, idAlmacen, folio, idComprobante, idImpuestoZona, desctoComercial, desctoProntoPago, fecha, idUsuario, idMoneda, tipoCambio, status) " +
                    "VALUES (35, "+solicitud.getIdCedis()+", "+solicitud.getIdEmpresa()+", "+solicitud.getIdAlmacen()+", "+folio+", 0, "+solicitud.getIdImpuestoZona()+", 0, 0, GETDATE(), "+this.idUsuario+", 1, 1, 0)";
            st.executeUpdate(strSQL);
            rs=st.executeQuery("SELECT @@IDENTITY AS idMovto");
            if(rs.next()) {
                idMovto=rs.getInt("idMovto");
            }
            strSQL="INSERT INTO comprobantes (idAlmacen, idProveedor, tipoComprobante, remision, serie, numero, idUsuario, fecha, statusOficina, statusAlmacen, propietario) " +
                    "VALUES ("+idAlmacenSolicita+", "+solicitud.getIdAlmacen()+", 0, '"+idMovto+"', '', '', "+this.idUsuario+", GETDATE(), 0, 0, 0)";
            st.executeUpdate(strSQL);
            
            idComprobante=0;
            rs=st.executeQuery("SELECT @@IDENTITY AS idComprobante");
            if(rs.next()) {
                idComprobante=rs.getInt("idComprobante");
            }
            strSQL="UPDATE movimientos SET idComprobante="+idComprobante+" WHERE idMovto="+idMovto;
            st.executeUpdate(strSQL);
            
            int folioAlmacen=0;
            strSQL="SELECT folio FROM movimientosFoliosAlmacen WHERE idAlmacen="+solicitud.getIdAlmacen()+" AND idTipo=35";
            rs=st.executeQuery(strSQL);
            if(rs.next()) {
                folioAlmacen=rs.getInt("folio");
                strSQL="UPDATE movimientosFoliosAlmacen SET folio=folio+1 WHERE idAlmacen="+solicitud.getIdAlmacen()+" AND idTipo=35";
            } else {
                folioAlmacen=1;
                strSQL="INSERT INTO movimientosFoliosAlmacen (idAlmacen, idTipo, folio) VALUES ("+solicitud.getIdAlmacen()+", 35, 2)";
            }
            st.executeUpdate(strSQL);
            
            idMovtoAlmacen=0;
            strSQL="INSERT INTO movimientosAlmacen (idTipo, idCedis, idEmpresa, idAlmacen, folio, idComprobante, fecha, idUsuario) " +
                    "VALUES (35, "+solicitud.getIdCedis()+", "+solicitud.getIdEmpresa()+", "+solicitud.getIdAlmacen()+", "+folioAlmacen+", "+idComprobante+", GETDATE(), "+this.idUsuario+")";
            st.executeUpdate(strSQL);
            rs=st.executeQuery("SELECT @@IDENTITY AS idMovtoAlmacen");
            if(rs.next()) {
                idMovtoAlmacen=rs.getInt("idMovtoAlmacen");
            }
            strSQL="INSERT INTO movimientosRelacionados (idMovto, idMovtoAlmacen) VALUES ("+idMovto+", "+idMovtoAlmacen+")";
            st.executeUpdate(strSQL);
            
            strSQL="INSERT INTO movimientosDetalle (idMovto, idEmpaque, cantOrdenada, cantFacturada, cantSinCargo, cantRecibida, costo, desctoProducto1, desctoProducto2, desctoConfidencial, unitario, idImpuestoGrupo, fecha, existenciaAnterior) " +
                    "VALUES ("+idMovto+", ?, ?, 0, 0, 0, 0, 0, 0, 0, 0, ?, GETDATE(), 0)";
            PreparedStatement ps=cn.prepareStatement(strSQL);
            for(MovimientoProducto p: productos) {
                ps.setInt(1, p.getProducto().getIdProducto());
                ps.setDouble(2, p.getCantOrdenada());
                ps.setInt(3, p.getProducto().getArticulo().getImpuestoGrupo().getIdGrupo());
                ps.executeUpdate();
            }
            st.executeUpdate("COMMIT TRANSACTION");
        } catch(SQLException e) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw(e);
        } finally {
            cn.close();
        }
        return ok;
    }
    
    public double obtenerPrecioUltimaCompra(int idEmpresa, int idEmpaque) throws SQLException {
        double precioLista=0;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        
        String strSQL="SELECT idMovtoUltimaEntrada FROM empresasEmpaques "
                + "WHERE idEmpresa="+idEmpresa+" AND idEmpaque="+idEmpaque;
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                int idMovto=rs.getInt("idMovtoUltimaEntrada");
                
                strSQL="SELECT costo FROM movimientosDetalle "
                        + "WHERE idMovto="+idMovto+" AND idEmpaque="+idEmpaque;
                rs=st.executeQuery(strSQL);
                if(rs.next()) {
                    precioLista=rs.getDouble("costo");
                }
            }
        } catch(SQLException e) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw(e);
        } finally {
            cn.close();
        }
        return precioLista;
    }
    
    public boolean cancelarEntrada(int idMovto) throws SQLException {
        ResultSet rs;
        ResultSet rs1;
        String strSQL;
        int idEmpresa, idAlmacen, idEmpaque;
        double existenciaAnterior, cantidad, cantFacturada, unitario;
        
        boolean ok=false;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        PreparedStatement ps;
        PreparedStatement ps1;
        PreparedStatement ps2;
        PreparedStatement ps3;
        
        idAlmacen=0;
        idEmpresa=0;
        rs=st.executeQuery("SELECT idEmpresa, idAlmacen FROM movimientos WHERE idMovto="+idMovto);
        if(rs.next()) {
            idEmpresa=rs.getInt("idEmpresa");
            idAlmacen=rs.getInt("idAlmacen");
        }
        strSQL="SELECT existenciaOficina FROM almacenesEmpaques WHERE idAlmacen="+idAlmacen+" AND idEmpaque=?";
        ps=cn.prepareStatement(strSQL);
        
        strSQL="INSERT INTO kardexOficina (idAlmacen, idEmpaque, fecha, idTipoMovto, idMovto, existenciaAnterior, cantidad) "
                + "VALUES ("+idAlmacen+", ?, GETDATE(), 7, "+idMovto+", ?, ?)";
        ps1=cn.prepareStatement(strSQL);

        strSQL="UPDATE almacenesEmpaques SET existenciaOficina=existenciaOficina-? "
                + "WHERE idAlmacen="+idAlmacen+" AND idEmpaque=?";
        ps2=cn.prepareStatement(strSQL);

        strSQL="UPDATE empresasEmpaques " +
                "SET existenciaOficina=existenciaOficina-?, " +
                    "promedioPonderado=(existenciaOficina*promedioPonderado-?*?)/(existenciaOficina+?) " +
                "WHERE idEmpresa="+idEmpresa+" AND idEmpaque=?";
        ps3=cn.prepareStatement(strSQL);
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            
            st.executeUpdate("UPDATE movimientos SET status=3 WHERE idMovto="+idMovto);
            
            rs=st.executeQuery("SELECT idEmpaque, cantFacturada, cantSinCargo, unitario "
                            + "FROM movimientosDetalle WHERE idMovto="+idMovto);
            while(rs.next()) {
                idEmpaque=rs.getInt("idEmpaque");
                cantFacturada=rs.getDouble("cantFacturada");
                cantidad=rs.getDouble("cantFacturada")+rs.getDouble("cantSinCargo");
                unitario=rs.getDouble("unitario");
                
                
                
                ps.setInt(1, idEmpaque);
                rs1=ps.executeQuery();
                //rs1=st.executeQuery(strSQL);
                existenciaAnterior=0;
                if(rs1.next()) {
                    existenciaAnterior=rs1.getDouble("existenciaOficina");
                }
                ps1.setInt(1, idEmpaque);
                ps1.setDouble(2, existenciaAnterior);
                ps1.setDouble(3, cantidad);
                ps1.execute();
                
                ps2.setDouble(1, cantidad);
                ps2.setInt(2, idEmpaque);
                ps2.execute();
                
                ps3.setDouble(1, cantidad);
                ps3.setDouble(2, cantFacturada);
                ps3.setDouble(3, unitario);
                ps3.setDouble(4, cantidad);
                ps3.setInt(5, idEmpaque);
                ps3.executeUpdate();
            }
            st.executeUpdate("COMMIT TRANSACTION");
        } catch(SQLException e) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw(e);
        } finally {
            cn.close();
        }
        return ok;
    }
    
    public boolean grabarComprasAlmacen(TOMovimiento m, ArrayList<MovimientoProducto> productos, int idOrdenCompra) throws SQLException {
        boolean ok=false;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        String strSQL;
        try {
            st.executeUpdate("BEGIN TRANSACTION");
            strSQL="SELECT statusAlmacen FROM comprobantes where idComprobante="+m.getIdReferencia();
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                if(rs.getBoolean("statusAlmacen")) {
                    throw new SQLException("Ya se ha capturado y cerrado la entrada");
                } else {
                    strSQL="UPDATE comprobantes SET statusAlmacen=1 WHERE idComprobante="+m.getIdReferencia();
                    st.executeUpdate(strSQL);
                }
            } else {
                throw new SQLException("No se encontro el comprobante");
            }
            int folio=0;
            strSQL="SELECT folio FROM movimientosFoliosAlmacen WHERE idAlmacen="+m.getIdAlmacen()+" AND idTipo=1";
            rs=st.executeQuery(strSQL);
            if(rs.next()) {
                folio=rs.getInt("folio");
                strSQL="UPDATE movimientosFoliosAlmacen SET folio=folio+1 WHERE idAlmacen="+m.getIdAlmacen()+" AND idTipo=1";
            } else {
                folio=1;
                strSQL="INSERT INTO movimientosFoliosAlmacen (idAlmacen, idTipo, folio) VALUES ("+m.getIdAlmacen()+", 1, 2)";
            }
            st.executeUpdate(strSQL);
            
            String lote="";
            strSQL="SELECT lote FROM lotes WHERE fecha=CONVERT(date, GETDATE())";
            rs=st.executeQuery(strSQL);
            if(rs.next()) {
                lote=rs.getString("lote")+"1";
            } else {
                throw new SQLException("No se encontro el lote de fecha de hoy");
            }
            strSQL="INSERT INTO movimientosAlmacen (idTipo, idCedis, idEmpresa, idAlmacen, folio, idComprobante, fecha, idUsuario) "
                    + "VALUES (1, "+m.getIdCedis()+", "+m.getIdEmpresa()+", "+m.getIdAlmacen()+", "+folio+", "+m.getIdReferencia()+", GETDATE(), "+this.idUsuario+")";
            st.executeUpdate(strSQL);
            rs=st.executeQuery("SELECT @@IDENTITY AS idMovto");
            if(rs.next()) {
                m.setIdMovto(rs.getInt("idMovto"));
            }
            int idProducto;
            for(MovimientoProducto p: productos) {
                if(p.getCantRecibida()>0) {
                    idProducto=p.getProducto().getIdProducto();
                    
                    strSQL="SELECT saldo FROM lotesAlmacenes "
                            + "WHERE idAlmacen="+m.getIdAlmacen()+" AND idEmpaque="+idProducto+" AND lote='"+lote+"'";
                    rs=st.executeQuery(strSQL);
                    if(rs.next()) {
                        strSQL="UPDATE lotesAlmacenes SET cantidad=cantidad+"+p.getCantRecibida()+", saldo=saldo+"+p.getCantRecibida()+" "
                                + "WHERE idAlmacen="+m.getIdAlmacen()+" AND idEmpaque="+idProducto+" AND lote='"+lote+"'";
                    } else {
                        strSQL="INSERT INTO lotesAlmacenes (idAlmacen, idEmpaque, fechaCaducidad, lote, cantidad, saldo, existenciaFisica, separados) "
                                + "VALUES ("+m.getIdAlmacen()+", "+idProducto+", DATEADD(DAY, 365, convert(date, GETDATE())), '"+lote+"', "+p.getCantRecibida()+", "+p.getCantRecibida()+", 0, 0)";
                    }
                    st.executeUpdate(strSQL);
                    
                    strSQL="INSERT INTO lotesKardex (idAlmacen, idMovto, idEmpaque, lote, cantidad, suma, fecha, existenciaAnterior) "
                            + "VALUES ("+m.getIdAlmacen()+", "+m.getIdMovto()+", "+idProducto+", '"+lote+"', "+p.getCantRecibida()+", 1, GETDATE(), 0)";
                    st.executeUpdate(strSQL);
                }
            }
            if(idOrdenCompra!=0) {
                strSQL="UPDATE ordenCompra SET propietario=0, estado=2 WHERE idOrdenCompra="+idOrdenCompra;
                st.executeUpdate(strSQL);
            }
            st.executeUpdate("COMMIT TRANSACTION");
            ok=true;
        } catch(SQLException e) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw(e);
        } finally {
            cn.close();
        }
        return ok;
    }
    
    public boolean grabarComprasOficina(TOMovimiento m, ArrayList<MovimientoProducto> productos, int idOrdenCompra) throws SQLException {
        int capturados;
        boolean ok=false;
        boolean nueva;
        ArrayList<ImpuestosProducto> impuestos;
        
        Connection cn=this.ds.getConnection();
        String strSQL="INSERT INTO movimientosDetalle (idMovto, idEmpaque, cantFacturada, cantSinCargo, costo, desctoProducto1, desctoProducto2, desctoConfidencial, unitario, cantOrdenada, cantRecibida, idImpuestoGrupo, fecha, existenciaAnterior) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, getdate(), ?)";
        PreparedStatement ps=cn.prepareStatement(strSQL);
        
        String strSQL1="UPDATE movimientosDetalle " +
                    "SET costo=?, desctoProducto1=?, desctoProducto2=?, desctoConfidencial=?, unitario=?, cantFacturada=?, cantSinCargo=?, existenciaAnterior=? " +
                    "WHERE idMovto="+m.getIdMovto()+" AND idEmpaque=?";
        PreparedStatement ps1=cn.prepareStatement(strSQL1);
        
        String strSQL2="UPDATE movimientosDetalleImpuestos " +
                    "SET importe=? " +
                    "WHERE idMovto="+m.getIdMovto()+" AND idEmpaque=?";
        PreparedStatement ps2=cn.prepareStatement(strSQL2);
        
//        String strSQL3="INSERT INTO kardexOficina (idAlmacen, idMovto, idTipoMovto, idEmpaque, fecha, existenciaAnterior, cantidad) " +
//                    "VALUES ("+m.getIdAlmacen()+", "+m.getIdMovto()+", 1, ?, GETDATE(), ?, ?)";
//        PreparedStatement ps3=cn.prepareStatement(strSQL3);
        
        String strSQL4="INSERT INTO almacenesEmpaques (idAlmacen, idEmpaque, existenciaAlmacen, existenciaOficina, existenciaMinima, existenciaMaxima) " +
                    "VALUES (?, ?, 0, ?, 0, 0)";
        PreparedStatement ps4=cn.prepareStatement(strSQL4);
        
        String strSQL5="UPDATE almacenesEmpaques "
                    + "SET existenciaOficina=existenciaOficina+? "
                    + "WHERE idAlmacen="+m.getIdAlmacen()+" AND idEmpaque=?";
        PreparedStatement ps5=cn.prepareStatement(strSQL5);
        
        String strSQL6="INSERT INTO empresasEmpaques (idEmpresa, idEmpaque, promedioPonderado, existenciaOficina, idMovtoUltimaEntrada) "
                    + "VALUES ("+m.getIdEmpresa()+", ?, ?, ?, ?)";
        PreparedStatement ps6=cn.prepareStatement(strSQL6);
        
        String strSQL7="UPDATE empresasEmpaques " +
                    "SET existenciaOficina=existenciaOficina+?" +
                        ", promedioPonderado=(existenciaOficina*promedioPonderado+?*?)/(existenciaOficina+?+?)" +
                        ", idMovtoUltimaEntrada=? "+
                    "WHERE idEmpresa="+m.getIdEmpresa()+" AND idEmpaque=?";
        PreparedStatement ps7=cn.prepareStatement(strSQL7);
        
        ResultSet rs;
        int idEmpaque, folio, idImpuestoGrupo;
        double existenciaAnterior;
        Statement st=cn.createStatement();
        try {
            capturados=0;
            st.executeUpdate("BEGIN TRANSACTION");
            
            strSQL="SELECT statusOficina FROM comprobantes where idComprobante="+m.getIdReferencia();
            rs=st.executeQuery(strSQL);
            if(rs.next()) {
                if(rs.getBoolean("statusOficina")) {
                    throw new SQLException("Ya se ha capturado y cerrado la entrada");
                } else {
                    strSQL="UPDATE comprobantes SET statusOficina=1 WHERE idComprobante="+m.getIdReferencia();
                    st.executeUpdate(strSQL);
                }
            } else {
                throw new SQLException("No se encontro el comprobante");
            }
            if(m.getIdMovto()==0) {
                folio=0;
                nueva=true;
                rs=st.executeQuery("SELECT folio FROM movimientosFolios WHERE idAlmacen="+m.getIdAlmacen()+" AND idTipo=1");
                if(rs.next()) {
                    folio=rs.getInt("folio");
                    strSQL="UPDATE movimientosFolios SET folio=folio+1 WHERE idAlmacen="+m.getIdAlmacen()+" AND idTipo=1";
                } else {
                    folio=1;
                    strSQL="INSERT INTO movimientosFolios (idAlmacen, idTipo, folio) VALUES ("+m.getIdAlmacen()+", 1, 2)";
                }
                st.executeUpdate(strSQL);
                
                strSQL="INSERT INTO movimientos (idTipo, idCedis, folio, idEmpresa, idAlmacen, idComprobante, idImpuestoZona, idMoneda, tipoCambio, desctoComercial, desctoProntoPago, idUsuario, fecha, status) "
                        + "VALUES (1, "+m.getIdCedis()+", "+folio+", "+m.getIdEmpresa()+", "+m.getIdAlmacen()+", "+m.getIdReferencia()+", "+m.getIdImpuestoZona()+", "+m.getIdMoneda()+", "+m.getTipoCambio()+", "+m.getDesctoComercial()+", "+m.getDesctoProntoPago()+", "+this.idUsuario+", getdate(), 1)";
                st.executeUpdate(strSQL);

                rs=st.executeQuery("SELECT @@IDENTITY AS idMovto");
                if(rs.next()) {
                    m.setIdMovto(rs.getInt("idMovto"));
                }
                if(idOrdenCompra!=0) {
                    strSQL="UPDATE ordenCompra SET propietario=0, estado=2 WHERE idOrdenCompra="+idOrdenCompra;
                    st.executeUpdate(strSQL);
                }
            } else {
                nueva=false;
                st.executeUpdate("UPDATE movimientos "
                        + "SET idMoneda="+m.getIdMoneda()+", tipoCambio="+m.getTipoCambio()+" "
                                + ", desctoComercial="+m.getDesctoComercial()+", desctoProntoPago="+m.getDesctoProntoPago()+" "
                                + ", fecha=GETDATE(), status=1 "
                        + "WHERE idMovto="+m.getIdMovto());
            }
            
            //rs=st.executeQuery("select DATEPART(weekday, getdate()-1) AS DIA, DATEPART(week, GETDATE()) AS SEM, DATEPART(YEAR, GETDATE())%10 AS ANIO");
            //lote=""+rs.getInt("DIA")+String.format("%02d", rs.getInt("SEM"))+rs.getInt("ANIO")+"1";
            
            for(MovimientoProducto p: productos) {
                idEmpaque=p.getProducto().getIdProducto();
                
                if(p.getCantFacturada()> 0) {
                    capturados++;
                    rs=st.executeQuery("SELECT existenciaOficina " +
                                        "FROM almacenesEmpaques " +
                                        "WHERE idAlmacen="+m.getIdAlmacen()+" AND idEmpaque="+idEmpaque);
                    if(rs.next()) {
                        existenciaAnterior=rs.getDouble("existenciaOficina");
                        
                        ps5.setDouble(1, p.getCantFacturada()+p.getCantSinCargo());
                        ps5.setInt(2, idEmpaque);
                        ps5.executeUpdate();
                        
                        ps7.setDouble(1, p.getCantFacturada()+p.getCantSinCargo());
                        ps7.setDouble(2, p.getCantFacturada());
                        ps7.setDouble(3, p.getUnitario());
                        ps7.setDouble(4, p.getCantFacturada());
                        ps7.setDouble(5, p.getCantSinCargo());
                        ps7.setInt(6, m.getIdMovto());
                        ps7.setInt(7, idEmpaque);
                        ps7.executeUpdate();
                    } else {
                        existenciaAnterior=0;
                        
                        ps4.setInt(1, m.getIdAlmacen());
                        ps4.setInt(2, idEmpaque);
                        ps4.setDouble(3, p.getCantFacturada()+p.getCantSinCargo());
                        ps4.executeUpdate();
                        
                        rs=st.executeQuery("SELECT existenciaOficina "
                                + "FROM empresasEmpaques "
                                + "WHERE idEmpresa="+m.getIdEmpresa()+" AND idEmpaque="+idEmpaque);
                        if(rs.next()) {
                            ps7.setDouble(1, p.getCantFacturada()+p.getCantSinCargo());
                            ps7.setDouble(2, p.getCantFacturada());
                            ps7.setDouble(3, p.getUnitario());
                            ps7.setDouble(4, p.getCantFacturada());
                            ps7.setDouble(5, p.getCantSinCargo());
                            ps7.setInt(6, m.getIdMovto());
                            ps7.setInt(7, idEmpaque);
                            ps7.executeUpdate();
                        } else {
                            ps6.setInt(1, idEmpaque);
                            ps6.setDouble(2, p.getUnitario());
                            ps6.setDouble(3, p.getCantFacturada()+p.getCantSinCargo());
                            ps6.setInt(4, m.getIdMovto());
                            ps6.executeUpdate();
                        }
                    }
//                    ps3.setInt(1, idEmpaque);
//                    ps3.setDouble(2, existenciaAnterior);
//                    ps3.setDouble(3, p.getCantFacturada()+p.getCantSinCargo());
//                    ps3.executeUpdate();
                    if(nueva) {
                        ps.setInt(1, m.getIdMovto());
                        ps.setInt(2, idEmpaque);
                        ps.setDouble(3, p.getCantFacturada());
                        ps.setDouble(4, p.getCantSinCargo());
                        ps.setDouble(5, p.getCosto());
                        ps.setDouble(6, p.getDesctoProducto1());
                        ps.setDouble(7, p.getDesctoProducto2());
                        ps.setDouble(8, p.getDesctoConfidencial());
                        ps.setDouble(9, p.getUnitario());
                        ps.setDouble(10, p.getCantOrdenada());
                        ps.setDouble(11, 0);
                        ps.setInt(12, p.getProducto().getArticulo().getImpuestoGrupo().getIdGrupo());
                        ps.setDouble(13, existenciaAnterior);
                        ps.executeUpdate();

                        idImpuestoGrupo=p.getProducto().getArticulo().getImpuestoGrupo().getIdGrupo();
                        this.agregarImpuestosProducto(cn, idImpuestoGrupo, m.getIdImpuestoZona(), m.getIdMovto(), idEmpaque);
                        this.calculaImpuestosProducto(cn, m.getIdMovto(), idEmpaque, p.getUnitario(), p.getProducto().getPiezas());
                    } else {
                        ps1.setDouble(1, p.getCosto());
                        ps1.setDouble(2, p.getDesctoProducto1());
                        ps1.setDouble(3, p.getDesctoProducto2());
                        ps1.setDouble(4, p.getDesctoConfidencial());
                        ps1.setDouble(5, p.getUnitario());
                        ps1.setDouble(6, p.getCantFacturada());
                        ps1.setDouble(7, p.getCantSinCargo());
                        ps1.setDouble(8, existenciaAnterior);
                        ps1.setInt(9, idEmpaque);
                        ps1.executeUpdate();
                    }
                    impuestos=p.getImpuestos();
                    for(ImpuestosProducto i:impuestos) {
                        ps2.setDouble(1, i.getImporte());
                        ps2.setInt(2, idEmpaque);
                        ps2.executeUpdate();
                    }
                }
            }
            if(capturados==0) {
                st.executeUpdate("UPDATE comprobantes SET statusOficina=0 WHERE idComprobante="+m.getIdReferencia());
            }
            st.executeUpdate("COMMIT TRANSACTION");
            ok=true;
        } catch(SQLException e) {
            st.executeUpdate("ROLLBACK TRANSACTION");
            throw(e);
        } finally {
            cn.close();
        }
        return ok;
    }
    
    public int obtenerIdOrdenCompra(int idComprobante, int idEntrada) throws SQLException {
        int idOrdenCompra=0;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT idOrdenCompra FROM comprobantesOrdenesCompra " +
                                         "WHERE idComprobante="+idComprobante+" AND idEntrada="+idEntrada);
            if(rs.next()) {
                idOrdenCompra=rs.getInt("idOrdenCompra");
            }
        } finally {
            cn.close();
        }
        return idOrdenCompra;
    } 
    
    private void calculaImpuestosProducto(Connection cn, int idMovto, int idEmpaque, double unitario, double piezas) throws SQLException {
        Statement st=cn.createStatement();
        String strSQL="UPDATE movimientosDetalleImpuestos " +
                        "SET importe=CASE WHEN aplicable=0 THEN 0 " +
                                        "WHEN modo=1 THEN " + unitario + "*valor/100.00 " +
                                        "ELSE "+piezas+"*valor END " +
                        "WHERE idMovto="+idMovto+" AND idEmpaque="+idEmpaque+" AND acumulable=1";
        st.executeUpdate(strSQL);
        
        strSQL="UPDATE d " +
                "SET importe=CASE WHEN aplicable=0 THEN 0 " +
                                 "WHEN modo=1 THEN ("+unitario+"+COALESCE(a.acumulable, 0))*valor/100.00 " +
                		 "ELSE "+piezas+"*valor END " +
                "FROM movimientosDetalleImpuestos d " +
                "LEFT JOIN (SELECT idMovto, idEmpaque, SUM(importe) AS acumulable " +
                	   "FROM movimientosDetalleImpuestos " +
                	   "WHERE idMovto=4 AND idEmpaque=8 AND acumulable=1 " +
                	   "GROUP BY idMovto, idEmpaque) a ON a.idMovto=d.idMovto AND a.idEmpaque=d.idEmpaque " +
                "WHERE d.idMovto="+idMovto+" AND d.idEmpaque="+idEmpaque+" AND d.acumulable=0";
        st.executeUpdate(strSQL);
    }
    
    public ArrayList<ImpuestosProducto> obtenerImpuestosProducto(int idMovto, int idEmpaque) throws SQLException {
        ArrayList<ImpuestosProducto> impuestos=new ArrayList<ImpuestosProducto>();
        Connection cn = this.ds.getConnection();
        Statement st = cn.createStatement();
        try {
            String strSQL="select idImpuesto, impuesto, valor, aplicable, modo, acreditable, importe, acumulable\n" +
                            "from movimientosDetalleImpuestos\n" +
                            "where idMovto="+idMovto+" and idEmpaque="+idEmpaque;
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                impuestos.add(construirImpuestosProducto(rs));
            }
        } finally {
            cn.close();
        }
        return impuestos;
    }
    
    public ArrayList<ImpuestosProducto> generarImpuestosProducto(int idImpuestoGrupo, int idZona) throws SQLException {
        ArrayList<ImpuestosProducto> impuestos=new ArrayList<ImpuestosProducto>();
        Connection cn = this.ds.getConnection();
        Statement st=cn.createStatement();
        String strSQL="SELECT id.idImpuesto, i.impuesto, id.valor, i.aplicable, i.modo, i.acreditable, 0.00 as importe, i.acumulable\n" +
                    "FROM impuestosDetalle id\n" +
                    "INNER JOIN impuestos i ON i.idImpuesto=id.idImpuesto\n" +
                    "WHERE id.idGrupo="+idImpuestoGrupo+" and id.idZona="+idZona+" and GETDATE() between fechaInicial and fechaFinal";
        ResultSet rs=st.executeQuery(strSQL);
        while(rs.next()) {
            impuestos.add(this.construirImpuestosProducto(rs));
        }
        return impuestos;
    }
    
    private ImpuestosProducto construirImpuestosProducto(ResultSet rs) throws SQLException {
        ImpuestosProducto ip=new ImpuestosProducto();
        ip.setIdImpuesto(rs.getInt("idImpuesto"));
        ip.setImpuesto(rs.getString("impuesto"));
        ip.setValor(rs.getDouble("valor"));
        ip.setAplicable(rs.getBoolean("aplicable"));
        ip.setModo(rs.getInt("modo"));
        ip.setAcreditable(rs.getBoolean("acreditable"));
        ip.setImporte(rs.getDouble("importe"));
        ip.setAcumulable(rs.getBoolean("acumulable"));
        return ip;
    }
    
    private void agregarImpuestosProducto(Connection cn, int idImpuestoGrupo, int idZona, int idMovto, int idEmpaque) throws SQLException {
        Statement st=cn.createStatement();
        String strSQL="insert into movimientosDetalleImpuestos (idMovto, idEmpaque, idImpuesto, impuesto, valor, aplicable, modo, acreditable, importe, acumulable) " +
                        "select "+idMovto+", "+idEmpaque+", id.idImpuesto, i.impuesto, id.valor, i.aplicable, i.modo, i.acreditable, 0.00 as importe, i.acumulable " +
                        "from impuestosDetalle id " +
                        "inner join impuestos i on i.idImpuesto=id.idImpuesto " +
                        "where id.idGrupo="+idImpuestoGrupo+" and id.idZona="+idZona+" and GETDATE() between fechaInicial and fechaFinal";
        st.executeUpdate(strSQL);
    }
    
    public ArrayList<TOMovimientoDetalle> obtenerDetalleMovimiento(int idMovto) throws SQLException, NamingException {
        ArrayList<TOMovimientoDetalle> productos=new ArrayList<TOMovimientoDetalle>();
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            ResultSet rs=st.executeQuery("SELECT * FROM movimientosDetalle WHERE idMovto="+idMovto);
            while(rs.next()) {
                productos.add(this.construirDetalle(rs));
            }
        } finally {
            cn.close();
        }
        return productos;
    }
    
    public TOMovimientoDetalle construirDetalle(ResultSet rs) throws SQLException {
        TOMovimientoDetalle to=new TOMovimientoDetalle();
        to.setIdProducto(rs.getInt("idEmpaque"));
        to.setCantOrdenada(rs.getDouble("cantOrdenada"));
        to.setCantFacturada(rs.getDouble("cantFacturada"));
        to.setCantSinCargo(rs.getDouble("cantSinCargo"));
        to.setCantRecibida(rs.getDouble("cantRecibida"));
        to.setCosto(rs.getDouble("costo"));
        to.setDesctoConfidencial(rs.getDouble("desctoConfidencial"));
        to.setDesctoProducto1(rs.getDouble("desctoProducto1"));
        to.setDesctoProducto2(rs.getDouble("desctoProducto2"));
        to.setUnitario(rs.getDouble("unitario"));
        return to;
    }
    
    public TOMovimiento obtenerMovimientoComprobante(int idComprobante) throws SQLException {
        TOMovimiento to=null;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            String strSQL="SELECT M.*, COALESCE(MR.idMovtoAlmacen, 0) as idMovtoAlmacen " +
                        "FROM movimientos M " +
                        "INNER JOIN comprobantes C ON C.idComprobante=M.idComprobante " +
                        "LEFT JOIN movimientosRelacionados MR ON MR.idMovto=M.idMovto " +
                        "WHERE C.idComprobante="+idComprobante;
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                to=construir(rs);
            }
        } finally {
            cn.close();
        }
        return to;
    }
    
    public ArrayList<TOMovimiento> obtenerMovimientos(int idAlmacen, int idTipo, int status) throws SQLException {
        String strSQL;
        ArrayList<TOMovimiento> solicitudes=new ArrayList<TOMovimiento>();
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        try {
            strSQL="SELECT M.*, COALESCE(MR.idMovtoAlmacen, 0) as idMovtoAlmacen "
                    + "FROM movimientos M "
                    + "LEFT JOIN movimientosRelacionados MR ON MR.idMovto=M.idMovto "
                    + "WHERE idAlmacen="+idAlmacen+" AND idTipo="+idTipo+" AND status="+status;
            ResultSet rs=st.executeQuery(strSQL);
            while(rs.next()) {
                solicitudes.add(this.construir(rs));
            }
        } finally {
            cn.close();
        }
        return solicitudes;
    }
    
//    public ArrayList<TOMovimiento> obtenerMovimientos(int idTipo, int status) throws SQLException {
//        String strSQL;
//        ArrayList<TOMovimiento> solicitudes=new ArrayList<TOMovimiento>();
//        Connection cn=this.ds.getConnection();
//        Statement st=cn.createStatement();
//        try {
//            strSQL="SELECT * FROM movimientos WHERE idTipo="+idTipo+(status==0?"":" AND status="+status);
//            ResultSet rs=st.executeQuery(strSQL);
//            while(rs.next()) {
//                solicitudes.add(this.construir(rs));
//            }
//        } finally {
//            cn.close();
//        }
//        return solicitudes;
//    }
    
    public TOMovimiento obtenerMovimiento(int idMovto) throws SQLException {
        TOMovimiento to=null;
        Connection cn=this.ds.getConnection();
        Statement st=cn.createStatement();
        String strSQL="SELECT M.*, COALESCE(MR.idMovtoAlmacen, 0) as idMovtoAlmacen " +
                "FROM movimientos M " +
                "LEFT JOIN movimientosRelacionados MR ON MR.idMovto=M.idMovto " +
                "WHERE M.idMovto="+idMovto;
        try {
            ResultSet rs=st.executeQuery(strSQL);
            if(rs.next()) {
                to=construir(rs);
            }
        } finally {
            cn.close();
        }
        return to;
    }
    
    private TOMovimiento construir(ResultSet rs) throws SQLException {
        TOMovimiento to=new TOMovimiento();
        to.setIdMovto(rs.getInt("idMovto"));
        to.setIdMovtoAlmacen(rs.getInt("idMovtoAlmacen"));
        to.setIdTipo(rs.getInt("idTipo"));
        to.setFolio(rs.getInt("folio"));
        to.setIdCedis(rs.getInt("idCedis"));
        to.setIdEmpresa(rs.getInt("idEmpresa"));
        to.setIdAlmacen(rs.getInt("idAlmacen"));
        to.setIdImpuestoZona(rs.getInt("idImpuestoZona"));
        to.setIdReferencia(rs.getInt("idComprobante"));
        to.setIdMoneda(rs.getInt("idMoneda"));
        to.setTipoCambio(rs.getDouble("tipoCambio"));
        to.setDesctoComercial(rs.getDouble("desctoComercial"));
        to.setDesctoProntoPago(rs.getDouble("desctoprontoPago"));
        java.sql.Date f=rs.getDate("fecha");
        to.setFecha(new java.util.Date(f.getTime()));
//        to.setFecha(new java.util.Date(rs.getDate("fecha").getTime()));
        to.setIdUsuario(rs.getInt("idUsuario"));
        to.setStatus(rs.getBoolean("status"));
        return to;
    }
    
    public int obtenerIdUsuario() {
        return this.idUsuario;
    }
}