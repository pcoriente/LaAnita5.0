package clientes;

import Message.Mensajes;
import agentes.MbMiniAgentes;
import clientes.DAOClientes.DAOClientes;
import clientes.dominio.Cliente;
import clientesBancos.DAOClientesBancos.DAOClientesBancos;
import clientesBancos.MbClientesBancos;
import contribuyentes.Contribuyente;
import contribuyentes.DAOContribuyentes;
import contribuyentes.MbContribuyentes;
import direccion.MbDireccion;
import direccion.dao.DAODireccion;
import direccion.dominio.Direccion;
import impuestos.MbZonas;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.context.FacesContext;
import javax.inject.Named;
import javax.naming.NamingException;
import leyenda.dominio.ClientesBancos;
import mbMenuClientesGrupos.MbClientesGrupos;
import org.primefaces.context.RequestContext;
import rutas.MbRutas;
import utilerias.Utilerias;

/**
 *
 * @author Usuario
 */
@Named(value = "mbClientesBueno")
@SessionScoped
public class MbClientes implements Serializable {

    @ManagedProperty(value = "#{mbDireccion}")
    private MbDireccion mbDireccion = new MbDireccion();
    @ManagedProperty(value = "#{mbRutas}")
    private MbRutas mbRutas = new MbRutas();
    @ManagedProperty(value = "#{mbAgentes}")
    private MbMiniAgentes mbAgentes = new MbMiniAgentes();
    @ManagedProperty(value = "#{mbContribuyente}")
    private MbContribuyentes mbContribuyente = new MbContribuyentes();
    @ManagedProperty(value = "#{mbZonas}")
    private MbClientesGrupos mbClientesGrupos = new MbClientesGrupos();
    @ManagedProperty(value = "#{mbClientesGrupos}")
    private MbZonas mbZonas = new MbZonas();
    @ManagedProperty(value = "#{mbClientesBancos}")
    private MbClientesBancos mbClientesBancos = new MbClientesBancos();
    private ArrayList<Cliente> lstCliente = null;
    private Cliente clienteSeleccionado = new Cliente();
    private Cliente cliente = new Cliente();
    private int personaFisica = 0;
    private boolean actualizarRfc = false;
    private int controlActualizar = 0;
    private ClientesBancos clientes = new ClientesBancos();
    private boolean actualizar = false;
    private boolean direccionCliente = false;
    private boolean nuevoContribuyente = false;
    private boolean activarRfc = false;

    /**
     * Creates a new instance of MbClientes
     */
    public MbClientes() {
    }

    public List<String> completarClientes(String query) {
        List<String> lst = new ArrayList<String>();
        try {
            DAOContribuyentes dao = new DAOContribuyentes();
            for (Contribuyente con : dao.dameRfcContribuyente(query)) {
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

    public ArrayList<Cliente> getLstCliente() {
        if (lstCliente == null) {
            try {
                lstCliente = new ArrayList<Cliente>();
                DAOClientes daoCliente = new DAOClientes();
                lstCliente = daoCliente.lstClientes();
            } catch (SQLException ex) {
                Mensajes.mensajeError(ex.getMessage());
            }
        }
        return lstCliente;
    }

    public void cargarFormatos() {
        mbClientesGrupos.getMbFormatos().setLstFormatos(null);
        mbClientesGrupos.getMbFormatos().cargarListaFormatos(mbClientesGrupos.getCmbClientesGrupos().getIdGrupoCte());
    }

    public String salir() {
        return "index.xhtml";
    }

    public void validarDireccion() {
        if (mbDireccion.validarDireccion() == true) {
            if (controlActualizar == 1) {
                try {
                    DAODireccion daoDireccion = new DAODireccion();
                    daoDireccion.modificar(mbDireccion.getDireccion().getIdDireccion(), mbDireccion.getDireccion().getCalle(), mbDireccion.getDireccion().getNumeroExterior(), mbDireccion.getDireccion().getNumeroInterior(), mbDireccion.getDireccion().getReferencia(), mbDireccion.getDireccion().getPais().getIdPais(), mbDireccion.getDireccion().getCodigoPostal(), mbDireccion.getDireccion().getEstado(), mbDireccion.getDireccion().getMunicipio(), mbDireccion.getDireccion().getLocalidad(), mbDireccion.getDireccion().getColonia(), mbDireccion.getDireccion().getNumeroLocalizacion());
                    if (direccionCliente == false) {
                        cliente.getContribuyente().setDireccion(mbDireccion.getDireccion());
                    } else {
                        cliente.setDireccion(mbDireccion.getDireccion());
                    }
                    Mensajes.mensajeSucces("Exito!! Dirección actualizada correctamente");
                } catch (NamingException ex) {
                    Logger.getLogger(MbClientes.class.getName()).log(Level.SEVERE, null, ex);
                } catch (SQLException ex) {
                    Logger.getLogger(MbClientes.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else if (direccionCliente == true) {
                cliente.setDireccion(mbDireccion.getDireccion());

            } else {
                cliente.getContribuyente().setDireccion(mbDireccion.getDireccion());
                mbContribuyente.getContribuyente().setDireccion(mbDireccion.getDireccion());
            }
        }
    }

    public void altaDireccionClientes() {
        direccionCliente = true;
        mbDireccion.setDireccion(cliente.getDireccion());
    }

    public void obtenerInformacionRfc() {
        mbContribuyente.limpiarContribuyente();
        mbDireccion.limpiarDireccion();
        cliente.getContribuyente().setDireccion(new Direccion());
        direccionCliente = false;
        Utilerias utilerias = new Utilerias();
        String mensaje = utilerias.verificarRfc(cliente.getContribuyente().getRfc());
        if (mensaje == "") {
            try {
                Mensajes.mensajeSucces("Rfc Valido");
                DAOContribuyentes dao = new DAOContribuyentes();
                Contribuyente contribuyente = dao.buscarContribuyente(cliente.getContribuyente().getRfc().toUpperCase());
                activarRfc = true;
                if (contribuyente.getContribuyente().equals("")) {
                    nuevoContribuyente = true;
                } else {
                    cliente.getContribuyente().setDireccion(contribuyente.getDireccion());
                    mbContribuyente.setContribuyente(contribuyente);
                }
            } catch (NamingException ex) {
                Mensajes.mensajeError(ex.getMessage());
            } catch (SQLException ex) {
                Mensajes.mensajeError(ex.getMessage());

            }
            dameStatusRfc();
        } else {
            if (cliente.getContribuyente().getRfc() == null) {
                RequestContext context = RequestContext.getCurrentInstance();
                context.addCallbackParam("ok", true);
                activarRfc = false;
            } else {
                Mensajes.mensajeError("Error!!" + mensaje);
            }
        }
    }

    public void mostrarDireccionContribuyente() {
        mbDireccion.setDireccion(mbContribuyente.getContribuyente().getDireccion());
    }

    public void cargarDatos() {
        this.setControlActualizar(1);
        this.setActualizarRfc(true);
        mbZonas.getZona().setIdZona(clienteSeleccionado.getImpuestoZona().getIdZona());
        cliente.setDireccion(clienteSeleccionado.getDireccion());
        cliente.setCodigoCliente(clienteSeleccionado.getCodigoCliente());
        cliente.getContribuyente().setDireccion(clienteSeleccionado.getContribuyente().getDireccion());
        cliente.setDescuentoComercial(clienteSeleccionado.getDescuentoComercial());
        cliente.setDiasBloqueo(clienteSeleccionado.getDiasBloqueo());
        cliente.setDiasCredito(clienteSeleccionado.getDiasCredito());
        cliente.setLimiteCredito(clienteSeleccionado.getLimiteCredito());
        cliente.setIdCliente(clienteSeleccionado.getIdCliente());
        mbZonas.getZona().setIdZona(clienteSeleccionado.getImpuestoZona().getIdZona());
        mbClientesGrupos.getCmbClientesGrupos().setIdGrupoCte(clienteSeleccionado.getClientesGrupos().getIdGrupoCte());
        mbAgentes.getCmbAgentes().setIdAgente(clienteSeleccionado.getAgente().getIdAgente());
        mbRutas.getCmbRuta().setIdRuta(clienteSeleccionado.getRuta().getIdRuta());
        cliente.getContribuyente().setRfc(clienteSeleccionado.getContribuyente().getRfc());
        cliente.setNombreComercial(clienteSeleccionado.getNombreComercial());
        cliente.setCodigoTienda(clienteSeleccionado.getCodigoTienda());
        mbClientesGrupos.getMbFormatos().setLstFormatos(null);
        mbClientesGrupos.getMbFormatos().cargarListaFormatos(clienteSeleccionado.getClientesGrupos().getIdGrupoCte());
    }

    public void cancelar() {

        clienteSeleccionado = null;
        this.controlActualizar = 0;
        this.limpiar();
        this.actualizarRfc = false;
        this.actualizar = false;
        cliente = new Cliente();
        mbAgentes.getCmbAgentes().setIdAgente(0);
        mbZonas.getZona().setIdZona(0);
        mbRutas.getCmbRuta().setIdRuta(0);
        mbClientesGrupos.getCmbClientesGrupos().setIdGrupoCte(0);
    }

    public void obtenerBancosClientes() {
        try {
            mbClientesBancos.getMbBanco().obtenerBancos(cliente.getIdCliente());

        } catch (SQLException ex) {
            Mensajes.mensajeError(ex.getMessage());
        }
    }

    public void dameStatusRfc() {
        int longitud = mbContribuyente.getContribuyente().getRfc().trim().length();
        if (longitud == 13) {
            personaFisica = 1;
        } else {
            personaFisica = 2;
        }
    }

    public void cargarDatosClientes(String rfc) {
        try {
            DAOClientes dao = new DAOClientes();
            Cliente cl = dao.dameInformacionCliente(rfc);
            this.setControlActualizar(1);
            this.setActualizarRfc(true);
            mbZonas.getZona().setIdZona(cl.getImpuestoZona().getIdZona());
            mbDireccion.setDireccion(cl.getContribuyente().getDireccion());
            cliente.setCodigoCliente(cl.getCodigoCliente());
            cliente.getContribuyente().setDireccion(cl.getContribuyente().getDireccion());
            cliente.setDescuentoComercial(cl.getDescuentoComercial());
            cliente.setDiasBloqueo(cl.getDiasBloqueo());
            cliente.setDiasCredito(cl.getDiasCredito());
            cliente.setLimiteCredito(cl.getLimiteCredito());
            cliente.setIdCliente(cl.getIdCliente());
            mbContribuyente.getContribuyente().setContribuyente(cl.getContribuyente().getContribuyente());
            mbContribuyente.getContribuyente().setCurp(cl.getContribuyente().getCurp());
            mbContribuyente.getContribuyente().setRfc(cl.getContribuyente().getRfc());
            mbContribuyente.getContribuyente().setIdRfc(cl.getContribuyente().getIdRfc());
            mbContribuyente.getContribuyente().setIdContribuyente(cl.getContribuyente().getIdContribuyente());
            mbClientesBancos.getMbBanco().obtenerBancos(cl.getCodigoCliente());
            mbClientesBancos.cargarBancos(cl.getCodigoCliente());
        } catch (SQLException ex) {
            Mensajes.mensajeError(ex.getMessage());
        }
    }

    public void guardarCuentasBancarias() {
        boolean ok = false;
        FacesMessage fMsg = null;
        RequestContext context = RequestContext.getCurrentInstance();
        ok = mbClientesBancos.validar();
        if (ok == true) {
            try {
                DAOClientesBancos daoClientesBancos = new DAOClientesBancos();
                mbClientesBancos.getClientesBancos().setCodigoCliente(cliente.getCodigoCliente());
                if (clientes.getBancoLeyenda().getIdBanco() == 0) {
                    daoClientesBancos.guardarClientesBancos(mbClientesBancos.getClientesBancos());
                    fMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "");
                    fMsg.setDetail("Exito!! Nueva Cuenta Disponible !!");
                } else {
                    mbClientesBancos.getClientesBancos().setIdClienteBanco(clientes.getIdClienteBanco());
                    daoClientesBancos.actualizarClientesBancos(mbClientesBancos.getClientesBancos());
                    fMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "");
                    fMsg.setDetail("Exito!! Cuenta Actualizada !!");
                }
                mbClientesBancos.cargarBancos(cliente.getCodigoCliente());
            } catch (SQLException ex) {
                ok = false;
                fMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", ex.getMessage());
            }
        }
        FacesContext.getCurrentInstance().addMessage(null, fMsg);
        context.addCallbackParam("ok", ok);
    }

    public void guardar() {
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        boolean ok = false;
        if (mbAgentes.getCmbAgentes().getIdAgente() == 0) {
            Mensajes.mensajeAlert("Error, Se requiere un Agente");
        } else if (mbClientesGrupos.getCmbClientesGrupos().getIdGrupoCte() == 0) {
            Mensajes.mensajeAlert("Error, Se requiere un cliente grupo");
        } else if (mbClientesGrupos.getMbFormatos().getLstFormatos().size() > 1 && mbClientesGrupos.getMbFormatos().getCmbClientesFormatos().getIdFormato() == 0) {
            Mensajes.mensajeAlert("Error, Se requiere un formato para este grupo");
        } else if (mbZonas.getZona().getIdZona() == 0) {
            Mensajes.mensajeAlert("Error, Se requiere una zona");
        } else {

            cliente.setClientesGrupos(mbClientesGrupos.getCmbClientesGrupos());
            cliente.setImpuestoZona(mbZonas.getZona());
            ok = this.validarClientes();
            if (ok == true) {
                cliente.setImpuestoZona(mbZonas.getZona());
                DAOClientes daoCliente = new DAOClientes();
                try {
                    cliente.getClientesFormato().setIdFormato(mbClientesGrupos.getMbFormatos().getCmbClientesFormatos().getIdFormato());
                    cliente.setAgente(mbAgentes.getCmbAgentes());
                    cliente.getContribuyente().setDireccion(mbDireccion.getDireccion());
                    FacesMessage fMsgs = new FacesMessage(FacesMessage.SEVERITY_INFO, "Aviso:", "");
                    if (controlActualizar == 0) {
                        daoCliente.guardarCliente(cliente);
                        fMsgs.setDetail("Exito!! Nuevo Cliente dado de Alta");
                    } else {
                        daoCliente.actualizarClientes(cliente);
                        fMsgs.setDetail("Exito!! Cliente Actualizado");
                    }
                    FacesContext.getCurrentInstance().addMessage(null, fMsgs);
                    lstCliente = null;
                    this.limpiar();
                    actualizar = false;
                    clienteSeleccionado = null;
                    actualizarRfc = false;
                    controlActualizar = 0;
                } catch (SQLException ex) {
                    ok = false;
                    fMsg.setDetail(ex.getMessage());
                    FacesContext.getCurrentInstance().addMessage(null, fMsg);
                }
            }
        }
        context.addCallbackParam("ok", ok);
    }

    private void limpiar() {
        mbDireccion.setDireccion(new Direccion());
        mbZonas.getZona().setIdZona(0);
        cliente.setCodigoCliente(0);
        mbContribuyente.setContribuyente(new Contribuyente());
        cliente.setDescuentoComercial((float) 0.00);
//      cliente.setDescuentoProntoPago(0.00);
        cliente.setDiasBloqueo(0);
        cliente.setDiasCredito(0);
        cliente.setIdCliente(0);
        cliente.setLimiteCredito((float) 0.00);
        cliente.getContribuyente().setDireccion(new Direccion());
    }

    public void validarContribuyente() {
        boolean ok = false;
        ok = mbContribuyente.valida();
        if (ok == true) {
            try {
                DAOContribuyentes dao = new DAOContribuyentes();
                if (actualizarRfc == false) {
                    boolean paso = dao.verificarContribuyente(mbContribuyente.getContribuyente().getRfc());
                    if (paso == false) {
                        dao.guardarContribuyente(mbContribuyente.getContribuyente(), mbContribuyente.getContribuyente().getDireccion());
                        Mensajes.mensajeSucces("Exito, nuevo contribuyente disponible");
                    } else {
                        Mensajes.mensajeAlert("Error, Este contribuyente ya esta dado de alta");
                    }
                } else {
                    dao.actualizarContribuyente(mbContribuyente.getContribuyente());
                }
            } catch (NamingException ex) {
                Mensajes.mensajeError(ex.getMessage());
                Logger.getLogger(MbClientes.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {

                Mensajes.mensajeError(ex.getMessage());
            }
        }
    }

    public boolean validarClientes() {
        boolean ok = false;
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
        if (cliente.getCodigoCliente() == 0) {
            fMsg.setDetail("Error!! Codigo Cliente Requerido");
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
            context.addCallbackParam("ok", ok);
        } else if (cliente.getCodigoTienda() == 0) {
            fMsg.setDetail("Error!! Codigo Tienda Requerido");
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
            context.addCallbackParam("ok", ok);
        } else if (cliente.getNombreComercial().equals("")) {
            fMsg.setDetail("Error!! Nombre comercial Requerido");
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
            context.addCallbackParam("ok", ok);
        } else if (!mbZonas.validar()) {
        } else if (cliente.getDiasCredito() == 0) {
            fMsg.setDetail("Error!! Dias de Credito Requerido");
            FacesContext.getCurrentInstance().addMessage(null, fMsg);
            context.addCallbackParam("ok", ok);
        } else {
            ok = true;
        }
        return ok;
    }

    public void dameInformacion() {
        if (clientes.getBancoLeyenda().getIdBanco() == 0) {
            mbClientesBancos.setClientesBancos(new ClientesBancos());
            mbClientesBancos.getMbBanco().getObjBanco().setIdBanco(0);
        } else {
            mbClientesBancos.setClientesBancos(clientes);
            mbClientesBancos.getMbBanco().getObjBanco().setIdBanco(clientes.getBancoLeyenda().getIdBanco());
        }
    }

    public void limpiarCampos() {
//        clientes.setIdBanco(0);
        actualizar = true;
        mbClientesBancos.cargarBancos(cliente.getCodigoCliente());
//        mbClientesBancos.getMbBanco().getObjBanco().setIdBanco(0);
    }

    public void dameCuentasBancarias() {
        mbClientesBancos.getMbBanco().cargarBancos(cliente.getIdCliente());
    }

    public void setLstCliente(ArrayList<Cliente> lstCliente) {
        this.lstCliente = lstCliente;
    }

    public Cliente getClienteSeleccionado() {
        return clienteSeleccionado;
    }

    public void setClienteSeleccionado(Cliente clienteSeleccionado) {
        this.clienteSeleccionado = clienteSeleccionado;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public MbDireccion getMbDireccion() {
        return mbDireccion;
    }

    public void setMbDireccion(MbDireccion mbDireccion) {
        this.mbDireccion = mbDireccion;
    }

    public MbZonas getMbZonas() {
        return mbZonas;
    }

    public void setMbZonas(MbZonas mbZonas) {
        this.mbZonas = mbZonas;
    }

    public MbContribuyentes getMbContribuyente() {
        return mbContribuyente;
    }

    public void setMbContribuyente(MbContribuyentes mbContribuyente) {
        this.mbContribuyente = mbContribuyente;
    }

    public int getPersonaFisica() {
        return personaFisica;
    }

    public void setPersonaFisica(int personaFisica) {
        this.personaFisica = personaFisica;
    }

    public boolean isActualizarRfc() {
        return actualizarRfc;
    }

    public void setActualizarRfc(boolean actualizarRfc) {
        this.actualizarRfc = actualizarRfc;
    }

    public int getControlActualizar() {
        return controlActualizar;
    }

    public void setControlActualizar(int controlActualizar) {
        this.controlActualizar = controlActualizar;
    }

    public MbClientesBancos getMbClientesBancos() {
        return mbClientesBancos;
    }

    public void setMbClientesBancos(MbClientesBancos mbClientesBancos) {
        this.mbClientesBancos = mbClientesBancos;
    }

    public ClientesBancos getClientes() {
        return clientes;
    }

    public void setClientes(ClientesBancos clientes) {
        this.clientes = clientes;
    }

    public boolean isActualizar() {
        return actualizar;
    }

    public void setActualizar(boolean actualizar) {
        this.actualizar = actualizar;
    }

    public MbClientesGrupos getMbClientesGrupos() {
        return mbClientesGrupos;
    }

    public void setMbClientesGrupos(MbClientesGrupos mbClientesGrupos) {
        this.mbClientesGrupos = mbClientesGrupos;
    }

    public MbRutas getMbRutas() {
        return mbRutas;
    }

    public void setMbRutas(MbRutas mbRutas) {
        this.mbRutas = mbRutas;
    }

    public boolean isDireccionCliente() {
        return direccionCliente;
    }

    public void setDireccionCliente(boolean direccionCliente) {
        this.direccionCliente = direccionCliente;
    }

    public MbMiniAgentes getMbAgentes() {
        return mbAgentes;
    }

    public void setMbAgentes(MbMiniAgentes mbAgentes) {
        this.mbAgentes = mbAgentes;
    }

    public boolean isNuevoContribuyente() {
        return nuevoContribuyente;
    }

    public void setNuevoContribuyente(boolean nuevoContribuyente) {
        this.nuevoContribuyente = nuevoContribuyente;
    }

    public boolean isActivarRfc() {
        return activarRfc;
    }

    public void setActivarRfc(boolean activarRfc) {
        this.activarRfc = activarRfc;
    }

}
