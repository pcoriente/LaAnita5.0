package movimientos.to;

import entradas.to.TOMovimientoDetalle;
import java.util.ArrayList;
import movimientos.dominio.Lote;

/**
 *
 * @author jesc
 */
public class TOEntradaProducto extends TOMovimientoDetalle {
    ArrayList<Lote> lotes;
    
    public TOEntradaProducto() {
        super();
        this.lotes=new ArrayList<Lote>();
    }

    public ArrayList<Lote> getLotes() {
        return lotes;
    }

    public void setLotes(ArrayList<Lote> lotes) {
        this.lotes = lotes;
    }
}
