package almacenes.dominio;

/**
 *
 * @author jesc
 */
public class MiniAlmacen {
    private int idAlmacen;
    private String Almacen;
//    private int idEmpresa;
//    private int idCedis;

    public MiniAlmacen() {
        this.idAlmacen=0;
        this.Almacen="";
//        this.idEmpresa=0;
//        this.idCedis=0;
    }

    @Override
    public String toString() {
        return this.Almacen;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + this.idAlmacen;
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
        final MiniAlmacen other = (MiniAlmacen) obj;
        if (this.idAlmacen != other.idAlmacen) {
            return false;
        }
        return true;
    }

    public int getIdAlmacen() {
        return idAlmacen;
    }

    public void setIdAlmacen(int idAlmacen) {
        this.idAlmacen = idAlmacen;
    }

    public String getAlmacen() {
        return Almacen;
    }

    public void setAlmacen(String Almacen) {
        this.Almacen = Almacen;
    }

//    public int getIdEmpresa() {
//        return idEmpresa;
//    }
//
//    public void setIdEmpresa(int idEmpresa) {
//        this.idEmpresa = idEmpresa;
//    }
//
//    public int getIdCedis() {
//        return idCedis;
//    }
//
//    public void setIdCedis(int idCedis) {
//        this.idCedis = idCedis;
//    }
}
