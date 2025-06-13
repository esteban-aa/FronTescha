package com.example.teschachatbot_f;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class HomeActivity extends AppCompatActivity {

    Button cerrarSesionBtn;
    TextView usuarioTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        usuarioTextView = findViewById(R.id.usuarioTextView);
        cerrarSesionBtn = findViewById(R.id.logoutButton);

        // Recuperar datos de sesión
        SharedPreferences preferences = getSharedPreferences("sesion", MODE_PRIVATE);
        String usuario = preferences.getString("usuario", "Invitado");
        String rol = preferences.getString("rol", "No definido");

        // Mostrar usuario y rol
        usuarioTextView.setText("Bienvenido, " + usuario + "\nRol: " + rol);

        cerrarSesionBtn.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear(); // Borrar sesión
            editor.apply();

            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });
    }
}
