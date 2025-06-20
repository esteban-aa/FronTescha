package com.example.teschachatbot_f;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class JefaturaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap googleMap;

    // Coordenadas del Edificio Bicentenario
    private final LatLng coordenadasBicentenario = new LatLng(19.2328927, -98.8412992);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.jefatura_activity);

        TextView textView = findViewById(R.id.textoJefatura);
        textView.setText("Bienvenido a la sección de Jefatura de Carrera.");

        // Inicializa el fragmento del mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapaJefatura);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;

        googleMap.addMarker(new MarkerOptions()
                .position(coordenadasBicentenario)
                .title("Edificio Bicentenario")
                .snippet("Jefaturas de Carrera"));

        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordenadasBicentenario, 17f));
    }
}
