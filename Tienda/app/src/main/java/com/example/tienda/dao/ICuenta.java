package com.example.tienda.dao;

import com.example.tienda.model.Cuenta;
import java.util.ArrayList;

public interface ICuenta {
    boolean crear(Cuenta c);
    Cuenta buscar(int id);
    Cuenta buscarPorNumero(String numeroCuenta);
    boolean actualizar(Cuenta c);
    boolean eliminar(int id);
    ArrayList<Cuenta> listar();
    void cargaInicial();
}
