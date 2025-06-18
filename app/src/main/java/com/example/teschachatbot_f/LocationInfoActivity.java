package com.example.teschachatbot_f;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LocationInfoActivity extends AppCompatActivity {

    private EditText etNombreZona, etDescripcion;
    private Button btnGuardarZona;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_info);

        // Enlazar elementos de la vista
        etNombreZona = findViewById(R.id.etNombreZona);
        etDescripcion = findViewById(R.id.etDescripcion);
        btnGuardarZona = findViewById(R.id.btnGuardarZona);

        // Acciones al dar clic en Guardar
        btnGuardarZona.setOnClickListener(v -> {
            String nombre = etNombreZona.getText().toString().trim();
            String descripcion = etDescripcion.getText().toString().trim();

            if (nombre.isEmpty() || descripcion.isEmpty()) {
                Toast.makeText(this, "Por favor completa todos los campos.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Aquí iría la lógica para guardar la información (a SQLite, API, etc.)
            Toast.makeText(this, "Zona guardada: " + nombre, Toast.LENGTH_SHORT).show();
        });
    }
}
