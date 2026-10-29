package com.example.tienda.model;

public class Contacto {
    private int id;
    private String nombre;
    private String telefono;
    private String aliasCbu;

    public Contacto() {
    }

    public Contacto(int id, String nombre, String telefono, String aliasCbu) {
        this.id = id;
        this.nombre = nombre;
        this.telefono = telefono;
        this.aliasCbu = aliasCbu;
    }

    public Contacto(String nombre, String telefono, String aliasCbu) {
        this.nombre = nombre;
        this.telefono = telefono;
        this.aliasCbu = aliasCbu;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getAliasCbu() {
        return aliasCbu;
    }

    public void setAliasCbu(String aliasCbu) {
        this.aliasCbu = aliasCbu;
    }
}
