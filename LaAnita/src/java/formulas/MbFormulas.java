package formulas;

import empresas.MbMiniEmpresas;
import empresas.dominio.MiniEmpresa;
import formulas.dominio.Formula;
import formulas.dominio.Insumo;
import formulas.dao.DAOFormulas;
import formulas.to.TOFormula;
import formulas.to.TOInsumo;
import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.naming.NamingException;
import org.primefaces.context.RequestContext;
import org.primefaces.event.SelectEvent;
import producto2.MbProductosBuscar;
import producto2.dominio.Producto;
import usuarios.MbAcciones;
import usuarios.dominio.Accion;

/**
 *
 * @author jesc
 */
@Named(value = "mbFormulas")
@SessionScoped
public class MbFormulas implements Serializable {
    private ArrayList<Accion> acciones;
    @ManagedProperty(value = "#{mbAcciones}")
    private MbAcciones mbAcciones;
    @ManagedProperty(value = "#{mbMiniEmpresas}")
    private MbMiniEmpresas mbEmpresas;
    @ManagedProperty(value = "#{mbProductosBuscar}")
    private MbProductosBuscar mbBuscar;
    
    private Formula formula;
    private Insumo insumo;
    private Insumo respInsumo;
    private String update;
    private int caso;
    private DAOFormulas dao;
    
    public MbFormulas() throws NamingException {
        this.formula=new Formula();
        this.insumo=new Insumo();
        this.respInsumo=new Insumo();
        
        this.mbAcciones = new MbAcciones();
        this.mbEmpresas = new MbMiniEmpresas();
        this.mbBuscar = new MbProductosBuscar();
    }
    
//    public void validaCantidad() {
//        this.formula.setSumaCantidad(this.formula.getSumaCantidad()-this.respInsumo.getCantidad()+this.insumo.getCantidad());
//        this.formula.setSumaCosto(this.formula.getSumaCosto()-this.respInsumo.getCostoPromedio()+this.insumo.getCostoPromedio());
//    }
    
    public String salir() {
        this.mbEmpresas.setEmpresa(new MiniEmpresa());
        this.mbEmpresas.setListaEmpresas(null);
        this.formula=new Formula();
        return "index.xhtml";
    }
    
    public void insumoSeleccionado(SelectEvent event) {
        this.insumo=(Insumo) event.getObject();
        respaldaInsumo();
    }
    
    public void respaldaInsumo() {
        this.respInsumo.setCantidad(this.insumo.getCantidad());
        this.respInsumo.setCod_pro(this.insumo.getCod_pro());
        this.respInsumo.setCostoPromedio(this.insumo.getCostoPromedio());
        this.respInsumo.setEmpaque(this.insumo.getEmpaque());
        this.respInsumo.setIdEmpaque(this.insumo.getIdEmpaque());
        this.respInsumo.setVariacion(this.insumo.getVariacion());
    }
    
    private Insumo convertirInsumo(TOInsumo to) {
        Producto producto=this.mbBuscar.obtenerProducto(to.getIdEmpaque());
        
        Insumo tmpInsumo=new Insumo();
        tmpInsumo.setNuevo(false);
        tmpInsumo.setIdEmpaque(to.getIdEmpaque());
        tmpInsumo.setCod_pro(producto.getCod_pro());
        tmpInsumo.setEmpaque(producto.toString());
        tmpInsumo.setCantidad(to.getCantidad());
        tmpInsumo.setVariacion(to.getPorcentVariacion());
        tmpInsumo.setCostoPromedio(to.getCostoUnitarioPromedio());
        return tmpInsumo;
    }
    
    public void convertirFormula(Producto producto, TOFormula toFormula) throws SQLException {
        this.formula=new Formula();
        this.formula.setIdEmpresa(this.mbEmpresas.getEmpresa().getIdEmpresa());
        this.formula.setIdEmpaque(producto.getIdProducto());
        this.formula.setCod_pro(producto.getCod_pro());
        this.formula.setEmpaque(producto.toString());
        this.formula.setIdFormula(toFormula.getIdFormula());
        this.formula.setCostoPromedio(toFormula.getCostoPromedio());
        this.formula.setMerma(toFormula.getMerma());
        this.formula.setObservaciones(toFormula.getObservaciones());
        this.formula.setSumaCantidad(0.00);
        this.formula.setSumaCosto(0.00);
        for(TOInsumo toInsumo:this.dao.obtenerInsumos(toFormula.getIdFormula())) {
            this.formula.getInsumos().add(convertirInsumo(toInsumo));
            this.formula.setSumaCantidad(this.formula.getSumaCantidad()+toInsumo.getCantidad());
            this.formula.setSumaCosto(this.formula.getSumaCosto()+toInsumo.getCostoUnitarioPromedio());
        }
    }
    
    public String getTotalSumaCostoPromedio() {
        return new DecimalFormat("###,##0.000000").format(this.formula.getSumaCosto());
    }
    
    public String getTotalSumaCantidad() {
        return new DecimalFormat("###,##0.000000").format(this.formula.getSumaCantidad());
    }
    
    public void validaProductoSeleccionado() {
        boolean nuevo = true;
        Insumo producto = new Insumo();
        producto.setIdEmpaque(this.mbBuscar.getProducto().getIdProducto());
        producto.setCod_pro(this.mbBuscar.getProducto().getCod_pro());
        producto.setEmpaque(this.mbBuscar.getProducto().toString());
        for (Insumo p : this.formula.getInsumos()) {
            if (p.equals(producto)) {
                this.insumo = p;
                nuevo = false;
                break;
            }
        }
        if (nuevo) {
            boolean ok = false;
            FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "validaProductoSeleccionado");
            try {
                this.dao = new DAOFormulas();
                producto.setCostoPromedio(this.dao.agregarInsumo(this.formula.getIdFormula(), this.formula.getIdEmpresa(), this.convertTOInsumo(producto)));
                this.insumo = producto;
                this.insumo.setNuevo(false);
                this.formula.setSumaCosto(this.formula.getSumaCosto()+this.insumo.getCostoPromedio());
                this.formula.getInsumos().add(this.insumo);
                ok = true;
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
        }
    }
    
    public void actualizaProductoSeleccionado() {
        boolean ok=false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "actualizaFormula");
        try {
            this.dao=new DAOFormulas();
            if(this.caso==1) {
                this.convertirFormula(this.mbBuscar.getProducto(), this.dao.obtenerFormula(this.mbEmpresas.getEmpresa().getIdEmpresa(), this.mbBuscar.getProducto().getIdProducto()));
            } else {
                this.validaProductoSeleccionado();
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
    
    public void buscar() {
        this.mbBuscar.buscarLista();
        if(this.mbBuscar.getProducto()!=null) {
            this.actualizaProductoSeleccionado();
        }
    }
    
    public void buscarEmpaqueInsumo() {
        this.caso=2;
        this.update=":main:mttoFormulas :main:formulaInsumos :main:mttoFormulasTotales :main:messages";
        this.mbBuscar.inicializar();
    }
    
    public void buscarEmpaqueFormula() {
        this.caso=1;
        this.update=":main:mttoFormulasDatos :main:mttoFormulas :main:formulaInsumos :main:mttoFormulasTotales :main:btnGrabarFormula :main:messages";
        this.mbBuscar.inicializar();
    }
    
    private TOInsumo convertTOInsumo(Insumo tmp) {
        TOInsumo to=new TOInsumo();
        to.setIdEmpaque(tmp.getIdEmpaque());
        to.setCantidad(tmp.getCantidad());
        to.setPorcentVariacion(tmp.getVariacion());
        to.setCostoUnitarioPromedio(tmp.getCostoPromedio());
        return to;
    }
    
    public void eliminarInsumo() {
        boolean ok=false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "eliminarInsumo");
        try {
            this.dao=new DAOFormulas();
            this.dao.eliminarInsumo(this.formula.getIdFormula(), this.insumo.getIdEmpaque());
            this.formula.setSumaCantidad(this.formula.getSumaCantidad()-this.insumo.getCantidad());
            this.formula.setSumaCosto(this.formula.getSumaCosto()-this.insumo.getCostoPromedio());
            this.formula.getInsumos().remove(this.insumo);
            ok=true;
        } catch (SQLException ex) {
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setDetail(ex.getMessage());
        }
        if (!ok) {
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
        }
        context.addCallbackParam("okInsumo", ok);
    }
    
    public void cancelarInsumo() {
        this.insumo.setCantidad(this.respInsumo.getCantidad());
        this.insumo.setVariacion(this.respInsumo.getVariacion());
    }
    
    private double calculaCostoPromedio() {
        double nuevoCosto=0.00;
        for(Insumo prod: this.formula.getInsumos()) {
            nuevoCosto+=prod.getCantidad()*prod.getCostoPromedio();
        }
        return nuevoCosto;
    }
    
    public void grabarInsumo() {
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "grabarInsumo");
        try {
            this.dao=new DAOFormulas();
            if(this.insumo.isNuevo()) {
                this.insumo.setCostoPromedio(this.dao.agregarInsumo(this.formula.getIdFormula(), this.formula.getIdEmpresa(), convertTOInsumo(this.insumo)));
                this.insumo.setNuevo(false);
                this.respInsumo.setCostoPromedio(0);
            } else {
                this.dao.modificarInsumo(this.formula.getIdFormula(), convertTOInsumo(this.insumo));
                this.formula.setSumaCantidad(this.formula.getSumaCantidad()-this.respInsumo.getCantidad()+this.insumo.getCantidad());
            }
            this.formula.setSumaCosto(this.formula.getSumaCosto()-this.respInsumo.getCostoPromedio()+this.insumo.getCostoPromedio());
            this.respaldaInsumo();
            this.formula.setCostoPromedio(this.calculaCostoPromedio());
            this.dao.modificarFormula(this.convertTOFormula(this.formula));
            fMsg.setDetail("La modificacion se realizo correctamente !!!");
        } catch (SQLException ex) {
            fMsg.setDetail(ex.getErrorCode() + " " + ex.getMessage());
        } catch (NamingException ex) {
            fMsg.setDetail(ex.getMessage());
        }
        FacesContext.getCurrentInstance().addMessage(null, fMsg);
    }
    
    private TOFormula convertTOFormula(Formula formula) {
        TOFormula to=new TOFormula();
        to.setIdFormula(formula.getIdFormula());
        to.setIdEmpresa(formula.getIdEmpresa());
        to.setIdEmpaque(formula.getIdEmpaque());
        to.setCostoPromedio(formula.getCostoPromedio());
        to.setMerma(formula.getMerma());
        to.setObservaciones(formula.getObservaciones());
        return to;
    }
    
    public void grabarFormula() {
        boolean ok=false;
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Aviso:", "grabarFormula");
        try {
            this.dao=new DAOFormulas();
            if(this.formula.getIdFormula()==0) {
                this.formula.setIdFormula(this.dao.agregarFormula(this.convertTOFormula(this.formula)));
            } else {
                this.dao.modificarFormula(this.convertTOFormula(this.formula));
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

    public Insumo getInsumo() {
        return insumo;
    }

    public void setInsumo(Insumo insumo) {
        this.insumo = insumo;
    }

    public Formula getFormula() {
        return formula;
    }

    public void setFormula(Formula formula) {
        this.formula = formula;
    }

    public ArrayList<Accion> getAcciones() {
        if (this.acciones == null) {
            this.acciones = this.mbAcciones.obtenerAcciones(29);
        }
        return acciones;
    }

    public void setAcciones(ArrayList<Accion> acciones) {
        this.acciones = acciones;
    }

    public MbAcciones getMbAcciones() {
        return mbAcciones;
    }

    public void setMbAcciones(MbAcciones mbAcciones) {
        this.mbAcciones = mbAcciones;
    }

    public MbProductosBuscar getMbBuscar() {
        return mbBuscar;
    }

    public void setMbBuscar(MbProductosBuscar mbBuscar) {
        this.mbBuscar = mbBuscar;
    }

    public MbMiniEmpresas getMbEmpresas() {
        return mbEmpresas;
    }

    public void setMbEmpresas(MbMiniEmpresas mbEmpresas) {
        this.mbEmpresas = mbEmpresas;
    }

    public String getUpdate() {
        return update;
    }

    public void setUpdate(String update) {
        this.update = update;
    }

    public int getCaso() {
        return caso;
    }

    public void setCaso(int caso) {
        this.caso = caso;
    }
}
