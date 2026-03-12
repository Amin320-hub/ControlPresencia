package com.example.controlpresencia;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrosFragment extends Fragment {
    private String token;
    private RecyclerView rvRegistros;
    private RegistroAdapter registroAdapter;

    public static RegistrosFragment newInstance(String token) {
        RegistrosFragment fragment = new RegistrosFragment();
        Bundle args = new Bundle();
        args.putString("TOKEN", token);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            token = getArguments().getString("TOKEN");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_registros, container, false);
        
        rvRegistros = view.findViewById(R.id.rvRegistros);
        rvRegistros.setLayoutManager(new LinearLayoutManager(getContext()));
        
        registroAdapter = new RegistroAdapter(new ArrayList<>(), false);
        rvRegistros.setAdapter(registroAdapter);
        
        cargarRegistros();
        return view;
    }

    private void cargarRegistros() {
        String authHeader = "Bearer " + token;
        RetrofitClient.getApiService().getMisRegistros(authHeader, null, null)
            .enqueue(new Callback<List<RegistroResponse>>() {
                @Override
                public void onResponse(Call<List<RegistroResponse>> call, Response<List<RegistroResponse>> response) {
                    if (isAdded() && response.isSuccessful() && response.body() != null) {
                         List<RegistroResponse> registros = response.body();
                         // Si la lista está vacía, podríamos crear un item sintético, pero en el adaptador de registros
                         // normal solo se muestra si hay datos. Si quieres puedes añadir lógica extra aquí.
                         registroAdapter.setRegistros(registros);
                    }
                }
                @Override
                public void onFailure(Call<List<RegistroResponse>> call, Throwable t) {
                    // Fallo de red
                }
            });
    }
}
