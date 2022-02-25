package com.pedidos.kiosco.model;

import android.widget.ImageView;

public class DetReporte {

    int idDetPedido;
    String nombreProducto;
    double cantiProd;
    Double precioVenta, monto, montoIva;
    String imgProducto;

    public DetReporte(int idDetPedido, String nombreProducto, double cantiProd, Double precioVenta, Double monto, Double montoIva, String imgProducto) {
        this.idDetPedido = idDetPedido;
        this.nombreProducto = nombreProducto;
        this.cantiProd = cantiProd;
        this.precioVenta = precioVenta;
        this.monto = monto;
        this.montoIva = montoIva;
        this.imgProducto = imgProducto;
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

    public String getImgProducto() {
        return imgProducto;
    }
}
