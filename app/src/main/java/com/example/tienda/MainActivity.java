package com.example.tienda;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tienda.dao.CuentaDAO;
import com.example.tienda.dao.MovimientoDAO;
import com.example.tienda.model.Cuenta;
import com.example.tienda.model.Movimiento;
import com.google.android.material.button.MaterialButton;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextView tvUserWelcome;
    private TextView tvAccountSubtitle;
    private TextView tvBalance;
    private TextView tvAccountNumber;

    private View cardNavCuentas;
    private View cardNavCompras;
    private View cardNavDeudas;
    private View cardNavMap;
    private View cardNavContactos;
    private View cardNavMovimientos;

    private CuentaDAO cuentaDAO;
    private MovimientoDAO movimientoDAO;
    private Cuenta cuentaActual;

    public static String formatCLP(double amount) {
        try {
            DecimalFormat clpFormat = (DecimalFormat) NumberFormat.getCurrencyInstance(new Locale("es", "CL"));
            clpFormat.setMaximumFractionDigits(0);
            return clpFormat.format(amount).replace("CLP", "").trim();
        } catch (Exception e) {
            return "$" + (long) amount;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cuentaDAO = new CuentaDAO(this);
        movimientoDAO = new MovimientoDAO(this);

        initViews();
        cargarDatosCuentaRealDB();
        setupNavigation();
    }

    private void initViews() {
        tvUserWelcome = findViewById(R.id.tvUserWelcome);
        tvAccountSubtitle = findViewById(R.id.tvAccountSubtitle);
        tvBalance = findViewById(R.id.tvBalance);
        tvAccountNumber = findViewById(R.id.tvAccountNumber);
        MaterialButton btnTransfer = findViewById(R.id.btnTransfer);
        MaterialButton btnDeposit = findViewById(R.id.btnDeposit);

        cardNavCuentas = findViewById(R.id.cardNavCuentas);
        cardNavCompras = findViewById(R.id.cardNavCompras);
        cardNavDeudas = findViewById(R.id.cardNavDeudas);
        cardNavMap = findViewById(R.id.cardNavMap);
        cardNavContactos = findViewById(R.id.cardNavContactos);
        cardNavMovimientos = findViewById(R.id.cardNavMovimientos);

        if (btnTransfer != null) {
            btnTransfer.setOnClickListener(v -> mostrarDialogoTransferencia());
        }

        if (btnDeposit != null) {
            btnDeposit.setOnClickListener(v -> mostrarDialogoDeposito());
        }
    }

    private void cargarDatosCuentaRealDB() {
        ArrayList<Cuenta> cuentas = cuentaDAO.listar();

        if (cuentas.isEmpty()) {
            Toast.makeText(this, "Debes registrar tu cuenta para continuar.", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        cuentaActual = cuentas.get(0);

        if (tvUserWelcome != null && tvBalance != null && tvAccountNumber != null && tvAccountSubtitle != null) {
            tvUserWelcome.setText("Hola, " + cuentaActual.getTitular() + " 👋");
            tvBalance.setText(formatCLP(cuentaActual.getSaldo()));
            tvAccountNumber.setText("RUT / Cuenta: " + cuentaActual.getNumeroCuenta());
            tvAccountSubtitle.setText(cuentaActual.getTipoCuenta() + " • LocalCash Chile");
        }
    }

    private void mostrarDialogoDeposito() {
        if (cuentaActual == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Realizar Ingreso en CLP");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        final EditText inputMonto = new EditText(this);
        inputMonto.setHint("Monto a ingresar en CLP ($)");
        inputMonto.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputMonto);

        final EditText inputDesc = new EditText(this);
        inputDesc.setHint("Descripción u origen (ej. Depósito sueldo)");
        layout.addView(inputDesc);

        builder.setView(layout);

        builder.setPositiveButton("Ingresar Dinero", (dialog, which) -> {
            String montoStr = inputMonto.getText().toString().trim();
            String desc = inputDesc.getText().toString().trim();

            if (TextUtils.isEmpty(montoStr)) {
                Toast.makeText(this, "Ingresa un monto válido.", Toast.LENGTH_SHORT).show();
                return;
            }

            double monto;
            try {
                monto = Double.parseDouble(montoStr);
                if (monto <= 0) {
                    Toast.makeText(this, "El monto debe ser mayor a $0.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Ingresa un número válido.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(desc)) {
                desc = "Depósito en Efectivo CLP";
            }

            double nuevoSaldo = cuentaActual.getSaldo() + monto;
            cuentaActual.setSaldo(nuevoSaldo);

            // Actualizar cuenta en SQLite
            boolean ok = cuentaDAO.actualizar(cuentaActual);

            // Registrar movimiento en SQLite
            String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
            Movimiento mov = new Movimiento(cuentaActual.getId(), "INGRESO", monto, fecha, desc, -33.4489, -70.6693);
            movimientoDAO.crear(mov);

            if (ok) {
                tvBalance.setText(formatCLP(nuevoSaldo));
                Toast.makeText(this, "¡Ingreso de " + formatCLP(monto) + " guardado en SQLite!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void mostrarDialogoTransferencia() {
        if (cuentaActual == null) return;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Realizar Transferencia CLP");

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(40, 20, 40, 10);

        final EditText inputMonto = new EditText(this);
        inputMonto.setHint("Monto a transferir en CLP ($)");
        inputMonto.setInputType(InputType.TYPE_CLASS_NUMBER);
        layout.addView(inputMonto);

        final EditText inputDestino = new EditText(this);
        inputDestino.setHint("RUT / Alias o Nombre Destinatario");
        layout.addView(inputDestino);

        builder.setView(layout);

        builder.setPositiveButton("Transferir Dinero", (dialog, which) -> {
            String montoStr = inputMonto.getText().toString().trim();
            String destino = inputDestino.getText().toString().trim();

            if (TextUtils.isEmpty(montoStr) || TextUtils.isEmpty(destino)) {
                Toast.makeText(this, "Ingresa monto y destinatario válidos.", Toast.LENGTH_SHORT).show();
                return;
            }

            double monto;
            try {
                monto = Double.parseDouble(montoStr);
                if (monto <= 0) {
                    Toast.makeText(this, "El monto debe ser mayor a $0.", Toast.LENGTH_SHORT).show();
                    return;
                }
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Ingresa un valor numérico válido.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (cuentaActual.getSaldo() < monto) {
                Toast.makeText(this, "Saldo insuficiente para realizar esta transferencia.", Toast.LENGTH_SHORT).show();
                return;
            }

            double nuevoSaldo = cuentaActual.getSaldo() - monto;
            cuentaActual.setSaldo(nuevoSaldo);

            // Actualizar saldo en SQLite
            boolean ok = cuentaDAO.actualizar(cuentaActual);

            // Registrar movimiento saliente en SQLite
            String fecha = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(new Date());
            Movimiento mov = new Movimiento(cuentaActual.getId(), "TRANSFERENCIA", monto, fecha, "Transferencia a " + destino, -33.4489, -70.6693);
            movimientoDAO.crear(mov);

            if (ok) {
                tvBalance.setText(formatCLP(nuevoSaldo));
                Toast.makeText(this, "¡Transferencia de " + formatCLP(monto) + " guardada en SQLite!", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", null);
        builder.show();
    }

    private void setupNavigation() {
        if (cardNavCuentas != null) {
            cardNavCuentas.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, CuentasActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        if (cardNavCompras != null) {
            cardNavCompras.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ComprasActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        if (cardNavDeudas != null) {
            cardNavDeudas.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, DeudasActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        if (cardNavMap != null) {
            cardNavMap.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, MapActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        if (cardNavContactos != null) {
            cardNavContactos.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, ContactosActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }

        if (cardNavMovimientos != null) {
            cardNavMovimientos.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, MovimientosActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            });
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cargarDatosCuentaRealDB();
    }
}
