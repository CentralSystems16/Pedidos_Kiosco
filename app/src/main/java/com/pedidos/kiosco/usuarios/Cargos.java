package com.pedidos.kiosco.usuarios;

public class Cargos {

    String nomCargo;
    int idCargo;

    public Cargos(){

    }

    public Cargos(String nomCargo, int idCargo) {
        this.nomCargo = nomCargo;
        this.idCargo = idCargo;
    }

    public void setNomCargo(String nomCargo) {
        this.nomCargo = nomCargo;
    }

    public void setIdCargo(int idCargo) {
        this.idCargo = idCargo;
    }

    @Override
    public String toString() {
        return idCargo + "-" + nomCargo;
    }
}
