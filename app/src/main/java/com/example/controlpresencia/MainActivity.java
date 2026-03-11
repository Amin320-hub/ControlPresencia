package com.example.controlpresencia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.auth0.android.jwt.JWT;
import com.google.android.material.textfield.TextInputEditText;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Usamos TextInputEditText para que coincida con el XML de Material Design
        TextInputEditText etNif = findViewById(R.id.etNif);
        TextInputEditText etPass = findViewById(R.id.etPass);
        Button btnLogin = findViewById(R.id.btnLogin);

        if (btnLogin != null) {
            btnLogin.setOnClickListener(v -> {
                String nif = etNif.getText() != null ? etNif.getText().toString().trim() : "";
                String pass = etPass.getText() != null ? etPass.getText().toString().trim() : "";

                if (!nif.isEmpty() && !pass.isEmpty()) {
                    login(nif, pass);
                } else {
                    Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
                }
            });
        }

        android.widget.TextView tvOlvidePassword = findViewById(R.id.tvOlvidePassword);
        if (tvOlvidePassword != null) {
            tvOlvidePassword.setOnClickListener(v -> {
                Intent intent = new Intent(MainActivity.this, CambiarPasswordActivity.class);
                startActivity(intent);
            });
        }
    }

    private void login(String nif, String pass) {
        LoginRequest req = new LoginRequest(nif, pass);

        RetrofitClient.getApiService().login(req).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String token = response.body().getAccessToken();
                    String rol = "Empleado";
                    try {
                        JWT jwt = new JWT(token);
                        String rolClaim = jwt.getClaim("rol").asString();
                        if (rolClaim != null) rol = rolClaim;
                    } catch (Exception e) {
                        // Error silencioso en el parseo del rol
                    }

                    Intent intent = new Intent(MainActivity.this, FicharActivity.class);
                    intent.putExtra("TOKEN", token);
                    intent.putExtra("ROL", rol);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}
