/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package formatos.Converter;

import Message.Mensajes;
import formatos.DAOFormatos.DAOFormatos;
import formatos.dominio.ClientesFormato;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.naming.NamingException;

/**
 *
 * @author Usuario
 */
public class ConverterFormatos implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        ClientesFormato clientes = null;
        int id = Integer.parseInt(value);
        if (id == 0) {
            clientes = new ClientesFormato();
        } else {
            try {
                DAOFormatos dao = new DAOFormatos();
                clientes = dao.obtenerClientesFormato(id);
            } catch (NamingException ex) {
                Mensajes.mensajeError(ex.getMessage());
                Logger.getLogger(ConverterFormatos.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SQLException ex) {
                Mensajes.mensajeError(ex.getMessage());
                Logger.getLogger(ConverterFormatos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return clientes;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        ClientesFormato clientes = new ClientesFormato();
        try {
            clientes = (ClientesFormato) value;
        } catch (Exception e) {
            System.err.println(e);
        }
        return Integer.toString(clientes.getIdFormato());
    }

}
