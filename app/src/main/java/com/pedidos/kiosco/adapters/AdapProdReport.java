package com.pedidos.kiosco.adapters;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gFoto;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.fragments.TicketDatos;
import com.pedidos.kiosco.model.DetReporte;
import com.pedidos.kiosco.other.ActualizarDetPedido;
import com.pedidos.kiosco.other.ActualizarPedido;
import com.pedidos.kiosco.other.EliminarDetPedido;
import java.text.DecimalFormat;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AdapProdReport extends RecyclerView.Adapter<AdapProdReport.ProdReportViewHolder>{

    Context context;
    List<DetReporte> listaProdReport;
    public static int lidDetPedido = 0;
    public static double lCantProducto = 0, lNewCantProducto = 0;
    public static Double lPrecioVta = 0.00, lDetMonto = 0.00, lDetMontoIva = 0.00, lDetMontoFinal = 0.00, lNewDetMonto = 0.00, lNewDetMontoIva = 0.00, lNewDetMontoFinal = 0.00,
            monto = 0.00, montoIva = 0.00;
    public static String lNombreProducto;
    DecimalFormat formatoDecimal = new DecimalFormat("#.00");
    public static double counter;

    public AdapProdReport(Context context, List<DetReporte> listaProdReport) {
        this.context = context;
        this.listaProdReport = listaProdReport;
    }

    @NonNull
    @Override
    public ProdReportViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rv_productos_reporte, parent, false);
        return new ProdReportViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ProdReportViewHolder holder, int position) {

        holder.setIsRecyclable(false);

        holder.tvNombre.setText(listaProdReport.get(position).getNombreProducto());
        lDetMonto = listaProdReport.get(position).getMonto();
        lDetMontoIva = listaProdReport.get(position).getMontoIva();
        lPrecioVta = listaProdReport.get(position).getPrecioVenta();
        lNombreProducto = listaProdReport.get(position).getNombreProducto();

        holder.tvPrecio.setText(String.format("%.2f", lPrecioVta));

        lCantProducto = listaProdReport.get(position).getCantiProd();
        holder.tvCantidad.setText(Double.toString(lCantProducto));
        lDetMontoFinal = lDetMonto + lDetMontoIva;
        holder.totalItem.setText(String.format("%.2f", lDetMontoFinal));

        if(lCantProducto == 1.0){
            holder.btnMenos.setEnabled(false);
        }

        holder.btnMas.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));
        holder.btnMenos.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));
        holder.btnEliminar.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));
        holder.btnMas.setOnClickListener(v -> {

                        holder.btnMenos.setEnabled(true);
                        counter = Double.parseDouble(holder.tvCantidad.getText().toString());
                        ++counter;
                        holder.tvCantidad.setText(String.valueOf(counter));

                        lidDetPedido = listaProdReport.get(position).getIdDetPedido();
                        lDetMonto = listaProdReport.get(position).getMonto();
                        lDetMontoIva = listaProdReport.get(position).getMontoIva();
                        lPrecioVta = listaProdReport.get(position).getPrecioVenta();

                        lNewCantProducto = counter;

                        lNewDetMontoFinal = lPrecioVta * lNewCantProducto;

                        lNewDetMonto = lNewDetMontoFinal / 1.13;

                        lNewDetMontoIva = lNewDetMonto * 0.13;

                        holder.totalItem.setText(String.format("%.2f", lNewDetMontoFinal));

                        Double newSubTotal = TicketDatos.gTotal + lPrecioVta;
                        TicketDatos.gTotal = TicketDatos.gTotal + lPrecioVta;

                        TicketDatos.totalFinal.setText(formatoDecimal.format(newSubTotal));

                        monto = newSubTotal / 1.13;
                        montoIva = monto * 0.13;

                        try {
                            new ActualizarDetPedido(holder.itemView.getContext()).execute().get();

                            /*exitoLocal =*/  new ActualizarDetPedido(holder.itemView.getContext()).execute().get();

                            //if(exitoLocal == true) {
                            listaProdReport.get(position).setCantiProd(lNewCantProducto);
                            listaProdReport.get(position).setMonto(lNewDetMonto);
                            listaProdReport.get(position).setMontoIva(lNewDetMontoIva);
                            // }
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }

                        try {
                            new ActualizarPedido(holder.itemView.getContext()).execute().get();
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }

            String actualizar_url = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/actualizarMovimiento.php"
                    + "?monto=" + AdapProdReport.monto
                    + "&monto_iva=" + AdapProdReport.montoIva
                    + "&id_prefactura=" + Login.gIdPedido;

            String actualizar_url2 = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/actualizarDetMovimiento.php"
                    + "?cantidad_producto=" + AdapProdReport.lNewCantProducto
                    + "&monto=" + AdapProdReport.lNewDetMonto
                    + "&monto_iva=" + AdapProdReport.lNewDetMontoIva
                    + "&id_fac_det_movimiento=" + AdapProdReport.lidDetPedido;

                        ejecutarServicio(actualizar_url);
                        ejecutarServicio(actualizar_url2);

        });

        holder.btnMenos.setOnClickListener(v -> {

                        double counter = Double.parseDouble(holder.tvCantidad.getText().toString());
                        if (counter > 1.0) {
                            --counter;
                            holder.tvCantidad.setText(String.valueOf(counter));
                        }
                        if (counter == 1.0){
                            holder.btnMenos.setEnabled(false);
                        }

                        lidDetPedido = listaProdReport.get(position).getIdDetPedido();
                        lDetMonto = listaProdReport.get(position).getMonto();
                        lDetMontoIva = listaProdReport.get(position).getMontoIva();
                        lPrecioVta = listaProdReport.get(position).getPrecioVenta();

                        lNewCantProducto = counter;

                        lNewDetMontoFinal = lPrecioVta * lNewCantProducto;

                        lNewDetMonto = lNewDetMontoFinal / 1.13;

                        lNewDetMontoIva = lNewDetMonto * 0.13;

                        holder.totalItem.setText(String.format("%.2f", lNewDetMontoFinal));

                        Double newSubTotal = TicketDatos.gTotal - lPrecioVta;
                        TicketDatos.gTotal = TicketDatos.gTotal - lPrecioVta;

                        TicketDatos.totalFinal.setText(formatoDecimal.format(newSubTotal));

                        monto = newSubTotal / 1.13;
                        montoIva = monto * 0.13;

                        try {
                            new ActualizarDetPedido(holder.itemView.getContext()).execute().get();
                            listaProdReport.get(position).setCantiProd(lNewCantProducto);
                            listaProdReport.get(position).setMonto(lNewDetMonto);
                            listaProdReport.get(position).setMontoIva(lNewDetMontoIva);
                        } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                        }

                        try {
                            new ActualizarPedido(holder.itemView.getContext()).execute().get();
                 } catch (ExecutionException | InterruptedException e) {
                            e.printStackTrace();
                 }
        });

        holder.btnEliminar.setOnClickListener(v -> {

            if (listaProdReport.size() == 1){
                Toast.makeText(context, "Esta tratando de eliminar todas las ordenes, por favor, 'vacie' el carrito en su lugar!", Toast.LENGTH_LONG).show();
            } else {

                lidDetPedido = listaProdReport.get(position).getIdDetPedido();
                lDetMontoFinal = listaProdReport.get(position).getMonto() + listaProdReport.get(position).getMontoIva();

                try {
                    new EliminarDetPedido(holder.itemView.getContext()).execute().get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

                //if(EliminarDetPedido.exitoDeleteProd == true) {

                double newSubTotal = TicketDatos.gTotal - lDetMontoFinal;

                TicketDatos.gTotal = TicketDatos.gTotal - lDetMontoFinal;

                TicketDatos.totalFinal.setText(String.format("%.2f",newSubTotal));
                //}

                monto = newSubTotal / 1.13;
                montoIva = monto * 0.13;

                try {
                    new ActualizarPedido(holder.itemView.getContext()).execute().get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }

                listaProdReport.remove(position);
                notifyDataSetChanged();
            }
        });

    }

    @Override
    public int getItemCount() {

        return listaProdReport.size();
    }

    public void ejecutarServicio (String URL){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {

                },
                error -> {});
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public static class ProdReportViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre, tvPrecio, tvCantidad, totalItem;
        Button btnMas, btnMenos;
        ImageButton btnEliminar;
        ImageView producto;

        public ProdReportViewHolder(@NonNull View itemView) {
            super(itemView);

            tvNombre = itemView.findViewById(R.id.tvProducto);
            tvPrecio = itemView.findViewById(R.id.precioBase);
            tvCantidad = itemView.findViewById(R.id.cantBase);
            btnMas = itemView.findViewById(R.id.btnMas);
            btnMenos = itemView.findViewById(R.id.btnMenos);
            totalItem = itemView.findViewById(R.id.totalItem);
            btnEliminar = itemView.findViewById(R.id.btnEliminar);
            producto = itemView.findViewById(R.id.imgProducto);

        }
    }
}