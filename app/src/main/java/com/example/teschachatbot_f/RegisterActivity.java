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
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.example.teschachatbot_f.database.AppDatabase;
import com.example.teschachatbot_f.database.UsuarioDao;
import com.example.teschachatbot_f.models.Usuario;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailOrMatriculaInput, passwordInput, confirmPasswordInput;
    private Spinner rolSpinner;
    private Button registerButton;
    private TextView goToLoginText;

    private AppDatabase db;
    private UsuarioDao usuarioDao;

    private OkHttpClient client = new OkHttpClient();

    private static final String URL_BACKEND = "http://192.168.1.191:5000/api/usuarios/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_activity);

        emailOrMatriculaInput = findViewById(R.id.emailOrMatriculaInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        rolSpinner = findViewById(R.id.rolSpinner);
        registerButton = findViewById(R.id.registerButton);
        goToLoginText = findViewById(R.id.goToLoginText);

        // Configurar base de datos Room
        db = Room.databaseBuilder(getApplicationContext(),
                        AppDatabase.class, "tescha-db")
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
        usuarioDao = db.usuarioDao();

        // Spinner roles
        String[] roles = {"Usuario", "Estudiante"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        rolSpinner.setAdapter(adapter);

        rolSpinner.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(android.widget.AdapterView<?> parent, View view, int position, long id) {
                String rol = parent.getItemAtPosition(position).toString();
                if (rol.equals("Usuario")) {
                    emailOrMatriculaInput.setHint("Correo electrónico");
                    emailOrMatriculaInput.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                } else if (rol.equals("Estudiante")) {
                    emailOrMatriculaInput.setHint("Matrícula");
                    emailOrMatriculaInput.setInputType(InputType.TYPE_CLASS_TEXT);
                }
            }

            @Override
            public void onNothingSelected(android.widget.AdapterView<?> parent) {
            }
        });

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = emailOrMatriculaInput.getText().toString().trim();
                String password = passwordInput.getText().toString().trim();
                String confirmPassword = confirmPasswordInput.getText().toString().trim();
                String rol = rolSpinner.getSelectedItem().toString();

                if (input.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    Toast.makeText(RegisterActivity.this, "Las contraseñas no coinciden.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Guardar en BD local
                Usuario nuevo = new Usuario(rol, input, password);
                usuarioDao.insertar(nuevo);

                // Enviar datos al backend
                enviarDatosAlBackend(rol, input, password);
            }
        });

        goToLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void enviarDatosAlBackend(String rol, String identificador, String password) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("rol", rol);
            jsonObject.put("identificador", identificador);
            jsonObject.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
            runOnUiThread(() ->
                    Toast.makeText(this, "Error preparando datos para el servidor", Toast.LENGTH_SHORT).show()
            );
            return;
        }

        RequestBody body = RequestBody.create(jsonObject.toString(), JSON);

        Request request = new Request.Builder()
                .url(URL_BACKEND)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(RegisterActivity.this, "Error al conectar con el servidor", Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String respuesta = response.body().string();
                    try {
                        JSONObject json = new JSONObject(respuesta);
                        String idBackend = json.getString("id");

                        runOnUiThread(() -> {
                            Toast.makeText(RegisterActivity.this, "Usuario registrado con ID backend: " + idBackend, Toast.LENGTH_SHORT).show();

                            // Crear y guardar usuario local
                            Usuario nuevoUsuario = new Usuario(rol, identificador, password);
                            nuevoUsuario.setIdBackend(idBackend);
                            usuarioDao.insertar(nuevoUsuario);

                            // Volver al login
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish();  // Finaliza el Register para que no puedas volver con back
                        });
                    } catch (JSONException e) {
                        runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Error procesando respuesta", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    runOnUiThread(() -> Toast.makeText(RegisterActivity.this, "Error en registro: " + response.message(), Toast.LENGTH_LONG).show());
                }
            }

        });
    }
}
