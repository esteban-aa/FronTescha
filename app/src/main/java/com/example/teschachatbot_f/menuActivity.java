package com.example.teschachatbot_f;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class menuActivity extends AppCompatActivity {

    Button btnCerrarSesion;
    Button btnMapaInfo, btnControlEscolar, btnHorariosDocentes, btnJefaturaCarrera;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        // Referencias a botones
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnMapaInfo = findViewById(R.id.btnMapaInfo);
        btnControlEscolar = findViewById(R.id.btnControlEscolar);
        btnHorariosDocentes = findViewById(R.id.btnHorariosDocentes);
        btnJefaturaCarrera = findViewById(R.id.btnJefaturaCarrera);

        // Acción del botón "Cerrar sesión"
        btnCerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Limpiar SharedPreferences
                SharedPreferences sharedPreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear(); // Borra todos los datos
                editor.apply();

                // Redirigir al login
                Intent intent = new Intent(menuActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Elimina el historial de actividades
                startActivity(intent);
                finish();
            }
        });

        // Aquí puedes agregar funcionalidad a los demás botones
        // Ejemplo:
        /*
        btnMapaInfo.setOnClickListener(v -> {
            startActivity(new Intent(menuActivity.this, MapaActivity.class));
        });
        */
    }
}
