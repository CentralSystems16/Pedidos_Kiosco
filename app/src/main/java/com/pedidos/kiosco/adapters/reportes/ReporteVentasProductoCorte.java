package com.pedidos.kiosco.adapters.reportes;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.model.Corte;
import com.pedidos.kiosco.model.Reporte;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ReporteVentasProductoCorte extends Fragment {

    TextView nombreProducto, cantProducto, cajero, fechaCierre;

    AsyncHttpClient datos;
    ArrayList<Corte> lista = new ArrayList<>();
    Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.reporte_ventas_producto_corte, container, false);

        Toolbar toolbar = vista.findViewById(R.id.toolbarReporteProductoCorte);
        toolbar.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));

        obtenerReporteProductos();

        datos = new AsyncHttpClient();

        MaterialCardView registro = vista.findViewById(R.id.cardViewCorte);
        registro.setStrokeColor(Color.rgb(gRed, gGreen, gBlue));

        spinner = vista.findViewById(R.id.spinnerCorte);

        nombreProducto = vista.findViewById(R.id.nombreProductoCorte);
        cantProducto = vista.findViewById(R.id.cantProductoCorte);

        cajero = vista.findViewById(R.id.cajeroC);
        fechaCierre = vista.findViewById(R.id.fCierreC);

        Button mostrarPorCorte = vista.findViewById(R.id.btnMostrarPorFecha);
        mostrarPorCorte.setOnClickListener(view -> {

            FragmentTransaction fr = getFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new ReporteVentasProducto());
            fr.commit();

        });

        llenarSpinner();

        return vista;
    }

    public void obtenerReporteProductos() {

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String URL_ESTADOS = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerReporteProductosCorte.php"
                + "?base=" + VariablesGlobales.dataBase + "&id_cierre_caja=" + VariablesGlobales.gIdCierreCaja;
        System.out.println(URL_ESTADOS);
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_ESTADOS,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Reporte");
                        StringBuilder sb=new StringBuilder("");
                        StringBuilder sb2=new StringBuilder("");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            sb.append(jsonObject1.getString("nombre_producto")+"\n\n");
                            sb2.append(jsonObject1.getInt("cantidad")+"\n\n");
                            cajero.setText(jsonObject1.getString("nombre_cliente"));
                            fechaCierre.setText(jsonObject1.getString("fecha_fin"));

                        }
                        nombreProducto.setText(sb.toString());
                        cantProducto.setText(sb2.toString());

                        progressDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                    progressDialog.dismiss();
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

    private void llenarSpinner(){

        String url ="http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/llenarCortes.php" + "?base=" + VariablesGlobales.dataBase;

        datos.post(url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (statusCode == 200){
                    cargarSpinner(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Ocurrió un error inesperado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarSpinner(String respuesta){

        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){

                Corte e = new Corte();
                e.setIdCierreCaja(jsonArreglo.getJSONObject(i).getInt("id_cierre_caja"));
                lista.add(e);
            }

            ArrayAdapter<Corte> a = new ArrayAdapter<>(getContext(), R.layout.spinner_item, lista);
            spinner.setAdapter(a);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}