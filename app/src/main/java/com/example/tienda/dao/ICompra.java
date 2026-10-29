package com.example.tienda.dao;

import com.example.tienda.model.Compra;
import java.util.ArrayList;

public interface ICompra {
    boolean crear(Compra c);
    Compra buscar(int id);
    boolean actualizar(Compra c);
    boolean eliminar(int id);
    ArrayList<Compra> listar();
    ArrayList<Compra> listarProgramadas();
    boolean completarCompra(int id);
}
