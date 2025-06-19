package com.example.teschachatbot_f;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Button;
import androidx.annotation.NonNull;


import androidx.recyclerview.widget.RecyclerView;

import com.example.teschachatbot_f.models.Usuario;

import java.util.List;

public class UsuarioAdapter extends RecyclerView.Adapter<UsuarioAdapter.UsuarioViewHolder> {

    private List<Usuario> listaUsuarios;
    private OnUsuarioClickListener listener;

    public interface OnUsuarioClickListener {
        void onEditarClick(Usuario usuario);
        void onEliminarClick(Usuario usuario);
    }

    public UsuarioAdapter(List<Usuario> lista, OnUsuarioClickListener listener) {
        this.listaUsuarios = lista;
        this.listener = listener;
    }

    @NonNull
    @Override
    public UsuarioViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.usuario_item, parent, false);
        return new UsuarioViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UsuarioViewHolder holder, int position) {
        Usuario usuario = listaUsuarios.get(position);
        holder.nombreText.setText(usuario.getNombre());
        holder.correoText.setText(usuario.getCorreo());

        holder.btnEditar.setOnClickListener(v -> listener.onEditarClick(usuario));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminarClick(usuario));
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public void actualizarLista(List<Usuario> nuevaLista) {
        listaUsuarios = nuevaLista;
        notifyDataSetChanged();
    }

    static class UsuarioViewHolder extends RecyclerView.ViewHolder {
        TextView nombreText, correoText;
        Button btnEditar, btnEliminar;

        public UsuarioViewHolder(@NonNull View itemView) {
            super(itemView);
            nombreText = itemView.findViewById(R.id.usuarioNombre);
            correoText = itemView.findViewById(R.id.usuarioCorreo);
            btnEditar = itemView.findViewById(R.id.btnEditarUsuario);
            btnEliminar = itemView.findViewById(R.id.btnEliminarUsuario);
        }
    }
}
