package com.example.teschachatbot_f;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.teschachatbot_f.database.AppDatabase;
import com.example.teschachatbot_f.database.UsuarioDao;
import com.example.teschachatbot_f.models.Usuario;

public class olvidasteActivity extends AppCompatActivity {

    private EditText inputIdentificador;
    private Spinner rolSpinner;
    private Button btnConsultar;
    private TextView resultadoContrasena;
    private TextView linkLogin;

    private UsuarioDao usuarioDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.olvidaste_activity);

        inputIdentificador = findViewById(R.id.emailInput);
        rolSpinner = findViewById(R.id.rolSpinner);
        btnConsultar = findViewById(R.id.btnConsultarContrasena);
        resultadoContrasena = findViewById(R.id.resultadoContrasena);
        linkLogin = findViewById(R.id.linkLogin);

        // Configurar spinner con roles: Usuario y Estudiante
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                new String[]{"Usuario", "Estudiante"}
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rolSpinner.setAdapter(adapter);

        // Cambiar hint e inputType según rol seleccionado
        rolSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String rolSeleccionado = (String) parent.getItemAtPosition(position);
                if (rolSeleccionado.equals("Estudiante")) {
                    inputIdentificador.setHint("Ingresa tu matrícula");
                    inputIdentificador.setInputType(InputType.TYPE_CLASS_TEXT);
                } else { // Usuario
                    inputIdentificador.setHint("Ingresa tu correo electrónico");
                    inputIdentificador.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {}
        });

        // Inicializar base de datos y DAO (con fallback para migraciones)
        AppDatabase db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "usuarios.db")
                .allowMainThreadQueries()  // Solo para pruebas
                .fallbackToDestructiveMigration() // Para evitar errores por migración
                .createFromAsset("usuarios.db")
                .build();

        usuarioDao = db.usuarioDao();

        // Botón consultar contraseña
        btnConsultar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String identificador = inputIdentificador.getText().toString().trim();
                String rolSeleccionado = (String) rolSpinner.getSelectedItem();

                if (identificador.isEmpty()) {
                    resultadoContrasena.setText("Por favor, ingresa el dato requerido.");
                    return;
                }

                // Buscar usuario por identificador y rol
                Usuario usuarioEncontrado = usuarioDao.buscarPorIdentificadorYRol(identificador, rolSeleccionado);

                if (usuarioEncontrado == null) {
                    resultadoContrasena.setText("No se encontró una cuenta con ese dato y rol.");
                    return;
                }

                // Mostrar contraseña
                resultadoContrasena.setText("Tu contraseña es: " + usuarioEncontrado.getPassword());
            }
        });

        // Link para volver a login
        linkLogin.setOnClickListener(v -> {
            startActivity(new Intent(olvidasteActivity.this, LoginActivity.class));
            finish();
        });
    }
}
