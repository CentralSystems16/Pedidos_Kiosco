package com.pedidos.kiosco.model;

public class Cierre {

    String fecha, nombreEmpleado;

    public Cierre(String fecha, String nombreEmpleado) {

        this.fecha = fecha;
        this.nombreEmpleado = nombreEmpleado;
    }

    public String getFecha() {
        return fecha;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }
}