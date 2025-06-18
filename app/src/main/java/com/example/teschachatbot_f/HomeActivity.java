package com.example.teschachatbot_f;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.EditText;
import java.io.FileOutputStream;
import java.io.IOException;


import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.teschachatbot_f.database.AppDatabase;
import com.example.teschachatbot_f.database.UsuarioDao;
import com.example.teschachatbot_f.models.Usuario;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class HomeActivity extends AppCompatActivity {

    private TextView usuarioTextView;
    private Button cerrarSesionBtn, btnAgregarUsuario, btnSincronizar;
    private RecyclerView recyclerUsuarios;

    private AppDatabase db;
    private UsuarioDao usuarioDao;
    private UsuarioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        // Crear el archivo de ejemplo si no existe aún
        try {
            String texto = "Este es un archivo de prueba para leer con FileInputStream.";
            FileOutputStream fos = openFileOutput("ejemplo.txt", MODE_PRIVATE);
            fos.write(texto.getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

// Botón para abrir la actividad que lee el archivo
        Button btnLeerArchivo = findViewById(R.id.btnLeerArchivo);
        btnLeerArchivo.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, ReadFileActivity.class);
            startActivity(intent);
        });


        usuarioTextView = findViewById(R.id.usuarioTextView);
        cerrarSesionBtn = findViewById(R.id.logoutButton);

        btnAgregarUsuario = findViewById(R.id.btnAgregarUsuario);
        btnSincronizar = findViewById(R.id.btnSincronizar);
        recyclerUsuarios = findViewById(R.id.recyclerUsuarios);

        SharedPreferences preferences = getSharedPreferences("sesion", MODE_PRIVATE);
        String usuario = preferences.getString("usuario", "Invitado");
        String rol = preferences.getString("rol", "No definido");

        usuarioTextView.setText("Bienvenido, " + usuario + "\nRol: " + rol);

        if (!rol.equals("administrador")) {
            btnAgregarUsuario.setVisibility(View.GONE);
            btnSincronizar.setVisibility(View.GONE);
            recyclerUsuarios.setVisibility(View.GONE);
        } else {
            db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "tescha-db")
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries()
                    .build();
            usuarioDao = db.usuarioDao();

            List<Usuario> lista = usuarioDao.obtenerTodos();

            adapter = new UsuarioAdapter(lista, new UsuarioAdapter.OnUsuarioClickListener() {
                @Override
                public void onEditarClick(Usuario usuario) {
                    mostrarDialogEditar(usuario);
                }

                @Override
                public void onEliminarClick(Usuario usuario) {
                    usuarioDao.eliminar(usuario);
                    recargarLista();
                    Toast.makeText(HomeActivity.this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                }
            });

            recyclerUsuarios.setLayoutManager(new LinearLayoutManager(this));
            recyclerUsuarios.setAdapter(adapter);

            btnAgregarUsuario.setOnClickListener(v -> {
                Usuario nuevo = new Usuario("Usuario " + System.currentTimeMillis(), "correo@example.com");
                usuarioDao.insertar(nuevo);
                recargarLista();
                Toast.makeText(this, "Usuario agregado", Toast.LENGTH_SHORT).show();
            });

            btnSincronizar.setOnClickListener(v -> {
                new Thread(() -> {
                    try {
                        URL url = new URL("http://10.0.2.2:5000/usuarios");
                        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                        conn.setRequestMethod("GET");

                        if (conn.getResponseCode() == 200) {
                            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                            StringBuilder jsonBuilder = new StringBuilder();
                            String line;
                            while ((line = in.readLine()) != null) {
                                jsonBuilder.append(line);
                            }
                            in.close();

                            JSONArray usuariosJson = new JSONArray(jsonBuilder.toString());

                            // Limpiar tabla si tienes método eliminarTodos()
                            // usuarioDao.eliminarTodos(); // Descomenta si existe
                            for (Usuario u : usuarioDao.obtenerTodos()) {
                                usuarioDao.eliminar(u);
                            }

                            for (int i = 0; i < usuariosJson.length(); i++) {
                                JSONObject obj = usuariosJson.getJSONObject(i);
                                String nombre = obj.getString("nombre");
                                String correo = obj.getString("correo");

                                Usuario u = new Usuario(nombre, correo);
                                usuarioDao.insertar(u);
                            }

                            runOnUiThread(() -> {
                                recargarLista();
                                Toast.makeText(HomeActivity.this, "Sincronización completa", Toast.LENGTH_SHORT).show();
                            });

                        } else {
                            runOnUiThread(() -> {
                                try {
                                    Toast.makeText(HomeActivity.this, "Error de servidor: " + conn.getResponseCode(), Toast.LENGTH_SHORT).show();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    Toast.makeText(HomeActivity.this, "Error al obtener código de respuesta", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        conn.disconnect();
                    } catch (Exception e) {
                        e.printStackTrace();
                        runOnUiThread(() ->
                                Toast.makeText(HomeActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show()
                        );
                    }
                }).start();
            });
        }

        cerrarSesionBtn.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            finish();
        });
    }

    private void recargarLista() {
        List<Usuario> lista = usuarioDao.obtenerTodos();
        adapter.actualizarLista(lista);
    }

    private void mostrarDialogEditar(Usuario usuario) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Editar Usuario");

        View viewInflated = LayoutInflater.from(this).inflate(R.layout.dialog_editar_usuario, null);
        final EditText inputNombre = viewInflated.findViewById(R.id.inputNombre);
        final EditText inputCorreo = viewInflated.findViewById(R.id.inputCorreo);

        inputNombre.setText(usuario.getNombre());
        inputCorreo.setText(usuario.getCorreo());

        builder.setView(viewInflated);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoNombre = inputNombre.getText().toString().trim();
            String nuevoCorreo = inputCorreo.getText().toString().trim();

            if (nuevoNombre.isEmpty() || nuevoCorreo.isEmpty()) {
                Toast.makeText(this, "Los campos no pueden estar vacíos", Toast.LENGTH_SHORT).show();
            } else {
                usuario.setNombre(nuevoNombre);
                usuario.setCorreo(nuevoCorreo);
                usuarioDao.actualizar(usuario);
                recargarLista();
                Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }
}
