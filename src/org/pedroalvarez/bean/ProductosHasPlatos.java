package org.pedroalvarez.bean;

public class ProductosHasPlatos {
    
    private int codigoProducto;
    private int codigoPlato;

    public ProductosHasPlatos() {
    }

    public ProductosHasPlatos(int codigoProducto, int codigoPlato) {
        this.codigoProducto = codigoProducto;
        this.codigoPlato = codigoPlato;
    }

    public int getCodigoProducto() {
        return codigoProducto;
    }

    public void setCodigoProducto(int codigoProducto) {
        this.codigoProducto = codigoProducto;
    }

    public int getCodigoPlato() {
        return codigoPlato;
    }

    public void setCodigoPlato(int codigoPlato) {
        this.codigoPlato = codigoPlato;
    }
}