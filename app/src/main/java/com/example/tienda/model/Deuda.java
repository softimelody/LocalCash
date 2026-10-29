package com.example.tienda.model;

public class Deuda {
    private int id;
    private int idContacto;
    private String nombreContacto;
    private double montoTotal;
    private int numPersonas;
    private double montoPorPersona;
    private String descripcion;
    private String fecha;
    private String estadoPago;

    public Deuda() {
    }

    public Deuda(int idContacto, String nombreContacto, double montoTotal, int numPersonas, double montoPorPersona, String descripcion, String fecha, String estadoPago) {
        this.idContacto = idContacto;
        this.nombreContacto = nombreContacto;
        this.montoTotal = montoTotal;
        this.numPersonas = numPersonas;
        this.montoPorPersona = montoPorPersona;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.estadoPago = estadoPago;
    }

    public Deuda(int id, int idContacto, String nombreContacto, double montoTotal, int numPersonas, double montoPorPersona, String descripcion, String fecha, String estadoPago) {
        this.id = id;
        this.idContacto = idContacto;
        this.nombreContacto = nombreContacto;
        this.montoTotal = montoTotal;
        this.numPersonas = numPersonas;
        this.montoPorPersona = montoPorPersona;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.estadoPago = estadoPago;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdContacto() {
        return idContacto;
    }

    public void setIdContacto(int idContacto) {
        this.idContacto = idContacto;
    }

    public String getNombreContacto() {
        return nombreContacto;
    }

    public void setNombreContacto(String nombreContacto) {
        this.nombreContacto = nombreContacto;
    }

    public double getMontoTotal() {
        return montoTotal;
    }

    public void setMontoTotal(double montoTotal) {
        this.montoTotal = montoTotal;
    }

    public int getNumPersonas() {
        return numPersonas;
    }

    public void setNumPersonas(int numPersonas) {
        this.numPersonas = numPersonas;
    }

    public double getMontoPorPersona() {
        return montoPorPersona;
    }

    public void setMontoPorPersona(double montoPorPersona) {
        this.montoPorPersona = montoPorPersona;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstadoPago() {
        return estadoPago;
    }

    public void setEstadoPago(String estadoPago) {
        this.estadoPago = estadoPago;
    }
}
