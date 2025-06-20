package com.example.teschachatbot_f;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import com.example.teschachatbot_f.database.AppDatabase;
import com.example.teschachatbot_f.database.UsuarioDao;
import com.example.teschachatbot_f.models.Usuario;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class menuActivity extends AppCompatActivity {

    private TextView usuarioTextView, currentLocationText;
    private Button btnCerrarSesion, btnAgregarUsuario, btnSincronizar;
    private Button btnMapaInfo, btnControlEscolar, btnHorariosDocentes, btnJefaturaCarrera;
    private RecyclerView recyclerUsuarios;

    private AppDatabase db;
    private UsuarioDao usuarioDao;
    private UsuarioAdapter adapter;

    private FusedLocationProviderClient fusedLocationClient;

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;
    private OkHttpClient client = new OkHttpClient();
    private static final String URL_BACKEND = "http://192.168.1.191:5000/api/usuarios/"; // Cambia esta URL por la correcta

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_activity);

        usuarioTextView = findViewById(R.id.usuarioTextView);
        currentLocationText = findViewById(R.id.currentLocationText);

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnAgregarUsuario = findViewById(R.id.btnAgregarUsuario);
        btnMapaInfo = findViewById(R.id.btnMapaInfo);
        btnControlEscolar = findViewById(R.id.btnControlEscolar);
        btnHorariosDocentes = findViewById(R.id.btnHorariosDocentes);
        btnJefaturaCarrera = findViewById(R.id.btnJefaturaCarrera);
        recyclerUsuarios = findViewById(R.id.recyclerUsuarios);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SharedPreferences preferences = getSharedPreferences("sesion", Context.MODE_PRIVATE);
        String usuario = preferences.getString("usuario", "Invitado");
        String rol = preferences.getString("rol", "No definido");

        usuarioTextView.setText("Bienvenido, " + usuario + "\nRol: " + rol);

        checkLocationPermissionAndGetLocation();

        if (!rol.equals("administrador")) {
            btnAgregarUsuario.setVisibility(View.GONE);
            recyclerUsuarios.setVisibility(View.GONE);
            if (btnSincronizar != null) btnSincronizar.setVisibility(View.GONE);
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
                    String idBackend = usuario.getIdBackend();
                    usuarioDao.eliminar(usuario);
                    eliminarUsuarioDelBackend(idBackend);
                    recargarLista();
                    Toast.makeText(menuActivity.this, "Usuario eliminado", Toast.LENGTH_SHORT).show();
                }
            });

            recyclerUsuarios.setLayoutManager(new LinearLayoutManager(this));
            recyclerUsuarios.setAdapter(adapter);

            btnAgregarUsuario.setOnClickListener(v -> mostrarDialogAgregarUsuario());

            if (btnSincronizar != null) {
                btnSincronizar.setOnClickListener(v -> sincronizarUsuariosDesdeBackend());
            }
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

        // ✅ Enlaces de botones
        btnMapaInfo.setOnClickListener(v -> {
            Intent intent = new Intent(menuActivity.this, DestinosActivity.class);
            startActivity(intent);
        });

        btnControlEscolar.setOnClickListener(v -> {
            Intent intent = new Intent(menuActivity.this, HorariosControlEscolarActivity.class);
            startActivity(intent);
        });

        btnHorariosDocentes.setOnClickListener(v -> {
            Intent intent = new Intent(menuActivity.this, HorariosDocentesActivity.class);
            startActivity(intent);
        });

        btnJefaturaCarrera.setOnClickListener(v -> {
            Intent intent = new Intent(menuActivity.this, JefaturaActivity.class);
            startActivity(intent);
        });
    }




    private void checkLocationPermissionAndGetLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        } else {
            obtenerUbicacionActual();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacionActual();
            } else {
                currentLocationText.setText("Permiso de ubicación denegado");
            }
        }
    }

    private void obtenerUbicacionActual() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            currentLocationText.setText("Permiso de ubicación no otorgado");
            return;
        }

        try {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            geocodificarUbicacion(location.getLatitude(), location.getLongitude());
                        } else {
                            currentLocationText.setText("No se pudo obtener la ubicación");
                        }
                    })
                    .addOnFailureListener(e -> currentLocationText.setText("Error al obtener la ubicación"));
        } catch (SecurityException e) {
            e.printStackTrace();
            currentLocationText.setText("Excepción de seguridad: permiso no concedido");
        }
    }

    private void geocodificarUbicacion(double lat, double lon) {
        try {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(lat, lon, 1);
            if (!addresses.isEmpty()) {
                Address address = addresses.get(0);
                String direccion = address.getAddressLine(0);
                currentLocationText.setText("Ubicación Actual: " + direccion);
            } else {
                currentLocationText.setText("Dirección no encontrada");
            }
        } catch (Exception e) {
            e.printStackTrace();
            currentLocationText.setText("Error al obtener dirección");
        }
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
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
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

            Usuario nuevo = new Usuario(tipo, input, password);
            usuarioDao.insertar(nuevo);
            enviarDatosAlBackend(tipo, input, password);
            recargarLista();
            Toast.makeText(this, tipo + " agregado", Toast.LENGTH_SHORT).show();
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
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
                    Toast.makeText(menuActivity.this, "Error preparando datos para el servidor", Toast.LENGTH_SHORT).show()
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
                        Toast.makeText(menuActivity.this, "Error al conectar con el servidor", Toast.LENGTH_LONG).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String respuesta = response.body().string();
                    try {
                        JSONObject json = new JSONObject(respuesta);
                        String idBackend = json.getString("id");

                        runOnUiThread(() -> Toast.makeText(menuActivity.this, "Usuario registrado en el backend con ID: " + idBackend, Toast.LENGTH_SHORT).show());

                        // Guardar el usuario localmente con idBackend
                        Usuario nuevoUsuario = new Usuario(rol, identificador, password);
                        nuevoUsuario.setIdBackend(idBackend);
                        usuarioDao.insertar(nuevoUsuario);
                        recargarLista();

                    } catch (JSONException e) {
                        runOnUiThread(() ->
                                Toast.makeText(menuActivity.this, "Error procesando respuesta del backend", Toast.LENGTH_SHORT).show()
                        );
                    }
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(menuActivity.this, "Error en el registro: " + response.message(), Toast.LENGTH_LONG).show()
                    );
                }
            }
        });
    }

    private void eliminarUsuarioDelBackend(String idBackend) {
        if (idBackend == null || idBackend.isEmpty()) {
            runOnUiThread(() -> Toast.makeText(menuActivity.this, "ID del backend no válido", Toast.LENGTH_SHORT).show());
            return;
        }

        Request request = new Request.Builder()
                .url(URL_BACKEND + idBackend)
                .delete()
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() ->
                        Toast.makeText(menuActivity.this, "Error al eliminar en el backend", Toast.LENGTH_SHORT).show()
                );
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    runOnUiThread(() ->
                            Toast.makeText(menuActivity.this, "Eliminado también del backend", Toast.LENGTH_SHORT).show()
                    );
                } else {
                    runOnUiThread(() ->
                            Toast.makeText(menuActivity.this, "No se pudo eliminar del backend", Toast.LENGTH_SHORT).show()
                    );
                }
            }
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
        final Spinner spinnerRol = viewInflated.findViewById(R.id.spinnerRol);
        final EditText inputIdentificador = viewInflated.findViewById(R.id.inputIdentificador);
        final EditText inputPassword = viewInflated.findViewById(R.id.inputPassword);

        ArrayAdapter<CharSequence> adapterSpinner = ArrayAdapter.createFromResource(this,
                R.array.roles_array, android.R.layout.simple_spinner_item);
        adapterSpinner.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRol.setAdapter(adapterSpinner);

        if (usuario.getRol() != null) {
            int spinnerPosition = adapterSpinner.getPosition(usuario.getRol());
            spinnerRol.setSelection(spinnerPosition);
        }
        inputIdentificador.setText(usuario.getIdentificador());
        inputPassword.setText(usuario.getPassword());

        spinnerRol.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String rol = parent.getItemAtPosition(position).toString();
                if (rol.equals("Estudiante")) {
                    inputIdentificador.setHint("Matrícula");
                    inputIdentificador.setInputType(android.text.InputType.TYPE_CLASS_TEXT);
                } else {
                    inputIdentificador.setHint("Correo");
                    inputIdentificador.setInputType(android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        builder.setView(viewInflated);

        builder.setPositiveButton("Guardar", (dialog, which) -> {
            String nuevoRol = spinnerRol.getSelectedItem().toString();
            String nuevoIdentificador = inputIdentificador.getText().toString().trim();
            String nuevoPassword = inputPassword.getText().toString().trim();

            if (nuevoRol.isEmpty() || nuevoIdentificador.isEmpty() || nuevoPassword.isEmpty()) {
                Toast.makeText(this, "Los campos no pueden estar vacíos", Toast.LENGTH_SHORT).show();
            } else {
                usuario.setRol(nuevoRol);
                usuario.setIdentificador(nuevoIdentificador);
                usuario.setPassword(nuevoPassword);
                usuarioDao.actualizar(usuario);
                recargarLista();
                Toast.makeText(this, "Usuario actualizado", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("Cancelar", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // Opcional: método para obtener token si usas autenticación con token
    private String obtenerToken() {
        SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
        return prefs.getString("token", null);
    }

    // Método para sincronizar usuarios desde backend (opcional, si usas btnSincronizar)
    private void sincronizarUsuariosDesdeBackend() {
        new Thread(() -> {
            HttpURLConnection conn = null;
            try {
                URL url = new URL(URL_BACKEND);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                SharedPreferences prefs = getSharedPreferences("sesion", MODE_PRIVATE);
                String token = prefs.getString("token", null);

                if (token != null) {
                    conn.setRequestProperty("Authorization", "Bearer " + token);
                }

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

                    usuarioDao.limpiarUsuarios();

                    for (int i = 0; i < usuariosJson.length(); i++) {
                        JSONObject obj = usuariosJson.getJSONObject(i);
                        String nombre = obj.getString("usuario");
                        String correo = obj.getString("correo");
                        String rol = "Usuario"; // Ajusta según tu lógica
                        Usuario u = new Usuario(rol, nombre, correo);
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
}