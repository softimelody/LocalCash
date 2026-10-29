package com.example.tienda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tienda.database.DatabaseHelper;
import com.example.tienda.model.Movimiento;

import java.util.ArrayList;

public class MovimientoDAO implements IMovimiento {

    public DatabaseHelper dbHelper;

    public MovimientoDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public boolean crear(Movimiento m) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_MOV_ID_CUENTA, m.getIdCuenta());
        cv.put(DatabaseHelper.COL_MOV_TIPO, m.getTipo());
        cv.put(DatabaseHelper.COL_MOV_MONTO, m.getMonto());
        cv.put(DatabaseHelper.COL_MOV_FECHA, m.getFecha());
        cv.put(DatabaseHelper.COL_MOV_DESCRIPCION, m.getDescripcion());
        cv.put(DatabaseHelper.COL_MOV_LATITUD, m.getLatitud());
        cv.put(DatabaseHelper.COL_MOV_LONGITUD, m.getLongitud());

        long result = db.insert(DatabaseHelper.TABLE_MOVIMIENTOS, null, cv);
        db.close();
        return result != -1;
    }

    @Override
    public Movimiento buscar(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Movimiento m = null;
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_MOVIMIENTOS + " WHERE " + DatabaseHelper.COL_MOV_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        try {
            if (cursor.moveToFirst()) {
                int idCuenta = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOV_ID_CUENTA));
                String tipo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOV_TIPO));
                double monto = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOV_MONTO));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOV_FECHA));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOV_DESCRIPCION));
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOV_LATITUD));
                double lon = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOV_LONGITUD));
                m = new Movimiento(id, idCuenta, tipo, monto, fecha, desc, lat, lon);
            }
        } finally {
            cursor.close();
            db.close();
        }
        return m;
    }

    @Override
    public boolean actualizar(Movimiento m) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_MOV_ID_CUENTA, m.getIdCuenta());
        cv.put(DatabaseHelper.COL_MOV_TIPO, m.getTipo());
        cv.put(DatabaseHelper.COL_MOV_MONTO, m.getMonto());
        cv.put(DatabaseHelper.COL_MOV_FECHA, m.getFecha());
        cv.put(DatabaseHelper.COL_MOV_DESCRIPCION, m.getDescripcion());
        cv.put(DatabaseHelper.COL_MOV_LATITUD, m.getLatitud());
        cv.put(DatabaseHelper.COL_MOV_LONGITUD, m.getLongitud());

        int resultado = db.update(
                DatabaseHelper.TABLE_MOVIMIENTOS,
                cv,
                DatabaseHelper.COL_MOV_ID + " = ?",
                new String[]{String.valueOf(m.getId())}
        );
        db.close();
        return resultado > 0;
    }

    @Override
    public boolean eliminar(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int resultado = db.delete(
                DatabaseHelper.TABLE_MOVIMIENTOS,
                DatabaseHelper.COL_MOV_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return resultado > 0;
    }

    @Override
    public ArrayList<Movimiento> listar() {
        ArrayList<Movimiento> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_MOVIMIENTOS + " ORDER BY " + DatabaseHelper.COL_MOV_ID + " DESC", null);
        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOV_ID));
                int idCuenta = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOV_ID_CUENTA));
                String tipo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOV_TIPO));
                double monto = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOV_MONTO));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOV_FECHA));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOV_DESCRIPCION));
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOV_LATITUD));
                double lon = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOV_LONGITUD));
                lista.add(new Movimiento(id, idCuenta, tipo, monto, fecha, desc, lat, lon));
            }
        } finally {
            cursor.close();
            db.close();
        }
        return lista;
    }

    @Override
    public ArrayList<Movimiento> listarPorCuenta(int idCuenta) {
        ArrayList<Movimiento> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_MOVIMIENTOS + " WHERE " + DatabaseHelper.COL_MOV_ID_CUENTA + " = ? ORDER BY " + DatabaseHelper.COL_MOV_ID + " DESC",
                new String[]{String.valueOf(idCuenta)}
        );
        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOV_ID));
                String tipo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOV_TIPO));
                double monto = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOV_MONTO));
                String fecha = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOV_FECHA));
                String desc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOV_DESCRIPCION));
                double lat = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOV_LATITUD));
                double lon = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_MOV_LONGITUD));
                lista.add(new Movimiento(id, idCuenta, tipo, monto, fecha, desc, lat, lon));
            }
        } finally {
            cursor.close();
            db.close();
        }
        return lista;
    }

    @Override
    public void cargaInicial() {
        // No insertar movimientos por defecto para trabajar con datos reales creados por el usuario
    }
}
