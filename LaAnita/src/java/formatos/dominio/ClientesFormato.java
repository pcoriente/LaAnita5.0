/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatos.dominio;

import java.io.Serializable;
import menuClientesGrupos.dominio.ClientesGrupos;

/**
 *
 * @author Usuario
 */
public class ClientesFormato implements Serializable {

    private int idFormato;
    private String formato;
    private ClientesGrupos clientesGrupos = new ClientesGrupos();

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

    public ClientesGrupos getClientesGrupos() {
        return clientesGrupos;
    }

    public void setClientesGrupos(ClientesGrupos clientesGrupos) {
        this.clientesGrupos = clientesGrupos;
    }

    @Override
    public String toString() {
        return "ClientesFormato{" + "formato=" + formato + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 83 * hash + this.idFormato;
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
        final ClientesFormato other = (ClientesFormato) obj;
        if (this.idFormato != other.idFormato) {
            return false;
        }
        return true;
    }

}
