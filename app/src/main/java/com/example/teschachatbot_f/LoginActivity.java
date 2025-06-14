package com.example.teschachatbot_f;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    Spinner rolSpinner;
    EditText inputField;
    EditText passwordInput;
    Button loginButton;
    TextView registerText;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login); // Asegúrate de que tu archivo login.xml tenga este nombre

        // Referencias a elementos del layout
        rolSpinner = findViewById(R.id.rolSpinner);
        inputField = findViewById(R.id.emailOrMatriculaInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerText = findViewById(R.id.registerText);

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);

        // Si ya hay sesión guardada, redirigir a HomeActivity
        if (sharedPreferences.getBoolean("logeado", false)) {
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        }

        // Llenar el Spinner con los roles
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rolSpinner.setAdapter(adapter);

        // Cambiar el hint dependiendo del rol
        rolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String rol = parent.getItemAtPosition(position).toString();
                if (rol.equals("Usuario")) {
                    inputField.setHint("Correo electrónico");
                    inputField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                } else if (rol.equals("Estudiante")) {
                    inputField.setHint("Matrícula");
                    inputField.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // No hacer nada
            }
        });

        // Acción del texto "¿No tienes cuenta? Regístrate"
        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        // Acción del botón "INICIAR SESIÓN"
        loginButton.setOnClickListener(v -> {
            String rol = rolSpinner.getSelectedItem().toString();
            String input = inputField.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (input.isEmpty()) {
                inputField.setError("Por favor ingresa " + (rol.equals("Usuario") ? "tu correo" : "tu matrícula"));
                inputField.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                passwordInput.setError("Por favor ingresa tu contraseña");
                passwordInput.requestFocus();
                return;
            }

            // Simulación de login (aquí podrías consultar tu base de datos o servidor)
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("logeado", true);
            editor.putString("rol", rol);
            editor.putString("usuario", input);
            editor.apply();

            Toast.makeText(LoginActivity.this, "Sesión iniciada como " + rol, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
            finish();
        });
    }
}
