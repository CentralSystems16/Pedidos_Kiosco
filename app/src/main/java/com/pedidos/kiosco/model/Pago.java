package com.pedidos.kiosco.model;

public class Pago {

    int idPago, activo;
    String nombrePago;
    String imgPago;

    public Pago(int idPago, String nombrePago, int activo, String imgPago) {
        this.idPago = idPago;
        this.activo = activo;
        this.nombrePago = nombrePago;
        this.imgPago = imgPago;
    }

    public int getIdPago() {
        return idPago;
    }

    public int getActivo() {
        return activo;
    }

    public String getNombrePago() {
        return nombrePago;
    }

    public String getImgPago() {
        return imgPago;
    }
}
