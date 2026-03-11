package com.example.controlpresencia;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Pantalla exclusiva para administradores.
// Desde aquí puedo ver empleados, incidencias de todos, y abrir el mapa de la empresa.
public class AdminActivity extends AppCompatActivity {

    private String token;
    
    // RecyclerViews
    private RecyclerView rvEmpleados;
    private RecyclerView rvIncidenciasAdmin;
    private RecyclerView rvRegistrosEmpleadoAdmin;

    // Adapters
    private EmpleadoAdapter empleadoAdapter;
    private IncidenciaAdapter incidenciaAdapter;
    private RegistroAdapter registroAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        token = getIntent().getStringExtra("TOKEN");

        // UI Binding para los botones principales
        Button btnVerEmpleados = findViewById(R.id.btnVerEmpleados);
        Button btnCrearEmpleado = findViewById(R.id.btnCrearEmpleado);
        Button btnVerIncidencias = findViewById(R.id.btnVerIncidenciasAdmin);
        Button btnVerMapa = findViewById(R.id.btnVerMapa);
        Button btnVerRegistros = findViewById(R.id.btnVerRegistrosEmpleado);
        EditText etDniEmpleado = findViewById(R.id.etDniEmpleadoRegistros);

        // UI Binding para los RecyclerViews
        rvEmpleados = findViewById(R.id.rvEmpleados);
        rvIncidenciasAdmin = findViewById(R.id.rvIncidenciasAdmin);
        rvRegistrosEmpleadoAdmin = findViewById(R.id.rvRegistrosEmpleadoAdmin);

        // Inicializar LayoutManagers para los recyclerviews
        rvEmpleados.setLayoutManager(new LinearLayoutManager(this));
        rvIncidenciasAdmin.setLayoutManager(new LinearLayoutManager(this));
        rvRegistrosEmpleadoAdmin.setLayoutManager(new LinearLayoutManager(this));

        // Inicializar adaptadores vacíos (EmpleadoAdapter usa OnItemClickListener luego para edit/delete)
        empleadoAdapter = new EmpleadoAdapter(new ArrayList<>(), this::abrirOpcionesEmpleado);
        incidenciaAdapter = new IncidenciaAdapter(new ArrayList<>());
        
        // Aquí le decimos true al adaptador para que muestre el campo "trabajador_nombre" (que no tenemos aún) o "ID Empleado" en admin
        registroAdapter = new RegistroAdapter(new ArrayList<>(), true);

        // Bind adaptadores
        rvEmpleados.setAdapter(empleadoAdapter);
        rvIncidenciasAdmin.setAdapter(incidenciaAdapter);
        rvRegistrosEmpleadoAdmin.setAdapter(registroAdapter);

        // Botones Funcionalidad Base
        btnVerEmpleados.setOnClickListener(v -> cargarEmpleados());
        btnVerIncidencias.setOnClickListener(v -> cargarIncidenciasAdmin());

        // Cargar registros de un empleado especifico por su DNI
        btnVerRegistros.setOnClickListener(v -> {
            String dni = etDniEmpleado.getText().toString().trim();
            if (dni.isEmpty()) {
                Toast.makeText(this, "Introduce un DNI", Toast.LENGTH_SHORT).show();
                return;
            }
            cargarRegistrosEmpleado(dni);
        });

        // Abro el mapa donde puedo ver y modificar el radio de fichaje de la empresa
        btnVerMapa.setOnClickListener(v -> {
            Intent i = new Intent(this, MapaEmpresaActivity.class);
            i.putExtra("TOKEN", token);
            startActivity(i);
        });
        
        // Crear Empleado 
        btnCrearEmpleado.setOnClickListener(v -> {
            Intent intent = new Intent(this, AdminTrabajadorActivity.class);
            intent.putExtra("TOKEN", token);
            startActivity(intent);
        });
    }

    private void abrirOpcionesEmpleado(TrabajadorResponse empleado) {
        Intent intent = new Intent(this, AdminTrabajadorActivity.class);
        intent.putExtra("TOKEN", token);
        intent.putExtra("ID_TRABAJADOR", empleado.id_trabajador);
        intent.putExtra("NOMBRE", empleado.nombre);
        intent.putExtra("APELLIDOS", empleado.apellidos);
        intent.putExtra("NIF", empleado.nif);
        intent.putExtra("EMAIL", empleado.email);
        intent.putExtra("ROL", empleado.rol);
        startActivity(intent);
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
                            List<TrabajadorResponse> list = response.body();
                            empleadoAdapter.setEmpleados(list);
                            if(list.isEmpty()){
                                 Toast.makeText(AdminActivity.this, "Lista de empleados vacía.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(AdminActivity.this, "Error al cargar empleados.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<TrabajadorResponse>> call, Throwable t) {
                         Toast.makeText(AdminActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Llamo a GET /api/admin/incidencias — todas las incidencias de todos los empleados
    private void cargarIncidenciasAdmin() {
        RetrofitClient.getApiService()
                .getIncidenciasAdmin("Bearer " + token)
                .enqueue(new Callback<List<IncidenciaAdminResponse>>() {
                    @Override
                    public void onResponse(Call<List<IncidenciaAdminResponse>> call,
                            Response<List<IncidenciaAdminResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Convertir IncidenciaAdminResponse a IncidenciaResponse 
                            // para que IncidenciaAdapter lo pueda leer
                            List<IncidenciaResponse> listConvertida = new ArrayList<>();
                            for(IncidenciaAdminResponse iA : response.body()){
                                IncidenciaResponse map = new IncidenciaResponse();
                                map.id_incidencia = 0; //No tenemos el id, lo mappeamos
                                map.fecha_hora = iA.fecha_hora;
                                map.descripcion = "[" + iA.trabajador + "] " + iA.descripcion;
                                listConvertida.add(map);
                            }
                            incidenciaAdapter.setIncidencias(listConvertida);
                            if(listConvertida.isEmpty()){
                                Toast.makeText(AdminActivity.this, "No hay incidencias.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                             Toast.makeText(AdminActivity.this, "Error al cargar incidencias.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<List<IncidenciaAdminResponse>> call, Throwable t) {
                        Toast.makeText(AdminActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Llamo a GET /api/admin/registros para un empleado especifico por su DNI
    private void cargarRegistrosEmpleado(String nif) {
        RetrofitClient.getApiService()
            .getRegistrosEmpleado("Bearer " + token, nif)
            .enqueue(new Callback<List<RegistroResponse>>() {
                @Override
                public void onResponse(Call<List<RegistroResponse>> call, Response<List<RegistroResponse>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        List<RegistroResponse> list = response.body();
                        registroAdapter.setRegistros(list);
                        if(list.isEmpty()){
                            Toast.makeText(AdminActivity.this, "El usuario no tiene registros", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                         Toast.makeText(AdminActivity.this, "Error (posible DNI inválido o bloqueado).", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<RegistroResponse>> call, Throwable t) {
                     Toast.makeText(AdminActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
    }
}
