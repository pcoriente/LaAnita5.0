package clientes.dominio;

import almacenes.dominio.AlmacenJS;
import almacenes.to.TOAlmacenJS;
import entradas.dominio.Comprobante;
import java.util.Date;
import monedas.Moneda;

/**
 *
 * @author jesc
 */
public class Venta {
    private int idMovto;
    private int idMovtoAlmacen;
    private int folio;
    //private int folioAlmacen;
    private TOAlmacenJS almacen;
    private Comprobante comprobante;
    private int idImpuestoZona;
    private Moneda moneda;
    private double tipoCambio;
    private double desctoComercial;
    private double desctoProntoPago;
    private Date fecha;
    private int idUsuario;
    private double subTotal;
    private double descuento;
    private double impuesto;
    private double total;
    
    public Venta() {
        this.almacen=new TOAlmacenJS();
        this.comprobante=new Comprobante();
        this.moneda=new Moneda();
        this.tipoCambio=1.00;
        this.fecha=new Date();
    }

    public int getIdMovto() {
        return idMovto;
    }

    public void setIdMovto(int idMovto) {
        this.idMovto = idMovto;
    }

    public int getIdMovtoAlmacen() {
        return idMovtoAlmacen;
    }

    public void setIdMovtoAlmacen(int idMovtoAlmacen) {
        this.idMovtoAlmacen = idMovtoAlmacen;
    }

    public TOAlmacenJS getAlmacen() {
        return almacen;
    }

    public void setAlmacen(TOAlmacenJS almacen) {
        this.almacen = almacen;
    }

    public int getFolio() {
        return folio;
    }

    public void setFolio(int folio) {
        this.folio = folio;
    }

//    public int getFolioAlmacen() {
//        return folioAlmacen;
//    }
//
//    public void setFolioAlmacen(int folioAlmacen) {
//        this.folioAlmacen = folioAlmacen;
//    }

    public Comprobante getComprobante() {
        return comprobante;
    }

    public void setComprobante(Comprobante comprobante) {
        this.comprobante = comprobante;
    }

    public int getIdImpuestoZona() {
        return idImpuestoZona;
    }

    public void setIdImpuestoZona(int idImpuestoZona) {
        this.idImpuestoZona = idImpuestoZona;
    }

    public Moneda getMoneda() {
        return moneda;
    }

    public void setMoneda(Moneda moneda) {
        this.moneda = moneda;
    }

    public double getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(double tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

    public double getDesctoComercial() {
        return desctoComercial;
    }

    public void setDesctoComercial(double desctoComercial) {
        this.desctoComercial = desctoComercial;
    }

    public double getDesctoProntoPago() {
        return desctoProntoPago;
    }

    public void setDesctoProntoPago(double desctoProntoPago) {
        this.desctoProntoPago = desctoProntoPago;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public double getSubTotal() {
        return subTotal;
    }

    public void setSubTotal(double subTotal) {
        this.subTotal = subTotal;
    }

    public double getDescuento() {
        return descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public double getImpuesto() {
        return impuesto;
    }

    public void setImpuesto(double impuesto) {
        this.impuesto = impuesto;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}
