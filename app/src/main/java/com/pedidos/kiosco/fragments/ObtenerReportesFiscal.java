package com.pedidos.kiosco.fragments;

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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.Splash;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.adapters.AdaptadorReportesFiscal;
import com.pedidos.kiosco.model.Fiscal;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ObtenerReportesFiscal extends Fragment {

    RecyclerView rvLista;
    ArrayList<Fiscal> reportes;
    AdaptadorReportesFiscal adaptador;
    public static int idAutFiscal, numeroAut;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_obtener_reportes_fiscal, container, false);

        TextView orden = vista.findViewById(R.id.orden);
        orden.setText(ObtenerEstados.estadosNombre);

        Toolbar fiscal = vista.findViewById(R.id.toolbarRepFiscal);
        fiscal.setBackgroundColor((Color.rgb(Splash.gRed, Splash.gGreen, Splash.gBlue)));

        final ImageButton regresa = vista.findViewById(R.id.regresarDatosFiscal);
        regresa.setOnClickListener(v -> {
            startActivity(new Intent(getContext(), ObtenerEstadoFiscal.class));
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

        String URL_REPORTES = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerAutorizaciones.php" + "?base=" + VariablesGlobales.dataBase + "&activo=" + ObtenerEstadoFiscal.fiscalActivo;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_REPORTES,
                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Reporte");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            reportes.add(
                                    new Fiscal(

                                            jsonObject1.getString("tipo_comprobante"),
                                            jsonObject1.getString("nombre_usuario"),
                                            jsonObject1.getString("nombre_caja"),
                                            jsonObject1.getString("nombre_sucursal"),
                                            jsonObject1.getInt("id_aut_fiscal"),
                                            jsonObject1.getString("serie"),
                                            jsonObject1.getString("fecha_autorizacion")));
                        }

                        adaptador = new AdaptadorReportesFiscal(getContext(), reportes);
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

}