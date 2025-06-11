package com.example.teschachatbot_f.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor; // Para el interceptor de logs

public class RetrofitClient {

    // ¡¡ESTO ES LO MÁS CRÍTICO!!
    // - Para EMULADOR: "http://10.0.2.2:5001/"
    // - Para DISPOSITIVO FÍSICO (misma red): "http://TU_IP_LOCAL:5001/" (ej. http://192.168.100.200:5001/)
    private static final String BASE_URL = "http://10.0.2.2:5001/"; // Cambia esto según tu caso

    private static RetrofitClient instance;
    private ApiService apiService;

    private RetrofitClient() {
        // Opcional: Interceptor para ver los logs HTTP en Logcat
        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY); // Muestra el cuerpo de la petición/respuesta

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging) // Añade el interceptor
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client) // Usa el cliente OkHttp con el interceptor
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static synchronized RetrofitClient getInstance() {
        if (instance == null) {
            instance = new RetrofitClient();
        }
        return instance;
    }

    public ApiService getApiService() {
        return apiService;
    }
}