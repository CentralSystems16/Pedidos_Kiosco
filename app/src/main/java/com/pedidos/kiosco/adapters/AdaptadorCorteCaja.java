package com.pedidos.kiosco.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.fragments.CrearReporteCierreCaja;
import com.pedidos.kiosco.fragments.TicketDatos;
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
    public void onBindViewHolder(@NonNull CategoriaViewHolder categoriaViewHolder, @SuppressLint("RecyclerView") int posicion) {

        categoriaViewHolder.tvFecha.setText(listaCorte.get(posicion).getFecha());
        categoriaViewHolder.tvCaja.setText(String.valueOf(listaCorte.get(posicion).getNoCaja()));
        categoriaViewHolder.tvUsers.setText(listaCorte.get(posicion).getNombreEmpleado());

        categoriaViewHolder.reimprimir.setOnClickListener(view -> {


        int corte = listaCorte.get(posicion).getIdCierreCaja();
            System.out.println("Corte en el adaptador: " + corte);
            Bundle datosAEnviar = new Bundle();
            datosAEnviar.putInt("cierre", corte);
            
            Fragment fragmento = new CrearReporteCierreCaja();
            fragmento.setArguments(datosAEnviar);
            FragmentManager fragmentManager = ((FragmentActivity) cContext).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.cortecaja, fragmento);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        });



    }

    @Override
    public int getItemCount() {
        return listaCorte.size();
    }

    public static class CategoriaViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsers, tvFecha, tvCaja;
        Button reimprimir;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);

        tvUsers = itemView.findViewById(R.id.tvNombreEmpleado);
        tvFecha = itemView.findViewById(R.id.tvFecha);
        tvCaja = itemView.findViewById(R.id.tvNoCaja);
        reimprimir = itemView.findViewById(R.id.reimprimirCorte);

        }
    }
}