package com.example.controlpresencia;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Activity del mapa de empresa (solo admin).
// Muestra la ubicación GPS de la empresa y el círculo del radio permitido para fichar.
// Desde aquí el admin también puede modificar el radio enviando un PUT a la API.
public class MapaEmpresaActivity extends AppCompatActivity implements OnMapReadyCallback {

    private String token;
    private GoogleMap mMap;
    private Circle circuloRadio;          // Guardo la referencia para poder redibujarlo al cambiar el radio
    private LatLng ubicacionEmpresa;      // La guardo para redibujar el círculo sin volver a llamar a la API

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa_empresa);

        token = getIntent().getStringExtra("TOKEN");

        Button btnActualizarRadio = findViewById(R.id.btnActualizarRadio);
        EditText etNuevoRadio     = findViewById(R.id.etNuevoRadio);

        // Inicializo el fragmento del mapa — cuando esté listo se llama a onMapReady
        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // El admin introduce el nuevo radio en metros y pulsa el botón para guardarlo
        btnActualizarRadio.setOnClickListener(v -> {
            String textoRadio = etNuevoRadio.getText().toString().trim();
            if (textoRadio.isEmpty()) {
                Toast.makeText(this, "Introduce un radio en metros", Toast.LENGTH_SHORT).show();
                return;
            }
            try {
                float nuevoRadio = Float.parseFloat(textoRadio);
                actualizarRadio(nuevoRadio);
            } catch (NumberFormatException e) {
                Toast.makeText(this, "Introduce un número válido", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Cuando el mapa está listo cargo los datos de la empresa desde la API
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        cargarUbicacionEmpresa();
    }

    // GET /api/admin/empresa — cargo lat, lon y radio y los pinto en el mapa
    private void cargarUbicacionEmpresa() {
        RetrofitClient.getApiService()
                .getEmpresa("Bearer " + token)
                .enqueue(new Callback<EmpresaResponse>() {
                    @Override
                    public void onResponse(Call<EmpresaResponse> call, Response<EmpresaResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            EmpresaResponse empresa = response.body();
                            ubicacionEmpresa = new LatLng(empresa.lat, empresa.lon);
                            pintarEnMapa(empresa.radio);
                        } else {
                            Toast.makeText(MapaEmpresaActivity.this,
                                    "No se pudo cargar la ubicación de la empresa",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<EmpresaResponse> call, Throwable t) {
                        Toast.makeText(MapaEmpresaActivity.this,
                                "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    // Pinto el marcador y el círculo en el mapa con el radio que recibo
    private void pintarEnMapa(float radio) {
        mMap.clear(); // Limpio el mapa por si estoy redibujando tras un cambio de radio

        // Marcador en la ubicación de la empresa
        mMap.addMarker(new MarkerOptions()
                .position(ubicacionEmpresa)
                .title("Centro de trabajo"));

        // Círculo del radio permitido para fichar
        circuloRadio = mMap.addCircle(new CircleOptions()
                .center(ubicacionEmpresa)
                .radius(radio)              // Radio en metros
                .strokeColor(0xFF0000FF)    // Borde azul
                .fillColor(0x220000FF));    // Relleno azul transparente

        // Centro la cámara sobre la empresa con zoom de nivel calle
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ubicacionEmpresa, 16f));
    }

    // PUT /api/admin/empresa — actualizo el radio en el servidor y redibujó el círculo
    private void actualizarRadio(float nuevoRadio) {
        EmpresaUpdateRequest request = new EmpresaUpdateRequest(nuevoRadio);

        RetrofitClient.getApiService()
                .updateEmpresa("Bearer " + token, request)
                .enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            Toast.makeText(MapaEmpresaActivity.this,
                                    response.body().msg, Toast.LENGTH_SHORT).show();
                            // Redibujó el círculo en el mapa con el nuevo radio sin recargar la pantalla
                            if (ubicacionEmpresa != null) {
                                pintarEnMapa(nuevoRadio);
                            }
                        } else {
                            Toast.makeText(MapaEmpresaActivity.this,
                                    "Error al actualizar el radio", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GenericResponse> call, Throwable t) {
                        Toast.makeText(MapaEmpresaActivity.this,
                                "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
