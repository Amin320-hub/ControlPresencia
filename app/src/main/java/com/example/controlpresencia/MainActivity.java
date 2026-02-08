package com.example.controlpresencia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        EditText etNif = findViewById(R.id.etNif);
        EditText etPass = findViewById(R.id.etPass);
        Button btnLogin = findViewById(R.id.btnLogin);

        btnLogin.setOnClickListener(v -> {
            String nif = etNif.getText().toString();
            String pass = etPass.getText().toString();

            if (!nif.isEmpty() && !pass.isEmpty()) {
                login(nif, pass);
            } else {
                Toast.makeText(this, "Rellena todos los campos", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void login(String nif, String pass) {
        LoginRequest req = new LoginRequest(nif, pass);

        // Llamada asíncrona usando enqueue
        RetrofitClient.getApiService().login(req).enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    // Si el login es correcto, guardamos el token
                    String token = response.body().getAccessToken();

                    // Pasamos a la siguiente actividad enviando el token
                    Intent intent = new Intent(MainActivity.this, FicharActivity.class);
                    intent.putExtra("TOKEN", token);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(MainActivity.this, "Credenciales incorrectas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                // Error de conexión [cite: 214]
                Toast.makeText(MainActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
}