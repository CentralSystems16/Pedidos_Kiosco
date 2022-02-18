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
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.fragments.TicketDatos;
import com.pedidos.kiosco.main.ObtenerReportes;
import com.pedidos.kiosco.model.Reportes;
import java.util.List;

public class AdaptadorReportes extends RecyclerView.Adapter<AdaptadorReportes.ReportesViewHolder> {

    Context cContext;
    public static List<Reportes> listaReportes;

    public AdaptadorReportes(Context cContext, List<Reportes> listaReportes) {

        this.cContext = cContext;
        AdaptadorReportes.listaReportes = listaReportes;
    }

    @NonNull
    @Override
    public ReportesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv_reportes, viewGroup, false);
        return new ReportesViewHolder(v);

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ReportesViewHolder reportesViewHolder, @SuppressLint("RecyclerView") int posicion) {

        reportesViewHolder.tvNombre.setText(listaReportes.get(posicion).getNombre());
        reportesViewHolder.tvFecha.setText(listaReportes.get(posicion).getFechaFinalizo());
        reportesViewHolder.tvPedido.setText(Integer.toString(listaReportes.get(posicion).getPedido()));

        reportesViewHolder.ver.setOnClickListener(v -> {

            Login.gIdPedido = listaReportes.get(posicion).getPedido();
            FragmentManager fragmentManager = ((FragmentActivity) cContext).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.reportes, new TicketDatos());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();


        });

        if (Principal.gIdEstadoCliente == 2){
            reportesViewHolder.anular.setVisibility(View.VISIBLE);
            reportesViewHolder.reeImprimir.setVisibility(View.VISIBLE);
        }

        reportesViewHolder.anular.setOnClickListener(view -> new AlertDialog.Builder(cContext)
                .setTitle("Confirmación")
                .setMessage("¿Esta seguro que desea anular el pedido?, ¡Esta acción ya no se puede revertir!")

                .setPositiveButton("CONFIRMAR", (dialog, which) -> {

                    Login.gIdPedidoReporte = listaReportes.get(posicion).getPedido();
                    ejecutarServicio("http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/actualizarEstado.php" + "?id_estado_prefactura=3" + "&id_prefactura=" + Login.gIdPedidoReporte);
                    Toast.makeText(cContext, "El pedido se ha anulado", Toast.LENGTH_SHORT).show();
                    cContext.startActivity(new Intent(cContext, ObtenerReportes.class));

                })
                .setNegativeButton("CANCELAR",
                        (dialog, which) -> dialog.dismiss()
                )
                .setIcon(android.R.drawable.ic_dialog_info)
                .show());

    }

    public void ejecutarServicio (String URL){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {},
                error -> {});
        RequestQueue requestQueue = Volley.newRequestQueue(cContext);
        requestQueue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return listaReportes.size();
    }

    public static class ReportesViewHolder extends RecyclerView.ViewHolder {

   TextView tvNombre, tvFecha, tvPedido;
   Button reeImprimir, ver, anular;

        public ReportesViewHolder(@NonNull View itemView) {
            super(itemView);

        tvNombre = itemView.findViewById(R.id.nombreCliente);
        tvFecha = itemView.findViewById(R.id.tvFecha);
        tvPedido = itemView.findViewById(R.id.numeroPedido);
        reeImprimir = itemView.findViewById(R.id.reimprimir);
        ver = itemView.findViewById(R.id.verReporte);
        anular = itemView.findViewById(R.id.anular);

        }
    }

}