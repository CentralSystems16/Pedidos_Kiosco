package com.pedidos.kiosco.model;

public class Caja {

    int idCaja;
    String nombreCaja;

    public Caja(int idCaja, String nombreCaja) {
        this.idCaja = idCaja;
        this.nombreCaja = nombreCaja;
    }

    public Caja(){

    }


    public int getIdCaja() {
        return idCaja;
    }

    public void setIdCaja(int idCaja) {
        this.idCaja = idCaja;
    }

    public String getNombreCaja() {
        return nombreCaja;
    }

    public void setNombreCaja(String nombreCaja) {
        this.nombreCaja = nombreCaja;
    }


    @Override
    public String toString() {
        return nombreCaja;

    }
}
