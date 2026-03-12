package com.example.controlpresencia;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

public class FichajeFragment extends Fragment {

    private static final int PERMISSION_REQUEST_LOCATION = 1001;
    private FusedLocationProviderClient fusedLocationClient;
    private FichajeViewModel viewModel;
    private String token;
    private Button btnFichar;
    private TextView tvStatus;

    public static FichajeFragment newInstance(String token, String rol) {
        FichajeFragment fragment = new FichajeFragment();
        Bundle args = new Bundle();
        args.putString("TOKEN", token);
        args.putString("ROL", rol);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            token = getArguments().getString("TOKEN");
        }
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity());
        viewModel = new ViewModelProvider(this).get(FichajeViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_fichaje, container, false);

        tvStatus = view.findViewById(R.id.tvStatus);
        btnFichar = view.findViewById(R.id.btnFichar);
        Button btnFichajeNFC = view.findViewById(R.id.btnFichajeNFC);

        // Al iniciar, cargamos el estado actual desde la API
        viewModel.cargarEstado(token);

        viewModel.getFichajeResult().observe(getViewLifecycleOwner(), response -> {
            tvStatus.setText(response.msg);
            if ("entrada".equals(response.status)) {
                // Si el estado es "entrada" (está dentro), el botón debe ser ROJO para SALIR
                btnFichar.setBackgroundTintList(ColorStateList.valueOf(Color.RED));
                btnFichar.setText("SALIR");
                // Programar aviso de salida en 8h (480 mins)
                NotificationScheduler.programarAvisoSalida(requireContext(), 480);
                NotificationScheduler.cancelarAvisoEntrada(requireContext());
            } else {
                // Si el estado es "salida" (está fuera), el botón debe ser VERDE para ENTRAR
                btnFichar.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#4CAF50")));
                btnFichar.setText("ENTRAR");
                // Cancelar aviso de salida
                NotificationScheduler.cancelarAvisoSalida(requireContext());
                // Programar aviso de entrada para mañana a las 8:00
                NotificationScheduler.programarAvisoEntrada(requireContext(), 8, 0);
            }
        });

        viewModel.getErrorMessage().observe(getViewLifecycleOwner(), error -> 
            Toast.makeText(getContext(), error, Toast.LENGTH_LONG).show());

        btnFichar.setOnClickListener(v -> obtenerUbicacionYFichar());

        btnFichajeNFC.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), FichajeNFCActivity.class);
            intent.putExtra("TOKEN", token);
            startActivity(intent);
        });

        return view;
    }

    private void obtenerUbicacionYFichar() {
        if (androidx.core.app.ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_LOCATION);
            return;
        }

        fusedLocationClient.getCurrentLocation(com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY, new com.google.android.gms.tasks.CancellationTokenSource().getToken()).addOnSuccessListener(location -> {
            if (location != null) {
                viewModel.fichar(token, location.getLatitude(), location.getLongitude());
            } else {
                Toast.makeText(getContext(), "No se pudo obtener la ubicación. Activa el GPS y prueba otra vez.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                obtenerUbicacionYFichar();
            }
        }
    }
}
