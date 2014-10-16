package contribuyentes;

import Message.Mensajes;
import clientes.MbClientes;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.inject.Named;
import javax.naming.NamingException;
import org.primefaces.context.RequestContext;
import utilerias.Utilerias;

/**
 *
 * @author jsolis
 */
@Named(value = "mbContribuyentes")
@SessionScoped
public class MbContribuyentes implements Serializable {

    private Contribuyente contribuyente;
    private ArrayList<Contribuyente> contribuyentes;
    private DAOContribuyentes dao;
    private ArrayList<SelectItem> listaContribuyentes;

    public MbContribuyentes() {
        this.contribuyente = new Contribuyente();
        this.contribuyentes = new ArrayList<Contribuyente>();
    }

    public void cancelar() {
        boolean ok = true;
        RequestContext context = RequestContext.getCurrentInstance();
        context.addCallbackParam("okContribuyente", ok);
    }

    public boolean valida() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if (this.contribuyente.getContribuyente().equals("")) {
            fMsg.setDetail("Se requiere un contribuyente !!");
        } else if (this.contribuyente.getRfc().isEmpty()) {
            fMsg.setDetail("Se requiere el RFC !!");
        } else {
            this.contribuyente.setRfc(this.contribuyente.getRfc().trim().toUpperCase());
            this.contribuyente.setCurp(this.contribuyente.getCurp().trim().toUpperCase());
            Utilerias utilerias = new Utilerias();
            String mensaje = utilerias.verificarRfc(this.contribuyente.getRfc());
            if (!mensaje.equals("")) {
                fMsg.setDetail(mensaje);
            } else if(this.contribuyente.getRfc().length()==12 || utilerias.validarCurp(this.contribuyente.getCurp())) {
                ok=true;
            } else {
                fMsg.setDetail("Error! Curp no valido");
            }
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okContribuyente", ok);
        return ok;
    }

    public Contribuyente copia(Contribuyente contribuyente) {
        Contribuyente c = new Contribuyente();
        c.setIdContribuyente(contribuyente.getIdContribuyente());
        c.setIdRfc(contribuyente.getIdRfc());
        c.setRfc(contribuyente.getRfc());
        c.setContribuyente(contribuyente.getContribuyente());
        c.setDireccion(contribuyente.getDireccion());
        return c;
    }

    public ArrayList<Contribuyente> obtenerContribuyentesCliente() {
        ArrayList<Contribuyente> lstContribuyentes = new ArrayList<Contribuyente>();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao = new DAOContribuyentes();
            lstContribuyentes = this.dao.obtenerContribuyentesCliente();
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        }
        FacesContext.getCurrentInstance().addMessage(null, fMsg);
        return lstContribuyentes;
    }

    public Contribuyente obtenerContribuyente(int idContribuyente) {
        Contribuyente c = null;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao = new DAOContribuyentes();
            c = this.dao.obtenerContribuyente(idContribuyente);
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        }
        FacesContext.getCurrentInstance().addMessage(null, fMsg);
        return c;
    }

    public void obtenerContribuyentesRFC() {
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        try {
            this.dao = new DAOContribuyentes();
            int idRfc = this.dao.obtenerRfc(this.contribuyente.getRfc());
            if (idRfc == 0) {
                idRfc = this.dao.grabarRFC(this.contribuyente.getRfc());
                this.contribuyente.setIdRfc(idRfc);
                this.contribuyentes = new ArrayList<Contribuyente>();
            } else {
                this.contribuyentes = this.dao.obtenerContribuyentesRFC(this.contribuyente.getRfc());
            }
        } catch (SQLException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setSeverity(FacesMessage.SEVERITY_ERROR);
            fMsg.setDetail(ex.getMessage());
        }
    }

    public String buscarContribuyente(String rfc) {
        String mensaje = "";
        try {
            Utilerias utilerias = new Utilerias();
            mensaje = utilerias.verificarRfc(rfc);
            if (mensaje == "") {
                DAOContribuyentes dao = new DAOContribuyentes();
                contribuyente = dao.buscarContribuyente(rfc);
                Mensajes.mensajeSucces("RFC valido!");
            }
        } catch (NamingException ex) {
            Mensajes.mensajeError(ex.getMessage());
            Logger.getLogger(MbContribuyentes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Mensajes.mensajeError(ex.getMessage());
            Logger.getLogger(MbContribuyentes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return mensaje;
    }

    public List<String> completarClientes(String rfc) {
        List<String> lst = new ArrayList<String>();
        try {
            DAOContribuyentes dao = new DAOContribuyentes();
            for (Contribuyente con : dao.dameRfcContribuyente(rfc)) {
                lst.add(con.getRfc());
            }
        } catch (NamingException ex) {
            Mensajes.mensajeError(ex.getMessage());
            Logger.getLogger(MbClientes.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SQLException ex) {
            Mensajes.mensajeError(ex.getMessage());
            Logger.getLogger(MbClientes.class.getName()).log(Level.SEVERE, null, ex);
        }
        return lst;
    }

    public boolean verificar(String rfc) {
        boolean ok = false;

        return ok;
    }

    public Contribuyente getContribuyente() {
        return contribuyente;
    }

    public void setContribuyente(Contribuyente contribuyente) {
        this.contribuyente = contribuyente;
    }

    public ArrayList<Contribuyente> getContribuyentes() {
        return contribuyentes;
    }

    public void setContribuyentes(ArrayList<Contribuyente> contribuyentes) {
        this.contribuyentes = contribuyentes;
    }

    public void limpiarContribuyente() {
        contribuyente = new Contribuyente();
    }

    public ArrayList<SelectItem> getListaContribuyentes() {
        if (listaContribuyentes == null) {
            try {
                listaContribuyentes = new ArrayList<SelectItem>();
                DAOContribuyentes dao = new DAOContribuyentes();
                Contribuyente c = new Contribuyente();
                c.setIdContribuyente(0);
                c.setContribuyente("Seleccione Contribuyente");
                listaContribuyentes.add(new SelectItem(c, c.getContribuyente()));
                for (Contribuyente contr : dao.dameContribuyentes()) {
                    listaContribuyentes.add(new SelectItem(contr, contr.getContribuyente()));
                }
            } catch (NamingException ex) {
                Message.Mensajes.mensajeError(ex.getMessage());
                Logger.getLogger(MbContribuyentes.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Message.Mensajes.mensajeError(ex.getMessage());
                Logger.getLogger(MbContribuyentes.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return listaContribuyentes;
    }

    public void setListaContribuyentes(ArrayList<SelectItem> listaContribuyentes) {
        this.listaContribuyentes = listaContribuyentes;
    }
}
