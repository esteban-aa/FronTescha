package com.example.teschachatbot_f.network;

import com.example.teschachatbot_f.models.Edificio;
import com.example.teschachatbot_f.models.Ventanilla;
import com.example.teschachatbot_f.models.Profesor;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {

    @GET("api/health/test-db")
    Call<Void> testDbConnection();

    @GET("api/file/read-file")
    Call<String> leerArchivo();

    @GET("api/edificios/")
    Call<List<Edificio>> getEdificios();

    @GET("api/edificios/nombre/{nombre}")
    Call<Edificio> getEdificioPorNombre(@Path("nombre") String nombre);

    @GET("api/ventanilla")
    Call<List<Ventanilla>> getVentanillas();

    @GET("api/profesores")
    Call<List<Profesor>> getProfesores();

    @PUT("api/edificios/nombre/{nombre}")
    Call<Void> actualizarEdificioPorNombre(@Path("nombre") String nombre, @Body Edificio edificio);
}
