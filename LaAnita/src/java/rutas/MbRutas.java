/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rutas;

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
import rutas.daoRutas.DAORutas;
import rutas.dominio.Ruta;

/**
 *
 * @author Usuario
 */
@Named(value = "mbRutas")
@SessionScoped
public class MbRutas implements Serializable {

    private ArrayList<SelectItem> lstRuta = null;
    private Ruta ruta = new Ruta();
    private Ruta cmbRuta = new Ruta();

    /**
     * Creates a new instance of MbRutas
     */
    public MbRutas() {
    }

    public boolean validar() {
        boolean ok = true;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if (ruta.getRuta().equals("")) {
            ok = false;
            fMsg.setDetail("Se requiere la calle !!");
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("ok", ok);
        return ok;
    }

    public ArrayList<SelectItem> getLstRuta() {
        if (lstRuta == null) {
            try {
                lstRuta = new ArrayList<SelectItem>();
                DAORutas dao = new DAORutas();
                ArrayList<Ruta> listaRutas = new ArrayList<Ruta>();
                try {
                    listaRutas = dao.dameListaRutas();
                } catch (SQLException ex) {
                    Logger.getLogger(MbRutas.class.getName()).log(Level.SEVERE, null, ex);
                }
                Ruta ruta = new Ruta();
                ruta.setIdRuta(0);
                ruta.setRuta("Nueva Ruta");
                SelectItem item = new SelectItem(ruta, ruta.getRuta());
                lstRuta.add(item);
                for (Ruta rut : listaRutas) {
                    SelectItem items = new SelectItem(rut, rut.getRuta());
                    lstRuta.add(items);
                }
            } catch (NamingException ex) {
                Logger.getLogger(MbRutas.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return lstRuta;
    }

    public void setLstRuta(ArrayList<SelectItem> lstRuta) {
        this.lstRuta = lstRuta;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }

    public Ruta getCmbRuta() {
        return cmbRuta;
    }

    public void setCmbRuta(Ruta cmbRuta) {
        this.cmbRuta = cmbRuta;
    }

}
