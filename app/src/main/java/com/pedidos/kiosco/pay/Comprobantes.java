package com.pedidos.kiosco.pay;

import androidx.annotation.NonNull;

public class Comprobantes {

    int idComprobante;
    String comprobante;

    public Comprobantes(int idComprobante, String comprobante) {
        this.idComprobante = idComprobante;
        this.comprobante = comprobante;
    }

    public Comprobantes() {

    }

    public int getIdComprobante() {
        return idComprobante;
    }

    public String getComprobante() {
        return comprobante;
    }

    public void setIdComprobante(int idComprobante) {
        this.idComprobante = idComprobante;
    }

    public void setComprobante(String comprobante) {
        this.comprobante = comprobante;
    }

    @NonNull
    @Override
    public String toString() {
        return comprobante;

    }
}
