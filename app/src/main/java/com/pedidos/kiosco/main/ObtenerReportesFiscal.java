package com.pedidos.kiosco.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.Splash;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.adapters.AdaptadorReportesFiscal;
import com.pedidos.kiosco.model.Fiscal;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ObtenerReportesFiscal extends AppCompatActivity {

    RecyclerView rvLista;
    ArrayList<Fiscal> reportes;
    AdaptadorReportesFiscal adaptador;
    public static int idAutFiscal, numeroAut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.obtener_reportes_fiscal);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TextView orden = findViewById(R.id.orden);
        orden.setText(ObtenerEstados.estadosNombre);

        Toolbar fiscal = findViewById(R.id.toolbarRepFiscal);
        fiscal.setBackgroundColor((Color.rgb(Splash.gRed, Splash.gGreen, Splash.gBlue)));

        final ImageButton regresa = findViewById(R.id.regresarDatosFiscal);
        regresa.setOnClickListener(v -> {
                startActivity(new Intent(getApplicationContext(), ObtenerEstadoFiscal.class));
        });

        rvLista = findViewById(R.id.rvListaReportes);
        rvLista.setLayoutManager(new LinearLayoutManager(this));

        reportes = new ArrayList<>();

        obtenerReportes();

    }

    public void obtenerReportes() {

        ProgressDialog progressDialog = new ProgressDialog(ObtenerReportesFiscal.this, R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String URL_REPORTES = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerAutorizaciones.php" + "?activo=" + ObtenerEstadoFiscal.fiscalActivo;
        RequestQueue requestQueue = Volley.newRequestQueue(this);
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

                            adaptador = new AdaptadorReportesFiscal(this, reportes);
                            rvLista.setAdapter(adaptador);
                            progressDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }

                }, volleyError -> {
            Toast.makeText(getApplicationContext(), "Ocurrió un error inesperado, Error: " + volleyError, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {

    }

}