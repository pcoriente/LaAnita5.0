package clientes;

import contribuyentes.Contribuyente;
import contribuyentes.MbContribuyentes;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import javax.faces.bean.ManagedProperty;
import javax.faces.model.SelectItem;

/**
 *
 * @author jesc
 */
@Named(value = "mbMiniCliente")
@SessionScoped
public class MbMiniCliente implements Serializable {
    @ManagedProperty(value = "#{mbContribuyentes}")
    private MbContribuyentes mbContribuyentes;
    
    private ArrayList<SelectItem> listaClientes;
    private Contribuyente cliente;
    
    public MbMiniCliente() {
        inicializa();
    }
    
    private void cargaListaClientes() {
        this.listaClientes=new ArrayList<SelectItem>();
        Contribuyente c0=new Contribuyente();
        c0.setIdContribuyente(0);
        c0.setContribuyente("Seleccione");
        this.listaClientes.add(new SelectItem(c0, c0.toString()));

        for(Contribuyente c: this.mbContribuyentes.obtenerContribuyentesCliente()) {
            this.listaClientes.add(new SelectItem(c, c.toString()));
        }
    }
    
    public void inicializar() {
        inicializa();
    }
    
    private void inicializa() {
        this.mbContribuyentes = new MbContribuyentes();
        inicializaLocales();
    }
    
    private void inicializaLocales() {
        this.listaClientes=null;
        this.cliente=new Contribuyente();
    }

    public MbContribuyentes getMbContribuyentes() {
        return mbContribuyentes;
    }

    public void setMbContribuyentes(MbContribuyentes mbContribuyentes) {
        this.mbContribuyentes = mbContribuyentes;
    }

    public ArrayList<SelectItem> getListaClientes() {
        if(this.listaClientes==null) {
            this.cargaListaClientes();
        }
        return listaClientes;
    }

    public void setListaClientes(ArrayList<SelectItem> listaClientes) {
        this.listaClientes = listaClientes;
    }

    public Contribuyente getCliente() {
        return cliente;
    }

    public void setCliente(Contribuyente cliente) {
        this.cliente = cliente;
    }
}
