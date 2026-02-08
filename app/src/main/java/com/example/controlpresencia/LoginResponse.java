package com.example.controlpresencia;

public class LoginResponse {
    // Gson buscará este nombre exacto en el JSON
    private String access_token;

    public String getAccessToken() {
        return access_token;
    }
}