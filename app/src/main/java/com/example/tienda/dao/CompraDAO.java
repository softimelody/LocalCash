package com.example.tienda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tienda.database.DatabaseHelper;
import com.example.tienda.model.Compra;

import java.util.ArrayList;

public class CompraDAO implements ICompra {

    private final DatabaseHelper dbHelper;

    public CompraDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public boolean crear(Compra c) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_COMPRA_ID_PRODUCTO, c.getIdProducto());
        cv.put(DatabaseHelper.COL_COMPRA_NOMBRE_PROD, c.getNombreProducto());
        cv.put(DatabaseHelper.COL_COMPRA_CANTIDAD, c.getCantidad());
        cv.put(DatabaseHelper.COL_COMPRA_PRECIO_TOTAL, c.getPrecioTotal());
        cv.put(DatabaseHelper.COL_COMPRA_FECHA, c.getFecha());
        cv.put(DatabaseHelper.COL_COMPRA_FECHA_PROG, c.getFechaProgramada());
        cv.put(DatabaseHelper.COL_COMPRA_ES_PROG, c.isEsProgramada() ? 1 : 0);
        cv.put(DatabaseHelper.COL_COMPRA_ESTADO, c.getEstado());

        long result = db.insert(DatabaseHelper.TABLE_COMPRAS, null, cv);
        db.close();
        return result != -1;
    }

    @Override
    public Compra buscar(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Compra c = null;
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_COMPRAS + " WHERE " + DatabaseHelper.COL_COMPRA_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        try {
            if (cursor.moveToFirst()) {
                c = parseCompra(cursor);
            }
        } finally {
            cursor.close();
            db.close();
        }
        return c;
    }

    @Override
    public boolean actualizar(Compra c) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_COMPRA_ID_PRODUCTO, c.getIdProducto());
        cv.put(DatabaseHelper.COL_COMPRA_NOMBRE_PROD, c.getNombreProducto());
        cv.put(DatabaseHelper.COL_COMPRA_CANTIDAD, c.getCantidad());
        cv.put(DatabaseHelper.COL_COMPRA_PRECIO_TOTAL, c.getPrecioTotal());
        cv.put(DatabaseHelper.COL_COMPRA_FECHA, c.getFecha());
        cv.put(DatabaseHelper.COL_COMPRA_FECHA_PROG, c.getFechaProgramada());
        cv.put(DatabaseHelper.COL_COMPRA_ES_PROG, c.isEsProgramada() ? 1 : 0);
        cv.put(DatabaseHelper.COL_COMPRA_ESTADO, c.getEstado());

        int res = db.update(DatabaseHelper.TABLE_COMPRAS, cv, DatabaseHelper.COL_COMPRA_ID + " = ?", new String[]{String.valueOf(c.getId())});
        db.close();
        return res > 0;
    }

    @Override
    public boolean eliminar(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int res = db.delete(DatabaseHelper.TABLE_COMPRAS, DatabaseHelper.COL_COMPRA_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return res > 0;
    }

    @Override
    public ArrayList<Compra> listar() {
        ArrayList<Compra> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_COMPRAS + " ORDER BY " + DatabaseHelper.COL_COMPRA_ID + " DESC", null);
        try {
            while (cursor.moveToNext()) {
                lista.add(parseCompra(cursor));
            }
        } finally {
            cursor.close();
            db.close();
        }
        return lista;
    }

    @Override
    public ArrayList<Compra> listarProgramadas() {
        ArrayList<Compra> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_COMPRAS + " WHERE " + DatabaseHelper.COL_COMPRA_ES_PROG + " = 1 ORDER BY " + DatabaseHelper.COL_COMPRA_ID + " DESC",
                null
        );
        try {
            while (cursor.moveToNext()) {
                lista.add(parseCompra(cursor));
            }
        } finally {
            cursor.close();
            db.close();
        }
        return lista;
    }

    @Override
    public boolean completarCompra(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_COMPRA_ESTADO, "COMPLETADO");
        cv.put(DatabaseHelper.COL_COMPRA_ES_PROG, 0);

        int res = db.update(DatabaseHelper.TABLE_COMPRAS, cv, DatabaseHelper.COL_COMPRA_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return res > 0;
    }

    private Compra parseCompra(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COMPRA_ID));
        int idProd = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COMPRA_ID_PRODUCTO));
        String nom = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COMPRA_NOMBRE_PROD));
        int cant = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COMPRA_CANTIDAD));
        double precio = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COMPRA_PRECIO_TOTAL));
        String fecha = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COMPRA_FECHA));
        String fechaProg = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COMPRA_FECHA_PROG));
        boolean esProg = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COMPRA_ES_PROG)) == 1;
        String estado = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_COMPRA_ESTADO));

        return new Compra(id, idProd, nom, cant, precio, fecha, fechaProg, esProg, estado);
    }
}
