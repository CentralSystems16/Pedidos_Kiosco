package com.pedidos.kiosco.model;

public class Cierre {

    int idCierreCaja, noCaja;
    String fecha, nombreEmpleado;

    public Cierre(int idCierreCaja, int noCaja, String fecha, String nombreEmpleado) {
        this.idCierreCaja = idCierreCaja;
        this.noCaja = noCaja;
        this.fecha = fecha;
        this.nombreEmpleado = nombreEmpleado;
    }

    public int getIdCierreCaja() {
        return idCierreCaja;
    }

    public int getNoCaja() {
        return noCaja;
    }

    public String getFecha() {
        return fecha;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }
}