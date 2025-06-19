package com.example.teschachatbot_f.network;

import com.example.teschachatbot_f.models.Edificio;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;


public interface ApiService {

    // Solo una llamada GET simple que no espera cuerpo en la respuesta
    @GET("api/health/test-db")
    Call<Void> testDbConnection();
    @GET("api/file/read-file")
    Call<String> leerArchivo();

    @GET("api/edificios/") // ← OJO: termina en /
    Call<List<Edificio>> getEdificios();

    @GET("api/edificios/nombre/{nombre}") // Para obtener por nombre
    Call<Edificio> getEdificioPorNombre(@Path("nombre") String nombre);
}
