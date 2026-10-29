package com.example.tienda;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;

import com.example.tienda.dao.MovimientoDAO;
import com.example.tienda.model.Movimiento;

import java.util.ArrayList;

public class MovimientosActivity extends AppCompatActivity {

    private LinearLayout containerMovimientosActivity;
    private TextView tvEmptyMovimientos;
    private MovimientoDAO movimientoDAO;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movimientos);

        View btnBackMovimientos = findViewById(R.id.btnBackMovimientos);
        containerMovimientosActivity = findViewById(R.id.containerMovimientosActivity);
        tvEmptyMovimientos = findViewById(R.id.tvEmptyMovimientos);

        if (btnBackMovimientos != null) {
            btnBackMovimientos.setOnClickListener(v -> cerrarConAnimacion());
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                cerrarConAnimacion();
            }
        });

        movimientoDAO = new MovimientoDAO(this);

        cargarMovimientosDesdeDB();
    }

    private void cargarMovimientosDesdeDB() {
        if (containerMovimientosActivity == null) return;

        containerMovimientosActivity.removeAllViews();
        ArrayList<Movimiento> movimientos = movimientoDAO.listar();

        if (movimientos.isEmpty()) {
            if (tvEmptyMovimientos != null) {
                tvEmptyMovimientos.setVisibility(View.VISIBLE);
            }
            return;
        }

        if (tvEmptyMovimientos != null) {
            tvEmptyMovimientos.setVisibility(View.GONE);
        }

        for (Movimiento m : movimientos) {
            View itemView = getLayoutInflater().inflate(R.layout.item_movimiento, containerMovimientosActivity, false);
            TextView tvDesc = itemView.findViewById(R.id.tvMovimientoDesc);
            TextView tvFecha = itemView.findViewById(R.id.tvMovimientoFecha);
            TextView tvMonto = itemView.findViewById(R.id.tvMovimientoMonto);

            if (tvDesc != null) {
                tvDesc.setText(m.getDescripcion());
            }
            if (tvFecha != null) {
                tvFecha.setText(m.getFecha() + " • " + m.getTipo());
            }

            boolean isIngreso = "INGRESO".equalsIgnoreCase(m.getTipo());
            if (tvMonto != null) {
                tvMonto.setText((isIngreso ? "+" : "-") + MainActivity.formatCLP(m.getMonto()));
                tvMonto.setTextColor(isIngreso ? Color.parseColor("#00B894") : Color.parseColor("#D63031"));
            }

            containerMovimientosActivity.addView(itemView);
        }
    }

    private void cerrarConAnimacion() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
