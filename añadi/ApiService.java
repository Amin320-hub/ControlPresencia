package com.example.controlpresencia;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Query;

// Interfaz de Retrofit — declaro todos los endpoints de la API REST que consumo desde Android.
// Cada método corresponde exactamente a una ruta de app.py registrada bajo el prefijo /api/
public interface ApiService {

    // ── AUTENTICACIÓN ────────────────────────────────────────────────────────

    // Login: envío NIF y contraseña, el servidor me devuelve el token JWT
    @POST("api/auth/login")
    Call<LoginResponse> login(@Body LoginRequest request);

    // Solicito cambio de contraseña: el servidor enviará un email con el enlace
    @POST("api/auth/solicitar-cambio-password")
    Call<GenericResponse> solicitarCambioPassword(@Body EmailRequest request);


    // ── FICHAJE (con geolocalización) ────────────────────────────────────────

    // Fichar entrada/salida: mando el token JWT en la cabecera y las coordenadas GPS como query params.
    // El servidor valida que estoy dentro del radio permitido antes de registrar el fichaje.
    @POST("api/fichaje")
    Call<FichajeResponse> fichar(
            @Header("Authorization") String token,
            @Query("lat") double latitud,
            @Query("lon") double longitud
    );


    // ── INCIDENCIAS ──────────────────────────────────────────────────────────

    // Registro una incidencia nueva desde la app
    @POST("api/incidencias")
    Call<GenericResponse> registrarIncidencia(
            @Header("Authorization") String token,
            @Body IncidenciaRequest request
    );

    // Obtengo mis propias incidencias (solo las del trabajador autenticado)
    @GET("api/incidencias")
    Call<List<IncidenciaResponse>> getMisIncidencias(
            @Header("Authorization") String token
    );


    // ── PRESENCIA ────────────────────────────────────────────────────────────

    // Mis propios registros de entrada/salida, con filtro opcional de fechas (YYYY-MM-DD)
    @GET("api/presencia/mis-registros")
    Call<List<RegistroResponse>> getMisRegistros(
            @Header("Authorization") String token,
            @Query("desde") String desde,
            @Query("hasta") String hasta
    );

    // Resumen mensual: horas trabajadas, teóricas y extra (para la pantalla de horas extra)
    @GET("api/presencia/resumen-mensual")
    Call<ResumenMensualResponse> getResumenMensual(
            @Header("Authorization") String token,
            @Query("mes") String mes   // Formato: YYYY-MM
    );


    // ── ADMIN: EMPLEADOS ─────────────────────────────────────────────────────

    // Lista completa de todos los trabajadores (solo admin)
    @GET("api/admin/trabajadores")
    Call<List<TrabajadorResponse>> getTrabajadores(
            @Header("Authorization") String token
    );

    // Registros de entrada/salida de un empleado concreto (solo admin)
    @GET("api/admin/registros")
    Call<List<RegistroResponse>> getRegistrosEmpleado(
            @Header("Authorization") String token,
            @Query("id_trabajador") int idTrabajador
    );


    // ── ADMIN: INCIDENCIAS ───────────────────────────────────────────────────

    // Todas las incidencias de todos los empleados (solo admin)
    // Usa IncidenciaAdminResponse porque incluye el campo "trabajador" con el nombre del empleado
    @GET("api/admin/incidencias")
    Call<List<IncidenciaAdminResponse>> getIncidenciasAdmin(
            @Header("Authorization") String token
    );


    // ── ADMIN: EMPRESA / GEOLOCALIZACIÓN ─────────────────────────────────────

    // Obtengo los datos de geolocalización de la empresa (lat, lon, radio) para pintarlos en el mapa
    @GET("api/admin/empresa")
    Call<EmpresaResponse> getEmpresa(
            @Header("Authorization") String token
    );

    // Actualizo el radio (y opcionalmente lat/lon) desde la app Android.
    // El PDF pide que el admin pueda "consultar y modificar el radio aceptado".
    @PUT("api/admin/empresa")
    Call<GenericResponse> updateEmpresa(
            @Header("Authorization") String token,
            @Body EmpresaUpdateRequest request
    );
}
