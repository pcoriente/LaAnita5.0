package impuestos;

import impuestos.dao.DAOGrupos;
import impuestos.dominio.Impuesto;
import impuestos.dominio.ImpuestoGrupo;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.naming.NamingException;
import org.primefaces.context.RequestContext;

/**
 *
 * @author JULIOS
 */
@Named(value = "mbGrupos")
@SessionScoped
public class MbGrupos implements Serializable {

    private String strGrupo;
    private ImpuestoGrupo grupo;
    private ArrayList<SelectItem> listaGrupos;
    private Impuesto impuestoAgregado;
    private ArrayList<Impuesto> impuestosAgregados;
    private Impuesto impuestoDisponible;
    private ArrayList<Impuesto> impuestosDisponibles;
    private DAOGrupos dao;

    public MbGrupos() {
        this.grupo = new ImpuestoGrupo(0, "");
    }

    public void eliminarImpuesto() {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if (this.grupo == null || this.grupo.getIdGrupo() == 0) {
            fMsg.setDetail("Se requiere un grupo !!");
        } else if (this.impuestoAgregado == null) {
            fMsg.setDetail("Debe seleccionar el impuesto a eliminar !!");
        } else {
            try {
                this.dao = new DAOGrupos();
                this.impuestosAgregados = this.dao.eliminarImpuesto(this.grupo.getIdGrupo(), this.impuestoAgregado.getIdImpuesto());
                this.impuestosDisponibles = this.dao.obtenerImpuestosDisponibles(this.grupo.getIdGrupo());
                ok = true;
            } catch (SQLException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
            } catch (NamingException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getMessage());
            }
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }

    public void agregarImpuesto() {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if (this.grupo == null || this.grupo.getIdGrupo() == 0) {
            fMsg.setDetail("Se requiere un grupo !!");
        } else if (this.impuestoDisponible == null) {
            fMsg.setDetail("No ha seleccionado un impuesto disponible !!");
        } else {
            try {
                this.dao = new DAOGrupos();
                this.impuestosAgregados = this.dao.agregarImpuesto(this.grupo.getIdGrupo(), this.impuestoDisponible.getIdImpuesto());
                this.impuestosDisponibles = this.dao.obtenerImpuestosDisponibles(this.grupo.getIdGrupo());
                ok = true;
            } catch (SQLException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
            } catch (NamingException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getMessage());
            }
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }

    public ArrayList<Impuesto> obtenerImpuestosAgregados() {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        ArrayList<Impuesto> imps = new ArrayList<Impuesto>();
        try {
            if (this.grupo == null) {
                fMsg.setDetail("Se requiere un grupo !!");
            } else if (this.grupo.getIdGrupo() == 0) {
                ok = true;
            } else {
                this.dao = new DAOGrupos();
                imps = this.dao.obtenerImpuestosAgregados(this.grupo.getIdGrupo());
                ok = true;
            }
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        return imps;
    }

    public ArrayList<Impuesto> obtenerImpuestosDisponibles() {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        ArrayList<Impuesto> imps = new ArrayList<Impuesto>();
        try {
            if (this.grupo == null) {
                fMsg.setDetail("Se requiere un grupo !!");
            } else if (this.grupo.getIdGrupo() == 0) {
                ok = true;
            } else {
                this.dao = new DAOGrupos();
                imps = this.dao.obtenerImpuestosDisponibles(this.grupo.getIdGrupo());
                ok = true;
            }
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        return imps;
    }
    /*
    public boolean seleccionar() {
        boolean ok=false;
        return ok;
    }
    * */

    public boolean eliminar() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if (this.grupo == null || this.grupo.getIdGrupo() == 0) {
            fMsg.setDetail("Se requiere un grupo !!");
        } else {
            try {
                this.dao = new DAOGrupos();
                ok = this.dao.eliminar(this.grupo.getIdGrupo());
                if (ok) {
                    this.cargarGrupos();
                    this.grupo = new ImpuestoGrupo(0, "");
                } else {
                    fMsg.setDetail("El grupo no puede ser eliminado, actualmente está en uso !!");
                }
            } catch (SQLException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
            } catch (NamingException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getMessage());
            }
        }
        if(!ok){
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okEliminarGrupo", ok);
        return ok;
    }

    public void grabar() {
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if (this.grupo == null || this.grupo.getGrupo().isEmpty()) {
            fMsg.setDetail("Se requiere un grupo !!");
        } else {
            try {
                this.dao = new DAOGrupos();
                if (this.grupo.getIdGrupo() == 0) {
                    this.grupo.setIdGrupo(this.dao.agregar(grupo));
                } else {
                    this.dao.modificar(grupo);
                }
                this.cargarGrupos();
                this.setImpuestosAgregados(this.obtenerImpuestosAgregados());
                this.setImpuestosDisponibles(this.obtenerImpuestosDisponibles());
                fMsg.setSeverity(FacesMessage.SEVERITY_INFO);
                fMsg.setDetail("El grupo se grabo correctamente !!!");
            } catch (SQLException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
            } catch (NamingException ex) {
                fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
                fMsg.setDetail(ex.getMessage());
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, fMsg);
    }

    public void cargarGrupos() {
        this.listaGrupos = new ArrayList<SelectItem>();
        ImpuestoGrupo imp = new ImpuestoGrupo(0, "SELECCIONE");
        this.listaGrupos.add(new SelectItem(imp, imp.toString()));

        try {
            this.dao = new DAOGrupos();
            ArrayList<ImpuestoGrupo> lstGrupos = this.dao.obtenerGrupos();
            for (ImpuestoGrupo i : lstGrupos) {
                this.listaGrupos.add(new SelectItem(i, i.toString()));
            }
        } catch (SQLException ex) {
            Logger.getLogger(MbGrupos.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NamingException ex) {
            Logger.getLogger(MbGrupos.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public ImpuestoGrupo getGrupo() {
        return grupo;
    }

    public void setGrupo(ImpuestoGrupo grupo) {
        this.grupo = grupo;
    }

    public Impuesto getImpuestoAgregado() {
        return impuestoAgregado;
    }

    public void setImpuestoAgregado(Impuesto impuestoAgregado) {
        this.impuestoAgregado = impuestoAgregado;
    }

    public ArrayList<Impuesto> getImpuestosAgregados() {
        return impuestosAgregados;
    }

    public void setImpuestosAgregados(ArrayList<Impuesto> impuestosAgregados) {
        this.impuestosAgregados = impuestosAgregados;
    }

    public Impuesto getImpuestoDisponible() {
        return impuestoDisponible;
    }

    public void setImpuestoDisponible(Impuesto impuestoDisponible) {
        this.impuestoDisponible = impuestoDisponible;
    }

    public ArrayList<Impuesto> getImpuestosDisponibles() {
        return impuestosDisponibles;
    }

    public void setImpuestosDisponibles(ArrayList<Impuesto> impuestosDisponibles) {
        this.impuestosDisponibles = impuestosDisponibles;
    }

    public String getStrGrupo() {
        return strGrupo;
    }

    public void setStrGrupo(String strGrupo) {
        this.strGrupo = strGrupo;
    }

    public ArrayList<SelectItem> getListaGrupos() {
        if (this.listaGrupos == null) {
            this.cargarGrupos();
        }
        return listaGrupos;
    }

    public void setListaGrupos(ArrayList<SelectItem> listaGrupos) {
        this.listaGrupos = listaGrupos;
    }
}
