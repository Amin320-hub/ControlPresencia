package com.example.controlpresencia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class IncidenciaAdapter extends RecyclerView.Adapter<IncidenciaAdapter.IncidenciaViewHolder> {

    private List<IncidenciaResponse> listaIncidencias;

    public IncidenciaAdapter(List<IncidenciaResponse> listaIncidencias) {
        this.listaIncidencias = listaIncidencias;
    }

    public void setIncidencias(List<IncidenciaResponse> nuevasIncidencias) {
        this.listaIncidencias = nuevasIncidencias;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public IncidenciaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_incidencia, parent, false);
        return new IncidenciaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IncidenciaViewHolder holder, int position) {
        IncidenciaResponse incidencia = listaIncidencias.get(position);

        if (incidencia.fecha_hora != null && incidencia.fecha_hora.length() >= 16) {
             // La T se reemplaza por un espacio
            holder.tvFechaIncidencia.setText(incidencia.fecha_hora.replace("T", " ").substring(0, 16));
        } else {
            holder.tvFechaIncidencia.setText("Fecha desconocida");
        }

        holder.tvDescripcionIncidencia.setText(incidencia.descripcion != null ? incidencia.descripcion : "Sin descripción");

        // En Admin no pasamos un iAdmin sino que la info del trabajador viene dentro de descripcion. 
        holder.tvEmpleadoIncidencia.setVisibility(View.GONE);
    }

    @Override
    public int getItemCount() {
        return listaIncidencias != null ? listaIncidencias.size() : 0;
    }

    public static class IncidenciaViewHolder extends RecyclerView.ViewHolder {
        TextView tvFechaIncidencia, tvDescripcionIncidencia, tvEmpleadoIncidencia;

        public IncidenciaViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFechaIncidencia = itemView.findViewById(R.id.tvFechaIncidencia);
            tvDescripcionIncidencia = itemView.findViewById(R.id.tvDescripcionIncidencia);
            tvEmpleadoIncidencia = itemView.findViewById(R.id.tvEmpleadoIncidencia);
        }
    }
}
