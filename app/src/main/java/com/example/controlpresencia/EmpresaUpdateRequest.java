package com.example.controlpresencia;

// Cuerpo de la petición PUT para actualizar el radio de fichaje de la empresa
// Puedo modificar el radio desde la pantalla del mapa
public class EmpresaUpdateRequest {
    private float radio;

    public EmpresaUpdateRequest(float radio) {
        this.radio = radio;
    }
}
