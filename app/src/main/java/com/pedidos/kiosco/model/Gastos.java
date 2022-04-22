package com.pedidos.kiosco.model;

public class Gastos {

    String fechaCreo;
    Double monto;

    public Gastos(String fechaCreo, Double monto) {
        this.fechaCreo = fechaCreo;
        this.monto = monto;
    }

    public String getFechaCreo() {
        return fechaCreo;
    }

    public Double getMonto() {
        return monto;
    }
}
