package com.example.teschachatbot_f;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import android.view.LayoutInflater;

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
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

public class menuActivity extends AppCompatActivity {

    private TextView usuarioTextView;
    private Button btnCerrarSesion, btnAgregarUsuario, btnSincronizar;
    private Button btnMapaInfo, btnControlEscolar, btnHorariosDocentes, btnJefaturaCarrera;
    private RecyclerView recyclerUsuarios;

    private AppDatabase db;
    private UsuarioDao usuarioDao;
    private UsuarioAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        usuarioTextView = findViewById(R.id.usuarioTextView);
        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnAgregarUsuario = findViewById(R.id.btnAgregarUsuario);
        btnSincronizar = findViewById(R.id.btnSincronizar);

        btnMapaInfo = findViewById(R.id.btnMapaInfo);
        btnControlEscolar = findViewById(R.id.btnControlEscolar);
        btnHorariosDocentes = findViewById(R.id.btnHorariosDocentes);
        btnJefaturaCarrera = findViewById(R.id.btnJefaturaCarrera);

        recyclerUsuarios = findViewById(R.id.recyclerUsuarios);

        SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
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
                    Toast.makeText(menuActivity.this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                }
            });

            recyclerUsuarios.setLayoutManager(new LinearLayoutManager(this));
            recyclerUsuarios.setAdapter(adapter);

            btnAgregarUsuario.setOnClickListener(v -> mostrarDialogAgregarUsuario());

            btnSincronizar.setOnClickListener(v -> sincronizarUsuariosDesdeBackend());
        }

        btnCerrarSesion.setOnClickListener(v -> {
            SharedPreferences.Editor editor = preferences.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(menuActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });

        // Aquí la corrección para el botón mapa:
        btnMapaInfo.setOnClickListener(v -> {
            Intent intent = new Intent(menuActivity.this, DestinosActivity.class);
            startActivity(intent);
        });

        // Puedes agregar más listeners para otros botones si quieres
        /*
        btnControlEscolar.setOnClickListener(...);
        btnHorariosDocentes.setOnClickListener(...);
        btnJefaturaCarrera.setOnClickListener(...);
        */
    }

    private void mostrarDialogAgregarUsuario() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Agregar Usuario");

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_agregar_usuario, null);

        Spinner spinnerRol = view.findViewById(R.id.spinnerRol);
        EditText inputCorreoMatricula = view.findViewById(R.id.inputCorreoMatricula);
        EditText inputPassword = view.findViewById(R.id.inputPassword);

        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(adapterSpinner);

        spinnerRol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view1, int i, long l) {
                String seleccionado = spinnerRol.getSelectedItem().toString();
                if (seleccionado.equals("Estudiante")) {
                    inputCorreoMatricula.setHint("Matrícula");
                } else {
                    inputCorreoMatricula.setHint("Correo");
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {}
        });

        builder.setView(view);
        builder.setPositiveButton("Agregar", (dialog, which) -> {
            String tipo = spinnerRol.getSelectedItem().toString();
            String input = inputCorreoMatricula.getText().toString().trim();
            String password = inputPassword.getText().toString().trim();

            if (input.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            Usuario nuevo = new Usuario(input, password);
            usuarioDao.insertar(nuevo);
            recargarLista();
            Toast.makeText(this, tipo + " agregado", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    private void sincronizarUsuariosDesdeBackend() {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL("http://10.0.2.2:5000/usuarios");
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                int responseCode = conn.getResponseCode();
                if (responseCode == 200) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuilder jsonBuilder = new StringBuilder();
                    String line;
                    while ((line = in.readLine()) != null) {
                        jsonBuilder.append(line);
                    }
                    in.close();

                    JSONArray usuariosJson = new JSONArray(jsonBuilder.toString());

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
                        Toast.makeText(menuActivity.this, "Sincronización completa", Toast.LENGTH_SHORT).show();
                    });

                } else {
                    runOnUiThread(() -> Toast.makeText(menuActivity.this, "Error de servidor: " + responseCode, Toast.LENGTH_SHORT).show());
                }
            } catch (Exception e) {
                e.printStackTrace();
                runOnUiThread(() -> Toast.makeText(menuActivity.this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            } finally {
                if (conn != null) conn.disconnect();
            }
        }).start();
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
