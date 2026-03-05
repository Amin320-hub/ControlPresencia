package com.example.controlpresencia;

// Modelo de una incidencia que me devuelve el servidor al hacer GET /api/incidencias
public class IncidenciaResponse {
    public int id_incidencia;
    public String fecha_hora;
    public String descripcion;
}
