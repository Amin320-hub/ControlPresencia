package com.example.controlpresencia;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// ViewModel del fichaje — separo la lógica de negocio de la UI (arquitectura MVVM)
// La Activity solo observa LiveData, no hace llamadas directas a la API
public class FichajeViewModel extends ViewModel {

    // LiveData que la Activity observa para saber el resultado del fichaje
    private final MutableLiveData<FichajeResponse> fichajeResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<FichajeResponse> getFichajeResult() {
        return fichajeResult;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    // Método que llama a la API para fichar con geolocalización
    // Recibo el token JWT y las coordenadas del usuario
    public void fichar(String token, double latitud, double longitud) {
        String authHeader = "Bearer " + token;

        // Llamo al endpoint de fichaje pasando también las coordenadas GPS
        RetrofitClient.getApiService()
                .fichar(authHeader, latitud, longitud)
                .enqueue(new Callback<FichajeResponse>() {
                    @Override
                    public void onResponse(Call<FichajeResponse> call, Response<FichajeResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            // Actualizo el LiveData con el resultado — la Activity reaccionará
                            // automáticamente
                            fichajeResult.postValue(response.body());
                        } else {
                            // Si el servidor responde pero con error (ej: 403 fuera de rango GPS)
                            errorMessage.postValue("Error del servidor: " + response.code());
                        }
                    }

                    @Override
                    public void onFailure(Call<FichajeResponse> call, Throwable t) {
                        // Error de red o de conexión
                        errorMessage.postValue("Error de red: " + t.getMessage());
                    }
                });
    }
}
