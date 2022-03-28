package com.pedidos.kiosco.categorias;

import org.jetbrains.annotations.NotNull;

public class Categorias {

    String nombreCategoria;
    int idCategoria, estadoCategoria;
    String imgCategoria;

    public Categorias(){
    }

    public Categorias(int idCategoria, String nombreCategoria, String imgCategoria, int estadoCategoria) {

        this.nombreCategoria = nombreCategoria;
        this.imgCategoria = imgCategoria;
        this.idCategoria = idCategoria;
        this.estadoCategoria = estadoCategoria;

    }

    public String getNombreCategoria() {
        return nombreCategoria;
    }

    public void setNombreCategoria(String nombreCategoria) {
        this.nombreCategoria = nombreCategoria;
    }

    public int getIdCategoria() {
        return idCategoria;
    }

    public void setIdCategoria(int idCategoria) {
        this.idCategoria = idCategoria;
    }

    public String getImgCategoria() {
        return imgCategoria;
    }

    public int getEstadoCategoria() {
        return estadoCategoria;
    }

    @NotNull
    @Override
    public String toString() {

        return idCategoria + "-" + nombreCategoria;
    }
}
