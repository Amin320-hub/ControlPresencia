package com.example.controlpresencia;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Activity para registrar y ver mis incidencias
// El empleado puede escribir una incidencia y enviarla al servidor, y también ver las anteriores
public class IncidenciasActivity extends AppCompatActivity {

    private String token;
    private TextView tvListaIncidencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_incidencias);

        token = getIntent().getStringExtra("TOKEN");

        EditText etDescripcion = findViewById(R.id.etDescripcion);
        Button btnEnviar = findViewById(R.id.btnEnviarIncidencia);
        tvListaIncidencias = findViewById(R.id.tvListaIncidencias);

        // Al pulsar enviar, creo la incidencia con la fecha y hora actuales
        btnEnviar.setOnClickListener(v -> {
            String descripcion = etDescripcion.getText().toString().trim();

            if (descripcion.isEmpty()) {
                Toast.makeText(this, "Escribe una descripción", Toast.LENGTH_SHORT).show();
                return;
            }

            // Genero la fecha y hora actual en formato ISO 8601 que espera el servidor
            String fechaHora = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault())
                    .format(new Date());

            enviarIncidencia(fechaHora, descripcion);
            etDescripcion.setText("");
        });

        // Al cargar la pantalla, cargo también la lista de incidencias previas
        cargarMisIncidencias();
    }

    // Llamo a la API para registrar la incidencia en el servidor
    private void enviarIncidencia(String fechaHora, String descripcion) {
        IncidenciaRequest request = new IncidenciaRequest(fechaHora, descripcion);
        String authHeader = "Bearer " + token;

        RetrofitClient.getApiService()
                .registrarIncidencia(authHeader, request)
                .enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(IncidenciasActivity.this,
                                    response.body().msg, Toast.LENGTH_SHORT).show();
                            // Recargo la lista para que aparezca la nueva incidencia
                            cargarMisIncidencias();
                        } else {
                            Toast.makeText(IncidenciasActivity.this,
                                    "Error al enviar la incidencia", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GenericResponse> call, Throwable t) {
                        Toast.makeText(IncidenciasActivity.this,
                                "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Cargo mis incidencias previas desde el servidor y las muestro en pantalla
    private void cargarMisIncidencias() {
        String authHeader = "Bearer " + token;

        RetrofitClient.getApiService()
                .getMisIncidencias(authHeader)
                .enqueue(new Callback<List<IncidenciaResponse>>() {
                    @Override
                    public void onResponse(Call<List<IncidenciaResponse>> call,
                                           Response<List<IncidenciaResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Construyo un texto con todas mis incidencias para mostrarlo en pantalla
                            StringBuilder sb = new StringBuilder();
                            for (IncidenciaResponse inc : response.body()) {
                                sb.append("📅 ").append(inc.fecha_hora).append("\n");
                                sb.append("📝 ").append(inc.descripcion).append("\n\n");
                            }
                            tvListaIncidencias.setText(sb.length() > 0
                                    ? sb.toString()
                                    : "No tienes incidencias registradas.");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<IncidenciaResponse>> call, Throwable t) {
                        tvListaIncidencias.setText("Error al cargar incidencias.");
                    }
                });
    }
}
