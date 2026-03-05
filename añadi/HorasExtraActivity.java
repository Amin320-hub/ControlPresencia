package com.example.controlpresencia;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Activity que muestra el resumen de horas trabajadas, teóricas y extra de un mes concreto
// El empleado introduce el mes (YYYY-MM) y la app consulta la API
public class HorasExtraActivity extends AppCompatActivity {

    private String token;
    private TextView tvResultado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horas_extra);

        token = getIntent().getStringExtra("TOKEN");

        EditText etMes = findViewById(R.id.etMes);
        Button btnConsultar = findViewById(R.id.btnConsultarHoras);
        tvResultado = findViewById(R.id.tvResultadoHoras);

        btnConsultar.setOnClickListener(v -> {
            String mes = etMes.getText().toString().trim();

            // Valido que el formato sea YYYY-MM
            if (mes.isEmpty() || !mes.matches("\\d{4}-\\d{2}")) {
                Toast.makeText(this, "Introduce el mes en formato YYYY-MM (ej: 2026-03)",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            consultarResumenMensual(mes);
        });
    }

    // Consulto el resumen mensual en la API y muestro los resultados
    private void consultarResumenMensual(String mes) {
        String authHeader = "Bearer " + token;

        RetrofitClient.getApiService()
                .getResumenMensual(authHeader, mes)
                .enqueue(new Callback<ResumenMensualResponse>() {
                    @Override
                    public void onResponse(Call<ResumenMensualResponse> call,
                                           Response<ResumenMensualResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            ResumenMensualResponse resumen = response.body();

                            // Muestro el resumen de forma clara con emojis para que sea legible
                            String texto = "📅 Mes: " + resumen.mes + "\n\n" +
                                    "⏱️ Horas trabajadas: " + String.format("%.1f", resumen.horas_trabajadas) + "h\n" +
                                    "📋 Horas teóricas: " + String.format("%.1f", resumen.horas_teoricas) + "h\n\n" +
                                    (resumen.horas_extra >= 0
                                            ? "✅ Horas extra: +" + String.format("%.1f", resumen.horas_extra) + "h"
                                            : "⚠️ Horas pendientes: " + String.format("%.1f", resumen.horas_extra) + "h");

                            tvResultado.setText(texto);
                        } else {
                            tvResultado.setText("No se encontraron datos para ese mes.");
                        }
                    }

                    @Override
                    public void onFailure(Call<ResumenMensualResponse> call, Throwable t) {
                        tvResultado.setText("Error de red: " + t.getMessage());
                    }
                });
    }
}
