package com.example.teschachatbot_f;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ViewLocationActivity extends AppCompatActivity {

    private TextView tvNombreZona, tvDescripcionZona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location);

        tvNombreZona = findViewById(R.id.tvNombreZona);
        tvDescripcionZona = findViewById(R.id.tvDescripcionZona);

        // Aquí se podrían recibir los datos desde intent o cargar desde la BD
        String nombre = "Edificio A"; // Simulado
        String descripcion = "Cerca del laboratorio de redes"; // Simulado

        tvNombreZona.setText("Zona: " + nombre);
        tvDescripcionZona.setText("Descripción: " + descripcion);

        // El mapa se carga después cuando se agregue Google Maps
    }
}
