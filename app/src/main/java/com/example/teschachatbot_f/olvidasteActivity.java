package com.example.teschachatbot_f;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class olvidasteActivity extends AppCompatActivity {

    private EditText emailInput;
    private Spinner rolSpinner;
    private Button btnConsultar;
    private TextView resultadoContrasena;
    private TextView linkLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.olvidaste_activity); // Asegúrate que tu XML se llame así

        // Inicializar vistas
        emailInput = findViewById(R.id.emailInput);
        rolSpinner = findViewById(R.id.rolSpinner);
        btnConsultar = findViewById(R.id.btnConsultarContrasena);
        resultadoContrasena = findViewById(R.id.resultadoContrasena);
        linkLogin = findViewById(R.id.linkLogin);

        // Configurar el spinner con los roles
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Estudiante", "Invitado"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rolSpinner.setAdapter(adapter);
        

        // Lógica del botón para "Consultar Contraseña"
        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailInput.getText().toString().trim();
                String rol = rolSpinner.getSelectedItem().toString();

                if (email.isEmpty()) {
                    resultadoContrasena.setText("Por favor, ingresa tu correo.");
                } else {
                    // Simulación de recuperación
                    String contrasenaEjemplo = "12345"; // Aquí podrías conectar con SQLite
                    resultadoContrasena.setText("Tu contraseña para el rol " + rol + " es: " + contrasenaEjemplo);
                }
            }
        });

        // Acción para "INICIAR SESIÓN"
        linkLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(olvidasteActivity.this, LoginActivity.class);
                startActivity(intent);
                finish(); // Opcional: cerrar esta actividad
            }
        });
    }
}
