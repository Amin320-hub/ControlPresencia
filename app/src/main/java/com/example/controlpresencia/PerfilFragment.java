package com.example.controlpresencia;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class PerfilFragment extends Fragment {

    private String token;
    private String rol;

    public static PerfilFragment newInstance(String token, String rol) {
        PerfilFragment fragment = new PerfilFragment();
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
            rol = getArguments().getString("ROL");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);

        view.findViewById(R.id.cardHoras).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), HorasExtraActivity.class);
            intent.putExtra("TOKEN", token);
            startActivity(intent);
        });

        view.findViewById(R.id.cardIncidencias).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), IncidenciasActivity.class);
            intent.putExtra("TOKEN", token);
            startActivity(intent);
        });

        view.findViewById(R.id.cardPassword).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), CambiarPasswordActivity.class);
            intent.putExtra("TOKEN", token);
            startActivity(intent);
        });

        LinearLayout layoutAdmin = view.findViewById(R.id.layoutAdmin);
        if ("Administrador".equals(rol)) {
            layoutAdmin.setVisibility(View.VISIBLE);
            view.findViewById(R.id.btnAdminPanel).setOnClickListener(v -> {
                Intent intent = new Intent(getContext(), AdminActivity.class);
                intent.putExtra("TOKEN", token);
                startActivity(intent);
            });
        } else {
            layoutAdmin.setVisibility(View.GONE);
        }

        view.findViewById(R.id.btnLogout).setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }
}
