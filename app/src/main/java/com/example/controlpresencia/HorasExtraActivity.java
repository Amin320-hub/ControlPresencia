package com.example.controlpresencia;

import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.card.MaterialCardView;
import java.util.Calendar;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HorasExtraActivity extends AppCompatActivity {

    private String token;
    private TextView tvMesSeleccionado, tvHorasTeoricas, tvHorasTrabajadas, tvHorasExtra;
    private MaterialCardView cardResultados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_horas_extra);

        token = getIntent().getStringExtra("TOKEN");

        Button btnSeleccionarMes = findViewById(R.id.btnSeleccionarMes);
        tvMesSeleccionado = findViewById(R.id.tvMesSeleccionado);
        tvHorasTeoricas = findViewById(R.id.tvHorasTeoricas);
        tvHorasTrabajadas = findViewById(R.id.tvHorasTrabajadas);
        tvHorasExtra = findViewById(R.id.tvHorasExtra);
        cardResultados = findViewById(R.id.cardResultados);

        btnSeleccionarMes.setOnClickListener(v -> mostrarMonthYearPicker());
        
        // Carga automática del mes actual al iniciar
        Calendar c = Calendar.getInstance();
        consultarResumen(c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1);
    }

    private void mostrarMonthYearPicker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_month_year_picker, null);
        
        final NumberPicker monthPicker = dialogView.findViewById(R.id.picker_month);
        final NumberPicker yearPicker = dialogView.findViewById(R.id.picker_year);

        // Configuración del selector de meses
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        Calendar c = Calendar.getInstance();
        monthPicker.setValue(c.get(Calendar.MONTH) + 1);

        // Configuración del selector de años
        int year = c.get(Calendar.YEAR);
        yearPicker.setMinValue(year - 5);
        yearPicker.setMaxValue(year + 1);
        yearPicker.setValue(year);

        builder.setView(dialogView)
                .setTitle("Seleccionar periodo")
                .setPositiveButton("Aceptar", (dialog, id) -> {
                    consultarResumen(yearPicker.getValue(), monthPicker.getValue());
                })
                .setNegativeButton("Cancelar", (dialog, id) -> dialog.cancel());
        
        builder.create().show();
    }

    private void consultarResumen(int year, int month) {
        // Formato estricto YYYY-MM para la API
        String mesFormateado = String.format(Locale.US, "%04d-%02d", year, month);
        tvMesSeleccionado.setText("Resumen de " + mesFormateado);
        
        Log.d("HORAS_EXTRA", "Consultando mes: " + mesFormateado);
        
        RetrofitClient.getApiService().getResumenMensual("Bearer " + token, mesFormateado)
            .enqueue(new Callback<ResumenMensualResponse>() {
                @Override
                public void onResponse(Call<ResumenMensualResponse> call, Response<ResumenMensualResponse> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        ResumenMensualResponse res = response.body();
                        cardResultados.setVisibility(View.VISIBLE);
                        
                        // Mostramos los datos con formato de dos decimales
                        tvHorasTeoricas.setText(String.format(Locale.getDefault(), "%.2f h", res.horas_teoricas));
                        tvHorasTrabajadas.setText(String.format(Locale.getDefault(), "%.2f h", res.horas_trabajadas));
                        tvHorasExtra.setText(String.format(Locale.getDefault(), "%.2f h", res.horas_extra));
                        
                        // Lógica de colores profesional: Verde para positivo (extra), Rojo para negativo (pendientes)
                        if (res.horas_extra < 0) {
                            tvHorasExtra.setTextColor(getResources().getColor(android.R.color.holo_red_dark));
                        } else if (res.horas_extra > 0) {
                            tvHorasExtra.setTextColor(getResources().getColor(android.R.color.holo_green_dark));
                        } else {
                            tvHorasExtra.setTextColor(getResources().getColor(android.R.color.darker_gray));
                        }
                    } else {
                        // Si llegamos aquí es porque la API devolvió un error (400, 404, 500)
                        Log.e("HORAS_EXTRA", "Error API: " + response.code());
                        Toast.makeText(HorasExtraActivity.this, "No hay registros para el periodo seleccionado", Toast.LENGTH_SHORT).show();
                        cardResultados.setVisibility(View.GONE);
                    }
                }

                @Override
                public void onFailure(Call<ResumenMensualResponse> call, Throwable t) {
                    Log.e("HORAS_EXTRA", "Fallo de red", t);
                    Toast.makeText(HorasExtraActivity.this, "Error de conexion con el servidor", Toast.LENGTH_SHORT).show();
                }
            });
    }
}
