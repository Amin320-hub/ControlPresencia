package com.example.controlpresencia;

// Modelo de incidencia para la vista de administrador
// Incluye el nombre del trabajador para que el admin sepa de quién es cada incidencia
public class IncidenciaAdminResponse {
    public int id_incidencia;
    public String trabajador;
    public String fecha_hora;
    public String descripcion;
}
