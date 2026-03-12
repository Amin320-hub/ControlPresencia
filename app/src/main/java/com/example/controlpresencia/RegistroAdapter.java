package com.example.controlpresencia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class RegistroAdapter extends RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder> {

    private List<RegistroResponse> listaRegistros;
    private boolean isGlobalAdminView; // True si lo mostramos en el panel de Admin para ver registros de TODOS

    public RegistroAdapter(List<RegistroResponse> listaRegistros, boolean isGlobalAdminView) {
        this.listaRegistros = listaRegistros;
        this.isGlobalAdminView = isGlobalAdminView;
    }

    public void setRegistros(List<RegistroResponse> nuevosRegistros) {
        this.listaRegistros = nuevosRegistros;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RegistroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_registro, parent, false);
        return new RegistroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistroViewHolder holder, int position) {
        RegistroResponse registro = listaRegistros.get(position);

        // Parseo la fecha de la hora de entrada (si tiene)
        if (registro.hora_entrada != null && registro.hora_entrada.length() >= 10) {
            holder.tvFecha.setText(registro.hora_entrada.substring(0, 10)); // YYYY-MM-DD
            holder.tvHoraEntrada.setText(registro.hora_entrada.substring(11, 19)); // HH:MM:SS
        } else {
            holder.tvFecha.setText("Fecha desconocida");
            holder.tvHoraEntrada.setText("--:--");
        }

        if (registro.hora_salida != null && registro.hora_salida.length() >= 19) {
            holder.tvHoraSalida.setText(registro.hora_salida.substring(11, 19));
        } else {
            holder.tvHoraSalida.setText("En curso...");
            holder.tvHoraSalida.setTextColor(holder.itemView.getContext().getResources().getColor(R.color.text_secondary));
        }

        // Si es la vista de Admin, y la API devuelve el nombre, lo mostramos
        if (isGlobalAdminView && registro.nombre != null && !registro.nombre.isEmpty()) {
            holder.tvNombreEmpleado.setVisibility(View.VISIBLE);
            holder.tvNombreEmpleado.setText("Empleado: " + registro.nombre);
        } else {
            holder.tvNombreEmpleado.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return listaRegistros != null ? listaRegistros.size() : 0;
    }

    public static class RegistroViewHolder extends RecyclerView.ViewHolder {
        TextView tvFecha, tvHoraEntrada, tvHoraSalida, tvNombreEmpleado;

        public RegistroViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = itemView.findViewById(R.id.tvFecha);
            tvHoraEntrada = itemView.findViewById(R.id.tvHoraEntrada);
            tvHoraSalida = itemView.findViewById(R.id.tvHoraSalida);
            tvNombreEmpleado = itemView.findViewById(R.id.tvNombreEmpleado);
        }
    }
}
