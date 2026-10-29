package com.example.tienda.dao;

import com.example.tienda.model.Deuda;
import java.util.ArrayList;

public interface IDeuda {
    boolean crear(Deuda d);
    Deuda buscar(int id);
    boolean actualizarEstadoPago(int id, String estado);
    boolean eliminar(int id);
    ArrayList<Deuda> listar();
    double obtenerMontoTotalPendiente();
}
