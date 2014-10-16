package ventas;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.naming.NamingException;
import ventas.dao.DAOClientes;
import ventas.dominio.MiniCliente;
import ventas.to.TOCliente;

/**
 *
 * @author jesc
 */
@Named(value = "mbClientes1")
@SessionScoped
public class MbClientes implements Serializable {
    private int idClienteGpo;
    private int idFormato;
    private ArrayList<SelectItem> listaMiniClientes;
    private ArrayList<MiniCliente> miniClientes;
    private MiniCliente miniCliente;
    private DAOClientes dao;
    
    public MbClientes() {
        this.inicializa();
    }
    
    public MiniCliente obtenerMiniCliente(int idCliente) {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "cargaTiendas");
        MiniCliente mini=null;
        try {
            this.dao=new DAOClientes();
            mini=this.dao.convertirMiniCliente(this.dao.obtenerCliente(idCliente));
            ok=true;
        } catch (SQLException ex) {
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setDetail(ex.getMessage());
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        return mini;
    }
    
    public void cargarMiniClientes(int idFormato) {
        this.idFormato=idFormato;
        this.miniClientes=null;
        this.listaMiniClientes=new ArrayList<SelectItem>();
        MiniCliente c0=new MiniCliente();
        c0.setNombreComercial("Seleccione");
        this.listaMiniClientes.add(new SelectItem(c0, c0.toString()));
        for(MiniCliente c: this.getMiniClientes()) {
            this.listaMiniClientes.add(new SelectItem(c, c.toString()));
        }
    }
    
    private void cargaMiniClientes() {
        boolean ok = false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "cargaMiniClientes");
        this.miniClientes=new ArrayList<MiniCliente>();
        try {
            this.dao=new DAOClientes();
            for(TOCliente to: this.dao.obtenerClientes(this.idClienteGpo, this.idFormato)) {
                this.miniClientes.add(this.dao.convertirMiniCliente(to));
            }
            ok=true;
        } catch (SQLException ex) {
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setDetail(ex.getMessage());
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
    }
    
    public void inicializar() {
        this.inicializa();
    }
    
    private void inicializa() {
        this.inicializaLocales();
    }
    
    private void inicializaLocales() {
        this.idClienteGpo=0;
        this.idFormato=0;
        this.cargarMiniClientes(0);
        this.nuevoMiniCliente();
    }
    
    public void nuevoMiniCliente() {
        this.miniCliente=new MiniCliente();
    }

    public int getIdFormato() {
        return idFormato;
    }

    public void setIdFormato(int idFormato) {
        this.idFormato = idFormato;
    }

    public ArrayList<SelectItem> getListaMiniClientes() {
        return listaMiniClientes;
    }

    public void setListaMiniClientes(ArrayList<SelectItem> listaMiniClientes) {
        this.listaMiniClientes = listaMiniClientes;
    }

    public ArrayList<MiniCliente> getMiniClientes() {
        if(this.miniClientes==null) {
            this.cargaMiniClientes();
        }
        return miniClientes;
    }

    public void setMiniClientes(ArrayList<MiniCliente> miniClientes) {
        this.miniClientes = miniClientes;
    }

    public MiniCliente getMiniCliente() {
        return miniCliente;
    }

    public void setMiniCliente(MiniCliente miniCliente) {
        this.miniCliente = miniCliente;
    }

    public int getIdClienteGpo() {
        return idClienteGpo;
    }

    public void setIdClienteGpo(int idClienteGpo) {
        this.idClienteGpo = idClienteGpo;
    }
}
