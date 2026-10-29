package com.example.tienda;

import android.graphics.Typeface;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tienda.dao.CuentaDAO;
import com.example.tienda.model.Cuenta;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class CuentasActivity extends AppCompatActivity {

    private LinearLayout containerCuentas;
    private CuentaDAO cuentaDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuentas);

        cuentaDAO = new CuentaDAO(this);

        containerCuentas = findViewById(R.id.containerCuentas);
        View btnBackCuentas = findViewById(R.id.btnBackCuentas);
        MaterialButton btnAgregarCuenta = findViewById(R.id.btnAgregarCuenta);

        if (btnBackCuentas != null) {
            btnBackCuentas.setOnClickListener(v -> cerrarConAnimacion());
        }

        if (btnAgregarCuenta != null) {
            btnAgregarCuenta.setOnClickListener(v -> mostrarDialogoNuevaCuenta());
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                cerrarConAnimacion();
            }
        });

        cargarCuentas();
    }

    private void cargarCuentas() {
        if (containerCuentas == null) return;
        containerCuentas.removeAllViews();

        ArrayList<Cuenta> cuentas = cuentaDAO.listar();

        if (cuentas.isEmpty()) {
            TextView tvEmpty = new TextView(this);
            tvEmpty.setText("No hay cuentas registradas. Toca '+ Nueva' para crear una.");
            tvEmpty.setTextSize(14);
            tvEmpty.setPadding(20, 40, 20, 40);
            tvEmpty.setTextColor(getColor(R.color.texto_oceano));
            containerCuentas.addView(tvEmpty);
            return;
        }

        for (Cuenta c : cuentas) {
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

            TextView tvTitular = new TextView(this);
            tvTitular.setText(c.getTitular());
            tvTitular.setTextSize(18);
            tvTitular.setTypeface(null, Typeface.BOLD);
            tvTitular.setTextColor(getColor(R.color.texto_oceano));

            TextView tvDetalle = new TextView(this);
            tvDetalle.setText("RUT/Cuenta: " + c.getNumeroCuenta() + " • " + c.getTipoCuenta());
            tvDetalle.setTextSize(13);
            tvDetalle.setTextColor(getColor(R.color.texto_oceano));

            TextView tvSaldo = new TextView(this);
            tvSaldo.setText("Saldo: " + MainActivity.formatCLP(c.getSaldo()));
            tvSaldo.setTextSize(16);
            tvSaldo.setTypeface(null, Typeface.BOLD);
            tvSaldo.setTextColor(getColor(R.color.cash_mint));
            tvSaldo.setPadding(0, 8, 0, 12);

            LinearLayout btnRow = new LinearLayout(this);
            btnRow.setOrientation(LinearLayout.HORIZONTAL);

            MaterialButton btnEditar = new MaterialButton(this);
            btnEditar.setText("Editar Saldo");
            btnEditar.setTextSize(11);
            btnEditar.setOnClickListener(v -> mostrarDialogoEditarSaldo(c));

            MaterialButton btnEliminar = new MaterialButton(this);
            btnEliminar.setText("Eliminar");
            btnEliminar.setTextSize(11);
            btnEliminar.setBackgroundColor(getColor(R.color.expense_red));
            LinearLayout.LayoutParams btnLp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            btnLp.setMarginStart(16);
            btnEliminar.setLayoutParams(btnLp);
            btnEliminar.setOnClickListener(v -> {
                cuentaDAO.eliminar(c.getId());
                Toast.makeText(this, "Cuenta eliminada.", Toast.LENGTH_SHORT).show();
                cargarCuentas();
            });

            btnRow.addView(btnEditar);
            btnRow.addView(btnEliminar);

            itemLayout.addView(tvTitular);
            itemLayout.addView(tvDetalle);
            itemLayout.addView(tvSaldo);
            itemLayout.addView(btnRow);

            card.addView(itemLayout);
            containerCuentas.addView(card);
        }
    }

    private void mostrarDialogoNuevaCuenta() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar Nueva Cuenta Financiera");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        final EditText inputTitular = new EditText(this);
        inputTitular.setHint("Nombre del Titular");
        layout.addView(inputTitular);

        final EditText inputNumero = new EditText(this);
        inputNumero.setHint("Número de Cuenta / RUT");
        layout.addView(inputNumero);

        final EditText inputTipo = new EditText(this);
        inputTipo.setHint("Tipo (ej. Cuenta Vista, Corriente, Ahorro)");
        layout.addView(inputTipo);

        final EditText inputSaldo = new EditText(this);
        inputSaldo.setHint("Saldo Inicial ($)");
        inputSaldo.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputSaldo);

        builder.setView(layout);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String titular = inputTitular.getText().toString().trim();
            String numero = inputNumero.getText().toString().trim();
            String tipo = inputTipo.getText().toString().trim();
            String saldoStr = inputSaldo.getText().toString().trim();

            if (TextUtils.isEmpty(titular) || TextUtils.isEmpty(numero) || TextUtils.isEmpty(tipo) || TextUtils.isEmpty(saldoStr)) {
                Toast.makeText(this, "Completa todos los campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            double saldo;
            try {
                saldo = Double.parseDouble(saldoStr);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Ingresa un saldo válido.", Toast.LENGTH_SHORT).show();
                return;
            }

            Cuenta nueva = new Cuenta(numero, titular, saldo, tipo);
            boolean creada = cuentaDAO.crear(nueva);
            if (creada) {
                Toast.makeText(this, "Cuenta agregada exitosamente.", Toast.LENGTH_SHORT).show();
                cargarCuentas();
            } else {
                Toast.makeText(this, "Error al crear cuenta.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarDialogoEditarSaldo(Cuenta c) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Ajustar Saldo de " + c.getTitular());

        final EditText inputSaldo = new EditText(this);
        inputSaldo.setHint("Nuevo Saldo en CLP ($)");
        inputSaldo.setText(String.valueOf((long) c.getSaldo()));
        inputSaldo.setInputType(InputType.TYPE_CLASS_NUMBER);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);
        layout.addView(inputSaldo);
        builder.setView(layout);

        builder.setPositiveButton("Actualizar", (dialog, which) -> {
            String saldoStr = inputSaldo.getText().toString().trim();
            if (TextUtils.isEmpty(saldoStr)) return;

            try {
                double nuevoSaldo = Double.parseDouble(saldoStr);
                c.setSaldo(nuevoSaldo);
                cuentaDAO.actualizar(c);
                Toast.makeText(this, "Saldo actualizado.", Toast.LENGTH_SHORT).show();
                cargarCuentas();
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Monto inválido.", Toast.LENGTH_SHORT).show();
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
