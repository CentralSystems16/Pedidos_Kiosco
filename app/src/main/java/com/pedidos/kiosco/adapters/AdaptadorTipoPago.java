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

import com.bumptech.glide.Glide;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.desing.TipoPago;
import com.pedidos.kiosco.model.Categorias;
import com.pedidos.kiosco.model.Pago;

import java.util.List;

public class AdaptadorTipoPago extends RecyclerView.Adapter<AdaptadorTipoPago.CategoriaViewHolder> {

    Context cContext;
    public static List<Pago> listaPago;

    public AdaptadorTipoPago(Context cContext, List<Pago> listaPago) {

        this.cContext = cContext;
        AdaptadorTipoPago.listaPago = listaPago;

    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv_pago, viewGroup, false);
        return new CategoriaViewHolder(v);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder categoriaViewHolder, int posicion) {

        Pago pago = listaPago.get(posicion);

        categoriaViewHolder.tvUsers.setText(listaPago.get(posicion).getNombrePago());
        TipoPago.idTipoPago = listaPago.get(posicion).getIdPago();
        Glide.with(cContext).load(pago.getImgPago()).into(categoriaViewHolder.imagen);

        categoriaViewHolder.itemView.setOnClickListener(v -> {

            TipoPago.idTipoPago = listaPago.get(posicion).getIdPago();
            TipoPago myDialogFragment = new TipoPago();
            myDialogFragment.dismiss();

        });
    }

    @Override
    public int getItemCount() {
        return listaPago.size();
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