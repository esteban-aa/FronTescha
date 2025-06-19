package com.example.teschachatbot_f;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.InputType;
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
    TextView forgotPassword;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login); // Asegúrate que este es el layout correcto

        // Referencias a elementos del layout
        rolSpinner = findViewById(R.id.rolSpinner);
        inputField = findViewById(R.id.emailOrMatriculaInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerText = findViewById(R.id.registerText);
        forgotPassword = findViewById(R.id.forgotPassword);

        // Inicializar SharedPreferences
        sharedPreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);

        // Si ya hay sesión guardada, redirigir a menuActivity
        if (sharedPreferences.getBoolean("logeado", false)) {
            startActivity(new Intent(LoginActivity.this, menuActivity.class));
            finish();
        }

        // Llenar el Spinner con los roles (asegúrate que roles_array incluye "Administrador")
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rolSpinner.setAdapter(adapter);

        // Cambiar el hint dependiendo del rol seleccionado
        rolSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                String rol = parent.getItemAtPosition(position).toString();
                if (rol.equals("Usuario")) {
                    inputField.setHint("Correo electrónico");
                    inputField.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                } else if (rol.equals("Estudiante")) {
                    inputField.setHint("Matrícula");
                    inputField.setInputType(InputType.TYPE_CLASS_TEXT);
                } else if (rol.equals("Administrador")) {
                    inputField.setHint("Usuario administrador");
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

        // Acción del texto "¿Olvidaste tu contraseña?"
        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, olvidasteActivity.class);
            startActivity(intent);
        });

        // Acción del botón "INICIAR SESIÓN"
        loginButton.setOnClickListener(v -> {
            String rol = rolSpinner.getSelectedItem().toString();
            String input = inputField.getText().toString().trim();
            String password = passwordInput.getText().toString().trim();

            if (input.isEmpty()) {
                inputField.setError("Por favor ingresa " + (rol.equals("Usuario") ? "tu correo" : (rol.equals("Administrador") ? "tu usuario" : "tu matrícula")));
                inputField.requestFocus();
                return;
            }

            if (password.isEmpty()) {
                passwordInput.setError("Por favor ingresa tu contraseña");
                passwordInput.requestFocus();
                return;
            }

            // Login especial para administrador
            if (rol.equals("Administrador") && input.equals("admin") && password.equals("admin123")) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("logeado", true);
                editor.putString("rol", "administrador");
                editor.putString("usuario", "Administrador");
                editor.apply();

                Toast.makeText(LoginActivity.this, "Sesión iniciada como Administrador", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, menuActivity.class));
                finish();
                return;
            }

            // Login simulado para Usuario y Estudiante (aquí puedes agregar validación real)
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean("logeado", true);
            editor.putString("rol", rol.toLowerCase());
            editor.putString("usuario", input);
            editor.apply();

            Toast.makeText(LoginActivity.this, "Sesión iniciada como " + rol, Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, menuActivity.class));
            finish();
        });
    }
}
