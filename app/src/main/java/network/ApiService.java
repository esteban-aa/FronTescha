package com.example.teschachatbot_f.network;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {

    // Solo una llamada GET simple que no espera cuerpo en la respuesta
    @GET("api/health/test-db")
    Call<Void> testDbConnection();

}
