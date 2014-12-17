package formulas.dominio;

import java.util.ArrayList;

/**
 *
 * @author jesc
 */
public class Formula {
    private int idFormula;
    private int idEmpresa;
    private int idEmpaque;
    private String cod_pro;
    private String empaque;
    private double merma;
    private double costoPromedio;
    private String observaciones;
    private ArrayList<Insumo> insumos;
    private double sumaCantidad;
    private double sumaCosto;
    
    public Formula() {
        this.cod_pro="";
        this.empaque="";
        this.observaciones="";
        this.insumos=new ArrayList<Insumo>();
    }

    public int getIdFormula() {
        return idFormula;
    }

    public void setIdFormula(int idFormula) {
        this.idFormula = idFormula;
    }

    public int getIdEmpresa() {
        return idEmpresa;
    }

    public void setIdEmpresa(int idEmpresa) {
        this.idEmpresa = idEmpresa;
    }

    public int getIdEmpaque() {
        return idEmpaque;
    }

    public void setIdEmpaque(int idEmpaque) {
        this.idEmpaque = idEmpaque;
    }

    public String getCod_pro() {
        return cod_pro;
    }

    public void setCod_pro(String cod_pro) {
        this.cod_pro = cod_pro;
    }

    public String getEmpaque() {
        return empaque;
    }

    public void setEmpaque(String empaque) {
        this.empaque = empaque;
    }

    public double getMerma() {
        return merma;
    }

    public void setMerma(double merma) {
        this.merma = merma;
    }

    public double getCostoPromedio() {
        return costoPromedio;
    }

    public void setCostoPromedio(double costoPromedio) {
        this.costoPromedio = costoPromedio;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }

    public ArrayList<Insumo> getInsumos() {
        return insumos;
    }

    public void setInsumos(ArrayList<Insumo> insumos) {
        this.insumos = insumos;
    }

    public double getSumaCantidad() {
        return sumaCantidad;
    }

    public void setSumaCantidad(double sumaCantidad) {
        this.sumaCantidad = sumaCantidad;
    }

    public double getSumaCosto() {
        return sumaCosto;
    }

    public void setSumaCosto(double sumaCosto) {
        this.sumaCosto = sumaCosto;
    }
}
