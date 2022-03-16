package com.pedidos.kiosco.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.main.ObtenerEstados;
import com.pedidos.kiosco.main.ObtenerMovimientos;
import com.pedidos.kiosco.model.Estados;
import java.util.List;

public class AdaptadorEstados extends RecyclerView.Adapter<AdaptadorEstados.EstadosViewHolder> {

    Context cContext;
    public static List<Estados> listaEstados;

    public AdaptadorEstados(Context cContext, List<Estados> listaEstados) {

        this.cContext = cContext;
        AdaptadorEstados.listaEstados = listaEstados;
    }

    @NonNull
    @Override
    public EstadosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_estados, parent, false);
        return new EstadosViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull EstadosViewHolder holder, @SuppressLint("RecyclerView") int posicion) {

        if (posicion == 0){
            holder.itemView.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(233, 30,99)));
        }

        if (posicion == 1){
            holder.itemView.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(0, 150,136)));
        }

        if (posicion == 2){
            holder.itemView.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(139, 195,74)));
        }

        if (posicion == 3){
            holder.itemView.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(232, 171,96)));
        }

        if (posicion == 4){
            holder.itemView.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255, 102,102)));
        }

        if (posicion == 5){
            holder.itemView.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(82, 175,221)));
        }

        if (posicion == 6){
            holder.itemView.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(96, 125,139)));
        }

        if (posicion == 7){
            holder.itemView.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(255, 235,59)));
        }

        if (posicion == 8){
            holder.itemView.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(226, 109,94)));
        }

        if (posicion == 9){
            holder.itemView.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(187, 134,252)));
        }

        Estados estados = listaEstados.get(posicion);

        holder.estado.setText(listaEstados.get(posicion).getNomEstado());

        Principal.gIdEstado = listaEstados.get(posicion).getIdEstado();
        Glide.with(cContext).load(estados.getImgEstado()).into(holder.imageView);

        holder.itemView.setOnClickListener(view -> {
            Principal.gIdEstadoCliente = listaEstados.get(posicion).getIdEstado();
            if (Principal.gIdEstadoCliente == 2){
                Principal.gIdEstadoCliente = listaEstados.get(posicion).getIdEstado();
                ObtenerEstados.estadosNombre = listaEstados.get(posicion).getNomEstado();
                cContext.startActivity(new Intent(cContext, ObtenerMovimientos.class));
            } else {
                Principal.gIdEstadoCliente = listaEstados.get(posicion).getIdEstado();
                ObtenerEstados.estadosNombre = listaEstados.get(posicion).getNomEstado();
            }
        });
    }

    @Override
    public int getItemCount() {
        return listaEstados.size();
    }

    public static class EstadosViewHolder extends RecyclerView.ViewHolder {

        TextView estado;
        ImageView imageView;

        public EstadosViewHolder(@NonNull View itemView) {
            super(itemView);

            estado = itemView.findViewById(R.id.tvNombreEst);
            imageView = itemView.findViewById(R.id.imgEstados);

        }
    }

}
