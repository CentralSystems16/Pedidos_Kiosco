package com.pedidos.kiosco.adapters;

import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.model.Cierre;
import java.util.List;

public class AdaptadorCorteCaja extends RecyclerView.Adapter<AdaptadorCorteCaja.CategoriaViewHolder> {

    Context cContext;
    public static List<Cierre> listaCorte;

    public AdaptadorCorteCaja(Context cContext, List<Cierre> listaCorte) {

        this.cContext = cContext;
        AdaptadorCorteCaja.listaCorte = listaCorte;

    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv_corte, viewGroup, false);
        return new CategoriaViewHolder(v);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder categoriaViewHolder, int posicion) {

    }

    @Override
    public int getItemCount() {
        return listaCorte.size();
    }

    public static class CategoriaViewHolder extends RecyclerView.ViewHolder {

        ImageView imagen;
        TextView tvUsers;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);

        tvUsers = itemView.findViewById(R.id.tvTipoPago);
        imagen = itemView.findViewById(R.id.imgItemPago);

        }
    }
}