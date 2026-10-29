package com.example.tienda.dao;

import com.example.tienda.model.Movimiento;
import java.util.ArrayList;

public interface IMovimiento {
    boolean crear(Movimiento m);
    Movimiento buscar(int id);
    boolean actualizar(Movimiento m);
    boolean eliminar(int id);
    ArrayList<Movimiento> listar();
    ArrayList<Movimiento> listarPorCuenta(int idCuenta);
    void cargaInicial();
}
