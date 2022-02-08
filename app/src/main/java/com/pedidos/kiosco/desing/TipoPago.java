package com.pedidos.kiosco.desing;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.utils.widget.ImageFilterView;
import androidx.fragment.app.DialogFragment;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.fragments.Monto_inicial;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TipoPago extends DialogFragment {

    ImageFilterView btnDinero, btnCreditCard;
    Date d = new Date();
    SimpleDateFormat fecc = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.getDefault());
    String fechacComplString = fecc.format(d);
    SimpleDateFormat ho = new SimpleDateFormat("h:mm a");
    String horaString = ho.format(d);
    Double fondoInit;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View vista = inflater.inflate(R.layout.tipo_pago, container,false);
        vista.setFocusableInTouchMode(false);

        btnDinero = vista.findViewById(R.id.btnDinero);
        btnCreditCard = vista.findViewById(R.id.btnCreditCard);

        obtenerCierreCaja();

        btnDinero.setOnClickListener(view -> {

            String url = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/insertarCierreCaja.php"
                    + "?id_cierre_caja=" +VariablesGlobales.gIdCierreCaja
                    + "&id_usuario=" + Login.gIdUsuario
                    + "&monto_inicial=" + fondoInit
                    + "&monto_venta=0.00"
                    + "&monto_gastos=0.00"
                    + "&monto_fisico=0.00"
                    + "&diferencia=0.00"
                    + "&monto_devolucion=0.00"
                    + "&fecha_movimiento=" + fechacComplString + " a las " + horaString;

            ejecutarServicio(url);
            System.out.println(url);

            getDialog().dismiss();
            startActivity(new Intent(getContext(), EnviandoTicket.class));
        });

        btnCreditCard.setOnClickListener(view -> {

            ejecutarServicio("http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/insertarCierreCaja.php"
                    + "?id_cierre_caja=" +VariablesGlobales.gIdCierreCaja
                    + "&id_usuario=" + Login.gIdUsuario
                    + "&monto_inicial=" + Monto_inicial.montoInit
                    + "&monto_venta=0"
                    + "&monto_gastos=0"
                    + "&monto_fisico=0"
                    + "&diferencia=0"
                    + "&monto_devolucion=0"
                    + "&fecha_movimiento=" + fechacComplString + " a las " + horaString);

            getDialog().dismiss();

        });


        return vista;
    }

    public void obtenerCierreCaja(){

        String url_pedido = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerIdCierre.php" + "?id_usuario=" + Login.gIdUsuario;
        System.out.println(url_pedido);
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url_pedido,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Caja");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            fondoInit = jsonObject1.getDouble("fondo_inicial");

                        }

                        Toast.makeText(getContext(), ""+VariablesGlobales.gIdCierreCaja, Toast.LENGTH_SHORT).show();

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

    public void ejecutarServicio (String URL){

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {

                    progressDialog.dismiss();
                    startActivity(new Intent(getContext(), EnviandoTicket.class));
                },
                volleyError -> {
                    progressDialog.dismiss();
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        requestQueue.add(stringRequest);
    }

}
