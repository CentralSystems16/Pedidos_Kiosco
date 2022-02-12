package com.pedidos.kiosco.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.fragments.CierreCaja;
import com.pedidos.kiosco.model.Pago;
import com.pedidos.kiosco.other.InsertarFacTipoPagoCaja;
import com.pedidos.kiosco.other.SumaMonto;
import com.pedidos.kiosco.other.SumaMontoDevolucion;
import java.util.List;

public class AdaptadorCierreCaja extends RecyclerView.Adapter<AdaptadorCierreCaja.CategoriaViewHolder> {

    Context cContext;
    public static List<Pago> listaPago;
    Boolean desabilitado;

    public AdaptadorCierreCaja(Context cContext, List<Pago> listaPago) {

        this.cContext = cContext;
        AdaptadorCierreCaja.listaPago = listaPago;

    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv_cierre_caja, viewGroup, false);
        return new CategoriaViewHolder(v);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder categoriaViewHolder, @SuppressLint("RecyclerView") int posicion) {

        Pago pago = listaPago.get(posicion);

        categoriaViewHolder.tvUsers.setText(listaPago.get(posicion).getNombrePago());
        Glide.with(cContext).load(pago.getImgPago()).into(categoriaViewHolder.imagen);

        CierreCaja.lIdTipoPago = listaPago.get(posicion).getIdPago();
        CierreCaja.lTipoPago = listaPago.get(posicion).getNombrePago();
        new SumaMonto().execute();
        new SumaMontoDevolucion().execute();

        categoriaViewHolder.aceptar.setOnClickListener(view -> {

            CierreCaja.lIdTipoPago = listaPago.get(posicion).getIdPago();
            CierreCaja.lTipoPago = listaPago.get(posicion).getNombrePago();

            new SumaMontoDevolucion().execute();

            if (categoriaViewHolder.cierre != null && categoriaViewHolder.cierre.length() > 0) {
                CierreCaja.etCaja = Double.parseDouble(categoriaViewHolder.cierre.getText().toString());
            }

            new InsertarFacTipoPagoCaja(cContext).execute();

            categoriaViewHolder.aceptar.setEnabled(false);
            categoriaViewHolder.cierre.setEnabled(false);
            desabilitado = true;

            if (desabilitado){
                CierreCaja.aceptar.setEnabled(true);
            }

        });


    }

    @Override
    public int getItemCount() {
        return listaPago.size();
    }

    public static class CategoriaViewHolder extends RecyclerView.ViewHolder {

        ImageView imagen;
        TextView tvUsers;
        EditText cierre;
        Button aceptar;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);

        tvUsers = itemView.findViewById(R.id.tvCierre);
        imagen = itemView.findViewById(R.id.imgCierre);
        cierre = itemView.findViewById(R.id.etCierre);
        aceptar = itemView.findViewById(R.id.aceptarTipoPago);

        }
    }
}