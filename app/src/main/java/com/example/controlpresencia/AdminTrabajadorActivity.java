package com.example.controlpresencia;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminTrabajadorActivity extends AppCompatActivity {

    private String token;
    private int idTrabajador = -1; // -1 significa modo crear nuevo, > 0 significa modo editar

    private EditText etNombre, etApellidos, etNif, etEmail, etPassword;
    private Spinner spinnerRol;
    private Button btnGuardar, btnEliminar;
    private TextView tvTitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_trabajador);

        token = getIntent().getStringExtra("TOKEN");

        // Binding
        tvTitulo = findViewById(R.id.tvTitulo);
        etNombre = findViewById(R.id.etNombre);
        etApellidos = findViewById(R.id.etApellidos);
        etNif = findViewById(R.id.etNif);
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        spinnerRol = findViewById(R.id.spinnerRol);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnEliminar = findViewById(R.id.btnEliminar);

        // Setup Spinner
        String[] roles = {"empleado", "admin"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, roles);
        spinnerRol.setAdapter(adapter);

        // Checar si venimos a editar
        if (getIntent().hasExtra("ID_TRABAJADOR")) {
            idTrabajador = getIntent().getIntExtra("ID_TRABAJADOR", -1);
            tvTitulo.setText("Editar Empleado");
            
            // Cargar datos
            etNombre.setText(getIntent().getStringExtra("NOMBRE"));
            etApellidos.setText(getIntent().getStringExtra("APELLIDOS"));
            etNif.setText(getIntent().getStringExtra("NIF"));
            etEmail.setText(getIntent().getStringExtra("EMAIL"));
            
            String rol = getIntent().getStringExtra("ROL");
            if(rol != null && rol.equals("admin")) spinnerRol.setSelection(1);
            else spinnerRol.setSelection(0);

            // Mostrar el boton de eliminar
            btnEliminar.setVisibility(android.view.View.VISIBLE);
        } else {
            tvTitulo.setText("Crear Empleado");
            btnEliminar.setVisibility(android.view.View.GONE);
        }

        btnGuardar.setOnClickListener(v -> guardarEmpleado());
        btnEliminar.setOnClickListener(v -> eliminarEmpleado());
    }

    private void guardarEmpleado() {
        String nombre = etNombre.getText().toString().trim();
        String apellidos = etApellidos.getText().toString().trim();
        String nif = etNif.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String rol = spinnerRol.getSelectedItem().toString();

        if (nombre.isEmpty() || apellidos.isEmpty() || nif.isEmpty() || email.isEmpty()) {
            Toast.makeText(this, "Completa todos los campos básicos.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (idTrabajador == -1 && password.isEmpty()) {
            Toast.makeText(this, "La contraseña es obligatoria al crear un usuario.", Toast.LENGTH_SHORT).show();
            return;
        }

        String passToSend = password.isEmpty() ? null : password;
        TrabajadorRequest request = new TrabajadorRequest(nombre, apellidos, nif, email, passToSend, rol, null);

        String authHeader = "Bearer " + token;
        ApiService api = RetrofitClient.getApiService();

        if (idTrabajador == -1) { // Modo Crear
            api.crearTrabajador(authHeader, request).enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminTrabajadorActivity.this, "Empleado creado con éxito", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AdminTrabajadorActivity.this, "Error al crear: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse> call, Throwable t) {
                    Toast.makeText(AdminTrabajadorActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        } else { // Modo Editar
            api.updateTrabajador(authHeader, idTrabajador, request).enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AdminTrabajadorActivity.this, "Empleado actualizado", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(AdminTrabajadorActivity.this, "Error al actualizar: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse> call, Throwable t) {
                    Toast.makeText(AdminTrabajadorActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void eliminarEmpleado() {
        if (idTrabajador == -1) return;

        RetrofitClient.getApiService().deleteTrabajador("Bearer " + token, idTrabajador)
                .enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(AdminTrabajadorActivity.this, "Empleado eliminado", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            Toast.makeText(AdminTrabajadorActivity.this, "Error al eliminar.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GenericResponse> call, Throwable t) {
                        Toast.makeText(AdminTrabajadorActivity.this, "Error de red: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
