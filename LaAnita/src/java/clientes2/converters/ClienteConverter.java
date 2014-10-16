package clientes2.converters;

import clientes2.dao.DAOClientes;
import clientes2.dominio.ClienteSEA;
import java.util.ResourceBundle;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.ConverterException;

public class ClienteConverter implements Converter {

    @Override
    public Object getAsObject(FacesContext context, UIComponent component, String value) {
        //System.out.println("Valor de VALUE:"+value);
        ClienteSEA cs = null;
        try {
            int cod_cli = Integer.parseInt(value);
            if (cod_cli == 0) {
                cs = new ClienteSEA();
                cs.setCod_cli(0);
                cs.setNombre("Seleccione UN CLIENTE: ");
            } else {
            //S    cs.setCod_cli(cod_cli);
                DAOClientes dao = new DAOClientes();
                cs = dao.obtenerClienteSEA(cod_cli);
            }

        } catch (Throwable ex) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages");
            FacesMessage msg = new FacesMessage(bundle.getString("Mensaje_conversion_Cliente_getAsObject"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(msg);
        }
        System.out.println("El CLIENTE  de la clase converter es " + cs.getNombre()+cs.getCod_cli());
        return cs;
    }

    @Override
    public String getAsString(FacesContext context, UIComponent component, Object value) {
        String val;
        try {
            ClienteSEA cs = (ClienteSEA) value;
            val = Integer.toString(cs.getCod_cli());
        } catch (Throwable ex) {
            ResourceBundle bundle = ResourceBundle.getBundle("messages");
            FacesMessage msg = new FacesMessage(bundle.getString("Mensaje_conversion_Cliente_getAsString"));
            msg.setSeverity(FacesMessage.SEVERITY_ERROR);
            throw new ConverterException(msg);
        }
        return val;
    }
}
