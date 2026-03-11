package com.example.controlpresencia;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // --- AUTENTICACIÓN ---
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    @POST("api/auth/solicitar-cambio-password")
    Call<GenericResponse> solicitarCambioPassword(@Body EmailRequest request);

    // --- FICHAJE ---
    @GET("api/fichaje/estado")
    Call<FichajeResponse> getEstadoFichaje(@Header("Authorization") String token);

    @POST("api/fichaje")
    Call<FichajeResponse> fichar(
            @Header("Authorization") String token,
            @Query("lat") double latitud,
            @Query("lon") double longitud
    );

    // --- INCIDENCIAS ---
    @POST("api/incidencias")
    Call<GenericResponse> registrarIncidencia(
            @Header("Authorization") String token,
            @Body IncidenciaRequest request
    );

    @GET("api/incidencias")
    Call<List<IncidenciaResponse>> getMisIncidencias(
            @Header("Authorization") String token
    );

    // --- PRESENCIA ---
    @GET("api/presencia/mis-registros")
    Call<List<RegistroResponse>> getMisRegistros(
            @Header("Authorization") String token,
            @Query("desde") String desde,
            @Query("hasta") String hasta
    );

    @GET("api/presencia/resumen-mensual")
    Call<ResumenMensualResponse> getResumenMensual(
            @Header("Authorization") String token,
            @Query("mes") String mes
    );

    // --- ADMIN ---
    @GET("api/admin/trabajadores")
    Call<List<TrabajadorResponse>> getTrabajadores(@Header("Authorization") String token);

    @POST("api/admin/trabajadores")
    Call<GenericResponse> crearTrabajador(
            @Header("Authorization") String token,
            @Body TrabajadorRequest request
    );

    @PUT("api/admin/trabajadores/{id}")
    Call<GenericResponse> updateTrabajador(
            @Header("Authorization") String token,
            @Path("id") int idTrabajador,
            @Body TrabajadorRequest request
    );

    @DELETE("api/admin/trabajadores/{id}")
    Call<GenericResponse> deleteTrabajador(
            @Header("Authorization") String token,
            @Path("id") int idTrabajador
    );

    @GET("api/admin/registros")
    Call<List<RegistroResponse>> getRegistrosEmpleado(
            @Header("Authorization") String token,
            @Query("nif") String nif
    );

    @GET("api/admin/incidencias")
    Call<List<IncidenciaAdminResponse>> getIncidenciasAdmin(@Header("Authorization") String token);

    @GET("api/admin/empresa")
    Call<EmpresaResponse> getEmpresa(@Header("Authorization") String token);

    @PUT("api/admin/empresa")
    Call<GenericResponse> updateEmpresa(
            @Header("Authorization") String token,
            @Body EmpresaUpdateRequest request
    );
}
