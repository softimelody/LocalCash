package com.example.tienda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tienda.database.DatabaseHelper;
import com.example.tienda.model.Contacto;

import java.util.ArrayList;

public class ContactoDAO implements IContacto {

    public DatabaseHelper dbHelper;

    public ContactoDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public boolean crear(Contacto c) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_CONTACTO_NOMBRE, c.getNombre());
        cv.put(DatabaseHelper.COL_CONTACTO_TELEFONO, c.getTelefono());
        cv.put(DatabaseHelper.COL_CONTACTO_ALIAS, c.getAliasCbu());

        long result = db.insert(DatabaseHelper.TABLE_CONTACTOS, null, cv);
        db.close();
        return result != -1;
    }

    @Override
    public Contacto buscar(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Contacto contacto = null;
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_CONTACTOS + " WHERE " + DatabaseHelper.COL_CONTACTO_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        try {
            if (cursor.moveToFirst()) {
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CONTACTO_NOMBRE));
                String telefono = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CONTACTO_TELEFONO));
                String alias = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CONTACTO_ALIAS));
                contacto = new Contacto(id, nombre, telefono, alias);
            }
        } finally {
            cursor.close();
            db.close();
        }
        return contacto;
    }

    @Override
    public boolean actualizar(Contacto c) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_CONTACTO_NOMBRE, c.getNombre());
        cv.put(DatabaseHelper.COL_CONTACTO_TELEFONO, c.getTelefono());
        cv.put(DatabaseHelper.COL_CONTACTO_ALIAS, c.getAliasCbu());

        int resultado = db.update(
                DatabaseHelper.TABLE_CONTACTOS,
                cv,
                DatabaseHelper.COL_CONTACTO_ID + " = ?",
                new String[]{String.valueOf(c.getId())}
        );
        db.close();
        return resultado > 0;
    }

    @Override
    public boolean eliminar(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int resultado = db.delete(
                DatabaseHelper.TABLE_CONTACTOS,
                DatabaseHelper.COL_CONTACTO_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return resultado > 0;
    }

    @Override
    public ArrayList<Contacto> listar() {
        ArrayList<Contacto> contactos = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_CONTACTOS, null);
        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CONTACTO_ID));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CONTACTO_NOMBRE));
                String telefono = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CONTACTO_TELEFONO));
                String alias = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CONTACTO_ALIAS));
                contactos.add(new Contacto(id, nombre, telefono, alias));
            }
        } finally {
            cursor.close();
            db.close();
        }
        return contactos;
    }

    @Override
    public void cargaInicial() {
        // No insertar contactos por defecto para que todo funcione con datos reales del usuario
    }
}
