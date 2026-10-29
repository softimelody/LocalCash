package com.example.tienda;

import android.os.Bundle;
import android.text.TextUtils;

import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tienda.dao.ContactoDAO;
import com.example.tienda.model.Contacto;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class ContactosActivity extends AppCompatActivity {

    private LinearLayout containerContactosActivity;
    private TextView tvEmptyContactos;
    private ContactoDAO contactoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contactos);

        View btnBackContactos = findViewById(R.id.btnBackContactos);
        containerContactosActivity = findViewById(R.id.containerContactosActivity);
        tvEmptyContactos = findViewById(R.id.tvEmptyContactos);
        FloatingActionButton btnAddContacto = findViewById(R.id.btnAddContacto);

        if (btnBackContactos != null) {
            btnBackContactos.setOnClickListener(v -> cerrarConAnimacion());
        }

        if (btnAddContacto != null) {
            btnAddContacto.setOnClickListener(v -> mostrarDialogoAgregarContacto());
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                cerrarConAnimacion();
            }
        });

        contactoDAO = new ContactoDAO(this);

        cargarContactosDesdeDB();
    }

    private void cargarContactosDesdeDB() {
        if (containerContactosActivity == null) return;

        containerContactosActivity.removeAllViews();
        ArrayList<Contacto> contactos = contactoDAO.listar();

        if (contactos.isEmpty()) {
            if (tvEmptyContactos != null) {
                tvEmptyContactos.setVisibility(View.VISIBLE);
            }
            return;
        }

        if (tvEmptyContactos != null) {
            tvEmptyContactos.setVisibility(View.GONE);
        }

        for (Contacto c : contactos) {
            View itemView = getLayoutInflater().inflate(R.layout.item_contacto, containerContactosActivity, false);
            TextView tvInicial = itemView.findViewById(R.id.tvContactoInicial);
            TextView tvNombre = itemView.findViewById(R.id.tvContactoNombre);
            TextView tvAlias = itemView.findViewById(R.id.tvContactoAlias);

            if (c.getNombre() != null && !c.getNombre().isEmpty() && tvInicial != null) {
                tvInicial.setText(String.valueOf(c.getNombre().charAt(0)).toUpperCase());
            }
            if (tvNombre != null) {
                tvNombre.setText(c.getNombre());
            }
            if (tvAlias != null) {
                tvAlias.setText("Alias: " + c.getAliasCbu() + " | Tel: " + c.getTelefono());
            }

            // Eliminar contacto con long press
            itemView.setOnLongClickListener(v -> {
                new AlertDialog.Builder(this)
                        .setTitle("Eliminar Contacto")
                        .setMessage("¿Deseas eliminar a " + c.getNombre() + " de la base de datos?")
                        .setPositiveButton("Eliminar", (dialog, which) -> {
                            boolean ok = contactoDAO.eliminar(c.getId());
                            if (ok) {
                                Toast.makeText(this, "Contacto eliminado de SQLite", Toast.LENGTH_SHORT).show();
                                cargarContactosDesdeDB();
                            }
                        })
                        .setNegativeButton("Cancelar", null)
                        .show();
                return true;
            });

            containerContactosActivity.addView(itemView);
        }
    }

    private void mostrarDialogoAgregarContacto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Añadir Nuevo Contacto");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        final EditText inputNombre = new EditText(this);
        inputNombre.setHint("Nombre del Contacto");
        layout.addView(inputNombre);

        final EditText inputAlias = new EditText(this);
        inputAlias.setHint("Alias CBU / Número de Cuenta");
        layout.addView(inputAlias);

        final EditText inputTel = new EditText(this);
        inputTel.setHint("Teléfono");
        layout.addView(inputTel);

        builder.setView(layout);

        builder.setPositiveButton("Guardar en SQLite", (dialog, which) -> {
            String nombre = inputNombre.getText().toString().trim();
            String alias = inputAlias.getText().toString().trim();
            String tel = inputTel.getText().toString().trim();

            if (TextUtils.isEmpty(nombre) || TextUtils.isEmpty(alias)) {
                Toast.makeText(this, "Ingresa al menos el nombre y alias del contacto.", Toast.LENGTH_SHORT).show();
                return;
            }

            Contacto nuevo = new Contacto(nombre, tel, alias);
            boolean guardado = contactoDAO.crear(nuevo);

            if (guardado) {
                Toast.makeText(this, "Contacto guardado exitosamente en SQLite", Toast.LENGTH_SHORT).show();
                cargarContactosDesdeDB();
            } else {
                Toast.makeText(this, "Error al guardar contacto en la base de datos.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void cerrarConAnimacion() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
