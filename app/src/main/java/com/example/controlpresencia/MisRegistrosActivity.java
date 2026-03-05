package com.example.controlpresencia;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Pantalla donde veo mis propios registros de entrada y salida
// Opcionalmente puedo filtrar por fechas
public class MisRegistrosActivity extends AppCompatActivity {

    private String token;
    private TextView tvRegistros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_registros);

        token = getIntent().getStringExtra("TOKEN");
        tvRegistros = findViewById(R.id.tvRegistros);

        Button btnCargar = findViewById(R.id.btnCargarRegistros);

        // Al pulsar el botón cargo todos los registros sin filtro de fecha
        btnCargar.setOnClickListener(v -> cargarRegistros(null, null));

        // Cargo los registros al abrir la pantalla
        cargarRegistros(null, null);
    }

    // Llamo a la API y muestro los registros en pantalla
    private void cargarRegistros(String desde, String hasta) {
        String authHeader = "Bearer " + token;

        RetrofitClient.getApiService()
                .getMisRegistros(authHeader, desde, hasta)
                .enqueue(new Callback<List<RegistroResponse>>() {
                    @Override
                    public void onResponse(Call<List<RegistroResponse>> call,
                            Response<List<RegistroResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            List<RegistroResponse> registros = response.body();

                            if (registros.isEmpty()) {
                                tvRegistros.setText("No tienes registros de fichaje.");
                                return;
                            }

                            // Construyo la lista de registros para mostrar en pantalla
                            StringBuilder sb = new StringBuilder();
                            for (RegistroResponse reg : registros) {
                                sb.append("🟢 Entrada: ").append(reg.hora_entrada).append("\n");
                                if (reg.hora_salida != null) {
                                    sb.append("🔴 Salida:  ").append(reg.hora_salida);
                                } else {
                                    sb.append("🔴 Salida:  (Aún dentro)");
                                }
                                sb.append("\n──────────────────\n");
                            }
                            tvRegistros.setText(sb.toString());

                        } else {
                            tvRegistros.setText("Error al obtener los registros.");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RegistroResponse>> call, Throwable t) {
                        tvRegistros.setText("Error de red: " + t.getMessage());
                    }
                });
    }
}
