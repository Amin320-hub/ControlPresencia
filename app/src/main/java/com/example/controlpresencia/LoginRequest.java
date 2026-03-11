package com.example.controlpresencia;

public class LoginRequest {
    private String nif;
    private String passw;

    public LoginRequest(String nif, String passw) {
        this.nif = nif;
        this.passw = passw;
    }

    public String getNif() {
        return nif;
    }

    public String getPassw() {
        return passw;
    }
}
