package com.pedidos.kiosco.productos;

import org.jetbrains.annotations.NotNull;
import java.io.Serializable;

public class Productos implements Serializable {

    int idProducto, opciones, estadoProducto;
    String nombreProducto;
    Double precioProducto;
    private final String imgProducto;


    public Productos(int idProducto, String nombreProducto, Double precioProducto, int opciones, String imgProducto, int estadoProducto) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.precioProducto = precioProducto;
        this.opciones = opciones;
        this.imgProducto = imgProducto;
        this.estadoProducto = estadoProducto;

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

    public int getOpciones() {
        return opciones;
    }

    public String getImgProducto() {
        return imgProducto;
    }

    public int getEstadoProducto() {
        return estadoProducto;
    }

    @NotNull
    @Override
    public String toString() {
        return "Productos{" +
                "idProducto=" + idProducto +
                '}';
    }
}