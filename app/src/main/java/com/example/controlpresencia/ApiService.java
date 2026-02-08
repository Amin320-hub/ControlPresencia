package com.example.controlpresencia;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Header;
import retrofit2.http.POST;

public interface ApiService {

    // Login: Enviamos usuario y contraseña en el Body
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // Fichar: Enviamos el token en la cabecera (Authorization)
    @POST("api/fichaje")
    Call<FichajeResponse> fichar(@Header("Authorization") String token);
}