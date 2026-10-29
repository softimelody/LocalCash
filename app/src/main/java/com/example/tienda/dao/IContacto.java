package com.example.tienda.dao;

import com.example.tienda.model.Contacto;
import java.util.ArrayList;

public interface IContacto {
    boolean crear(Contacto c);
    Contacto buscar(int id);
    boolean actualizar(Contacto c);
    boolean eliminar(int id);
    ArrayList<Contacto> listar();
    void cargaInicial();
}
