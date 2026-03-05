package com.example.controlpresencia;

// Datos de la empresa que me devuelve el servidor
// Los uso en la pantalla de admin para mostrar el mapa con la ubicación y el radio de fichaje
public class EmpresaResponse {
    public double lat;
    public double lon;
    public float radio; // Radio en metros permitido para fichar
    public String nombrecomercial;
}
