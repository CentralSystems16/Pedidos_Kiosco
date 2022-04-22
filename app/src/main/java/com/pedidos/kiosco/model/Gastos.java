package com.pedidos.kiosco.model;

public class Gastos {

    String fechaCreo, descripcion;
    Double monto;
    int tipoComprobante, idFacMovimiento;

    public Gastos(String fechaCreo, Double monto, int tipoComprobante, String descripcion, int idFacMovimiento) {
        this.fechaCreo = fechaCreo;
        this.monto = monto;
        this.tipoComprobante = tipoComprobante;
        this.descripcion = descripcion;
        this.idFacMovimiento = idFacMovimiento;
    }

    public String getFechaCreo() {
        return fechaCreo;
    }

    public Double getMonto() {
        return monto;
    }

    public int getTipoComprobante() {
        return tipoComprobante;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public int getIdFacMovimiento() {
        return idFacMovimiento;
    }
}
