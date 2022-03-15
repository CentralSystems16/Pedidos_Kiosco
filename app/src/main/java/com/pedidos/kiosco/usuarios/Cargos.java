package com.pedidos.kiosco.usuarios;

public class Cargos {

    String nomCargo;
    int idCargo;

    public Cargos(){

    }

    public void setNomCargo(String nomCargo) {
        this.nomCargo = nomCargo;
    }

    public void setIdCargo(int idCargo) {
        this.idCargo = idCargo;
    }

    public int getIdCargo() {
        return idCargo;
    }

    @Override
    public String toString() {
        return idCargo + "-" + nomCargo;
    }
}
