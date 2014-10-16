package cliente.to;

public class TOClienteSEA {
    private String cod_cli;
    private String cod_gru;
    private String cod_age;
    private String nombre;

    public String getCod_age() {
        return cod_age;
    }

    public void setCod_age(String cod_age) {
        this.cod_age = cod_age;
    }

    public String getCod_cli() {
        return cod_cli;
    }

    public void setCod_cli(String cod_cli) {
        this.cod_cli = cod_cli;
    }

    public String getCod_gru() {
        return cod_gru;
    }

    public void setCod_gru(String cod_gru) {
        this.cod_gru = cod_gru;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final TOClienteSEA other = (TOClienteSEA) obj;
        if ((this.cod_cli == null) ? (other.cod_cli != null) : !this.cod_cli.equals(other.cod_cli)) {
            return false;
        }
        if ((this.cod_gru == null) ? (other.cod_gru != null) : !this.cod_gru.equals(other.cod_gru)) {
            return false;
        }
        if ((this.cod_age == null) ? (other.cod_age != null) : !this.cod_age.equals(other.cod_age)) {
            return false;
        }
        if ((this.nombre == null) ? (other.nombre != null) : !this.nombre.equals(other.nombre)) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 17 * hash + (this.cod_cli != null ? this.cod_cli.hashCode() : 0);
        hash = 17 * hash + (this.cod_gru != null ? this.cod_gru.hashCode() : 0);
        hash = 17 * hash + (this.cod_age != null ? this.cod_age.hashCode() : 0);
        hash = 17 * hash + (this.nombre != null ? this.nombre.hashCode() : 0);
        return hash;
    }
    
    
   
    
}

interface interCliente{
      
    

}

