/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientesTienda;

import java.io.Serializable;
import javax.enterprise.context.SessionScoped;
import javax.inject.Named;

/**
 *
 * @author Usuario
 */
@Named(value = "mbClientesTienda")
@SessionScoped
public class MbClientesTienda implements Serializable {

//    private ClienteTienda clienteTienda = new ClienteTienda();
//    private ClienteTienda clienteTiendaSeleccionado = null;
//    @ManagedProperty(value = "#{mbDireccion}")
//    private MbDireccion mbDireccion = new MbDireccion();
//    @ManagedProperty(value = "#{mbRutas}")
//    private MbRutas mbRutas = new MbRutas();
//    @ManagedProperty(value = "#{mbFormatos}")
//    private MbFormatos mbFormatos = new MbFormatos();
//    private ArrayList<ClienteTienda> lstClientesGrupos = null;
//    private boolean editar = false;

    public MbClientesTienda() {
    }

//    public void cargarDatos() {
//        mbDireccion.setDireccion(clienteTiendaSeleccionado.getDireccion());
//        mbFormatos.getCmbFormato().setIdFormato(clienteTiendaSeleccionado.getFormatos().getIdFormato());
//        mbRutas.getCmbRuta().setIdRuta(clienteTiendaSeleccionado.getRuta().getIdRuta());
//        this.setClienteTienda(clienteTiendaSeleccionado);
//        this.setEditar(true);
//    }
//
//    public void actualizar() {
//        editar = true;
//    }
//
//    public void validarDireccion() {
//        boolean ok = mbDireccion.validarDireccion();
//        if (ok == true) {
//            Direccion direccion;
//            RequestContext context = RequestContext.getCurrentInstance();
//            FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito:", "");
//            fMsg.setDetail("Nueva Direccion Disponible");
//            FacesContext.getCurrentInstance().addMessage(null, fMsg);
//            context.addCallbackParam("okContribuyente", ok);
//            direccion = mbDireccion.getDireccion();
//            clienteTienda.setDireccion(direccion);
//            if (editar == true) {
//                try {
//                    DAODireccion dao = new DAODireccion();
//                    dao.modificar(direccion.getIdDireccion(), direccion.getCalle(), direccion.getNumeroExterior(), direccion.getNumeroInterior(), direccion.getReferencia(), direccion.getPais().getIdPais(), direccion.getCodigoPostal(), direccion.getEstado(), direccion.getMunicipio(), direccion.getLocalidad(), direccion.getColonia(), direccion.getNumeroLocalizacion());
//                } catch (NamingException ex) {
//                    Logger.getLogger(MbClientesTienda.class.getName()).log(Level.SEVERE, null, ex);
//                } catch (SQLException ex) {
//                    Logger.getLogger(MbClientesTienda.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }
//    }
//
//    public void guardarClientesTiendas() {
//        boolean ok = validar();
//        RequestContext context = RequestContext.getCurrentInstance();
//        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito:", "");
//        if (ok == true) {
//            try {
//                DAOClientesTienda daoClienteTienda = new DAOClientesTienda();
//                clienteTienda.getFormatos().setIdFormato(mbFormatos.getCmbFormato().getIdFormato());
//                clienteTienda.getRuta().setIdRuta(mbRutas.getCmbRuta().getIdRuta());
//                if (editar == false) {
//                    daoClienteTienda.guardarClientesTienda(clienteTienda);
//                    fMsg.setDetail("Nuvos Clientes Tienda Disponibles");
//                } else {
//                    DAOClientesTienda dao = new DAOClientesTienda();
//                    dao.actualizarClientesTienda(clienteTienda);
//                    fMsg.setDetail("Datos Actualizados");
//                }
//                lstClientesGrupos = null;
//            } catch (NamingException ex) {
//                fMsg.setDetail(ex.getMessage());
//                Logger.getLogger(MbClientesTienda.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (SQLException ex) {
//                fMsg.setDetail(ex.getMessage());
//                Logger.getLogger(MbClientesTienda.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            FacesContext.getCurrentInstance().addMessage(null, fMsg);
//            context.addCallbackParam("ok", ok);
//        }
//        this.clienteTiendaSeleccionado = null;
//        this.setEditar(false);
//    }
//
//    public boolean validar() {
//        boolean ok = false;
//        RequestContext context = RequestContext.getCurrentInstance();
//        FacesMessage fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", "");
//        if (clienteTienda.getCodigoTienda() == 0) {
//            fMsg.setDetail("Se requiere un codigo de tienda!!");
//        } else if (clienteTienda.getNombre().equals("")) {
//            fMsg.setDetail("Se requiere un nombre!!");
//        } else if (clienteTienda.getDireccion().getCalle().equals("")) {
//            fMsg.setDetail("Se requiere la calle !!");
//        } else if (mbFormatos.getCmbFormato().getIdFormato() == 0) {
//            fMsg.setDetail("Se requiere un formato !!");
//        } else if (mbRutas.getCmbRuta().getIdRuta() == 0) {
//            fMsg.setDetail("Se requiere una ruta !!");
//        } else if (clienteTienda.getCodigoCliente() == 0) {
//            fMsg.setDetail("Se requiere un codigo cliente !!");
//        } else {
//            ok = true;
//        }
//        if (!ok) {
//            FacesContext.getCurrentInstance().addMessage(null, fMsg);
//
//        }
//        context.addCallbackParam("ok", ok);
//        return ok;
//    }
//
//    public void guardarRutas() {
//        boolean ok;
//        RequestContext context = RequestContext.getCurrentInstance();
//        FacesMessage fMsg;
//        ok = mbRutas.validar();
//        if (ok == true) {
//            try {
//                DAORutas dao = new DAORutas();
//                if (mbRutas.getRuta().getIdRuta() == 0) {
//                    dao.guardar(mbRutas.getRuta());
//                    fMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito:", "Nueva ruta disponible");
//                } else {
//                    dao.actualizarRutas(mbRutas.getRuta());
//                    fMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito:", "Ruta actualizada");
//                }
//                ArrayList<SelectItem> lst = null;
//                mbRutas.setLstRuta(lst);
//            } catch (NamingException ex) {
//                ok = false;
//                fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", ex.getMessage());
//            } catch (SQLException ex) {
//                ok = false;
//                fMsg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Aviso:", ex.getMessage());
//            }
//            FacesContext.getCurrentInstance().addMessage(null, fMsg);
//            context.addCallbackParam("ok", ok);
//        }
//    }
//
////    public void guardarFormatos() {
//        int idFormato = mbFormatos.getMbClientesGrupos().getCmbClientesGrupos().getIdGrupoCte();
//        boolean ok = mbFormatos.validar();
//        RequestContext context = RequestContext.getCurrentInstance();
//        FacesMessage fMsg;
//        if (ok == true) {
//            try {
//                DAOClientesLista dao = new DAOClientesLista();
//                ClientesFormatos formato = new ClientesFormatos();
//                formato.setFormato(mbFormatos.getFormato().getFormato());
//                formato.getClientesGrupo().setIdGrupoCte(idFormato);
//                if (mbFormatos.getCmbFormato().getIdFormato() == 0) {
//                    dao.guardarFormato(formato);
//                    fMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito:", "Nuevos formatos disponibles");
//                } else {
//                    formato.setIdFormato(mbFormatos.getFormato().getIdFormato());
//                    dao.actualizar(formato);
//                    fMsg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Exito:", "Formato Actualizado");
//                }
//                mbFormatos.setLstItems(null);
//            } catch (NamingException ex) {
//                ok = false;
//                fMsg = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error:", ex.getMessage());
//                Logger.getLogger(MbClientesTienda.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (SQLException ex) {
//                ok = false;
//                fMsg = new FacesMessage(FacesMessage.SEVERITY_FATAL, "Error:", ex.getMessage());
//                Logger.getLogger(MbClientesTienda.class.getName()).log(Level.SEVERE, null, ex);
//            }
//            FacesContext.getCurrentInstance().addMessage(null, fMsg);
//            context.addCallbackParam("ok", ok);
//        }
//    }
//
//    public void cargarFormatos() {
//        if (mbFormatos.getCmbFormato().getIdFormato() > 0) {
//            mbFormatos.getFormato().setIdFormato(mbFormatos.getCmbFormato().getIdFormato());
//            mbFormatos.getFormato().setFormato(mbFormatos.getCmbFormato().getFormato());
//            mbFormatos.getMbClientesGrupos().getCmbClientesGrupos().setIdGrupoCte(mbFormatos.getCmbFormato().getClientesGrupo().getIdGrupoCte());
////            editar = true;
//        } else {
//            mbFormatos.getFormato().setIdFormato(0);
//            mbFormatos.getFormato().setFormato("");
//            mbFormatos.getMbClientesGrupos().getCmbClientesGrupos().setIdGrupoCte(0);
////          editar = false;
//        }
//    }
//
//    public void cargarRutas() {
//        if (mbRutas.getCmbRuta().getIdRuta() > 0) {
//            mbRutas.getRuta().setIdRuta(mbRutas.getCmbRuta().getIdRuta());
//            mbRutas.getRuta().setRuta(mbRutas.getCmbRuta().getRuta());
//            editar = true;
//        } else {
//            mbRutas.getRuta().setIdRuta(0);
//            mbRutas.getRuta().setRuta("");
//            editar = false;
//        }
//    }
//
//    public void limpiarCampos() {
//        mbRutas.getCmbRuta().setIdRuta(0);
//        mbFormatos.getCmbFormato().setIdFormato(0);
//        mbDireccion.setDireccion(new Direccion());
//        clienteTienda = new ClienteTienda();
//        mbFormatos.getFormato().setFormato("");
//        mbFormatos.getMbClientesGrupos().getCmbClientesGrupos().setIdGrupoCte(0);
//        mbRutas.getRuta().setRuta("");
//        this.clienteTiendaSeleccionado = null;
//        this.setEditar(false);
//    }
//
//    public ClienteTienda getClienteTienda() {
//        return clienteTienda;
//    }
//
//    public void setClienteTienda(ClienteTienda clienteTienda) {
//        this.clienteTienda = clienteTienda;
//    }
//
//    public MbDireccion getMbDireccion() {
//        return mbDireccion;
//    }
//
//    public void setMbDireccion(MbDireccion mbDireccion) {
//        this.mbDireccion = mbDireccion;
//    }
//
//    public MbRutas getMbRutas() {
//        return mbRutas;
//    }
//
//    public void setMbRutas(MbRutas mbRutas) {
//        this.mbRutas = mbRutas;
//    }
//
//   
//
//    public ArrayList<ClienteTienda> getLstClientesGrupos() {
//        if (lstClientesGrupos == null) {
//            try {
//                DAOClientesTienda daoClientes = new DAOClientesTienda();
//                lstClientesGrupos = daoClientes.listaClientesTienda();
//            } catch (SQLException ex) {
//                Logger.getLogger(MbClientesTienda.class.getName()).log(Level.SEVERE, null, ex);
//            } catch (NamingException ex) {
//                Logger.getLogger(MbClientesTienda.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        return lstClientesGrupos;
//    }
//
//    public void setLstClientesGrupos(ArrayList<ClienteTienda> lstClientesGrupos) {
//        this.lstClientesGrupos = lstClientesGrupos;
//    }
//
//    public ClienteTienda getClienteTiendaSeleccionado() {
//        return clienteTiendaSeleccionado;
//    }
//
//    public void setClienteTiendaSeleccionado(ClienteTienda clienteTiendaSeleccionado) {
//        this.clienteTiendaSeleccionado = clienteTiendaSeleccionado;
//    }
//
//    public boolean isEditar() {
//        return editar;
//    }
//
//    public void setEditar(boolean editar) {
//        this.editar = editar;
//    }

}
