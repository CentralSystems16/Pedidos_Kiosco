package com.pedidos.kiosco.adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.Splash;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.main.ObtenerReportes;
import com.pedidos.kiosco.model.Reportes;
import com.pedidos.kiosco.other.Ayuda;
import com.pedidos.kiosco.utils.Numero_a_Letra;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdaptadorReportes extends RecyclerView.Adapter<AdaptadorReportes.ReportesViewHolder> {

    Context cContext;
    public static List<Reportes> listaReportes;
    public static final int PERMISSION_BLUETOOTH = 1;
    String gNombre, sucursal, gFecha, gNombreProd, encodedPDF;
    double gTotal, gCantidad, gPrecioUni, gDesc, exento, gravado, noSujeto;
    int gIdFacMovimiento, noCaja;
    StringBuilder sb1 = new StringBuilder("");

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

            cContext.startActivity(new Intent(cContext, Ayuda.class));


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