package entradas.to;

/**
 *
 * @author jesc
 */
public class TOMovimientoDetalle {
    private int idProducto;
    private double costoOrdenado;
    private double cantOrdenada;
    private double cantFacturada;
    private double cantSinCargo;
    private double cantRecibida;
    private double costo;
//    private double precio;
    private double desctoProducto1;
    private double desctoProducto2;
    private double desctoConfidencial;
    private double unitario;
    private int idImpuestoGrupo;
    private double neto;
    private double importe;
    
    public TOMovimientoDetalle() {}

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public double getCostoOrdenado() {
        return costoOrdenado;
    }

    public void setCostoOrdenado(double costoOrdenado) {
        this.costoOrdenado = costoOrdenado;
    }

    public double getCantOrdenada() {
        return cantOrdenada;
    }

    public void setCantOrdenada(double cantOrdenada) {
        this.cantOrdenada = cantOrdenada;
    }

    public double getCantFacturada() {
        return cantFacturada;
    }

    public void setCantFacturada(double cantFacturada) {
        this.cantFacturada = cantFacturada;
    }

    public double getCantSinCargo() {
        return cantSinCargo;
    }

    public void setCantSinCargo(double cantSinCargo) {
        this.cantSinCargo = cantSinCargo;
    }

    public double getCantRecibida() {
        return cantRecibida;
    }

    public void setCantRecibida(double cantRecibida) {
        this.cantRecibida = cantRecibida;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }
//
//    public double getPrecio() {
//        return precio;
//    }
//
//    public void setPrecio(double precio) {
//        this.precio = precio;
//    }

    public double getDesctoProducto1() {
        return desctoProducto1;
    }

    public void setDesctoProducto1(double desctoProducto1) {
        this.desctoProducto1 = desctoProducto1;
    }

    public double getDesctoProducto2() {
        return desctoProducto2;
    }

    public void setDesctoProducto2(double desctoProducto2) {
        this.desctoProducto2 = desctoProducto2;
    }

    public double getDesctoConfidencial() {
        return desctoConfidencial;
    }

    public void setDesctoConfidencial(double desctoConfidencial) {
        this.desctoConfidencial = desctoConfidencial;
    }

    public double getUnitario() {
        return unitario;
    }

    public void setUnitario(double unitario) {
        this.unitario = unitario;
    }

    public int getIdImpuestoGrupo() {
        return idImpuestoGrupo;
    }

    public void setIdImpuestoGrupo(int idImpuestoGrupo) {
        this.idImpuestoGrupo = idImpuestoGrupo;
    }

    public double getNeto() {
        return neto;
    }

    public void setNeto(double neto) {
        this.neto = neto;
    }

    public double getImporte() {
        return importe;
    }

    public void setImporte(double importe) {
        this.importe = importe;
    }
}
