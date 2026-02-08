package com.example.controlpresencia;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FicharActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fichar);

        TextView tvStatus = findViewById(R.id.tvStatus);
        Button btnFichar = findViewById(R.id.btnFichar);

        String token = getIntent().getStringExtra("TOKEN");

        btnFichar.setOnClickListener(v -> {
            String authHeader = "Bearer " + token;

            RetrofitClient.getApiService().fichar(authHeader).enqueue(new Callback<FichajeResponse>() {
                @Override
                public void onResponse(Call<FichajeResponse> call, Response<FichajeResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {

                        // 1. Mostramos el mensaje del servidor
                        tvStatus.setText(response.body().msg);

                        // 2. CAMBIAMOS EL COLOR SEGÚN EL ESTADO
                        String estado = response.body().status; // "entrada" o "salida"

                        if (estado.equals("entrada")) {
                            // Si acabamos de ENTRAR, ponemos el botón ROJO (para salir)
                            btnFichar.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                            btnFichar.setText("SALIR"); // Opcional: Cambiar texto
                        } else {
                            // Si acabamos de SALIR, ponemos el botón VERDE (para volver a entrar)
                            btnFichar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50"))); // Verde bonito
                            btnFichar.setText("ENTRAR"); // Opcional
                        }

                        Toast.makeText(FicharActivity.this, "Fichaje: " + estado, Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(FicharActivity.this, "Error al fichar", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<FichajeResponse> call, Throwable t) {
                    Toast.makeText(FicharActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
    }
}