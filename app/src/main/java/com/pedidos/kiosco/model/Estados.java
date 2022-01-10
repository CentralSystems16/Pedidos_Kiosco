package com.pedidos.kiosco.model;

public class Estados {

    String nomEstado, imgEstado;
    int idEstado;

    public Estados(String nomEstado, int idEstado, String imgEstado) {
        this.nomEstado = nomEstado;
        this.idEstado = idEstado;
        this.imgEstado = imgEstado;
    }

    public String getNomEstado() {
        return nomEstado;
    }

    public int getIdEstado() {
        return idEstado;
    }

    public String getImgEstado() {
        return imgEstado;
    }
}
