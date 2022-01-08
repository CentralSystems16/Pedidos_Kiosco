package com.pedidos.kiosco.model;

public class DetReporte {

    int idDetPedido;
    String nombreProducto;
    double cantiProd;
    Double precioVenta, monto, montoIva;

    public DetReporte(int idDetPedido, String nombreProducto, double cantiProd, Double precioVenta, Double monto, Double montoIva) {
        this.idDetPedido = idDetPedido;
        this.nombreProducto = nombreProducto;
        this.cantiProd = cantiProd;
        this.precioVenta = precioVenta;
        this.monto = monto;
        this.montoIva = montoIva;
    }

    public int getIdDetPedido() {
        return idDetPedido;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public double getCantiProd() {
        return cantiProd;
    }

    public void setCantiProd(double cantiProd) {
        this.cantiProd = cantiProd;
    }

    public Double getPrecioVenta() {
        return precioVenta;
    }

    public Double getMonto() {
        return monto;
    }

    public void setMonto(Double monto) {
        this.monto = monto;
    }

    public Double getMontoIva() {
        return montoIva;
    }

    public void setMontoIva(Double montoIva) {
        this.montoIva = montoIva;
    }

}
