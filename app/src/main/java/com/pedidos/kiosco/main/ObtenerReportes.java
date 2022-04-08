package com.pedidos.kiosco.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.Splash;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.adapters.AdaptadorReportes;
import com.pedidos.kiosco.model.Reportes;
import com.pedidos.kiosco.z.Login;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ObtenerReportes extends Fragment {

    RecyclerView rvLista;
    ArrayList<Reportes> reportes;
    AdaptadorReportes adaptador;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_obtener_reportes, container, false);

        TextView orden = vista.findViewById(R.id.orden);
        orden.setText(ObtenerEstados.estadosNombre);

        Toolbar fiscal = vista.findViewById(R.id.toolbarPrefac);
        fiscal.setBackgroundColor((Color.rgb(Splash.gRed, Splash.gGreen, Splash.gBlue)));

        final ImageButton regresa = vista.findViewById(R.id.regresarDatos);
        regresa.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ObtenerEstados.class));
            obtenerPedidosAct();
        });

        rvLista = vista.findViewById(R.id.rvListaReportes);
        rvLista.setLayoutManager(new LinearLayoutManager(getContext()));

        reportes = new ArrayList<>();

        obtenerReportes();

        return vista;
    }

    public void obtenerReportes() {

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String URL_REPORTES = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerRepPrefactura.php" + "?id_estado_prefactura=" + Principal.gIdEstadoCliente + "&id_usuario=" + Login.gIdUsuario;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_REPORTES,
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Reporte");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            reportes.add(
                                    new Reportes(

                                            jsonObject1.getString("nombre_cliente"),
                                            jsonObject1.getString("fecha_creo"),
                                            jsonObject1.getInt("id_prefactura"),
                                            jsonObject1.getInt("id_cliente"),
                                            jsonObject1.getInt("id_estado_prefactura"),
                                            jsonObject1.getString("fecha_finalizo")));
                        }

                        adaptador = new AdaptadorReportes(getContext(), reportes);
                        rvLista.setAdapter(adaptador);


                        progressDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }

                }, volleyError -> {
            Toast.makeText(getContext(), "Ocurrió un error inesperado, Error: " + volleyError, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    public void obtenerPedidosAct() {

        String url = "http://"+ VariablesGlobales.host + "/android/res/cliente/scripts/scripts_php/obtenerPedidosActivos.php" + "?id_estado_prefactura=1" + "&id_usuario=" + Login.gIdUsuario;

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("PedidosAct");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            Login.gIdPedido = jsonObject1.getInt("id_prefactura");
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, volleyError -> {
            String message = null;
            if (volleyError instanceof NetworkError) {
                message = "Cannot connect to Internet...Please check your connection!";
            } else if (volleyError instanceof ServerError) {
                message = "Parece que se ha perdido la conexion a internet";
            } else if (volleyError instanceof AuthFailureError) {
                message = "Cannot connect to Internet...Please check your connection!";
            } else if (volleyError instanceof ParseError) {
                message = "Parsing error! Please try again after some time!!";
            } else if (volleyError instanceof TimeoutError) {
                message = "Connection TimeOut! Please check your internet connection.";
            }
            Toast.makeText(getContext(), "" + message, Toast.LENGTH_LONG).show();
        }
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }

}