package com.example.controlpresencia;

import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Pantalla donde veo mis propios registros de entrada y salida
// Opcionalmente puedo filtrar por fechas
public class MisRegistrosActivity extends AppCompatActivity {

    private String token;
    private RecyclerView rvRegistros;
    private RegistroAdapter registroAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mis_registros);

        token = getIntent().getStringExtra("TOKEN");
        
        rvRegistros = findViewById(R.id.rvRegistros);
        rvRegistros.setLayoutManager(new LinearLayoutManager(this));
        registroAdapter = new RegistroAdapter(new ArrayList<>(), false);
        rvRegistros.setAdapter(registroAdapter);

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
                                Toast.makeText(MisRegistrosActivity.this, "No tienes registros de fichaje.", Toast.LENGTH_SHORT).show();
                            }

                            registroAdapter.setRegistros(registros);
                        } else {
                            Toast.makeText(MisRegistrosActivity.this, "Error al obtener los registros.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<RegistroResponse>> call, Throwable t) {
                         Toast.makeText(MisRegistrosActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
