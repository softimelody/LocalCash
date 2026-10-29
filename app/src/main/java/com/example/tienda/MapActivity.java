package com.example.tienda;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.tienda.dao.MovimientoDAO;
import com.example.tienda.model.Movimiento;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private MovimientoDAO movimientoDAO;

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                Boolean fineLocation = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                Boolean coarseLocation = result.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                if (mMap != null && (Boolean.TRUE.equals(fineLocation) || Boolean.TRUE.equals(coarseLocation))) {
                    try {
                        mMap.setMyLocationEnabled(true);
                    } catch (SecurityException ignored) {}
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Inicializador explícito del motor de renderizado de Google Maps (Renderer LATEST)
        try {
            MapsInitializer.initialize(getApplicationContext(), MapsInitializer.Renderer.LATEST, renderer -> {});
        } catch (Exception ignored) {}

        setContentView(R.layout.activity_map);

        View btnBackMap = findViewById(R.id.btnBackMap);
        movimientoDAO = new MovimientoDAO(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        if (btnBackMap != null) {
            btnBackMap.setOnClickListener(v -> cerrarConAnimacion());
        }

        getOnBackPressedDispatcher().addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                cerrarConAnimacion();
            }
        });
    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;

        // Ajustes explícitos de la vista e interacción de Google Maps
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setZoomGesturesEnabled(true);
        mMap.getUiSettings().setScrollGesturesEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);

        checkPermissions();

        // Cargar movimientos reales de SQLite en Google Maps
        LatLng centroSantiago = new LatLng(-33.4489, -70.6693);
        ArrayList<Movimiento> movimientos = movimientoDAO.listar();

        if (movimientos.isEmpty()) {
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(centroSantiago, 14f));
        } else {
            LatLng posFinal = centroSantiago;
            boolean tieneUbicacion = false;
            for (Movimiento m : movimientos) {
                if (m.getLatitud() != 0 && m.getLongitud() != 0) {
                    LatLng itemPos = new LatLng(m.getLatitud(), m.getLongitud());
                    posFinal = itemPos;
                    tieneUbicacion = true;

                    mMap.addMarker(new MarkerOptions()
                            .position(itemPos)
                            .title(m.getTipo() + ": " + MainActivity.formatCLP(m.getMonto()))
                            .snippet(m.getDescripcion() + " • " + m.getFecha()));
                }
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(tieneUbicacion ? posFinal : centroSantiago, 14f));
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (mMap != null) {
                try {
                    mMap.setMyLocationEnabled(true);
                } catch (SecurityException ignored) {}
            }
        } else {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void cerrarConAnimacion() {
        finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }
}
