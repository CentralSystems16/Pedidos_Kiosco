package com.pedidos.kiosco.model;

import androidx.annotation.NonNull;

public class Reporte {

    public Reporte(){
    }

    String nomReporte;

    public void setNomReporte(String nomReporte) {
        this.nomReporte = nomReporte;
    }

    @NonNull
    @Override
    public String toString() {
        return nomReporte;
    }
}
