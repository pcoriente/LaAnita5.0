package ventas;

import clientes.MbMiniCliente;
import clientes.MbTiendas;
import clientes.dominio.Venta;
import clientes.dominio.VentaProducto;
import entradas.MbComprobantes;
import entradas.dao.DAOMovimientos;
import entradas.to.TOMovimiento;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import producto2.MbProductosBuscar;
import usuarios.MbAcciones;
import usuarios.dominio.Accion;

/**
 *
 * @author jesc
 */
@Named(value = "mbVentas")
@SessionScoped
public class MbVentas implements Serializable {
    private ArrayList<Accion> acciones;
    @ManagedProperty(value = "#{mbAcciones}")
    private MbAcciones mbAcciones;
    @ManagedProperty(value = "#{mbComprobantes}")
    private MbComprobantes mbComprobantes;
    @ManagedProperty(value = "#{mbProductosBuscar}")
    private MbProductosBuscar mbBuscar;
    private boolean modoEdicion;
    
    @ManagedProperty(value = "#{mbMiniCliente}")
    private MbMiniCliente mbClientes;
    @ManagedProperty(value = "#{mbTiendas}")
    private MbTiendas mbTiendas;
    private DAOMovimientos dao;
    
    private Venta venta;
    private ArrayList<VentaProducto> ventaDetalle;
    private VentaProducto ventaProducto;
    
    public MbVentas() throws NamingException {
        this.mbAcciones = new MbAcciones();
        this.mbComprobantes = new MbComprobantes();
        this.mbBuscar = new MbProductosBuscar();
        this.mbClientes = new MbMiniCliente();
        this.mbTiendas=new MbTiendas();
        this.inicializa();
    }
    
    public void capturar() {
        boolean ok=false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "capturar");
        if(this.mbComprobantes.getMbAlmacenes().getToAlmacen().getIdAlmacen()==0) {
            fMsg.setSeverity(FacesMessage.SEVERITY_WARN);
            fMsg.setDetail("Se requiere seleccionar un almacen");
        } else if(this.mbClientes.getCliente().getIdContribuyente()==0) {
            fMsg.setSeverity(FacesMessage.SEVERITY_WARN);
            fMsg.setDetail("Se requiere seleccionar un cliente");
        } else if(this.mbTiendas.getMiniTienda().getIdTienda()==0) {
            fMsg.setSeverity(FacesMessage.SEVERITY_WARN);
            fMsg.setDetail("Se requiere seleccionar un cliente");
        } else {
            this.venta=new Venta();
            this.venta.setAlmacen(this.mbComprobantes.getMbAlmacenes().getToAlmacen());
            try {
                this.dao=new DAOMovimientos();
                int idMovto=this.dao.agregarMovimientoRelacionado(this.convertirTO());
                this.venta=this.convertir(this.dao.obtenerMovimiento(idMovto));
                //public ArrayList<TOMovimiento> movimientosPendientes(boolean oficina, int entrada)
                this.ventaDetalle=new ArrayList<VentaProducto>();
                this.ventaProducto=new VentaProducto();
                this.modoEdicion=true;
                ok=true;
            } catch (SQLException ex) {
                fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
            } catch (NamingException ex) {
                fMsg.setDetail(ex.getMessage());
            }
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }
    
    private Venta convertir(TOMovimiento to) {
        Venta vta=new Venta();
        vta.setIdMovto(to.getIdMovto());
        vta.setIdMovtoAlmacen(to.getIdMovtoAlmacen());
        vta.setFolio(to.getFolio());
        vta.setAlmacen(this.mbComprobantes.getMbAlmacenes().getToAlmacen());
        //vta.setComprobante(null);
        vta.setDesctoComercial(to.getDesctoComercial());
        vta.setDesctoProntoPago(to.getDesctoProntoPago());
        //vta.setDescuento();
        vta.setFecha(to.getFecha());
        vta.setFolio(to.getFolio());
        vta.setIdImpuestoZona(to.getIdImpuestoZona());
        //vta.setMoneda();
        vta.setIdUsuario(to.getIdUsuario());
        //vta.setSubTotal();
        vta.setTipoCambio(to.getTipoCambio());
        //vta.setTotal(total);
        return vta;
    }
    
    private TOMovimiento convertirTO() {
        TOMovimiento to=new TOMovimiento();
        to.setIdMovto(this.venta.getIdMovto());
        to.setIdTipo(30);
        to.setFolio(this.venta.getFolio());
        to.setIdCedis(this.venta.getAlmacen().getIdCedis());
        to.setIdEmpresa(this.venta.getAlmacen().getIdEmpresa());
        to.setIdAlmacen(this.venta.getAlmacen().getIdAlmacen());
        to.setFecha(this.venta.getFecha());
        to.setIdUsuario(this.venta.getIdUsuario());
        return to;
    }
    
    public void cargaTiendas() {
        this.mbTiendas.cargaTiendas(this.mbClientes.getCliente().getIdContribuyente());
    }
    
    public String terminar() {
        this.acciones=null;
        this.inicializa();
        return "index.xhtml";
    }
    
    private void inicializa() {
        this.inicializar();
    }
    
    public void inicializar() {
        this.mbComprobantes.getMbAlmacenes().getMbCedis().obtenerDefaultCedis();
        this.mbComprobantes.getMbAlmacenes().cargaAlmacenes();
        this.mbBuscar.inicializar();
        this.mbClientes.inicializar();
        this.mbTiendas.inicializar();
        this.modoEdicion=false;
        this.ventaDetalle=new ArrayList<VentaProducto>();
    }

    public ArrayList<Accion> obtenerAcciones(int idModulo) {
        if (this.acciones == null) {
            this.acciones = this.mbAcciones.obtenerAcciones(idModulo);
        }
        return acciones;
    }

    public ArrayList<Accion> getAcciones() {
        if (this.acciones == null) {
            this.acciones = this.mbAcciones.obtenerAcciones(29);
        }
        return acciones;
    }

    public void setAcciones(ArrayList<Accion> acciones) {
        this.acciones = acciones;
    }

    public MbAcciones getMbAcciones() {
        return mbAcciones;
    }

    public void setMbAcciones(MbAcciones mbAcciones) {
        this.mbAcciones = mbAcciones;
    }

    public MbComprobantes getMbComprobantes() {
        return mbComprobantes;
    }

    public void setMbComprobantes(MbComprobantes mbComprobantes) {
        this.mbComprobantes = mbComprobantes;
    }

    public MbProductosBuscar getMbBuscar() {
        return mbBuscar;
    }

    public void setMbBuscar(MbProductosBuscar mbBuscar) {
        this.mbBuscar = mbBuscar;
    }

    public boolean isModoEdicion() {
        return modoEdicion;
    }

    public void setModoEdicion(boolean modoEdicion) {
        this.modoEdicion = modoEdicion;
    }

    public MbMiniCliente getMbClientes() {
        return mbClientes;
    }

    public void setMbClientes(MbMiniCliente mbClientes) {
        this.mbClientes = mbClientes;
    }

    public MbTiendas getMbTiendas() {
        return mbTiendas;
    }

    public void setMbTiendas(MbTiendas mbTiendas) {
        this.mbTiendas = mbTiendas;
    }
}
