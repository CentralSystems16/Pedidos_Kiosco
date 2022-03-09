package com.pedidos.kiosco.model;

public class Corte {

    public Corte(){
    }

    int IdCierreCaja;

    public void setIdCierreCaja(int idCierreCaja) {
        IdCierreCaja = idCierreCaja;
    }

    @Override
    public String toString() {
        return String.valueOf(IdCierreCaja);
    }
}
