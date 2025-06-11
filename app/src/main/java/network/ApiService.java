package com.example.teschachatbot_f.network;

import com.example.teschachatbot_f.models.ApiResponse; // Asegúrate del paquete
import com.example.teschachatbot_f.models.Producto; // Asegúrate del paquete

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;

public interface ApiService {

    // Endpoint para probar la conexión a la DB en tu backend Flask
    // Asegúrate que la ruta coincida con la de tu Flask (ej. /api/health/test-db)
    @GET("api/health/test-db")
    Call<ApiResponse> testDbConnection();

    // Endpoint para obtener todos los productos
    @GET("api/productos") // Asegúrate que la ruta coincida con la de tu Flask
    Call<List<Producto>> getProducts();

    // Endpoint para agregar un producto
    @POST("api/productos") // Asegúrate que la ruta coincida con la de tu Flask
    Call<ApiResponse> addProduct(@Body Producto producto);
}