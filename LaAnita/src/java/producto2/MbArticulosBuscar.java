package producto2;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import org.primefaces.context.RequestContext;
import producto2.dao.DAOArticulosBuscar;
import producto2.dominio.Articulo;
import producto2.dominio.ArticuloBuscar;
import producto2.dominio.Grupo;
import producto2.dominio.SubGrupo;
import producto2.dominio.Tipo;

/**
 *
 * @author jesc
 */
@Named(value = "mbArticulosBuscar")
@SessionScoped
public class MbArticulosBuscar implements Serializable {
    @ManagedProperty(value = "#{mbParte}")
    private MbParte mbParte;
    
    private String tipoBuscar;
    private String strBuscar;
    private ArticuloBuscar articulo;
    private ArrayList<ArticuloBuscar> articulos;
    private ArrayList<ArticuloBuscar> filtrados;
    private SelectItem[] arrayTipos;
    private SelectItem[] arrayGrupos;
    private SelectItem[] arraySubGrupos;
    private DAOArticulosBuscar dao;
    
    public MbArticulosBuscar() {
        this.mbParte=new MbParte();
        this.inicializaLocales();
    }
    
    public void inicializar() {
        this.inicializa();
    }
    
    private void inicializa() {
        this.inicializaLocales();
        this.mbParte.nueva();
//        this.mbParte.setListaPartes(null);
    }
    
    private void inicializaLocales() {
        this.tipoBuscar="2";
        this.strBuscar="";
        this.articulo=null;
        this.articulos=null;
        this.filtrados=null;
        this.arrayTipos = new SelectItem[1];
        this.arrayTipos[0] = new SelectItem("", "Seleccione un tipo");
        this.arrayGrupos = new SelectItem[1];
        this.arrayGrupos[0] = new SelectItem("", "Seleccione un grupo");
        this.arraySubGrupos = new SelectItem[1];
        this.arraySubGrupos[0] = new SelectItem("", "Seleccione un subgrupo");
    }
    
    public Articulo obtenerArticulo(int idArticulo) {
        Articulo a=null;
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "obtenerArticulo");
        try {
            this.dao=new DAOArticulosBuscar();
            a=dao.obtenerArticulo(idArticulo);
            ok=true;
        } catch (NamingException ex) {
            fMsg.setDetail(ex.getMessage());
        } catch (SQLException ex) {
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        }
        if(!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        return a;
    }
    
    public void buscarLista() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "buscarLista");
        try {
            this.dao=new DAOArticulosBuscar();
            if(this.tipoBuscar.equals("1")) {
                this.strBuscar="1";
            } else {
                this.articulos=new ArrayList<ArticuloBuscar>();
                ArrayList<Tipo> lstTipos = new ArrayList<Tipo>();
                ArrayList<Grupo> lstGrupos = new ArrayList<Grupo>();
                ArrayList<SubGrupo> lstSubGrupos = new ArrayList<SubGrupo>();
                ArrayList<Articulo> lstArticulos;
                if(this.tipoBuscar.equals("2")) {
//                    lstArticulos=this.dao.obtenerArticulos(this.mbParte.getParte().getIdParte());
                    lstArticulos=this.dao.obtenerArticulos(this.mbParte.getParte());
                } else {
                    lstArticulos=this.dao.obtenerArticulos(this.strBuscar);
                }
                for(Articulo a:lstArticulos) {
                    if (lstTipos.indexOf(a.getTipo()) == -1) {
                        lstTipos.add(a.getTipo());
                    }
                    if (lstGrupos.indexOf(a.getGrupo()) == -1) {
                        lstGrupos.add(a.getGrupo());
                    }
                    if (lstSubGrupos.indexOf(a.getSubGrupo()) == -1) {
                        lstSubGrupos.add(a.getSubGrupo());
                    }
                    this.articulos.add(new ArticuloBuscar(a.getIdArticulo(),a.getTipo().getTipo(), a.getGrupo().getGrupo(), a.getSubGrupo().getSubGrupo(), a.toString()));
                }
                int i = 0;
                this.arrayTipos = new SelectItem[lstTipos.size() + 1];
                this.arrayTipos[i++] = new SelectItem("", "Seleccione un tipo");
                for (Tipo t : lstTipos) {
                    this.arrayTipos[i++] = new SelectItem(t.getTipo(), t.getTipo());
                }
                i = 0;
                this.arrayGrupos = new SelectItem[lstGrupos.size() + 1];
                this.arrayGrupos[i++] = new SelectItem("", "Seleccione un grupo");
                for (Grupo g : lstGrupos) {
                    this.arrayGrupos[i++] = new SelectItem(g.getGrupo(), g.getGrupo());
                }
                i = 0;
                this.arraySubGrupos = new SelectItem[lstSubGrupos.size() + 1];
                this.arraySubGrupos[i++] = new SelectItem("", "Seleccione un subGrupo");
                for (SubGrupo sg : lstSubGrupos) {
                    this.arraySubGrupos[i++] = new SelectItem(sg.getSubGrupo(), sg.getSubGrupo());
                }
                if(this.articulos.isEmpty()) {
                    fMsg.setSeverity(FacesMessage.SEVERITY_INFO);
                    fMsg.setDetail("No se encontraron productos en la busqueda");
                    FacesContext.getCurrentInstance().addMessage(null, fMsg);
                }
            }
        } catch (NamingException ex) {
            fMsg.setDetail(ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        } catch (SQLException ex) {
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okBuscar", ok);
    }
    
    public void verCambio() {
        if (this.tipoBuscar.equals("2")) {
            this.mbParte.nueva();
        } else {
            this.strBuscar = "";
        }
        this.articulo=null;
        this.articulos=null;
        this.filtrados=null;
        this.arrayTipos=new SelectItem[0];
        this.arrayGrupos=new SelectItem[0];
        this.arraySubGrupos=new SelectItem[0];
    }

    public MbParte getMbParte() {
        return mbParte;
    }

    public void setMbParte(MbParte mbParte) {
        this.mbParte = mbParte;
    }

    public String getTipoBuscar() {
        return tipoBuscar;
    }

    public void setTipoBuscar(String tipoBuscar) {
        this.tipoBuscar = tipoBuscar;
    }

    public String getStrBuscar() {
        return strBuscar;
    }

    public void setStrBuscar(String strBuscar) {
        this.strBuscar = strBuscar;
    }

    public ArticuloBuscar getArticulo() {
        return articulo;
    }

    public void setArticulo(ArticuloBuscar articulo) {
        this.articulo = articulo;
    }

    public ArrayList<ArticuloBuscar> getArticulos() {
        return articulos;
    }

    public void setArticulos(ArrayList<ArticuloBuscar> articulos) {
        this.articulos = articulos;
    }

    public ArrayList<ArticuloBuscar> getFiltrados() {
        return filtrados;
    }

    public void setFiltrados(ArrayList<ArticuloBuscar> filtrados) {
        this.filtrados = filtrados;
    }

    public SelectItem[] getArrayTipos() {
        return arrayTipos;
    }

    public void setArrayTipos(SelectItem[] arrayTipos) {
        this.arrayTipos = arrayTipos;
    }

    public SelectItem[] getArrayGrupos() {
        return arrayGrupos;
    }

    public void setArrayGrupos(SelectItem[] arrayGrupos) {
        this.arrayGrupos = arrayGrupos;
    }

    public SelectItem[] getArraySubGrupos() {
        return arraySubGrupos;
    }

    public void setArraySubGrupos(SelectItem[] arraySubGrupos) {
        this.arraySubGrupos = arraySubGrupos;
    }
}
