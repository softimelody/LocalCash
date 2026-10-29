package com.example.tienda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tienda.database.DatabaseHelper;
import com.example.tienda.model.Deuda;

import java.util.ArrayList;

public class DeudaDAO implements IDeuda {

    private final DatabaseHelper dbHelper;

    public DeudaDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public boolean crear(Deuda d) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_DEUDA_ID_CONTACTO, d.getIdContacto());
        cv.put(DatabaseHelper.COL_DEUDA_NOMBRE_CONTACTO, d.getNombreContacto());
        cv.put(DatabaseHelper.COL_DEUDA_MONTO_TOTAL, d.getMontoTotal());
        cv.put(DatabaseHelper.COL_DEUDA_NUM_PERSONAS, d.getNumPersonas());
        cv.put(DatabaseHelper.COL_DEUDA_MONTO_POR_PERSONA, d.getMontoPorPersona());
        cv.put(DatabaseHelper.COL_DEUDA_DESCRIPCION, d.getDescripcion());
        cv.put(DatabaseHelper.COL_DEUDA_FECHA, d.getFecha());
        cv.put(DatabaseHelper.COL_DEUDA_ESTADO_PAGO, d.getEstadoPago());

        long res = db.insert(DatabaseHelper.TABLE_DEUDAS, null, cv);
        db.close();
        return res != -1;
    }

    @Override
    public Deuda buscar(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Deuda d = null;
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_DEUDAS + " WHERE " + DatabaseHelper.COL_DEUDA_ID + " = ?", new String[]{String.valueOf(id)});
        try {
            if (cursor.moveToFirst()) {
                d = parseDeuda(cursor);
            }
        } finally {
            cursor.close();
            db.close();
        }
        return d;
    }

    @Override
    public boolean actualizarEstadoPago(int id, String estado) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_DEUDA_ESTADO_PAGO, estado);

        int res = db.update(DatabaseHelper.TABLE_DEUDAS, cv, DatabaseHelper.COL_DEUDA_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return res > 0;
    }

    @Override
    public boolean eliminar(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int res = db.delete(DatabaseHelper.TABLE_DEUDAS, DatabaseHelper.COL_DEUDA_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
        return res > 0;
    }

    @Override
    public ArrayList<Deuda> listar() {
        ArrayList<Deuda> lista = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_DEUDAS + " ORDER BY " + DatabaseHelper.COL_DEUDA_ID + " DESC", null);
        try {
            while (cursor.moveToNext()) {
                lista.add(parseDeuda(cursor));
            }
        } finally {
            cursor.close();
            db.close();
        }
        return lista;
    }

    @Override
    public double obtenerMontoTotalPendiente() {
        double total = 0;
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery(
                "SELECT SUM(" + DatabaseHelper.COL_DEUDA_MONTO_POR_PERSONA + ") FROM " + DatabaseHelper.TABLE_DEUDAS + " WHERE " + DatabaseHelper.COL_DEUDA_ESTADO_PAGO + " = 'PENDIENTE'",
                null
        );
        try {
            if (cursor.moveToFirst()) {
                total = cursor.getDouble(0);
            }
        } finally {
            cursor.close();
            db.close();
        }
        return total;
    }

    private Deuda parseDeuda(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DEUDA_ID));
        int idContacto = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DEUDA_ID_CONTACTO));
        String nom = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DEUDA_NOMBRE_CONTACTO));
        double montoTotal = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DEUDA_MONTO_TOTAL));
        int numPersonas = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DEUDA_NUM_PERSONAS));
        double montoPorPersona = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DEUDA_MONTO_POR_PERSONA));
        String desc = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DEUDA_DESCRIPCION));
        String fecha = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DEUDA_FECHA));
        String estado = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_DEUDA_ESTADO_PAGO));

        return new Deuda(id, idContacto, nom, montoTotal, numPersonas, montoPorPersona, desc, fecha, estado);
    }
}
