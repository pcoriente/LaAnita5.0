/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agentes.converter;

import agentes.dao.DaoAgentes;
import agentes.dominio.Agentes;
import java.sql.SQLException;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 * @author Usuario
 */
public class AgentesConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        int id = Integer.parseInt(value);
        Agentes agentes = null;
        if (id == 0) {
            agentes = new Agentes();
        } else {
            DaoAgentes dao = new DaoAgentes();
            try {
                agentes = dao.dameAgentes(id);
            } catch (SQLException ex) {
                Message.Mensajes.mensajeError(ex.getMessage());
            }
        }
        return agentes;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        Agentes agentes = (Agentes) value;
        return Integer.toString(agentes.getIdAgente());
    }
}
