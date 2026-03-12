package com.example.controlpresencia;

public class TrabajadorRequest {
    public String nombre;
    public String apellidos;
    public String nif;
    public String email;
    public String password; // Optional on update
    public String rol; // 'empleado' o 'admin'
    public Integer id_horario; // Optional

    public TrabajadorRequest(String nombre, String apellidos, String nif, String email, String password, String rol, Integer id_horario) {
        this.nombre = nombre;
        this.apellidos = apellidos;
        this.nif = nif;
        this.email = email;
        this.password = password;
        this.rol = rol;
        this.id_horario = id_horario;
    }
}
