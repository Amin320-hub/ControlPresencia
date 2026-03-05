package com.example.controlpresencia;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// Pantalla para solicitar el cambio de contraseña por email
// Introduzco mi email y el servidor me manda un enlace para cambiarla desde la web
public class CambiarPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambiar_password);

        EditText etEmail = findViewById(R.id.etEmail);
        Button btnSolicitar = findViewById(R.id.btnSolicitarCambio);

        btnSolicitar.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(this, "Introduce un email válido", Toast.LENGTH_SHORT).show();
                return;
            }

            solicitarCambio(email);
        });
    }

    // Llamo a la API para que el servidor envíe el email de recuperación
    private void solicitarCambio(String email) {
        EmailRequest request = new EmailRequest(email);

        // Este endpoint no requiere autenticación JWT porque puedo no estar logueado
        RetrofitClient.getApiService()
                .solicitarCambioPassword(request)
                .enqueue(new Callback<GenericResponse>() {
                    @Override
                    public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Muestro el mensaje del servidor (siempre responde con éxito por seguridad
                            // para no revelar si el email existe o no)
                            Toast.makeText(CambiarPasswordActivity.this,
                                    response.body().msg, Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(CambiarPasswordActivity.this,
                                    "Error al enviar la solicitud", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<GenericResponse> call, Throwable t) {
                        Toast.makeText(CambiarPasswordActivity.this,
                                "Error de red: " + t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
