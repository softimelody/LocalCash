package com.example.tienda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tienda.database.DatabaseHelper;
import com.example.tienda.model.Cuenta;

import java.util.ArrayList;

public class CuentaDAO implements ICuenta {

    public DatabaseHelper dbHelper;

    public CuentaDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public boolean crear(Cuenta c) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_CUENTA_NUMERO, c.getNumeroCuenta());
        cv.put(DatabaseHelper.COL_CUENTA_TITULAR, c.getTitular());
        cv.put(DatabaseHelper.COL_CUENTA_SALDO, c.getSaldo());
        cv.put(DatabaseHelper.COL_CUENTA_TIPO, c.getTipoCuenta());

        long result = db.insert(DatabaseHelper.TABLE_CUENTAS, null, cv);
        db.close();
        return result != -1;
    }

    @Override
    public Cuenta buscar(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cuenta cuenta = null;
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_CUENTAS + " WHERE " + DatabaseHelper.COL_CUENTA_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        try {
            if (cursor.moveToFirst()) {
                String numero = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CUENTA_NUMERO));
                String titular = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CUENTA_TITULAR));
                double saldo = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CUENTA_SALDO));
                String tipo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CUENTA_TIPO));
                cuenta = new Cuenta(id, numero, titular, saldo, tipo);
            }
        } finally {
            cursor.close();
            db.close();
        }
        return cuenta;
    }

    @Override
    public Cuenta buscarPorNumero(String numeroCuenta) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cuenta cuenta = null;
        Cursor cursor = db.rawQuery(
                "SELECT * FROM " + DatabaseHelper.TABLE_CUENTAS + " WHERE " + DatabaseHelper.COL_CUENTA_NUMERO + " = ?",
                new String[]{numeroCuenta}
        );
        try {
            if (cursor.moveToFirst()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CUENTA_ID));
                String titular = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CUENTA_TITULAR));
                double saldo = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CUENTA_SALDO));
                String tipo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CUENTA_TIPO));
                cuenta = new Cuenta(id, numeroCuenta, titular, saldo, tipo);
            }
        } finally {
            cursor.close();
            db.close();
        }
        return cuenta;
    }

    @Override
    public boolean actualizar(Cuenta c) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(DatabaseHelper.COL_CUENTA_NUMERO, c.getNumeroCuenta());
        cv.put(DatabaseHelper.COL_CUENTA_TITULAR, c.getTitular());
        cv.put(DatabaseHelper.COL_CUENTA_SALDO, c.getSaldo());
        cv.put(DatabaseHelper.COL_CUENTA_TIPO, c.getTipoCuenta());

        int resultado = db.update(
                DatabaseHelper.TABLE_CUENTAS,
                cv,
                DatabaseHelper.COL_CUENTA_ID + " = ?",
                new String[]{String.valueOf(c.getId())}
        );
        db.close();
        return resultado > 0;
    }

    @Override
    public boolean eliminar(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int resultado = db.delete(
                DatabaseHelper.TABLE_CUENTAS,
                DatabaseHelper.COL_CUENTA_ID + " = ?",
                new String[]{String.valueOf(id)}
        );
        db.close();
        return resultado > 0;
    }

    @Override
    public ArrayList<Cuenta> listar() {
        ArrayList<Cuenta> cuentas = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.TABLE_CUENTAS, null);
        try {
            while (cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CUENTA_ID));
                String numero = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CUENTA_NUMERO));
                String titular = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CUENTA_TITULAR));
                double saldo = cursor.getDouble(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CUENTA_SALDO));
                String tipo = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COL_CUENTA_TIPO));
                cuentas.add(new Cuenta(id, numero, titular, saldo, tipo));
            }
        } finally {
            cursor.close();
            db.close();
        }
        return cuentas;
    }

    @Override
    public void cargaInicial() {
        // No insertar datos duros automáticos para permitir al usuario crear su propia cuenta
    }
}
