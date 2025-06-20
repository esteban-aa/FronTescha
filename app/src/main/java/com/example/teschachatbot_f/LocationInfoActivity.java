package com.example.teschachatbot_f;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teschachatbot_f.models.Edificio;
import com.example.teschachatbot_f.network.ApiService;
import com.example.teschachatbot_f.network.RetrofitClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LocationInfoActivity extends AppCompatActivity implements OnMapReadyCallback {

    private TextView tvNombreZona, tvDescripcion;
    private EditText etNombreZona, etDescripcion;
    private Button btnEditar, btnGuardar;
    private GoogleMap mMap;
    private double latitud = 0.0;
    private double longitud = 0.0;
    private boolean modoEdicion = false;
    private String tipoUsuario = "";
    private String nombreEdificio = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_info);

        // Referencias UI
        tvNombreZona = findViewById(R.id.tvNombreZona);
        tvDescripcion = findViewById(R.id.tvDescripcion);
        etNombreZona = findViewById(R.id.etNombreZona);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnEditar = findViewById(R.id.btnEditar);
        btnGuardar = findViewById(R.id.btnGuardarZona);

        // Ocultar botones y campos de edición por defecto
        btnEditar.setVisibility(View.GONE);
        btnGuardar.setVisibility(View.GONE);
        etNombreZona.setVisibility(View.GONE);
        etDescripcion.setVisibility(View.GONE);

        // Obtener extras de Intent
        nombreEdificio = getIntent().getStringExtra("nombreEdificio");
        tipoUsuario = getIntent().getStringExtra("tipoUsuario");

        if (tipoUsuario != null) {
            tipoUsuario = tipoUsuario.trim().toLowerCase();
        } else {
            tipoUsuario = "";
        }

        Log.d("LocationInfoActivity", "Tipo usuario recibido: '" + tipoUsuario + "'");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapFragment);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Mostrar botón editar solo si es admin
        if (tipoUsuario.equals("admin")) {
            btnEditar.setVisibility(View.VISIBLE);

            btnEditar.setOnClickListener(v -> activarModoEdicion(true));
            btnGuardar.setOnClickListener(v -> guardarCambios());
        }

        // Cargar datos del edificio desde API
        if (nombreEdificio != null && !nombreEdificio.isEmpty()) {
            cargarEdificioPorNombre(nombreEdificio);
        } else {
            Toast.makeText(this, "No se recibió el nombre del edificio", Toast.LENGTH_SHORT).show();
        }
    }

    private void cargarEdificioPorNombre(String nombre) {
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        apiService.getEdificioPorNombre(nombre).enqueue(new Callback<Edificio>() {
            @Override
            public void onResponse(Call<Edificio> call, Response<Edificio> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Edificio edificio = response.body();
                    mostrarInfo(edificio);
                } else {
                    Toast.makeText(LocationInfoActivity.this, "Edificio no encontrado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Edificio> call, Throwable t) {
                Toast.makeText(LocationInfoActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void mostrarInfo(Edificio edificio) {
        // Guardar coordenadas para mostrar en mapa
        latitud = edificio.getCoordenadas()[0];
        longitud = edificio.getCoordenadas()[1];

        if (mMap != null) {
            mostrarUbicacionEnMapa();
        }

        if (tipoUsuario.equals("admin")) {
            // Mostrar campos edición/lectura según modoEdicion
            tvNombreZona.setVisibility(modoEdicion ? View.GONE : View.VISIBLE);
            tvDescripcion.setVisibility(modoEdicion ? View.GONE : View.VISIBLE);

            etNombreZona.setVisibility(modoEdicion ? View.VISIBLE : View.GONE);
            etDescripcion.setVisibility(modoEdicion ? View.VISIBLE : View.GONE);

            etNombreZona.setText(edificio.getNombre());
            etDescripcion.setText(edificio.getDescripcion());

            if (!modoEdicion) {
                tvNombreZona.setText(edificio.getNombre());
                tvDescripcion.setText(edificio.getDescripcion());
            }
        } else {
            // Usuario normal: solo texto
            tvNombreZona.setVisibility(View.VISIBLE);
            tvDescripcion.setVisibility(View.VISIBLE);
            etNombreZona.setVisibility(View.GONE);
            etDescripcion.setVisibility(View.GONE);

            tvNombreZona.setText(edificio.getNombre());
            tvDescripcion.setText(edificio.getDescripcion());

            btnEditar.setVisibility(View.GONE);
            btnGuardar.setVisibility(View.GONE);
        }
    }

    private void activarModoEdicion(boolean activar) {
        modoEdicion = activar;

        btnEditar.setVisibility(activar ? View.GONE : View.VISIBLE);
        btnGuardar.setVisibility(activar ? View.VISIBLE : View.GONE);

        tvNombreZona.setVisibility(activar ? View.GONE : View.VISIBLE);
        tvDescripcion.setVisibility(activar ? View.GONE : View.VISIBLE);

        etNombreZona.setVisibility(activar ? View.VISIBLE : View.GONE);
        etDescripcion.setVisibility(activar ? View.VISIBLE : View.GONE);
    }

    private void guardarCambios() {
        String nuevoNombre = etNombreZona.getText().toString().trim();
        String nuevaDescripcion = etDescripcion.getText().toString().trim();

        if (nuevoNombre.isEmpty() || nuevaDescripcion.isEmpty()) {
            Toast.makeText(this, "Por favor llena todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        // Actualizar solo nombre y descripción (sin tocar coordenadas)
        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);
        Edificio edificioActualizado = new Edificio();
        edificioActualizado.setNombre(nuevoNombre);
        edificioActualizado.setDescripcion(nuevaDescripcion);
        // Mantener coordenadas viejas
        edificioActualizado.setCoordenadas(new double[]{latitud, longitud});

        apiService.actualizarEdificioPorNombre(nombreEdificio, edificioActualizado).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(LocationInfoActivity.this, "Información guardada correctamente", Toast.LENGTH_SHORT).show();
                    // Refrescar pantalla con datos nuevos
                    nombreEdificio = nuevoNombre; // Actualizar nombre local
                    activarModoEdicion(false);
                    cargarEdificioPorNombre(nuevoNombre);
                } else {
                    Toast.makeText(LocationInfoActivity.this, "Error al guardar información", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(LocationInfoActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if (latitud != 0.0 && longitud != 0.0) {
            mostrarUbicacionEnMapa();
        }
    }

    private void mostrarUbicacionEnMapa() {
        LatLng ubicacion = new LatLng(latitud, longitud);
        mMap.clear();
        mMap.addMarker(new MarkerOptions().position(ubicacion).title("Ubicación del edificio"));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacion, 17));
    }
}

