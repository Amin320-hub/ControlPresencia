package com.example.controlpresencia;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polygon;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Pantalla del mapa de empresa (solo admin).
// Muestro la ubicación GPS de la empresa y el círculo del radio permitido para fichar.
// Uso OpenStreetMap (osmdroid) — no necesito API Key de Google.
// Desde aquí puedo modificar el radio enviando un PUT a la API.
public class MapaEmpresaActivity extends AppCompatActivity {

    private String token;
    private MapView mapView;
    private GeoPoint ubicacionEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Configuro osmdroid con el user-agent de la app (obligatorio)
        Configuration.getInstance().setUserAgentValue(getPackageName());

        setContentView(R.layout.activity_mapa_empresa);

        token = getIntent().getStringExtra("TOKEN");

        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setMultiTouchControls(true); // Habilito zoom con pellizco
        mapView.getController().setZoom(16.0);

        Button btnActualizarRadio = findViewById(R.id.btnActualizarRadio);
        EditText etNuevoRadio = findViewById(R.id.etNuevoRadio);

        // Recojo el nuevo radio en metros que introduce el admin y lo envío al servidor
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

        // Cargo los datos de la empresa al abrir la pantalla
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
                            ubicacionEmpresa = new GeoPoint(empresa.lat, empresa.lon);
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
        mapView.getOverlays().clear(); // Limpio el mapa por si estoy redibujando

        // Marcador en la ubicación de la empresa
        Marker marker = new Marker(mapView);
        marker.setPosition(ubicacionEmpresa);
        marker.setTitle("Centro de trabajo");
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        mapView.getOverlays().add(marker);

        // Círculo del radio permitido para fichar
        Polygon circulo = new Polygon();
        circulo.setPoints(Polygon.pointsAsCircle(ubicacionEmpresa, radio));
        circulo.setStrokeColor(Color.BLUE);
        circulo.setStrokeWidth(3f);
        circulo.setFillColor(Color.argb(34, 0, 0, 255)); // Azul transparente
        circulo.setTitle("Radio de fichaje: " + (int) radio + "m");
        mapView.getOverlays().add(circulo);

        // Centro la cámara sobre la empresa
        mapView.getController().setCenter(ubicacionEmpresa);
        mapView.invalidate();
    }

    // PUT /api/admin/empresa — actualizo el radio en el servidor y redibujo el
    // círculo
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
                            // Redibujo el círculo en el mapa con el nuevo radio
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

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }
}
