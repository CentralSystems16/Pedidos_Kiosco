package com.pedidos.kiosco.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.main.ObtenerReportesFiscal;
import com.pedidos.kiosco.model.Fiscal;
import com.pedidos.kiosco.pay.ModificarAutorizacion;
import java.util.List;

public class AdaptadorReportesFiscal extends RecyclerView.Adapter<AdaptadorReportesFiscal.ReportesViewHolder> {

    Context cContext;
    public static List<Fiscal> listaReportes;

    public AdaptadorReportesFiscal(Context cContext, List<Fiscal> listaReportes) {

        this.cContext = cContext;
        AdaptadorReportesFiscal.listaReportes = listaReportes;
    }

    @NonNull
    @Override
    public ReportesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv_reportes_fiscal, viewGroup, false);
        return new ReportesViewHolder(v);

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ReportesViewHolder reportesViewHolder, @SuppressLint("RecyclerView") int posicion) {

        reportesViewHolder.tvNombre.setText(listaReportes.get(posicion).getNombreUsuario());
        reportesViewHolder.tvFecha.setText(listaReportes.get(posicion).getFechaAut());
        reportesViewHolder.tvComprobante.setText(listaReportes.get(posicion).getTipoComprobante());
        reportesViewHolder.tvcaja.setText((listaReportes.get(posicion).getNombreCaja()));
        reportesViewHolder.tvserie.setText(listaReportes.get(posicion).getSerie());

        reportesViewHolder.editar.setOnClickListener(v -> {

            ObtenerReportesFiscal.idAutFiscal = listaReportes.get(posicion).getIdAutFiscal();
            cContext.startActivity(new Intent(cContext, ModificarAutorizacion.class));

        });
    }

    @Override
    public int getItemCount() {
        return listaReportes.size();
    }

    public static class ReportesViewHolder extends RecyclerView.ViewHolder {

   TextView tvNombre, tvFecha, tvComprobante, tvcaja, tvserie;
   Button editar;

        public ReportesViewHolder(@NonNull View itemView) {
            super(itemView);

        tvNombre = itemView.findViewById(R.id.nombreFiscal);
        tvFecha = itemView.findViewById(R.id.tvFechaAut);
        tvComprobante = itemView.findViewById(R.id.comprobante);
        tvcaja = itemView.findViewById(R.id.cajaFiscal);
        tvserie= itemView.findViewById(R.id.serieFiscal);
        editar = itemView.findViewById(R.id.editarFiscal);

        }
    }

}