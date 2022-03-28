package com.pedidos.kiosco.model;

public class Caja {

    int idCaja, ocupada, numeroCaja;
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

    public int getNumeroCaja() {
        return numeroCaja;
    }

    public void setNumeroCaja(int numeroCaja) {
        this.numeroCaja = numeroCaja;
    }

    public String getNombreCaja() {
        return nombreCaja;
    }

    public void setIdCaja(int idCaja) {
        this.idCaja = idCaja;
    }

    public void setNombreCaja(String nombreCaja) {
        this.nombreCaja = nombreCaja;
    }

    public int getOcupada() {
        return ocupada;
    }

    @Override
    public String toString() {
        return nombreCaja;

    }
}
