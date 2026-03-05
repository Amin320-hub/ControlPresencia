package com.example.controlpresencia;

// Modelo de un registro de entrada/salida devuelto por la API
// hora_salida puede ser null si el trabajador todavía está dentro
public class RegistroResponse {
    public int id_registro;
    public String hora_entrada;
    public String hora_salida; // Null si no ha salido aún
    public String nombre; // Solo viene relleno en los endpoints de admin
}
