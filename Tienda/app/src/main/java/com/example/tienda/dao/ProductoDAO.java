package com.example.tienda.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.tienda.database.DatabaseHelper;
import com.example.tienda.model.Producto;

import java.util.ArrayList;

public class ProductoDAO implements IProducto {

    public DatabaseHelper dbHelper;

    public ProductoDAO(Context context) {
        dbHelper = new DatabaseHelper(context);
    }

    @Override
    public boolean crear(Producto p) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("nombre", p.getNombre());
        cv.put("cantidad", p.getCantidad());
        cv.put("precio", p.getPrecio());

        long result = db.insert("productos", null, cv);
        db.close();

        return result != -1;
    }

    @Override
    public Producto buscar(int id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Producto pro = null;

        Cursor cursor = db.rawQuery(
                "SELECT * FROM productos WHERE id = ?",
                new String[]{String.valueOf(id)}
        );
        try{
            if (cursor.moveToFirst()) {
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
                int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"));

                pro.setId(id);
                pro.setNombre(nombre);
                pro.setPrecio(precio);
                pro.setCantidad(cantidad);
            }
        } finally {
            cursor.close();
            db.close();
        }

        return pro;
    }

    @Override
    public boolean actualizar(Producto producto) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues cv = new ContentValues();
        cv.put("nombre", producto.getNombre());
        cv.put("cantidad", producto.getCantidad());
        cv.put("precio", producto.getPrecio());

        int resultado = db.update(
                "productos",
                cv,
                "id = ?",
                new String[]{String.valueOf(producto.getId())}
        );
        db.close();
        return resultado != -1;
    }

    @Override
    public boolean eliminar(int id) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int resultado = db.delete(
                "productos",
                "id = ?",
                new String[] {String.valueOf(id)}
        );
        db.close();
        return resultado != -1;
    }

    @Override
    public ArrayList<Producto> listar() {
        ArrayList<Producto> productos = new ArrayList<>();

        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM productos", null);

        try  {
            while(cursor.moveToNext()) {
                int id = cursor.getInt(cursor.getColumnIndexOrThrow("id"));
                String nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombre"));
                double precio = cursor.getDouble(cursor.getColumnIndexOrThrow("precio"));
                int cantidad = cursor.getInt(cursor.getColumnIndexOrThrow("cantidad"));

                productos.add(new Producto(id,nombre,precio,cantidad));
            }
        } finally {
            cursor.close();
            db.close();
        }

        return  productos;
    }

    @Override
    public void cargaMasiva() {
        crear(new Producto(1, "Laptop Lenovo", 599990, 10));
        crear(new Producto(2, "Mouse Logitech", 12990, 25));
        crear(new Producto(3, "Teclado Mecanico", 45990, 15));
        crear(new Producto(4, "Monitor Samsung", 189990, 8));
        crear(new Producto(5, "Impresora Epson", 149990, 6));
        crear(new Producto(6, "Audifonos Sony", 39990, 20));
        crear(new Producto(7, "Webcam Genius", 24990, 12));
        crear(new Producto(8, "Tablet Samsung", 219990, 7));
        crear(new Producto(9, "Smartphone Xiaomi", 299990, 14));
        crear(new Producto(10, "Cargador USB C", 9990, 30));

        crear(new Producto(11, "Disco SSD Kingston", 69990, 18));
        crear(new Producto(12, "Memoria USB Sandisk", 8990, 40));
        crear(new Producto(13, "Tarjeta SD Kingston", 12990, 22));
        crear(new Producto(14, "Router TP-Link", 34990, 9));
        crear(new Producto(15, "Cable HDMI", 5990, 35));
        crear(new Producto(16, "Parlante JBL", 49990, 11));
        crear(new Producto(17, "Microfono HyperX", 79990, 5));
        crear(new Producto(18, "Joystick Xbox", 64990, 13));
        crear(new Producto(19, "Consola PlayStation", 549990, 4));
        crear(new Producto(20, "Camara Canon", 429990, 3));

        crear(new Producto(21, "Refrigerador LG", 499990, 5));
        crear(new Producto(22, "Microondas Oster", 89990, 8));
        crear(new Producto(23, "Licuadora Philips", 32990, 12));
        crear(new Producto(24, "Tostadora Moulinex", 19990, 16));
        crear(new Producto(25, "Cafetera Nespresso", 119990, 7));
        crear(new Producto(26, "Hervidor Thomas", 24990, 14));
        crear(new Producto(27, "Aspiradora Bosch", 99990, 6));
        crear(new Producto(28, "Ventilador Sindelen", 29990, 10));
        crear(new Producto(29, "Calefactor Ursus", 39990, 9));
        crear(new Producto(30, "Plancha Philips", 27990, 13));

        crear(new Producto(31, "Silla Ergonomica", 129990, 6));
        crear(new Producto(32, "Escritorio Gamer", 179990, 4));
        crear(new Producto(33, "Lampara LED", 14990, 20));
        crear(new Producto(34, "Reloj Casio", 29990, 17));
        crear(new Producto(35, "Mochila Samsonite", 59990, 10));
        crear(new Producto(36, "Botella Termica", 12990, 25));
        crear(new Producto(37, "Paraguas Reforzado", 9990, 19));
        crear(new Producto(38, "Maleta American Tourister", 89990, 5));
        crear(new Producto(39, "Linterna Recargable", 15990, 15));
        crear(new Producto(40, "Power Bank Anker", 34990, 12));

        crear(new Producto(41, "Zapatillas Nike", 79990, 8));
        crear(new Producto(42, "Polera Adidas", 19990, 22));
        crear(new Producto(43, "Chaqueta Columbia", 109990, 6));
        crear(new Producto(44, "Jeans Levi's", 49990, 11));
        crear(new Producto(45, "Gorra Puma", 14990, 18));
        crear(new Producto(46, "Balon Adidas", 24990, 14));
        crear(new Producto(47, "Raqueta Wilson", 69990, 5));
        crear(new Producto(48, "Mancuernas Ajustables", 44990, 7));
        crear(new Producto(49, "Colchoneta Yoga", 17990, 16));
        crear(new Producto(50, "Bicicleta Trek", 399990, 3));
    }


}
