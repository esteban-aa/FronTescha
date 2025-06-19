package com.example.teschachatbot_f;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teschachatbot_f.models.Edificio;
import com.example.teschachatbot_f.network.ApiService;
import com.example.teschachatbot_f.network.RetrofitClient;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewLocationActivity extends AppCompatActivity {

    private RecyclerView rvEdificios;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_location);

        rvEdificios = findViewById(R.id.rvEdificios);
        rvEdificios.setLayoutManager(new LinearLayoutManager(this));

        ApiService api = RetrofitClient.getInstance().create(ApiService.class);
        api.getEdificios().enqueue(new Callback<List<Edificio>>() {
            @Override
            public void onResponse(Call<List<Edificio>> call, Response<List<Edificio>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    rvEdificios.setAdapter(new EdificioAdapter(response.body(), ViewLocationActivity.this));
                } else {
                    Toast.makeText(ViewLocationActivity.this, "Error al cargar edificios", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Edificio>> call, Throwable t) {
                Toast.makeText(ViewLocationActivity.this, "Fallo de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
