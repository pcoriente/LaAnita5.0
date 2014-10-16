/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package clientesListas.dominio;

import java.io.Serializable;
import menuClientesGrupos.dominio.ClientesGrupos;
import monedas.Moneda;

/**
 *
 * @author Usuario
 */
public class ClientesFormatos implements Serializable {

    private int idFormato;
    private String formato;
    private ClientesGrupos clientesGrupo = new ClientesGrupos();
//    private Moneda moneda  = new Moneda();

    public int getIdFormato() {
        return idFormato;
    }

    public void setIdFormato(int idFormato) {
        this.idFormato = idFormato;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public ClientesGrupos getClientesGrupo() {
        return clientesGrupo;
    }

    public void setClientesGrupo(ClientesGrupos clientesGrupo) {
        this.clientesGrupo = clientesGrupo;
    }

    @Override
    public String toString() {
        return formato;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.idFormato;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ClientesFormatos other = (ClientesFormatos) obj;
        if (this.idFormato != other.idFormato) {
            return false;
        }
        return true;
    }

//    public Moneda getMoneda() {
//        return moneda;
//    }
//
//    public void setMoneda(Moneda moneda) {
//        this.moneda = moneda;
//    }
    
    
    
}
