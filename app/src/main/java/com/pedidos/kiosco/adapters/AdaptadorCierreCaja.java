package com.pedidos.kiosco.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.fragments.CierreCaja;
import com.pedidos.kiosco.model.Pago;
import com.pedidos.kiosco.other.InsertarFacTipoPagoCaja;
import com.pedidos.kiosco.other.SumaMonto;
import com.pedidos.kiosco.other.SumaMontoDevolucion;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class AdaptadorCierreCaja extends RecyclerView.Adapter<AdaptadorCierreCaja.CierreCajaViewHolder> {

    Context cContext;
    public static List<Pago> listaPago;
    Double fondoInit;
    int selectedItemPosition = 0;


    public AdaptadorCierreCaja(Context cContext, List<Pago> listaPago) {

        this.cContext = cContext;
        AdaptadorCierreCaja.listaPago = listaPago;

    }

    @NonNull
    @Override
    public CierreCajaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv_cierre_caja, viewGroup, false);
        return new CierreCajaViewHolder(v);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull CierreCajaViewHolder cierreCajaViewHolder, @SuppressLint("RecyclerView") int posicion) {

        Pago pago = listaPago.get(posicion);

        cierreCajaViewHolder.tvUsers.setText(listaPago.get(posicion).getNombrePago());
        Glide.with(cContext).load(pago.getImgPago()).into(cierreCajaViewHolder.imagen);

        CierreCaja.lIdTipoPago = listaPago.get(posicion).getIdPago();
        CierreCaja.lTipoPago = listaPago.get(posicion).getNombrePago();

        /*if (CierreCaja.lIdTipoPago != 1){
            cierreCajaViewHolder.cvCierre.setVisibility(View.INVISIBLE);
        }*/

        cierreCajaViewHolder.aceptar.setOnClickListener(view -> {

            if (cierreCajaViewHolder.cierre.getText().toString().equals("")){
                Toast.makeText(cContext, "Por favor, agregue el monto.", Toast.LENGTH_SHORT).show();
            }

            else {

                CierreCaja.lIdTipoPago = listaPago.get(posicion).getIdPago();
                CierreCaja.lTipoPago = listaPago.get(posicion).getNombrePago();

                if (cierreCajaViewHolder.cierre != null && cierreCajaViewHolder.cierre.length() > 0) {
                    CierreCaja.montoFisico = Double.parseDouble(cierreCajaViewHolder.cierre.getText().toString());
                }

                obtenerCierreCaja();
                new SumaMonto().execute();
                new SumaMontoDevolucion().execute();

                new InsertarFacTipoPagoCaja(cContext).execute();

                cierreCajaViewHolder.aceptar.setEnabled(false);
                cierreCajaViewHolder.aceptar.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(117, 117, 117)));
                cierreCajaViewHolder.cierre.setEnabled(false);

                CierreCaja.aceptar.setEnabled(true);
                CierreCaja.aceptar.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(63, 81, 181)));
            }
        });
    }

    public void obtenerCierreCaja(){

        String url_pedido = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerIdCierre.php" + "?id_usuario=" + Login.gIdUsuario;
        RequestQueue requestQueue = Volley.newRequestQueue(cContext);
        System.out.println(url_pedido);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url_pedido,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Caja");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            fondoInit = jsonObject1.getDouble("fondo_inicial");

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }, Throwable::printStackTrace
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }

    @Override
    public int getItemCount() {
        return listaPago.size();
    }

    public static class CierreCajaViewHolder extends RecyclerView.ViewHolder {

        ImageView imagen;
        TextView tvUsers;
        EditText cierre;
        CardView cvCierre;
        Button aceptar;

        public CierreCajaViewHolder(@NonNull View itemView) {
            super(itemView);

        tvUsers = itemView.findViewById(R.id.tvCierre);
        imagen = itemView.findViewById(R.id.imgCierre);
        cierre = itemView.findViewById(R.id.etCierre);
        aceptar = itemView.findViewById(R.id.aceptarTipoPago);
        cvCierre = itemView.findViewById(R.id.cvCierreCaja);

        }
    }
}