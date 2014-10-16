/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package clientesBancos.converters;

import clientesBancos.DAOClientesBancos.DAOClientesBancos;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import leyenda.dominio.ClientesBancos;

/**
 *
 * @author Usuario
 */
public class ConverterClientesBancos implements Converter{

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
       ClientesBancos clientes = new ClientesBancos();
       if(value.equals("")|| value  == null){
           clientes = new ClientesBancos();
       }else{
           try {
               DAOClientesBancos dao = new DAOClientesBancos();
               clientes = dao.dameClientesBancos(Integer.parseInt(value));
           } catch (SQLException ex) {
               Logger.getLogger(ConverterClientesBancos.class.getName()).log(Level.SEVERE, null, ex);
           }
       }
       return  clientes;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String id = "";
        ClientesBancos cli = (ClientesBancos) value;
        id = Integer.toString(cli.getIdClienteBanco());
        return id;
    }
    
}
