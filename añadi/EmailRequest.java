package com.example.controlpresencia;

// Cuerpo de la petición para solicitar el cambio de contraseña por email
// El servidor recibirá este JSON y enviará un enlace al correo indicado
public class EmailRequest {
    private String email;

    public EmailRequest(String email) {
        this.email = email;
    }
}
