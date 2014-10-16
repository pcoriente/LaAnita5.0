package entradas.to;

import java.util.Date;

/**
 *
 * @author jesc
 */
public class TOMovimiento {
    private int idMovto;
    private int idMovtoAlmacen;
    private int idTipo;
    private int folio;
    private int idCedis;
    private int idEmpresa;
    private int idAlmacen;
    private int idReferencia;
    private int idImpuestoZona;
    private int idOrdenCompra;
    private int idMoneda;
    private double tipoCambio;
    private double desctoComercial;
    private double desctoProntoPago;
    private Date fecha;
    private int idUsuario;
    private boolean status;

    public TOMovimiento() {
        this.idMovto=0;
        this.idMovtoAlmacen=0;
        this.idTipo=0;
        this.folio=0;
        this.idCedis=0;
        this.idEmpresa=0;
        this.idAlmacen=0;
        this.idReferencia=0;
        this.idImpuestoZona=1;
        this.idOrdenCompra=0;
        this.idMoneda=0;
        this.tipoCambio=0;
        this.desctoComercial=0;
        this.desctoProntoPago=0;
        this.fecha=new Date();
        this.idUsuario=0;
        this.status=false;
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

    public int getIdTipo() {
        return idTipo;
    }

    public void setIdTipo(int idTipo) {
        this.idTipo = idTipo;
    }

    public int getIdAlmacen() {
        return idAlmacen;
    }

    public void setIdAlmacen(int idAlmacen) {
        this.idAlmacen = idAlmacen;
    }

    public int getIdReferencia() {
        return idReferencia;
    }

    public void setIdReferencia(int idReferencia) {
        this.idReferencia = idReferencia;
    }

    public int getIdImpuestoZona() {
        return idImpuestoZona;
    }

    public void setIdImpuestoZona(int idImpuestoZona) {
        this.idImpuestoZona = idImpuestoZona;
    }

    public int getIdOrdenCompra() {
        return idOrdenCompra;
    }

    public void setIdOrdenCompra(int idOrdenCompra) {
        this.idOrdenCompra = idOrdenCompra;
    }

    public int getIdMoneda() {
        return idMoneda;
    }

    public void setIdMoneda(int idMoneda) {
        this.idMoneda = idMoneda;
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

    public int getIdCedis() {
        return idCedis;
    }

    public void setIdCedis(int idCedis) {
        this.idCedis = idCedis;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public int getFolio() {
        return folio;
    }

    public void setFolio(int folio) {
        this.folio = folio;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
