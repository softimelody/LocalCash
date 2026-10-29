package com.example.tienda.model;

public class Compra {
    private int id;
    private int idProducto;
    private String nombreProducto;
    private int cantidad;
    private double precioTotal;
    private String fecha;
    private String fechaProgramada;
    private boolean esProgramada;
    private String estado;

    public Compra() {
    }

    public Compra(int idProducto, String nombreProducto, int cantidad, double precioTotal, String fecha, String fechaProgramada, boolean esProgramada, String estado) {
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioTotal = precioTotal;
        this.fecha = fecha;
        this.fechaProgramada = fechaProgramada;
        this.esProgramada = esProgramada;
        this.estado = estado;
    }

    public Compra(int id, int idProducto, String nombreProducto, int cantidad, double precioTotal, String fecha, String fechaProgramada, boolean esProgramada, String estado) {
        this.id = id;
        this.idProducto = idProducto;
        this.nombreProducto = nombreProducto;
        this.cantidad = cantidad;
        this.precioTotal = precioTotal;
        this.fecha = fecha;
        this.fechaProgramada = fechaProgramada;
        this.esProgramada = esProgramada;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdProducto() {
        return idProducto;
    }

    public void setIdProducto(int idProducto) {
        this.idProducto = idProducto;
    }

    public String getNombreProducto() {
        return nombreProducto;
    }

    public void setNombreProducto(String nombreProducto) {
        this.nombreProducto = nombreProducto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getFechaProgramada() {
        return fechaProgramada;
    }

    public void setFechaProgramada(String fechaProgramada) {
        this.fechaProgramada = fechaProgramada;
    }

    public boolean isEsProgramada() {
        return esProgramada;
    }

    public void setEsProgramada(boolean esProgramada) {
        this.esProgramada = esProgramada;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
