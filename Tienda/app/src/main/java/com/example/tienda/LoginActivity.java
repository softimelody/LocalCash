package com.example.tienda;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import com.example.tienda.dao.CuentaDAO;
import com.example.tienda.model.Cuenta;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.concurrent.Executor;

public class LoginActivity extends AppCompatActivity {

    private MaterialCardView cardLogin;
    private MaterialCardView cardRegister;

    private TextView tvUserInitial;
    private TextView tvUserLoginTitle;
    private TextView tvUserAccountSubtitle;
    private TextView tvAuthStatus;

    private EditText etTitular;
    private EditText etNumeroCuenta;
    private EditText etTipoCuenta;
    private EditText etSaldo;

    private CuentaDAO cuentaDAO;
    private Cuenta cuentaActual;

    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initViews();
        cuentaDAO = new CuentaDAO(this);

        setupBiometrics();
        evaluarEstadoCuenta();
    }

    private void initViews() {
        cardLogin = findViewById(R.id.cardLogin);
        cardRegister = findViewById(R.id.cardRegister);

        tvUserInitial = findViewById(R.id.tvUserInitial);
        tvUserLoginTitle = findViewById(R.id.tvUserLoginTitle);
        tvUserAccountSubtitle = findViewById(R.id.tvUserAccountSubtitle);
        tvAuthStatus = findViewById(R.id.tvAuthStatus);

        MaterialButton btnLoginBiometric = findViewById(R.id.btnLoginBiometric);
        MaterialButton btnGoToRegister = findViewById(R.id.btnGoToRegister);

        etTitular = findViewById(R.id.etTitular);
        etNumeroCuenta = findViewById(R.id.etNumeroCuenta);
        etTipoCuenta = findViewById(R.id.etTipoCuenta);
        etSaldo = findViewById(R.id.etSaldo);

        MaterialButton btnCrearCuenta = findViewById(R.id.btnCrearCuenta);
        MaterialButton btnBackToLogin = findViewById(R.id.btnBackToLogin);

        View btnNavMapLogin = findViewById(R.id.btnNavMapLogin);
        View btnAyudaLogin = findViewById(R.id.btnAyudaLogin);

        if (btnLoginBiometric != null) {
            btnLoginBiometric.setOnClickListener(v -> iniciarAutenticacionBiometrica());
        }

        if (btnGoToRegister != null) {
            btnGoToRegister.setOnClickListener(v -> mostrarModoRegistro());
        }

        if (btnBackToLogin != null) {
            btnBackToLogin.setOnClickListener(v -> evaluarEstadoCuenta());
        }

        if (btnCrearCuenta != null) {
            btnCrearCuenta.setOnClickListener(v -> registrarCuentaYAutenticar());
        }

        if (btnNavMapLogin != null) {
            btnNavMapLogin.setOnClickListener(v -> {
                ArrayList<Cuenta> cuentas = cuentaDAO.listar();
                if (cuentas.isEmpty()) {
                    Toast.makeText(this, "Debes registrar tu cuenta primero para ver el mapa.", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent(LoginActivity.this, MapActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                }
            });
        }

        if (btnAyudaLogin != null) {
            btnAyudaLogin.setOnClickListener(v ->
                Toast.makeText(LoginActivity.this, "Soporte LocalCash Offline 24/7", Toast.LENGTH_SHORT).show()
            );
        }
    }

    private void evaluarEstadoCuenta() {
        ArrayList<Cuenta> cuentas = cuentaDAO.listar();
        if (!cuentas.isEmpty()) {
            cuentaActual = cuentas.get(0);
            mostrarModoLogin();
            iniciarAutenticacionBiometrica();
        } else {
            cuentaActual = null;
            mostrarModoRegistro();
            MaterialButton btnBackToLogin = findViewById(R.id.btnBackToLogin);
            if (btnBackToLogin != null) {
                btnBackToLogin.setVisibility(View.GONE);
            }
        }
    }

    private void mostrarModoLogin() {
        if (cardLogin != null && cardRegister != null) {
            cardLogin.setVisibility(View.VISIBLE);
            cardRegister.setVisibility(View.GONE);
        }

        if (cuentaActual != null) {
            if (tvUserInitial != null && cuentaActual.getTitular() != null && !cuentaActual.getTitular().isEmpty()) {
                tvUserInitial.setText(String.valueOf(cuentaActual.getTitular().charAt(0)).toUpperCase());
            }
            if (tvUserLoginTitle != null) {
                tvUserLoginTitle.setText(cuentaActual.getTitular());
            }
            if (tvUserAccountSubtitle != null) {
                tvUserAccountSubtitle.setText(cuentaActual.getNumeroCuenta() + " • " + cuentaActual.getTipoCuenta());
            }
        }
    }

    private void mostrarModoRegistro() {
        if (cardLogin != null && cardRegister != null) {
            cardLogin.setVisibility(View.GONE);
            cardRegister.setVisibility(View.VISIBLE);
        }

        MaterialButton btnBackToLogin = findViewById(R.id.btnBackToLogin);
        if (btnBackToLogin != null) {
            ArrayList<Cuenta> cuentas = cuentaDAO.listar();
            btnBackToLogin.setVisibility(cuentas.isEmpty() ? View.GONE : View.VISIBLE);
        }
    }

    private void setupBiometrics() {
        Executor executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                if (tvAuthStatus != null) {
                    tvAuthStatus.setText("Error biométrico: " + errString);
                }
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                if (tvAuthStatus != null) {
                    tvAuthStatus.setText("¡Verificación Exitosa!");
                }
                irAMainActivity();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                if (tvAuthStatus != null) {
                    tvAuthStatus.setText("Huella no reconocida. Reintenta.");
                }
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("LocalCash Bóveda")
                .setSubtitle("Autentícate con tu huella dactilar")
                .setNegativeButtonText("Cancelar")
                .build();
    }

    private void iniciarAutenticacionBiometrica() {
        if (cuentaActual == null) {
            Toast.makeText(this, "Debes crear una cuenta antes de autenticarte.", Toast.LENGTH_SHORT).show();
            mostrarModoRegistro();
            return;
        }
        if (tvAuthStatus != null) {
            tvAuthStatus.setText("Ingresa con tu huella dactilar...");
        }
        biometricPrompt.authenticate(promptInfo);
    }

    private void registrarCuentaYAutenticar() {
        String titular = etTitular.getText() != null ? etTitular.getText().toString().trim() : "";
        String numeroCuenta = etNumeroCuenta.getText() != null ? etNumeroCuenta.getText().toString().trim() : "";
        String tipoCuenta = etTipoCuenta.getText() != null ? etTipoCuenta.getText().toString().trim() : "";
        String saldoStr = etSaldo.getText() != null ? etSaldo.getText().toString().trim() : "";

        if (TextUtils.isEmpty(titular) || TextUtils.isEmpty(numeroCuenta) || TextUtils.isEmpty(tipoCuenta) || TextUtils.isEmpty(saldoStr)) {
            Toast.makeText(this, "Completa todos los campos para crear tu cuenta.", Toast.LENGTH_SHORT).show();
            return;
        }

        double saldo;
        try {
            saldo = Double.parseDouble(saldoStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Ingresa un saldo numérico válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        Cuenta nuevaCuenta = new Cuenta(numeroCuenta, titular, saldo, tipoCuenta);
        boolean creada = cuentaDAO.crear(nuevaCuenta);

        if (creada) {
            cuentaActual = cuentaDAO.buscarPorNumero(numeroCuenta);
            if (cuentaActual == null) {
                cuentaActual = nuevaCuenta;
            }
            Toast.makeText(this, "¡Cuenta guardada exitosamente! Procede con tu huella.", Toast.LENGTH_SHORT).show();
            mostrarModoLogin();
            iniciarAutenticacionBiometrica();
        } else {
            Toast.makeText(this, "Error al guardar la cuenta en la base de datos.", Toast.LENGTH_SHORT).show();
        }
    }

    private void irAMainActivity() {
        if (cuentaActual == null) {
            ArrayList<Cuenta> cuentas = cuentaDAO.listar();
            if (cuentas.isEmpty()) {
                Toast.makeText(this, "No existe ninguna cuenta registrada.", Toast.LENGTH_SHORT).show();
                mostrarModoRegistro();
                return;
            }
            cuentaActual = cuentas.get(0);
        }

        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.putExtra("EXTRA_CUENTA_ID", cuentaActual.getId());
        intent.putExtra("EXTRA_TITULAR", cuentaActual.getTitular());
        intent.putExtra("EXTRA_NUMERO", cuentaActual.getNumeroCuenta());
        intent.putExtra("EXTRA_TIPO", cuentaActual.getTipoCuenta());
        intent.putExtra("EXTRA_SALDO", cuentaActual.getSaldo());

        startActivity(intent);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        finish();
    }
}
