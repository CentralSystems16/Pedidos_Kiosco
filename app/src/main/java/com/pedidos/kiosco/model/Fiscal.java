package com.pedidos.kiosco.model;

public class Fiscal {

    String nombre, fecha, caja, comprobante, serie, sucursal;
    int idFiscal;

    public Fiscal(String comprobante, String nombre, String caja, String sucursal, int idFiscal, String serie, String fecha) {
        this.nombre = nombre;
        this.fecha = fecha;
        this.caja = caja;
        this.sucursal = sucursal;
        this.idFiscal = idFiscal;
        this.comprobante = comprobante;
        this.serie = serie;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFecha() {
        return fecha;
    }

    public String getCaja() {
        return caja;
    }

    public String getComprobante() {
        return comprobante;
    }

    public String getSerie() {
        return serie;
    }

    public int getIdFiscal() {
        return idFiscal;
    }

    public String getSucursal() {
        return sucursal;
    }
}
