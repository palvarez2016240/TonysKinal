package org.pedroalvarez.bean;

public class ServiciosHasPlatos {
    
    private int codigoServicio;
    private int codigoPlato;

    public ServiciosHasPlatos() {
    }

    public ServiciosHasPlatos(int codigoServicio, int codigoPlato) {
        this.codigoServicio = codigoServicio;
        this.codigoPlato = codigoPlato;
    }

    public int getCodigoServicio() {
        return codigoServicio;
    }

    public void setCodigoServicio(int codigoServicio) {
        this.codigoServicio = codigoServicio;
    }

    public int getCodigoPlato() {
        return codigoPlato;
    }

    public void setCodigoPlato(int codigoPlato) {
        this.codigoPlato = codigoPlato;
    }
}