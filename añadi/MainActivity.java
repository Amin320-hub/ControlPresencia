package com.example.controlpresencia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.auth0.android.jwt.JWT; // Necesitas añadir la dependencia: implementation 'com.auth0.android:jwtdecode:2.0.2'

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Activity de login — punto de entrada de la app
// Solo me encargo de recoger las credenciales y llamar a la API
public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText etNif = findViewById(R.id.etNif);
        EditText etPass = findViewById(R.id.etPass);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String nif = etNif.getText().toString().trim();
            String pass = etPass.getText().toString().trim();

            if (!nif.isEmpty() && !pass.isEmpty()) {
                login(nif, pass);
            } else {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login(String nif, String pass) {
        LoginRequest req = new LoginRequest(nif, pass);

        // Llamada asíncrona al endpoint de login de la API REST
        RetrofitClient.getApiService().login(req).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getAccessToken();

                    // Decodifico el token JWT para extraer el rol del usuario
                    // Así puedo mostrar u ocultar funcionalidades de admin en la siguiente pantalla
                    String rol = "Empleado"; // Valor por defecto
                    try {
                        JWT jwt = new JWT(token);
                        String rolClaim = jwt.getClaim("rol").asString();
                        if (rolClaim != null) {
                            rol = rolClaim;
                        }
                    } catch (Exception e) {
                        // Si falla el parseo del token, continúo con rol por defecto
                    }

                    // Paso a la pantalla principal enviando tanto el token como el rol
                    Intent intent = new Intent(MainActivity.this, FicharActivity.class);
                    intent.putExtra("TOKEN", token);
                    intent.putExtra("ROL", rol);
                    startActivity(intent);
                    finish(); // Cierro el login para que no vuelva atrás con el botón back
                } else {
                    Toast.makeText(MainActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Error de conexión con el servidor
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
