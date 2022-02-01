package com.pedidos.kiosco.main;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Bundle;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.adapters.AdaptadorReportesFiscal;
import com.pedidos.kiosco.adapters.AdaptadorReportesMov;
import com.pedidos.kiosco.model.Fiscal;
import com.pedidos.kiosco.model.Movimientos;
import com.pedidos.kiosco.pay.ResumenPago;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ObtenerMovimientos extends AppCompatActivity {

    RecyclerView rvLista;
    ArrayList<Movimientos> reportes;
    AdaptadorReportesMov adaptador;
    public static int idMov;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.obtener_movimientos);

        rvLista = findViewById(R.id.rvListaReportesMov);
        rvLista.setLayoutManager(new LinearLayoutManager(this));

        reportes = new ArrayList<>();

        obtenerMovimientos();
    }

    public void obtenerMovimientos(){

        ProgressDialog progressDialog = new ProgressDialog(ObtenerMovimientos.this, R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url_pedido = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerMovimientos.php" + "?id_usuario=" + Login.gIdUsuario;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        System.out.println(url_pedido);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url_pedido,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Movimientos");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            reportes.add(
                                    new Movimientos(

                            jsonObject1.getString("nombre_cliente"),
                            jsonObject1.getString("fecha_creo"),
                            jsonObject1.getString("nombre_sucursal"),
                            jsonObject1.getDouble("monto_exento"),
                            jsonObject1.getDouble("monto_gravado"),
                            jsonObject1.getDouble("monto_no_sujeto"),
                            jsonObject1.getInt("id_fac_movimiento")

                            ));
                        }

                        adaptador = new AdaptadorReportesMov(this, reportes);
                        rvLista.setAdapter(adaptador);
                        progressDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }, Throwable::printStackTrace
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }

}