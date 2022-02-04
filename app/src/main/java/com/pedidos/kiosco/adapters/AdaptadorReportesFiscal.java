package com.pedidos.kiosco.adapters;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
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

        reportesViewHolder.tvNombre.setText(String.valueOf(listaReportes.get(posicion).getIdFiscal()));
        ObtenerReportesFiscal.numeroAut = listaReportes.get(posicion).getIdFiscal();
        reportesViewHolder.tvComprobante.setText(listaReportes.get(posicion).getComprobante());
        reportesViewHolder.tvcaja.setText((listaReportes.get(posicion).getCaja()));
        reportesViewHolder.tvserie.setText(listaReportes.get(posicion).getSucursal());

        reportesViewHolder.editar.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));
        reportesViewHolder.editar.setOnClickListener(v -> {

            ObtenerReportesFiscal.idAutFiscal = listaReportes.get(posicion).getIdFiscal();
            cContext.startActivity(new Intent(cContext, ModificarAutorizacion.class));

        });
    }

    @Override
    public int getItemCount() {
        return listaReportes.size();
    }

    public static class ReportesViewHolder extends RecyclerView.ViewHolder {

   TextView tvNombre, tvComprobante, tvcaja, tvserie;
   Button editar;

        public ReportesViewHolder(@NonNull View itemView) {
            super(itemView);

        tvNombre = itemView.findViewById(R.id.nombreFiscal2);
        tvComprobante = itemView.findViewById(R.id.comprobante);
        tvcaja = itemView.findViewById(R.id.cajaFiscal);
        tvserie= itemView.findViewById(R.id.serieFiscal);
        editar = itemView.findViewById(R.id.editarFiscal);


        }
    }

}