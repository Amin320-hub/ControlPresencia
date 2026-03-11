package com.example.controlpresencia;

import com.google.gson.annotations.SerializedName;

// Modelo de una incidencia que me devuelve el servidor al hacer GET /api/incidencias
public class IncidenciaResponse {
    @SerializedName("id")
    public int id_incidencia;
    @SerializedName("fecha")
    public String fecha_hora;
    public String descripcion;
}
