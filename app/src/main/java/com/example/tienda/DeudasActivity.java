package com.example.tienda;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
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

import com.example.tienda.dao.ContactoDAO;
import com.example.tienda.dao.DeudaDAO;
import com.example.tienda.model.Contacto;
import com.example.tienda.model.Deuda;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DeudasActivity extends AppCompatActivity {

    private LinearLayout containerDeudas;
    private TextView tvTotalPendiente;

    private DeudaDAO deudaDAO;
    private ContactoDAO contactoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deudas);

        deudaDAO = new DeudaDAO(this);
        contactoDAO = new ContactoDAO(this);

        containerDeudas = findViewById(R.id.containerDeudas);
        tvTotalPendiente = findViewById(R.id.tvTotalPendiente);

        View btnBackDeudas = findViewById(R.id.btnBackDeudas);
        MaterialButton btnDividirGasto = findViewById(R.id.btnDividirGasto);

        if (btnBackDeudas != null) {
            btnBackDeudas.setOnClickListener(v -> cerrarConAnimacion());
        }

        if (btnDividirGasto != null) {
            btnDividirGasto.setOnClickListener(v -> mostrarDialogoDividirGasto());
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                cerrarConAnimacion();
            }
        });

        cargarDeudas();
    }

    private void cargarDeudas() {
        if (containerDeudas == null) return;
        containerDeudas.removeAllViews();

        ArrayList<Deuda> deudas = deudaDAO.listar();
        double totalPendiente = deudaDAO.obtenerMontoTotalPendiente();

        if (tvTotalPendiente != null) {
            tvTotalPendiente.setText(MainActivity.formatCLP(totalPendiente));
        }

        if (deudas.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("No hay gastos grupales divididos ni deudas registradas.");
            tvEmpty.setTextSize(14);
            tvEmpty.setPadding(20, 40, 20, 40);
            tvEmpty.setTextColor(getColor(R.color.texto_oceano));
            containerDeudas.addView(tvEmpty);
            return;
        }

        for (Deuda d : deudas) {
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
            tvNombre.setText("Contacto: " + d.getNombreContacto());
            tvNombre.setTextSize(17);
            tvNombre.setTypeface(null, Typeface.BOLD);
            tvNombre.setTextColor(getColor(R.color.texto_oceano));

            TextView tvDesc = new TextView(this);
            tvDesc.setText("Gasto: " + d.getDescripcion() + " (" + d.getFecha() + ")");
            tvDesc.setTextSize(13);
            tvDesc.setTextColor(getColor(R.color.texto_oceano));

            TextView tvDivision = new TextView(this);
            tvDivision.setText("Total: " + MainActivity.formatCLP(d.getMontoTotal()) + " entre " + d.getNumPersonas() + " personas");
            tvDivision.setTextSize(13);
            tvDivision.setTextColor(getColor(R.color.texto_oceano));

            TextView tvPorPersona = new TextView(this);
            tvPorPersona.setText("Cuota por Persona: " + MainActivity.formatCLP(d.getMontoPorPersona()));
            tvPorPersona.setTextSize(16);
            tvPorPersona.setTypeface(null, Typeface.BOLD);
            tvPorPersona.setTextColor(getColor(R.color.cash_mint));

            TextView tvEstado = new TextView(this);
            tvEstado.setText("Estado: " + d.getEstadoPago());
            tvEstado.setTextSize(12);
            tvEstado.setTypeface(null, Typeface.BOLD_ITALIC);
            boolean esPendiente = "PENDIENTE".equalsIgnoreCase(d.getEstadoPago());
            tvEstado.setTextColor(esPendiente ? getColor(R.color.expense_red) : getColor(R.color.cash_mint));
            tvEstado.setPadding(0, 4, 0, 8);

            LinearLayout btnRow = new LinearLayout(this);
            btnRow.setOrientation(LinearLayout.HORIZONTAL);

            if (esPendiente) {
                MaterialButton btnPagar = new MaterialButton(this);
                btnPagar.setText("Marcar Pagado");
                btnPagar.setTextSize(11);
                btnPagar.setOnClickListener(v -> {
                    deudaDAO.actualizarEstadoPago(d.getId(), "PAGADO");
                    Toast.makeText(this, "Deuda marcada como PAGADA.", Toast.LENGTH_SHORT).show();
                    cargarDeudas();
                });
                btnRow.addView(btnPagar);
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
                deudaDAO.eliminar(d.getId());
                Toast.makeText(this, "Registro eliminado.", Toast.LENGTH_SHORT).show();
                cargarDeudas();
            });
            btnRow.addView(btnEliminar);

            itemLayout.addView(tvNombre);
            itemLayout.addView(tvDesc);
            itemLayout.addView(tvDivision);
            itemLayout.addView(tvPorPersona);
            itemLayout.addView(tvEstado);
            itemLayout.addView(btnRow);

            card.addView(itemLayout);
            containerDeudas.addView(card);
        }
    }

    private void mostrarDialogoDividirGasto() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Dividir Gasto Grupal");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        TextView tvSelectContacto = new TextView(this);
        tvSelectContacto.setText("Selecciona un contacto asignado:");
        layout.addView(tvSelectContacto);

        Spinner spinnerContactos = new Spinner(this);
        ArrayList<Contacto> contactos = contactoDAO.listar();
        ArrayList<String> nombresContactos = new ArrayList<>();
        if (contactos.isEmpty()) {
            nombresContactos.add("Amigo / Contacto General");
        } else {
            for (Contacto c : contactos) {
                nombresContactos.add(c.getNombre() + " (" + c.getAliasCbu() + ")");
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, nombresContactos);
        spinnerContactos.setAdapter(adapter);
        layout.addView(spinnerContactos);

        final EditText inputDesc = new EditText(this);
        inputDesc.setHint("Descripción del gasto (ej. Cena, Viaje)");
        layout.addView(inputDesc);

        final EditText inputMontoTotal = new EditText(this);
        inputMontoTotal.setHint("Monto Total del Gasto ($)");
        inputMontoTotal.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputMontoTotal);

        final EditText inputNumPersonas = new EditText(this);
        inputNumPersonas.setHint("Número de Personas a dividir");
        inputNumPersonas.setText("2");
        inputNumPersonas.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputNumPersonas);

        final TextView tvResultadoDiv = new TextView(this);
        tvResultadoDiv.setText("Monto calculado por persona: $0");
        tvResultadoDiv.setTextSize(14);
        tvResultadoDiv.setPadding(0, 16, 0, 16);
        tvResultadoDiv.setTypeface(null, Typeface.BOLD);
        tvResultadoDiv.setTextColor(getColor(R.color.texto_oceano));
        layout.addView(tvResultadoDiv);

        TextWatcher textWatcher = new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {
                String mStr = inputMontoTotal.getText().toString().trim();
                String pStr = inputNumPersonas.getText().toString().trim();
                if (!TextUtils.isEmpty(mStr) && !TextUtils.isEmpty(pStr)) {
                    try {
                        double total = Double.parseDouble(mStr);
                        int num = Integer.parseInt(pStr);
                        if (num > 0) {
                            double porPersona = total / num;
                            tvResultadoDiv.setText("Monto calculado por persona: " + MainActivity.formatCLP(porPersona));
                        }
                    } catch (NumberFormatException ignored) {}
                }
            }
        };

        inputMontoTotal.addTextChangedListener(textWatcher);
        inputNumPersonas.addTextChangedListener(textWatcher);

        builder.setView(layout);

        builder.setPositiveButton("Guardar Deuda", (dialog, which) -> {
            String desc = inputDesc.getText().toString().trim();
            String mStr = inputMontoTotal.getText().toString().trim();
            String pStr = inputNumPersonas.getText().toString().trim();

            if (TextUtils.isEmpty(desc) || TextUtils.isEmpty(mStr) || TextUtils.isEmpty(pStr)) {
                Toast.makeText(this, "Completa los campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            double total = 0;
            int personas = 2;
            try {
                total = Double.parseDouble(mStr);
                personas = Integer.parseInt(pStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Valores numéricos inválidos.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (personas <= 0) personas = 1;
            double porPersona = total / personas;

            int idContacto = 0;
            String nombreContacto = "Contacto General";
            if (!contactos.isEmpty()) {
                int pos = spinnerContactos.getSelectedItemPosition();
                if (pos >= 0 && pos < contactos.size()) {
                    idContacto = contactos.get(pos).getId();
                    nombreContacto = contactos.get(pos).getNombre();
                }
            }

            String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());

            Deuda nuevaDeuda = new Deuda(
                    idContacto,
                    nombreContacto,
                    total,
                    personas,
                    porPersona,
                    desc,
                    fecha,
                    "PENDIENTE"
            );

            boolean ok = deudaDAO.crear(nuevaDeuda);
            if (ok) {
                Toast.makeText(this, "¡Gasto dividido guardado exitosamente!", Toast.LENGTH_SHORT).show();
                cargarDeudas();
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
