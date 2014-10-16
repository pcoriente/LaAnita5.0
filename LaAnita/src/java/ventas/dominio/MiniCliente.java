package ventas.dominio;

/**
 *
 * @author jesc
 */
public class MiniCliente {
    private int idCliente;
    private int codigoTienda;
    private String nombreComercial;
    private int idGrupoCte;
    private int idFormato;
    
    
    public MiniCliente() {
        this.nombreComercial="";
    }

    @Override
    public String toString() {
        return this.nombreComercial;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 43 * hash + this.idCliente;
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
        final MiniCliente other = (MiniCliente) obj;
        if (this.idCliente != other.idCliente) {
            return false;
        }
        return true;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getCodigoTienda() {
        return codigoTienda;
    }

    public void setCodigoTienda(int codigoTienda) {
        this.codigoTienda = codigoTienda;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public int getIdGrupoCte() {
        return idGrupoCte;
    }

    public void setIdGrupoCte(int idGrupoCte) {
        this.idGrupoCte = idGrupoCte;
    }

    public int getIdFormato() {
        return idFormato;
    }

    public void setIdFormato(int idFormato) {
        this.idFormato = idFormato;
    }
}
