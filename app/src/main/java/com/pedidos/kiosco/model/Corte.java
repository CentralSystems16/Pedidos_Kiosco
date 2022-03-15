package com.pedidos.kiosco.model;

import androidx.annotation.NonNull;

public class Corte {

    public Corte(){
    }

    int IdCierreCaja;
    String nombreCajero;

    public int getIdCierreCaja() {
        return IdCierreCaja;
    }

    public void setIdCierreCaja(int idCierreCaja) {
        IdCierreCaja = idCierreCaja;
    }

    public String getNombreCajero() {
        return nombreCajero;
    }

    public void setNombreCajero(String nombreCajero) {
        this.nombreCajero = nombreCajero;
    }

    @NonNull
    @Override
    public String toString() {
        return IdCierreCaja + " - " + nombreCajero;
    }
}
