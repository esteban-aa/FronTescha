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
import androidx.room.Room;

import com.example.teschachatbot_f.database.AppDatabase;
import com.example.teschachatbot_f.database.UsuarioDao;
import com.example.teschachatbot_f.models.Usuario;

public class LoginActivity extends AppCompatActivity {

    Spinner rolSpinner;
    EditText inputField;
    EditText passwordInput;
    Button loginButton;
    TextView registerText;
    TextView forgotPassword;

    SharedPreferences sharedPreferences;
    private AppDatabase db;
    private UsuarioDao usuarioDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login); // Verifica que sea el layout correcto

        rolSpinner = findViewById(R.id.rolSpinner);
        inputField = findViewById(R.id.emailOrMatriculaInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerText = findViewById(R.id.registerText);
        forgotPassword = findViewById(R.id.forgotPassword);

        sharedPreferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);

        // Inicializar base de datos
        db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "tescha-db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()  // Para pruebas, en producción usa async
                .build();
        usuarioDao = db.usuarioDao();

        // Si ya hay sesión iniciada, ir directo al menú
        Usuario adminExistente = usuarioDao.buscarPorIdentificador("admin@tescha.com");
        if (adminExistente == null) {
            Usuario admin = new Usuario("Administrador", "admin@tescha.com", "admin123");
            usuarioDao.insertar(admin);
        }

        // Spinner roles
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rolSpinner.setAdapter(adapter);

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
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        registerText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        forgotPassword.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, olvidasteActivity.class);
            startActivity(intent);
        });

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

            // Login especial administrador hardcoded
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

            // Validar usuario o estudiante en BD
            Usuario usuarioBD = usuarioDao.buscarPorIdentificador(input);
            if (usuarioBD != null && usuarioBD.getPassword().equals(password)) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("logeado", true);
                editor.putString("rol", rol.toLowerCase());
                editor.putString("usuario", input);
                editor.apply();

                Toast.makeText(LoginActivity.this, "Sesión iniciada como " + rol, Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this, menuActivity.class));
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
