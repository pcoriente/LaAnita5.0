package movimientos;

import entradas.MbComprobantes;
import entradas.dao.DAOMovimientos;
import entradas.dominio.MovimientoProducto;
import entradas.to.TOMovimiento;
import entradas.to.TOMovimientoDetalle;
import impuestos.dao.DAOImpuestosProducto;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import movimientos.dao.DAOLotes;
import movimientos.dominio.Envio;
import movimientos.dominio.Lote;
import movimientos.dominio.SalidaProducto;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import producto2.MbProductosBuscar;
import usuarios.MbAcciones;
import usuarios.dominio.Accion;

/**
 *
 * @author jesc
 */
@Named(value = "mbEnvios")
@SessionScoped
public class MbEnvios implements Serializable {

    private boolean modoEdicion;
    private Lote lote;
    private double resSeparados;
    private double sumaLotes;
    private Envio envio;
//    private int idMovtoAlmacen;
    private ArrayList<Envio> envios;
    private ArrayList<SalidaProducto> envioDetalle;
    private SalidaProducto envioProducto;
    private SalidaProducto resEnvioProducto;
    private DAOMovimientos dao;
    private DAOImpuestosProducto daoImps;
    private DAOLotes daoLotes;
    private ArrayList<Accion> acciones;
    @ManagedProperty(value = "#{mbAcciones}")
    private MbAcciones mbAcciones;
    @ManagedProperty(value = "#{mbComprobantes}")
    private MbComprobantes mbComprobantes;
    @ManagedProperty(value = "#{mbProductosBuscar}")
    private MbProductosBuscar mbBuscar;

    public MbEnvios() throws NamingException {
        this.mbAcciones = new MbAcciones();
        this.mbComprobantes = new MbComprobantes();
        this.mbBuscar = new MbProductosBuscar();
        this.inicializa();
    }

    private void inicializa() {
        inicializar();
    }

    public void inicializar() {
        this.mbComprobantes.getMbAlmacenes().getMbCedis().obtenerDefaultCedis();
        this.mbComprobantes.getMbAlmacenes().cargaAlmacenes();
        this.mbBuscar.inicializar();
        inicializaLocales();
    }

    private void inicializaLocales() {
        this.modoEdicion = false;
        this.resEnvioProducto = new SalidaProducto();
        this.lote = new Lote();
    }

    public void grabarEnvio() {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "gestionLotes");
        int idAlmacenDestino=this.envio.getComprobante().getAlmacen().getIdAlmacen();
        try {
            this.dao=new DAOMovimientos();
            this.dao.grabarTraspasoEnvio(idAlmacenDestino, this.convertirTOMovimiento(), this.convertirDetalle());
            this.modoEdicion=false;
            ok=true;
        } catch (SQLException ex) {
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setDetail(ex.getMessage());
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }

    public ArrayList<TOMovimientoDetalle> convertirDetalle() {
        ArrayList<TOMovimientoDetalle> listaDetalle = new ArrayList<TOMovimientoDetalle>();
        for (SalidaProducto p : this.envioDetalle) {
            listaDetalle.add(this.convertirTOMovimientoDetalle(p));
        }
        return listaDetalle;
    }

    public TOMovimientoDetalle convertirTOMovimientoDetalle(SalidaProducto d) {
        TOMovimientoDetalle to = new TOMovimientoDetalle();
        to.setIdProducto(d.getProducto().getIdProducto());
        to.setCantFacturada(d.getCantFacturada());
        to.setCantOrdenada(d.getCantOrdenada());
        to.setCantRecibida(0);
        to.setCantSinCargo(0);
        to.setCostoOrdenado(0);
        to.setNeto(0);
        to.setDesctoConfidencial(0);
        to.setDesctoProducto1(0);
        to.setDesctoProducto2(0);
        to.setUnitario(0);
        to.setIdImpuestoGrupo(0);
        return to;
    }

    public TOMovimiento convertirTOMovimiento() {
        TOMovimiento toMovimiento = new TOMovimiento();
        toMovimiento.setIdMovto(this.envio.getIdMovto());
        toMovimiento.setIdMovtoAlmacen(this.envio.getIdMovtoAlmacen());
        toMovimiento.setIdTipo(2);
        toMovimiento.setFolio(this.envio.getFolio());
        toMovimiento.setIdCedis(this.envio.getAlmacen().getCedis().getIdCedis());
        toMovimiento.setIdEmpresa(this.envio.getAlmacen().getEmpresa().getIdEmpresa());
        toMovimiento.setIdAlmacen(this.envio.getAlmacen().getIdAlmacen());
        toMovimiento.setIdReferencia(this.envio.getComprobante().getIdComprobante());
        toMovimiento.setIdImpuestoZona(this.envio.getIdImpuestoZona());
        toMovimiento.setDesctoComercial(this.envio.getDesctoComercial());
        toMovimiento.setDesctoProntoPago(this.envio.getDesctoProntoPago());
        toMovimiento.setFecha(this.envio.getFecha());
        toMovimiento.setIdUsuario(this.envio.getIdUsuario());
        toMovimiento.setIdMoneda(this.envio.getMoneda().getIdMoneda());
        toMovimiento.setTipoCambio(this.envio.getTipoCambio());
        return toMovimiento;
    }

    public void actualizarCantidad() {
        this.envioProducto.setCantFacturada(this.sumaLotes);
        this.resEnvioProducto.setCantFacturada(this.sumaLotes);
    }

    public void gestionarLotes() {
        boolean cierra = false;
        RequestContext context = RequestContext.getCurrentInstance();
        this.gestionLotes();
        if (this.envioProducto.getCantFacturada() == this.sumaLotes) {
            cierra = true;
        }
        context.addCallbackParam("okLotes", cierra);
    }

    public void gestionLotes() {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "gestionLotes");
        try {
            this.daoLotes = new DAOLotes();
            double separar = this.lote.getSeparados() - this.resSeparados;
            if (separar > 0) {
                if (this.sumaLotes + separar > this.envioProducto.getCantOrdenada()) {
                    fMsg.setDetail("Cantidad enviar mayor que cantidad solicitada");
                    this.lote.setSeparados(this.resSeparados);
                } else {
                    double separados = this.daoLotes.separa(this.envio.getIdMovto(), this.envio.getIdMovtoAlmacen(), this.lote, separar);
                    if (separados < separar) {
                        fMsg.setSeverity(FacesMessage.SEVERITY_WARN);
                        fMsg.setDetail("No se pudieron obtener los lotes solicitados");
                    } else {
                        ok = true;
                    }
                    this.lote.setSeparados(this.resSeparados + separados);
                    this.sumaLotes += separados;
                    this.resSeparados = this.lote.getSeparados();
                }
            } else {
                this.daoLotes.libera(this.envio.getIdMovto(), this.envio.getIdMovtoAlmacen(), this.lote, -separar);
                this.lote.setSeparados(this.resSeparados + separar);    // separar es negativo por esos se suma
                this.sumaLotes += separar;
                this.resSeparados = this.lote.getSeparados();
                ok = true;
            }
        } catch (SQLException ex) {
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setDetail(ex.getMessage());
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }

    public void respaldaSeparados() {
        this.resSeparados = this.lote.getSeparados();
    }

    public boolean comparaLotes(Lote lote) {
        boolean disable = true;
        if (this.lote.getLote().equals(lote.getLote())) {
            disable = false;
        }
        return disable;
    }

    public void editarLotes() {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "editarLotes");
        RequestContext context = RequestContext.getCurrentInstance();
        if (this.envioProducto.getCantFacturada() < 0) {
            fMsg.setDetail("La cantidad enviada no puede ser menor que cero");
        } else if (this.envioProducto.getCantFacturada() > this.envioProducto.getCantOrdenada()) {
            fMsg.setDetail("La cantidad enviada no puede ser mayor a la cantidad solicitada");
        } else if (this.envioProducto.getCantFacturada() != this.envioProducto.getSeparados()) {
            try {
                this.sumaLotes = 0;
                this.daoLotes = new DAOLotes();
//                this.envioProducto.setLotes(this.daoLotes.obtenerLotes(this.envio.getAlmacen().getIdAlmacen(), this.idMovtoAlmacen, this.envioProducto.getProducto().getIdProducto()));
                this.envioProducto.setLotes(this.daoLotes.obtenerLotes(this.envio.getAlmacen().getIdAlmacen(), this.envio.getIdMovtoAlmacen(), this.envioProducto.getProducto().getIdProducto()));
                for (Lote l : this.envioProducto.getLotes()) {
                    this.sumaLotes += l.getSeparados();
                }
                this.lote = new Lote();
                ok = true;
            } catch (SQLException ex) {
                fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
            } catch (NamingException ex) {
                fMsg.setDetail(ex.getMessage());
            }
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okLotes", ok);
    }

    public void obtenerSolicitudes() {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "obtenerSolicitudes");
        this.envios = new ArrayList<Envio>();
        try {
            this.dao = new DAOMovimientos();
            for (TOMovimiento m : this.dao.obtenerMovimientos(this.mbComprobantes.getMbAlmacenes().getToAlmacen().getIdAlmacen(), 35, 0)) {
                this.envios.add(this.convertir(m));
            }
            ok = true;
        } catch (SQLException ex) {
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setDetail(ex.getMessage());
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }

    private Envio convertir(TOMovimiento to) {
        Envio e = new Envio();
        e.setIdMovto(to.getIdMovto());
        e.setIdMovtoAlmacen(to.getIdMovtoAlmacen());
        e.setAlmacen(this.mbComprobantes.getMbAlmacenes().obtenerAlmacen(to.getIdAlmacen()));
        e.setComprobante(this.mbComprobantes.obtenerComprobante(to.getIdReferencia()));
        e.setFolio(to.getFolio());
        e.setFecha(to.getFecha());
        e.setIdUsuario(to.getIdUsuario());
        return e;
    }

    public boolean comparaProductos(MovimientoProducto prod) {
        if (prod.getProducto().getIdProducto() == this.envioProducto.getProducto().getIdProducto()) {
            return false;
        } else {
            return true;
        }
    }

    public void cargaDetalleSolicitud(SelectEvent event) {
        this.envio = (Envio) event.getObject();

        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "cargaDetalleSolicitud");
        try {
            this.dao = new DAOMovimientos();
//            this.idMovtoAlmacen=this.dao.obtenerIdMovtoAlmacen(this.envio.getAlmacen().getIdAlmacen(), 35, this.envio.getComprobante().getNumero());
            this.daoLotes = new DAOLotes();
            this.daoImps = new DAOImpuestosProducto();
            this.envioDetalle = new ArrayList<SalidaProducto>();
            for (TOMovimientoDetalle p : this.dao.obtenerDetalleMovimiento(this.envio.getIdMovto())) {
                this.envioDetalle.add(this.convertirDetalle(p));
            }
            this.envioProducto = new SalidaProducto();
            this.modoEdicion = true;
            ok = true;
        } catch (SQLException ex) {
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setDetail(ex.getMessage());
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }

    private SalidaProducto convertirDetalle(TOMovimientoDetalle to) throws SQLException {
        SalidaProducto p = new SalidaProducto();
        p.setProducto(this.mbBuscar.obtenerProducto(to.getIdProducto()));
        p.setCantFacturada(to.getCantFacturada());
        p.setCantOrdenada(to.getCantOrdenada());
        p.setCantRecibida(to.getCantRecibida());
        p.setCantSinCargo(to.getCantSinCargo());
        p.setCostoOrdenado(to.getCostoOrdenado());
        p.setDesctoConfidencial(to.getDesctoConfidencial());
        p.setDesctoProducto1(to.getDesctoProducto1());
        p.setDesctoProducto2(to.getDesctoProducto2());
        p.setImporte(to.getImporte());
        p.setImpuestos(this.daoImps.obtenerImpuestosProducto(this.envio.getIdMovto(), to.getIdProducto()));
        p.setNeto(to.getNeto());
        p.setUnitario(to.getUnitario());
        p.setLotes(this.daoLotes.obtenerLotes(this.envio.getAlmacen().getIdAlmacen(), this.envio.getIdMovtoAlmacen(), to.getIdProducto()));
        this.sumaLotes = 0;
        for (Lote l : p.getLotes()) {
            this.sumaLotes += l.getSeparados();
        }
        if (p.getCantFacturada() != this.sumaLotes) {
            throw new SQLException("Error de sincronizacion Lotes en producto: " + p.getProducto().getIdProducto());
        }
        return p;
    }

    public void salir() {
        this.inicializar();
        this.modoEdicion = false;
    }

    public void respaldaFila() {
        this.resEnvioProducto.setCantOrdenada(this.envioProducto.getCantOrdenada());
        this.resEnvioProducto.setCantFacturada(this.envioProducto.getCantFacturada());
        this.resEnvioProducto.setCantRecibida(this.envioProducto.getCantRecibida());
        this.resEnvioProducto.setDesctoConfidencial(this.envioProducto.getDesctoConfidencial());
        this.resEnvioProducto.setDesctoProducto1(this.envioProducto.getDesctoProducto1());
        this.resEnvioProducto.setDesctoProducto2(this.envioProducto.getDesctoProducto2());
        this.resEnvioProducto.setProducto(this.envioProducto.getProducto());
        this.resEnvioProducto.setImporte(this.envioProducto.getImporte());
        this.resEnvioProducto.setNeto(this.envioProducto.getNeto());
        this.resEnvioProducto.setUnitario(this.envioProducto.getUnitario());
        this.resEnvioProducto.setCosto(this.envioProducto.getCosto());
        this.resEnvioProducto.setLotes(this.envioProducto.getLotes());
        this.resEnvioProducto.setImpuestos(this.envioProducto.getImpuestos());
    }

    public String terminar() {
        this.modoEdicion = false;
        this.acciones = null;
        this.inicializar();
        return "index.xhtml";
    }

    public boolean isModoEdicion() {
        return modoEdicion;
    }

    public void setModoEdicion(boolean modoEdicion) {
        this.modoEdicion = modoEdicion;
    }

    public ArrayList<SalidaProducto> getEnvioDetalle() {
        return envioDetalle;
    }

    public void setEnvioDetalle(ArrayList<SalidaProducto> envioDetalle) {
        this.envioDetalle = envioDetalle;
    }

    public SalidaProducto getEnvioProducto() {
        return envioProducto;
    }

    public void setEnvioProducto(SalidaProducto envioProducto) {
        this.envioProducto = envioProducto;
    }

    public MovimientoProducto getResEnvioProducto() {
        return resEnvioProducto;
    }

    public void setResEnvioProducto(SalidaProducto resEnvioProducto) {
        this.resEnvioProducto = resEnvioProducto;
    }

    public ArrayList<Accion> obtenerAcciones(int idModulo) {
        if (this.acciones == null) {
            this.acciones = this.mbAcciones.obtenerAcciones(idModulo);
        }
        return acciones;
    }

    public ArrayList<Accion> getAcciones() {
        if (this.acciones == null) {
            this.acciones = this.mbAcciones.obtenerAcciones(18);
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

    public Envio getEnvio() {
        return envio;
    }

    public void setEnvio(Envio envio) {
        this.envio = envio;
    }

    public ArrayList<Envio> getEnvios() {
        return envios;
    }

    public void setEnvios(ArrayList<Envio> envios) {
        this.envios = envios;
    }

    public Lote getLote() {
        return lote;
    }

    public void setLote(Lote lote) {
        this.lote = lote;
    }

    public double getSumaLotes() {
        return sumaLotes;
    }

    public void setSumaLotes(double sumaLotes) {
        this.sumaLotes = sumaLotes;
    }
}
