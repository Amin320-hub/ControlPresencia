package com.example.controlpresencia;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class EmpleadoAdapter extends RecyclerView.Adapter<EmpleadoAdapter.EmpleadoViewHolder> {

    private List<TrabajadorResponse> listaEmpleados;
    private OnEmpleadoClickListener listener;

    public interface OnEmpleadoClickListener {
        void onEmpleadoClick(TrabajadorResponse empleado);
    }

    public EmpleadoAdapter(List<TrabajadorResponse> listaEmpleados, OnEmpleadoClickListener listener) {
        this.listaEmpleados = listaEmpleados;
        this.listener = listener;
    }

    public void setEmpleados(List<TrabajadorResponse> nuevosEmpleados) {
        this.listaEmpleados = nuevosEmpleados;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public EmpleadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_empleado, parent, false);
        return new EmpleadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmpleadoViewHolder holder, int position) {
        TrabajadorResponse empleado = listaEmpleados.get(position);

        holder.tvNombreCompleto.setText(empleado.nombre + " " + empleado.apellidos + " (" + empleado.nif + ")");
        holder.tvEmailEmpleado.setText(empleado.email != null ? empleado.email : "Sin email");
        holder.tvRolEmpleado.setText(empleado.rol != null ? empleado.rol : "Sin Rol");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onEmpleadoClick(empleado);
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaEmpleados != null ? listaEmpleados.size() : 0;
    }

    public static class EmpleadoViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreCompleto, tvEmailEmpleado, tvRolEmpleado;

        public EmpleadoViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreCompleto = itemView.findViewById(R.id.tvNombreCompleto);
            tvEmailEmpleado = itemView.findViewById(R.id.tvEmailEmpleado);
            tvRolEmpleado = itemView.findViewById(R.id.tvRolEmpleado);
        }
    }
}
