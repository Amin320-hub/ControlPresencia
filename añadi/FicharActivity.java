package com.example.controlpresencia;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

// Activity de fichaje — solo gestiona la UI y observa el ViewModel (arquitectura MVVM)
// La lógica de negocio está en FichajeViewModel, no aquí
public class FicharActivity extends AppCompatActivity {

    // Código de petición para el permiso de localización
    private static final int PERMISSION_REQUEST_LOCATION = 1001;

    // Cliente de Google para obtener la última ubicación conocida del dispositivo
    private FusedLocationProviderClient fusedLocationClient;

    private FichajeViewModel viewModel;
    private String token;
    private Button btnFichar;
    private TextView tvStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fichar);

        tvStatus = findViewById(R.id.tvStatus);
        btnFichar = findViewById(R.id.btnFichar);
        Button btnIncidencias = findViewById(R.id.btnIncidencias);
        Button btnMisRegistros = findViewById(R.id.btnMisRegistros);
        Button btnHorasExtra = findViewById(R.id.btnHorasExtra);
        Button btnAdmin = findViewById(R.id.btnAdmin);      // Solo visible si es admin
        Button btnCambiarPass = findViewById(R.id.btnCambiarPass);

        // Recupero el token JWT que me pasó la MainActivity al hacer login
        token = getIntent().getStringExtra("TOKEN");
        String rol = getIntent().getStringExtra("ROL"); // También paso el rol desde MainActivity

        // Inicializo el cliente de localización de Google Play Services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Creo el ViewModel — Android lo gestiona para que sobreviva a rotaciones de pantalla
        viewModel = new ViewModelProvider(this).get(FichajeViewModel.class);

        // Observo el resultado del fichaje: cuando el ViewModel actualice el LiveData, actualizo la UI
        viewModel.getFichajeResult().observe(this, fichajeResponse -> {
            tvStatus.setText(fichajeResponse.msg);

            if ("entrada".equals(fichajeResponse.status)) {
                // Si acabo de entrar, pongo el botón en ROJO para indicar que el siguiente toque será salida
                btnFichar.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                btnFichar.setText("SALIR");
            } else {
                // Si acabo de salir, pongo el botón en VERDE para indicar que el siguiente toque será entrada
                btnFichar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                btnFichar.setText("ENTRAR");
            }
            Toast.makeText(this, "Fichaje: " + fichajeResponse.status, Toast.LENGTH_SHORT).show();
        });

        // Observo los errores del ViewModel
        viewModel.getErrorMessage().observe(this, error ->
                Toast.makeText(this, error, Toast.LENGTH_LONG).show()
        );

        // Al pulsar fichar, primero obtengo la ubicación GPS y luego llamo al ViewModel
        btnFichar.setOnClickListener(v -> obtenerUbicacionYFichar());

        // Navego a la pantalla de incidencias
        btnIncidencias.setOnClickListener(v -> {
            Intent i = new Intent(this, IncidenciasActivity.class);
            i.putExtra("TOKEN", token);
            startActivity(i);
        });

        // Navego a ver mis propios registros
        btnMisRegistros.setOnClickListener(v -> {
            Intent i = new Intent(this, MisRegistrosActivity.class);
            i.putExtra("TOKEN", token);
            startActivity(i);
        });

        // Navego a la pantalla de horas extra
        btnHorasExtra.setOnClickListener(v -> {
            Intent i = new Intent(this, HorasExtraActivity.class);
            i.putExtra("TOKEN", token);
            startActivity(i);
        });

        // Navego a cambio de contraseña
        btnCambiarPass.setOnClickListener(v -> {
            Intent i = new Intent(this, CambiarPasswordActivity.class);
            i.putExtra("TOKEN", token);
            startActivity(i);
        });

        // El botón de admin solo lo muestro si el usuario tiene rol de administrador
        if ("Administrador".equals(rol)) {
            btnAdmin.setVisibility(android.view.View.VISIBLE);
            btnAdmin.setOnClickListener(v -> {
                Intent i = new Intent(this, AdminActivity.class);
                i.putExtra("TOKEN", token);
                startActivity(i);
            });
        } else {
            btnAdmin.setVisibility(android.view.View.GONE);
        }
    }

    // Pido permiso de localización si no lo tengo, y obtengo la última ubicación conocida
    private void obtenerUbicacionYFichar() {
        // Compruebo si tengo permiso de localización
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // Si no tengo permiso, lo pido al usuario
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSION_REQUEST_LOCATION);
            return;
        }

        // Obtengo la última ubicación conocida del dispositivo
        fusedLocationClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                // Si tengo ubicación, llamo al ViewModel para fichar con las coordenadas
                viewModel.fichar(token, location.getLatitude(), location.getLongitude());
            } else {
                Toast.makeText(this,
                        "No se pudo obtener la ubicación. Activa el GPS.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // Gestiono la respuesta del usuario al diálogo de permisos
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // El usuario aceptó el permiso, reintento fichar
                obtenerUbicacionYFichar();
            } else {
                Toast.makeText(this,
                        "Necesito permiso de localización para fichar.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}
