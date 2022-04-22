package com.pedidos.kiosco.model;

public class Clients {

    String nombreCliente, direccion, dui, numero;
    int idCliente;

    public Clients(String nombreCliente, int idCliente, String direccion, String dui, String numero) {

        this.nombreCliente = nombreCliente;
        this.idCliente = idCliente;
        this.direccion = direccion;
        this.dui = dui;
        this.numero = numero;

    }

    public String getNombreCliente() {
        return nombreCliente;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getDui() {
        return dui;
    }

    public String getNumero() {
        return numero;
    }
}
