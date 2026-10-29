package com.example.tienda;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tienda.dao.CompraDAO;
import com.example.tienda.dao.ProductoDAO;
import com.example.tienda.model.Compra;
import com.example.tienda.model.Producto;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class ComprasActivity extends AppCompatActivity {

    private LinearLayout containerCompras;
    private CompraDAO compraDAO;
    private ProductoDAO productoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compras);

        compraDAO = new CompraDAO(this);
        productoDAO = new ProductoDAO(this);

        // Carga inicial masiva de productos si el catálogo está vacío
        if (productoDAO.listar().isEmpty()) {
            productoDAO.cargaMasiva();
        }

        containerCompras = findViewById(R.id.containerCompras);
        View btnBackCompras = findViewById(R.id.btnBackCompras);
        MaterialButton btnNuevaCompra = findViewById(R.id.btnNuevaCompra);
        MaterialButton btnProgramarCompra = findViewById(R.id.btnProgramarCompra);

        if (btnBackCompras != null) {
            btnBackCompras.setOnClickListener(v -> cerrarConAnimacion());
        }

        if (btnNuevaCompra != null) {
            btnNuevaCompra.setOnClickListener(v -> mostrarDialogoCompra(false));
        }

        if (btnProgramarCompra != null) {
            btnProgramarCompra.setOnClickListener(v -> mostrarDialogoCompra(true));
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                cerrarConAnimacion();
            }
        });

        cargarCompras();
    }

    private void cargarCompras() {
        if (containerCompras == null) return;
        containerCompras.removeAllViews();

        ArrayList<Compra> compras = compraDAO.listar();

        if (compras.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("No hay historial de compras ni agendamientos. Registra o programa una compra arriba.");
            tvEmpty.setTextSize(14);
            tvEmpty.setPadding(20, 40, 20, 40);
            tvEmpty.setTextColor(getColor(R.color.texto_oceano));
            containerCompras.addView(tvEmpty);
            return;
        }

        for (Compra c : compras) {
            MaterialCardView card = new MaterialCardView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            lp.setMargins(0, 0, 0, 16);
            card.setLayoutParams(lp);
            card.setRadius(24f);
            card.setCardElevation(0f);
            card.setCardBackgroundColor(getColor(R.color.glass_fondo));
            card.setStrokeColor(getColor(R.color.glass_borde_brillo));
            card.setStrokeWidth(2);

            LinearLayout itemLayout = new LinearLayout(this);
            itemLayout.setOrientation(LinearLayout.VERTICAL);
            itemLayout.setPadding(24, 24, 24, 24);

            TextView tvNombre = new TextView(this);
            tvNombre.setText(c.getNombreProducto() + " (x" + c.getCantidad() + ")");
            tvNombre.setTextSize(17);
            tvNombre.setTypeface(null, Typeface.BOLD);
            tvNombre.setTextColor(getColor(R.color.texto_oceano));

            TextView tvTotal = new TextView(this);
            tvTotal.setText("Total: " + MainActivity.formatCLP(c.getPrecioTotal()));
            tvTotal.setTextSize(15);
            tvTotal.setTypeface(null, Typeface.BOLD);
            tvTotal.setTextColor(getColor(R.color.cash_mint));

            TextView tvFechas = new TextView(this);
            String txtFecha = "Fecha registro: " + c.getFecha();
            if (c.isEsProgramada() && c.getFechaProgramada() != null) {
                txtFecha += "\n⏰ Agendada para: " + c.getFechaProgramada();
            }
            tvFechas.setText(txtFecha);
            tvFechas.setTextSize(12);
            tvFechas.setTextColor(getColor(R.color.texto_oceano));

            TextView tvEstado = new TextView(this);
            tvEstado.setText("Estado: " + c.getEstado());
            tvEstado.setTextSize(12);
            tvEstado.setTypeface(null, Typeface.BOLD_ITALIC);
            tvEstado.setTextColor(c.isEsProgramada() ? getColor(R.color.expense_red) : getColor(R.color.cash_mint));
            tvEstado.setPadding(0, 4, 0, 8);

            LinearLayout btnRow = new LinearLayout(this);
            btnRow.setOrientation(LinearLayout.HORIZONTAL);

            if (c.isEsProgramada()) {
                MaterialButton btnCompletar = new MaterialButton(this);
                btnCompletar.setText("Completar Compra");
                btnCompletar.setTextSize(11);
                btnCompletar.setOnClickListener(v -> {
                    compraDAO.completarCompra(c.getId());
                    Toast.makeText(this, "¡Compra completada exitosamente!", Toast.LENGTH_SHORT).show();
                    cargarCompras();
                });
                btnRow.addView(btnCompletar);
            }

            MaterialButton btnEliminar = new MaterialButton(this);
            btnEliminar.setText("Eliminar");
            btnEliminar.setTextSize(11);
            btnEliminar.setBackgroundColor(getColor(R.color.expense_red));
            LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            btnLp.setMarginStart(12);
            btnEliminar.setLayoutParams(btnLp);
            btnEliminar.setOnClickListener(v -> {
                compraDAO.eliminar(c.getId());
                Toast.makeText(this, "Compra eliminada del historial.", Toast.LENGTH_SHORT).show();
                cargarCompras();
            });
            btnRow.addView(btnEliminar);

            itemLayout.addView(tvNombre);
            itemLayout.addView(tvTotal);
            itemLayout.addView(tvFechas);
            itemLayout.addView(tvEstado);
            itemLayout.addView(btnRow);

            card.addView(itemLayout);
            containerCompras.addView(card);
        }
    }

    private void mostrarDialogoCompra(boolean esProgramada) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(esProgramada ? "Agendar Compra Futura" : "Registrar Compra Inmediata");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        TextView tvSelectProd = new TextView(this);
        tvSelectProd.setText("Selecciona un producto del catálogo:");
        layout.addView(tvSelectProd);

        Spinner spinnerProductos = new Spinner(this);
        ArrayList<Producto> productos = productoDAO.listar();
        ArrayList<String> nombresProd = new ArrayList<>();
        for (Producto p : productos) {
            nombresProd.add(p.getNombre() + " - " + MainActivity.formatCLP(p.getPrecio()));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nombresProd);
        spinnerProductos.setAdapter(adapter);
        layout.addView(spinnerProductos);

        final EditText inputCantidad = new EditText(this);
        inputCantidad.setHint("Cantidad a adquirir");
        inputCantidad.setText("1");
        inputCantidad.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputCantidad);

        final EditText inputFechaProg = new EditText(this);
        if (esProgramada) {
            inputFechaProg.setHint("Fecha programada (ej. 2025-12-01)");
            layout.addView(inputFechaProg);
        }

        builder.setView(layout);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            if (productos.isEmpty()) {
                Toast.makeText(this, "No hay productos en el catálogo.", Toast.LENGTH_SHORT).show();
                return;
            }

            int selPos = spinnerProductos.getSelectedItemPosition();
            if (selPos < 0 || selPos >= productos.size()) return;

            Producto prodSeleccionado = productos.get(selPos);
            String cantStr = inputCantidad.getText().toString().trim();
            if (TextUtils.isEmpty(cantStr)) return;

            int cantidad = 1;
            try {
                cantidad = Integer.parseInt(cantStr);
            } catch (NumberFormatException ignored) {}

            double precioTotal = prodSeleccionado.getPrecio() * cantidad;
            String fechaActual = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
            String fechaProgStr = esProgramada ? inputFechaProg.getText().toString().trim() : "";
            if (esProgramada && TextUtils.isEmpty(fechaProgStr)) {
                fechaProgStr = "2025-12-31";
            }

            String estado = esProgramada ? "PROGRAMADO" : "COMPLETADO";

            Compra nuevaCompra = new Compra(
                    prodSeleccionado.getId(),
                    prodSeleccionado.getNombre(),
                    cantidad,
                    precioTotal,
                    fechaActual,
                    fechaProgStr,
                    esProgramada,
                    estado
            );

            boolean ok = compraDAO.crear(nuevaCompra);
            if (ok) {
                Toast.makeText(this, "¡Compra " + (esProgramada ? "programada" : "registrada") + " exitosamente!", Toast.LENGTH_SHORT).show();
                cargarCompras();
            } else {
                Toast.makeText(this, "Error al guardar en la base de datos.", Toast.LENGTH_SHORT).show();
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
