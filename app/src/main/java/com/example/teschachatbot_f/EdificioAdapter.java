package com.example.teschachatbot_f;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teschachatbot_f.models.Edificio;

import java.util.List;

public class EdificioAdapter extends RecyclerView.Adapter<EdificioAdapter.ViewHolder> {

    private final List<Edificio> edificios;
    private final Context context;

    public EdificioAdapter(List<Edificio> edificios, Context context) {
        this.edificios = edificios;
        this.context = context;
    }

    @NonNull
    @Override
    public EdificioAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_edificio, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EdificioAdapter.ViewHolder holder, int position) {
        Edificio edificio = edificios.get(position);
        holder.tvNombreEdificio.setText(edificio.getNombre());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, LocationInfoActivity.class);
            intent.putExtra("nombre", edificio.getNombre());
            intent.putExtra("descripcion", edificio.getDescripcion());
            intent.putExtra("latitud", edificio.getCoordenadas()[0]);
            intent.putExtra("longitud", edificio.getCoordenadas()[1]);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return edificios.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvNombreEdificio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNombreEdificio = itemView.findViewById(R.id.tvNombreEdificio);
        }
    }
}
