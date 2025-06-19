package com.example.teschachatbot_f;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teschachatbot_f.network.ApiService;
import com.example.teschachatbot_f.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReadFileActivity extends AppCompatActivity {

    private TextView textViewContent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_file);

        textViewContent = findViewById(R.id.textViewContent);

        ApiService apiService = RetrofitClient.getInstance().create(ApiService.class);

        Call<String> call = apiService.leerArchivo();

        call.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful() && response.body() != null) {
                    textViewContent.setText(response.body());
                } else {
                    textViewContent.setText("Error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                textViewContent.setText("Fallo: " + t.getMessage());
            }
        });
    }
}
