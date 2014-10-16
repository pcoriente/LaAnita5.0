package almacenes;

import almacenes.dao.DAOMiniAlmacenes;
import almacenes.dominio.MiniAlmacen;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;

/**
 *
 * @author jesc
 */
@Named(value = "mbMiniAlmacenes")
@SessionScoped
public class MbMiniAlmacenes implements Serializable {
    private MiniAlmacen almacen;
    private ArrayList<SelectItem> listaAlmacenes;
    private DAOMiniAlmacenes dao;
    
    public MbMiniAlmacenes() {
        this.almacen=new MiniAlmacen();
    }
    
    public void cargaListaAlmacenes(int idEmpresa, int idCedis) {
        boolean ok=false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        this.listaAlmacenes=new ArrayList<SelectItem>();
        try {
            MiniAlmacen a0=new MiniAlmacen();
            a0.setIdAlmacen(0);
            a0.setAlmacen("Seleccione un almacen");
            this.listaAlmacenes.add(new SelectItem(a0, a0.toString()));
            
            this.dao = new DAOMiniAlmacenes();
            for (MiniAlmacen a: this.dao.obtenerAlmacenes(idEmpresa, idCedis)) {
                listaAlmacenes.add(new SelectItem(a, a.toString()));
            }
            ok=true;
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }

    public MiniAlmacen getAlmacen() {
        return almacen;
    }

    public void setAlmacen(MiniAlmacen almacen) {
        this.almacen = almacen;
    }

    public ArrayList<SelectItem> getListaAlmacenes() {
        if(this.listaAlmacenes==null) {
            this.listaAlmacenes=new ArrayList<SelectItem>();
            MiniAlmacen a0=new MiniAlmacen();
            a0.setIdAlmacen(0);
            a0.setAlmacen("Seleccione un almacen");
            this.listaAlmacenes.add(new SelectItem(a0, a0.toString()));
        }
        return listaAlmacenes;
    }

    public void setListaAlmacenes(ArrayList<SelectItem> listaAlmacenes) {
        this.listaAlmacenes = listaAlmacenes;
    }
}
