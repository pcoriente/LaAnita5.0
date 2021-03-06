package clientes.dominio;

import producto2.dominio.Producto;

/**
 *
 * @author jesc
 */
public class VentaProducto {
    private Producto producto;
    private double cantidad;
    private double separados;
    private double costo;
    
    public VentaProducto() {
        this.producto=new Producto();
    }
    
    public VentaProducto(Producto producto) {
        this.producto=producto;
    }
    
    @Override
    public String toString() {
        return this.producto.toString();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 41 * hash + (this.producto != null ? this.producto.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final VentaProducto other = (VentaProducto) obj;
        if (this.producto != other.producto && (this.producto == null || !this.producto.equals(other.producto))) {
            return false;
        }
        return true;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public double getCantidad() {
        return cantidad;
    }

    public void setCantidad(double cantidad) {
        this.cantidad = cantidad;
    }

    public double getSeparados() {
        return separados;
    }

    public void setSeparados(double separados) {
        this.separados = separados;
    }

    public double getCosto() {
        return costo;
    }

    public void setCosto(double costo) {
        this.costo = costo;
    }
}
