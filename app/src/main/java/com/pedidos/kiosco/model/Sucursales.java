package com.pedidos.kiosco.model;

public class Sucursales {

    String nomSucursal;
    int idSucursal, ocupada;

    public Sucursales() {

    }

    public String getNomSucursal() {
        return nomSucursal;
    }

    public void setNomSucursal(String nomSucursal) {
        this.nomSucursal = nomSucursal;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }



    @Override
    public String toString() {
        return nomSucursal;

    }
}
