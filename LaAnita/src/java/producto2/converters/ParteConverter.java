package producto2.converters;

import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;
import producto2.dao.DAOPartes;
import producto2.dominio.Parte;

/**
 *
 * @author jesc
 */
public class ParteConverter implements Converter {
    
    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Parte parte = null;
        try {
            int idParte=1;
            boolean error=false;
            try {
                idParte=Integer.parseInt(value);
            } catch (NumberFormatException e) {
                error=true;
            }
            if( idParte == 0 ) {
                parte=new Parte();
            } else {
                DAOPartes dao=new DAOPartes();
                if(error) {
                    idParte=dao.agregar(value.toUpperCase().trim());
                }
                parte=dao.obtenerParte(idParte);
            }
        } catch(Throwable ex) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages");
            FacesMessage msg = new FacesMessage(bundle.getString("Mensaje_conversion_Parte_getAsObject"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(msg);
        }
        return parte;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String val = null;
        try {
            if (value == null || value.equals("")) {  
                return "0";
            } else {
                Parte parte=(Parte) value;
                val = Integer.toString(parte.getIdParte());
            } 
        } catch(Throwable ex) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages");
            FacesMessage msg = new FacesMessage(bundle.getString("Mensaje_conversion_Parte_getAsString"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(msg);
        }
        return val;
    }
}
