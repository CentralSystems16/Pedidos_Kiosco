package com.pedidos.kiosco.model;

import java.io.Serializable;

public class Productos implements Serializable {

    int idProducto, cantidad, minimo, maximo;
    String nombreProducto, imgProducto;
    Double precioProducto;

    public Productos(int idProducto, String nombreProducto, Double precioProducto, String imgProducto, int cantidad, int minimo, int maximo) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.precioProducto = precioProducto;
        this.imgProducto = imgProducto;
        this.cantidad = cantidad;
        this.minimo = minimo;
        this.maximo = maximo;

    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public Double getPrecioProducto() {
        return precioProducto;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public String getImgProducto() {
        return imgProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public int getMinimo() {
        return minimo;
    }

    public int getMaximo() {
        return maximo;
    }
}