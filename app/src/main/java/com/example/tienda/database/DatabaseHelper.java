package com.example.tienda.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "localcash.db";
    private static final int DATABASE_VERSION = 3;

    // Tabla Productos (legacy)
    public static final String TABLE_PRODUCT = "productos";
    public static final String COL_ID = "id";
    public static final String COL_NAME = "nombre";
    public static final String COL_CANT = "cantidad";
    public static final String COL_PRICE = "precio";

    // Tabla Cuentas
    public static final String TABLE_CUENTAS = "cuentas";
    public static final String COL_CUENTA_ID = "id";
    public static final String COL_CUENTA_NUMERO = "numero_cuenta";
    public static final String COL_CUENTA_TITULAR = "titular";
    public static final String COL_CUENTA_SALDO = "saldo";
    public static final String COL_CUENTA_TIPO = "tipo_cuenta";

    // Tabla Contactos
    public static final String TABLE_CONTACTOS = "contactos";
    public static final String COL_CONTACTO_ID = "id";
    public static final String COL_CONTACTO_NOMBRE = "nombre";
    public static final String COL_CONTACTO_TELEFONO = "telefono";
    public static final String COL_CONTACTO_ALIAS = "alias_cbu";

    // Tabla Movimientos
    public static final String TABLE_MOVIMIENTOS = "movimientos";
    public static final String COL_MOV_ID = "id";
    public static final String COL_MOV_ID_CUENTA = "id_cuenta";
    public static final String COL_MOV_TIPO = "tipo";
    public static final String COL_MOV_MONTO = "monto";
    public static final String COL_MOV_FECHA = "fecha";
    public static final String COL_MOV_DESCRIPCION = "descripcion";
    public static final String COL_MOV_LATITUD = "latitud";
    public static final String COL_MOV_LONGITUD = "longitud";

    // Tabla Compras y Agendamiento
    public static final String TABLE_COMPRAS = "compras";
    public static final String COL_COMPRA_ID = "id";
    public static final String COL_COMPRA_ID_PRODUCTO = "id_producto";
    public static final String COL_COMPRA_NOMBRE_PROD = "nombre_producto";
    public static final String COL_COMPRA_CANTIDAD = "cantidad";
    public static final String COL_COMPRA_PRECIO_TOTAL = "precio_total";
    public static final String COL_COMPRA_FECHA = "fecha";
    public static final String COL_COMPRA_FECHA_PROG = "fecha_programada";
    public static final String COL_COMPRA_ES_PROG = "es_programada";
    public static final String COL_COMPRA_ESTADO = "estado";

    // Tabla Deudas y Gastos Compartidos
    public static final String TABLE_DEUDAS = "deudas";
    public static final String COL_DEUDA_ID = "id";
    public static final String COL_DEUDA_ID_CONTACTO = "id_contacto";
    public static final String COL_DEUDA_NOMBRE_CONTACTO = "nombre_contacto";
    public static final String COL_DEUDA_MONTO_TOTAL = "monto_total";
    public static final String COL_DEUDA_NUM_PERSONAS = "num_personas";
    public static final String COL_DEUDA_MONTO_POR_PERSONA = "monto_por_persona";
    public static final String COL_DEUDA_DESCRIPCION = "descripcion";
    public static final String COL_DEUDA_FECHA = "fecha";
    public static final String COL_DEUDA_ESTADO_PAGO = "estado_pago";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // SQL Tabla Productos
        String sqlProductos = "CREATE TABLE " + TABLE_PRODUCT + " (" +
                COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_NAME + " TEXT NOT NULL, " +
                COL_CANT + " INTEGER NOT NULL, " +
                COL_PRICE + " REAL NOT NULL)";
        db.execSQL(sqlProductos);

        // SQL Tabla Cuentas
        String sqlCuentas = "CREATE TABLE " + TABLE_CUENTAS + " (" +
                COL_CUENTA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CUENTA_NUMERO + " TEXT NOT NULL, " +
                COL_CUENTA_TITULAR + " TEXT NOT NULL, " +
                COL_CUENTA_SALDO + " REAL NOT NULL, " +
                COL_CUENTA_TIPO + " TEXT NOT NULL)";
        db.execSQL(sqlCuentas);

        // SQL Tabla Contactos
        String sqlContactos = "CREATE TABLE " + TABLE_CONTACTOS + " (" +
                COL_CONTACTO_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_CONTACTO_NOMBRE + " TEXT NOT NULL, " +
                COL_CONTACTO_TELEFONO + " TEXT NOT NULL, " +
                COL_CONTACTO_ALIAS + " TEXT NOT NULL)";
        db.execSQL(sqlContactos);

        // SQL Tabla Movimientos
        String sqlMovimientos = "CREATE TABLE " + TABLE_MOVIMIENTOS + " (" +
                COL_MOV_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_MOV_ID_CUENTA + " INTEGER NOT NULL, " +
                COL_MOV_TIPO + " TEXT NOT NULL, " +
                COL_MOV_MONTO + " REAL NOT NULL, " +
                COL_MOV_FECHA + " TEXT NOT NULL, " +
                COL_MOV_DESCRIPCION + " TEXT, " +
                COL_MOV_LATITUD + " REAL, " +
                COL_MOV_LONGITUD + " REAL, " +
                "FOREIGN KEY(" + COL_MOV_ID_CUENTA + ") REFERENCES " + TABLE_CUENTAS + "(" + COL_CUENTA_ID + "))";
        db.execSQL(sqlMovimientos);

        // SQL Tabla Compras
        String sqlCompras = "CREATE TABLE " + TABLE_COMPRAS + " (" +
                COL_COMPRA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_COMPRA_ID_PRODUCTO + " INTEGER, " +
                COL_COMPRA_NOMBRE_PROD + " TEXT NOT NULL, " +
                COL_COMPRA_CANTIDAD + " INTEGER NOT NULL, " +
                COL_COMPRA_PRECIO_TOTAL + " REAL NOT NULL, " +
                COL_COMPRA_FECHA + " TEXT NOT NULL, " +
                COL_COMPRA_FECHA_PROG + " TEXT, " +
                COL_COMPRA_ES_PROG + " INTEGER NOT NULL, " +
                COL_COMPRA_ESTADO + " TEXT NOT NULL)";
        db.execSQL(sqlCompras);

        // SQL Tabla Deudas
        String sqlDeudas = "CREATE TABLE " + TABLE_DEUDAS + " (" +
                COL_DEUDA_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COL_DEUDA_ID_CONTACTO + " INTEGER, " +
                COL_DEUDA_NOMBRE_CONTACTO + " TEXT NOT NULL, " +
                COL_DEUDA_MONTO_TOTAL + " REAL NOT NULL, " +
                COL_DEUDA_NUM_PERSONAS + " INTEGER NOT NULL, " +
                COL_DEUDA_MONTO_POR_PERSONA + " REAL NOT NULL, " +
                COL_DEUDA_DESCRIPCION + " TEXT NOT NULL, " +
                COL_DEUDA_FECHA + " TEXT NOT NULL, " +
                COL_DEUDA_ESTADO_PAGO + " TEXT NOT NULL)";
        db.execSQL(sqlDeudas);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PRODUCT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MOVIMIENTOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACTOS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CUENTAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPRAS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEUDAS);
        onCreate(db);
    }
}
