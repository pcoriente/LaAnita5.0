package clientes.converters;

import contribuyentes.Contribuyente;
import contribuyentes.DAOContribuyentes;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

/**
 *
 * @author jesc
 */
public class MiniClienteConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        Contribuyente cliente = null;
        try {
            int idContribuyente=Integer.parseInt(value);
            if(idContribuyente == 0) {
                cliente=new Contribuyente();
            } else {
                DAOContribuyentes dao=new DAOContribuyentes();
                cliente=dao.obtenerContribuyente(idContribuyente);
            }
        } catch(Throwable ex) {
            System.err.println(ex);
//            ResourceBundle bundle = ResourceBundle.getBundle("messages");
//            FacesMessage msg = new FacesMessage(bundle.getString("Mensaje_conversion_MiniProveedor_getAsObject"));
//            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
//            throw new ConverterException(msg);
        }
        return cliente;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String val = null;
        try {
            Contribuyente cliente = (Contribuyente) value;
            val = Integer.toString(cliente.getIdContribuyente());
        } catch(Throwable ex) {
         //   ResourceBundle bundle = ResourceBundle.getBundle("messages");
        //    FacesMessage msg = new FacesMessage(bundle.getString("Mensaje_conversion_MiniProveedor_getAsString"));
        //    msg.setSeverity(FacesMessage.SEVERITY_ERROR);
        //    throw new ConverterException(msg);
        }
        return val;
    }
    
}
