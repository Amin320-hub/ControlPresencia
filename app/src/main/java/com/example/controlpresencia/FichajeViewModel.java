package com.example.controlpresencia;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FichajeViewModel extends ViewModel {

    private final MutableLiveData<FichajeResponse> fichajeResult = new MutableLiveData<>();
    private final MutableLiveData<String> errorMessage = new MutableLiveData<>();

    public LiveData<FichajeResponse> getFichajeResult() {
        return fichajeResult;
    }

    public LiveData<String> getErrorMessage() {
        return errorMessage;
    }

    public void cargarEstado(String token) {
        RetrofitClient.getApiService().getEstadoFichaje("Bearer " + token)
                .enqueue(new Callback<FichajeResponse>() {
                    @Override
                    public void onResponse(Call<FichajeResponse> call, Response<FichajeResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            fichajeResult.postValue(response.body());
                        }
                    }

                    @Override
                    public void onFailure(Call<FichajeResponse> call, Throwable t) {
                        errorMessage.postValue("Error de conexión");
                    }
                });
    }

    public void fichar(String token, double latitud, double longitud) {
        RetrofitClient.getApiService().fichar("Bearer " + token, latitud, longitud)
                .enqueue(new Callback<FichajeResponse>() {
                    @Override
                    public void onResponse(Call<FichajeResponse> call, Response<FichajeResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            fichajeResult.postValue(response.body());
                        } else {
                            try {
                                if (response.errorBody() != null) {
                                    String errorStr = response.errorBody().string();
                                    org.json.JSONObject jObjError = new org.json.JSONObject(errorStr);
                                    if (jObjError.has("message")) {
                                        errorMessage.postValue(jObjError.getString("message"));
                                    } else if (jObjError.has("msg")) {
                                        errorMessage.postValue(jObjError.getString("msg"));
                                    } else {
                                        errorMessage.postValue("Error: " + response.code());
                                    }
                                } else {
                                    errorMessage.postValue("Error: " + response.code());
                                }
                            } catch (Exception e) {
                                errorMessage.postValue("Error: " + response.code());
                            }
                        }
                    }

                    @Override
                    public void onFailure(Call<FichajeResponse> call, Throwable t) {
                        errorMessage.postValue("Fallo de red");
                    }
                });
    }
}
