package com.pedidos.kiosco.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.gastos.FragmentGastos;
import com.pedidos.kiosco.gastos.ListarGastos;
import com.pedidos.kiosco.model.Gastos;
import java.util.List;

public class AdaptadorGastos extends RecyclerView.Adapter<AdaptadorGastos.ReportesViewHolder> {

    Context cContext;
    public static List<Gastos> listaReportes;

    public AdaptadorGastos(Context cContext, List<Gastos> listaReportes) {

        this.cContext = cContext;
        AdaptadorGastos.listaReportes = listaReportes;
    }

    @NonNull
    @Override
    public ReportesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv_gastos, viewGroup, false);
        return new ReportesViewHolder(v);

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ReportesViewHolder reportesViewHolder, @SuppressLint("RecyclerView") int posicion) {

        ListarGastos.monto = listaReportes.get(posicion).getMonto();
        ListarGastos.fecha = listaReportes.get(posicion).getFechaCreo();
        ListarGastos.gIdGastos = listaReportes.get(posicion).getTipoComprobante();
        ListarGastos.descripcion = listaReportes.get(posicion).getDescripcion();
        ListarGastos.idFacMovimientos = listaReportes.get(posicion).getIdFacMovimiento();
        ListarGastos.estado = listaReportes.get(posicion).getIdEstado();

        reportesViewHolder.fecha.setText(listaReportes.get(posicion).getFechaCreo());
        reportesViewHolder.comprobante.setText(String.valueOf(listaReportes.get(posicion).getMonto()));

        if (ListarGastos.estado == 1) {
            reportesViewHolder.usuario.setText("Activo");
        }
        else {
            reportesViewHolder.usuario.setText("Inactivo");
        }

        reportesViewHolder.ver.setOnClickListener(view -> {

            ListarGastos.monto = listaReportes.get(posicion).getMonto();
            ListarGastos.fecha = listaReportes.get(posicion).getFechaCreo();
            ListarGastos.gIdGastos = listaReportes.get(posicion).getTipoComprobante();
            ListarGastos.descripcion = listaReportes.get(posicion).getDescripcion();
            ListarGastos.idFacMovimientos = listaReportes.get(posicion).getIdFacMovimiento();
            ListarGastos.estado = listaReportes.get(posicion).getIdEstado();

            FragmentManager fragmentManager = ((FragmentActivity) cContext).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_layout, new FragmentGastos());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

    }

    @Override
    public int getItemCount() {
        return listaReportes.size();
    }

    public static class ReportesViewHolder extends RecyclerView.ViewHolder {

   TextView fecha, comprobante, usuario;
   Button ver;

        public ReportesViewHolder(@NonNull View itemView) {
            super(itemView);

        fecha = itemView.findViewById(R.id.fechaGasto);
        comprobante = itemView.findViewById(R.id.tipoPagoGasto);
        usuario = itemView.findViewById(R.id.usuarioGasto);
        ver = itemView.findViewById(R.id.verGasto);

        }
    }
}