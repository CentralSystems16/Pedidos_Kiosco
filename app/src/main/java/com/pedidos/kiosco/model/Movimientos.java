package com.pedidos.kiosco.model;

public class Movimientos {

    String nombreCliente, nombreSucursal, fechaCreo, numeroComprobante, tipoPago;
    Double exento, gravado, noSujeto;
    int idMov, idCliente, idPrefactura;

    public Movimientos(String nombreCliente, String nombreSucursal, String fechaCreo, Double exento, Double gravado, Double noSujeto, int idMov, String numeroComprobante, String tipoPago, int idCliente, int idPrefactura) {
        this.nombreCliente = nombreCliente;
        this.nombreSucursal = nombreSucursal;
        this.fechaCreo = fechaCreo;
        this.exento = exento;
        this.gravado = gravado;
        this.noSujeto = noSujeto;
        this.idMov = idMov;
        this.numeroComprobante = numeroComprobante;
        this.tipoPago = tipoPago;
        this.idCliente = idCliente;
        this.idPrefactura = idPrefactura;
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

    public int getIdMov() {
        return idMov;
    }

    public String getNumeroComprobante() {
        return numeroComprobante;
    }

    public String getTipoPago() {
        return tipoPago;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public int getIdPrefactura() {
        return idPrefactura;
    }
}
