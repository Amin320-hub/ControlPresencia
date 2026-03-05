package com.example.controlpresencia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Pantalla exclusiva para administradores.
// Desde aquí puedo ver empleados, incidencias de todos, y abrir el mapa de la empresa.
public class AdminActivity extends AppCompatActivity {

    private String token;
    private TextView tvEmpleados;
    private TextView tvIncidenciasAdmin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        token = getIntent().getStringExtra("TOKEN");

        tvEmpleados = findViewById(R.id.tvEmpleados);
        tvIncidenciasAdmin = findViewById(R.id.tvIncidenciasAdmin);

        Button btnVerEmpleados = findViewById(R.id.btnVerEmpleados);
        Button btnVerIncidencias = findViewById(R.id.btnVerIncidenciasAdmin);
        Button btnVerMapa = findViewById(R.id.btnVerMapa);

        // Cargo el listado de todos los empleados al pulsar el botón
        btnVerEmpleados.setOnClickListener(v -> cargarEmpleados());

        // Cargo todas las incidencias de todos los empleados (vista admin)
        btnVerIncidencias.setOnClickListener(v -> cargarIncidenciasAdmin());

        // Abro el mapa donde puedo ver y modificar el radio de fichaje de la empresa
        btnVerMapa.setOnClickListener(v -> {
            Intent i = new Intent(this, MapaEmpresaActivity.class);
            i.putExtra("TOKEN", token);
            startActivity(i);
        });
    }

    // Llamo a GET /api/admin/trabajadores y muestro la lista en pantalla
    private void cargarEmpleados() {
        RetrofitClient.getApiService()
                .getTrabajadores("Bearer " + token)
                .enqueue(new Callback<List<TrabajadorResponse>>() {
                    @Override
                    public void onResponse(Call<List<TrabajadorResponse>> call,
                            Response<List<TrabajadorResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            StringBuilder sb = new StringBuilder();
                            for (TrabajadorResponse t : response.body()) {
                                sb.append("👤 ").append(t.nombre).append(" ").append(t.apellidos).append("\n");
                                sb.append("   NIF: ").append(t.nif)
                                        .append("  |  Rol: ").append(t.rol).append("\n\n");
                            }
                            tvEmpleados.setText(sb.length() > 0 ? sb.toString() : "No hay empleados.");
                        } else {
                            tvEmpleados.setText("Error al cargar empleados.");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TrabajadorResponse>> call, Throwable t) {
                        tvEmpleados.setText("Error de red: " + t.getMessage());
                    }
                });
    }

    // Llamo a GET /api/admin/incidencias — todas las incidencias de todos los
    // empleados
    private void cargarIncidenciasAdmin() {
        RetrofitClient.getApiService()
                .getIncidenciasAdmin("Bearer " + token)
                .enqueue(new Callback<List<IncidenciaAdminResponse>>() {
                    @Override
                    public void onResponse(Call<List<IncidenciaAdminResponse>> call,
                            Response<List<IncidenciaAdminResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            StringBuilder sb = new StringBuilder();
                            for (IncidenciaAdminResponse inc : response.body()) {
                                sb.append("👤 ").append(inc.trabajador).append("\n");
                                sb.append("📅 ").append(inc.fecha_hora).append("\n");
                                sb.append("📝 ").append(inc.descripcion).append("\n");
                                sb.append("──────────────────\n");
                            }
                            tvIncidenciasAdmin.setText(sb.length() > 0
                                    ? sb.toString()
                                    : "No hay incidencias registradas.");
                        } else {
                            tvIncidenciasAdmin.setText("Error al cargar incidencias.");
                        }
                    }

                    @Override
                    public void onFailure(Call<List<IncidenciaAdminResponse>> call, Throwable t) {
                        tvIncidenciasAdmin.setText("Error de red: " + t.getMessage());
                    }
                });
    }
}
