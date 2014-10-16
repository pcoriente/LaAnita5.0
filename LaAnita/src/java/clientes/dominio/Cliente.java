/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package clientes.dominio;

import agentes.dominio.Agentes;
import contribuyentes.Contribuyente;
import direccion.dominio.Direccion;
import formatos.dominio.ClientesFormato;
import impuestos.dominio.ImpuestoZona;
import java.io.Serializable;
import java.util.Date;
import menuClientesGrupos.dominio.ClientesGrupos;
import rutas.dominio.Ruta;

/**
 *
 * @author Pjgt
 */
public class Cliente implements Serializable {

    private int idCliente;
    private int codigoCliente;
    private Contribuyente contribuyente = new Contribuyente();
    private Agentes agente = new Agentes();
    private ClientesGrupos clientesGrupos = new ClientesGrupos();
    private ImpuestoZona impuestoZona = new ImpuestoZona();
    private Date fechaAlta;
    private int diasCredito;
    private float limiteCredito;
    private float descuentoComercial;
//    private double descuentoProntoPago;
    private int diasBloqueo;
    private int codigoTienda;
    private String nombreComercial;
    private ClientesFormato clientesFormato = new ClientesFormato();
    private Ruta ruta = new Ruta();
    private Direccion direccion = new Direccion();

    public ClientesGrupos getClientesGrupos() {
        return clientesGrupos;
    }

    public void setClientesGrupos(ClientesGrupos clientesGrupos) {
        this.clientesGrupos = clientesGrupos;
    }

    public Cliente() {
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public int getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(int codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public Contribuyente getContribuyente() {
        return contribuyente;
    }

    public void setContribuyente(Contribuyente contribuyente) {
        this.contribuyente = contribuyente;
    }

    public ImpuestoZona getImpuestoZona() {
        return impuestoZona;
    }

    public void setImpuestoZona(ImpuestoZona impuestoZona) {
        this.impuestoZona = impuestoZona;
    }

    public Date getFechaAlta() {
        return fechaAlta;
    }

    public void setFechaAlta(Date fechaAlta) {
        this.fechaAlta = fechaAlta;
    }

    public int getDiasCredito() {
        return diasCredito;
    }

    public void setDiasCredito(int diasCredito) {
        this.diasCredito = diasCredito;
    }

    public float getLimiteCredito() {
        return limiteCredito;
    }

    public void setLimiteCredito(float limiteCredito) {
        this.limiteCredito = limiteCredito;
    }

    public float getDescuentoComercial() {
        return descuentoComercial;
    }

    public void setDescuentoComercial(float descuentoComercial) {
        this.descuentoComercial = descuentoComercial;
    }

    public int getDiasBloqueo() {
        return diasBloqueo;
    }

    public void setDiasBloqueo(int diasBloqueo) {
        this.diasBloqueo = diasBloqueo;
    }

    public int getCodigoTienda() {
        return codigoTienda;
    }

    public void setCodigoTienda(int codigoTienda) {
        this.codigoTienda = codigoTienda;
    }

    public String getNombreComercial() {
        return nombreComercial;
    }

    public void setNombreComercial(String nombreComercial) {
        this.nombreComercial = nombreComercial;
    }

    public Agentes getAgente() {
        return agente;
    }

    public void setAgente(Agentes agente) {
        this.agente = agente;
    }

    public ClientesFormato getClientesFormato() {
        return clientesFormato;
    }

    public void setClientesFormato(ClientesFormato clientesFormato) {
        this.clientesFormato = clientesFormato;
    }

    public Ruta getRuta() {
        return ruta;
    }

    public void setRuta(Ruta ruta) {
        this.ruta = ruta;
    }

    public Direccion getDireccion() {
        return direccion;
    }

    public void setDireccion(Direccion direccion) {
        this.direccion = direccion;
    }

}
