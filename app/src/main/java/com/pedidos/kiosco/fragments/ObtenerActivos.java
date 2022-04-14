package com.pedidos.kiosco.fragments;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.adapters.AdaptadorReportesMov;
import com.pedidos.kiosco.model.Movimientos;
import com.pedidos.kiosco.z.Login;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ObtenerActivos extends Fragment {

    RecyclerView rvLista;
    ArrayList<Movimientos> reportes;
    AdaptadorReportesMov adaptador;
    public static int idMov;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_obtener_movimientos, container, false);

        ImageButton regresar = vista.findViewById(R.id.regresarDatos2);
        regresar.setOnClickListener(view -> {
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new ObtenerEstados());
            fr.commit();
        });

        rvLista = vista.findViewById(R.id.rvListaReportesMov);
        rvLista.setLayoutManager(new LinearLayoutManager(getContext()));

        Toolbar estado = vista.findViewById(R.id.toolbarMovimientos);
        estado.setBackgroundColor((Color.rgb(gRed, gGreen, gBlue)));

        reportes = new ArrayList<>();

        TextView orden2 = vista.findViewById(R.id.orden2);
        orden2.setText(ObtenerEstados.estadosNombre);

        obtenerComprobantesVenta();

        return vista;
    }

    public void obtenerComprobantesVenta(){

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url_pedido = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerComprobantesVenta.php" + "?base=" + VariablesGlobales.dataBase + "&id_usuario=" + Login.gIdUsuario + "&id_estado_comprobante=1";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
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
                                            jsonObject1.getInt("id_fac_movimiento"),
                                            jsonObject1.getString("numero_comprobante"),
                                            jsonObject1.getString("tipo_pago"),
                                            jsonObject1.getInt("id_cliente"),
                                            jsonObject1.getInt("id_prefactura")

                                    ));
                        }

                        adaptador = new AdaptadorReportesMov(getContext(), reportes);
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