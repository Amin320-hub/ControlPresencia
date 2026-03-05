package com.example.controlpresencia;

// Cuerpo de la petición POST para registrar una incidencia desde la app Android
// El campo fecha_hora debe ir en formato ISO 8601 para que Flask lo parsee correctamente
public class IncidenciaRequest {
    private String fecha_hora;   // Ej: "2026-03-05T10:30:00"
    private String descripcion;

    public IncidenciaRequest(String fecha_hora, String descripcion) {
        this.fecha_hora = fecha_hora;
        this.descripcion = descripcion;
    }
}
