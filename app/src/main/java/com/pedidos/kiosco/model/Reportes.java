package com.pedidos.kiosco.model;

public class Reportes {

    public String nombre;
    String fecha, fechaFinalizo;
    int pedido, idCliente, estado;
    Double total;

    public Reportes(String nombre, String fecha, int pedido, int idCliente, int estado, String fechaFinalizo) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.pedido = pedido;
        this.idCliente = idCliente;
        this.estado = estado;
        this.fechaFinalizo = fechaFinalizo;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public int getPedido() {
        return pedido;
    }

    public void setPedido(int pedido) {
        this.pedido = pedido;
    }

    public Double getTotal() {
        return total;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public int getEstado() {
        return estado;
    }

    public String getFechaFinalizo() {
        return fechaFinalizo;
    }
}
