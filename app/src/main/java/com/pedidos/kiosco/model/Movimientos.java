package com.pedidos.kiosco.model;

public class Movimientos {

    String nombreCliente, nombreSucursal, fechaCreo, numeroComprobante;
    Double exento, gravado, noSujeto;
    int idMov;

    public Movimientos(String nombreCliente, String nombreSucursal, String fechaCreo, Double exento, Double gravado, Double noSujeto, int idMov, String numeroComprobante) {
        this.nombreCliente = nombreCliente;
        this.nombreSucursal = nombreSucursal;
        this.fechaCreo = fechaCreo;
        this.exento = exento;
        this.gravado = gravado;
        this.noSujeto = noSujeto;
        this.idMov = idMov;
        this.numeroComprobante = numeroComprobante;
    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public String getNombreSucursal() {
        return nombreSucursal;
    }

    public String getFechaCreo() {
        return fechaCreo;
    }

    public Double getExento() {
        return exento;
    }

    public Double getGravado() {
        return gravado;
    }

    public Double getNoSujeto() {
        return noSujeto;
    }

    public int getIdMov() {
        return idMov;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }
}
