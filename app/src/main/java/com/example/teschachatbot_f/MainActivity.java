package com.example.teschachatbot_f;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.List; // Importar para List
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Importar los modelos y el cliente Retrofit (¡Estos archivos deben existir!)
import com.example.teschachatbot_f.network.ApiService; // Asegúrate que el paquete es correcto
import com.example.teschachatbot_f.network.RetrofitClient; // Asegúrate que el paquete es correcto
import com.example.teschachatbot_f.models.ApiResponse; // Asegúrate que el paquete es correcto
import com.example.teschachatbot_f.models.Producto; // Asegúrate que el paquete es correcto


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity"; // Para logs

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this); // Mantén esta línea para EdgeToEdge
        setContentView(R.layout.activity_main);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // --- Inicialización de los botones y listeners ---
        Button btnTestDb = findViewById(R.id.btnTestDb);
        Button btnGetProducts = findViewById(R.id.btnGetProducts);
        Button btnAddProduct = findViewById(R.id.btnAddProduct);

        btnTestDb.setOnClickListener(v -> testDbConnection());
        btnGetProducts.setOnClickListener(v -> getProducts());
        btnAddProduct.setOnClickListener(v -> addProduct());
        // --- Fin de la inicialización de botones ---
    }

    // --- Métodos para hacer las llamadas a la API ---

    private void testDbConnection() {
        Log.d(TAG, "Iniciando testDbConnection...");
        RetrofitClient.getInstance().testDbConnection().enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    Toast.makeText(MainActivity.this, "DB Test: " + apiResponse.getMensaje(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "DB Test Success: " + apiResponse.getMensaje());
                } else {
                    String errorBody = "N/A";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing errorBody: " + e.getMessage());
                    }
                    Toast.makeText(MainActivity.this, "DB Test Error: " + response.code() + " - " + errorBody, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "DB Test Error: " + response.code() + " - " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "DB Test Failure: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "DB Test Failure", t);
            }
        });
    }

    private void getProducts() {
        Log.d(TAG, "Iniciando getProducts...");
        RetrofitClient.getInstance().getProducts().enqueue(new Callback<List<Producto>>() {
            @Override
            public void onResponse(Call<List<Producto>> call, Response<List<Producto>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Producto> productos = response.body();
                    Toast.makeText(MainActivity.this, "Productos obtenidos: " + productos.size(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Productos: " + productos.toString());
                    // Aquí podrías actualizar un RecyclerView o un TextView con los productos
                } else {
                    String errorBody = "N/A";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing errorBody: " + e.getMessage());
                    }
                    Toast.makeText(MainActivity.this, "Error al obtener productos: " + response.code() + " - " + errorBody, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error al obtener productos: " + response.code() + " - " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<List<Producto>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fallo al obtener productos: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Fallo al obtener productos", t);
            }
        });
    }

    private void addProduct() {
        Log.d(TAG, "Iniciando addProduct...");
        // Crea un objeto Producto para enviar
        Producto newProduct = new Producto("Nuevo Producto X", 99.99); // Ejemplo de producto

        RetrofitClient.getInstance().addProduct(newProduct).enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    Toast.makeText(MainActivity.this, "Producto agregado: " + apiResponse.getMensaje(), Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Producto agregado: " + apiResponse.getMensaje() + ", ID: " + apiResponse.getId());
                    getProducts(); // Opcional: refrescar la lista después de agregar
                } else {
                    String errorBody = "N/A";
                    try {
                        if (response.errorBody() != null) {
                            errorBody = response.errorBody().string();
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing errorBody: " + e.getMessage());
                    }
                    Toast.makeText(MainActivity.this, "Error al agregar producto: " + response.code() + " - " + errorBody, Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Error al agregar producto: " + response.code() + " - " + errorBody);
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Fallo al agregar producto: " + t.getMessage(), Toast.LENGTH_LONG).show();
                Log.e(TAG, "Fallo al agregar producto", t);
            }
        });
    }
}