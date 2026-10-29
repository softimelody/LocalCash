package com.example.tienda.dao;

import com.example.tienda.model.Producto;

import java.util.ArrayList;

public interface IProducto {
    boolean crear(Producto p);
    Producto buscar(int id);
    boolean actualizar(Producto producto);
    boolean eliminar(int id);
    ArrayList<Producto> listar();
    void cargaMasiva();
}
