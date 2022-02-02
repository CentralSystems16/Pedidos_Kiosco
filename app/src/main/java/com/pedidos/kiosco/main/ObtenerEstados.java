package com.pedidos.kiosco.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.Splash;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.adapters.AdaptadorEstados;
import com.pedidos.kiosco.model.Estados;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ObtenerEstados extends AppCompatActivity {

    RecyclerView rvLista;
    ArrayList<Estados> estados;
    AdaptadorEstados adaptador;
    ImageButton regresar;
    public static String estadosNombre;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.obtener_estados);

        regresar = findViewById(R.id.regresara);
        regresar.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Principal.class)));

        rvLista = findViewById(R.id.listaEstados);
        rvLista.setLayoutManager(new GridLayoutManager(this, 3));

        estados = new ArrayList<>();

        Toolbar estado = findViewById(R.id.toolbarEstados);
        estado.setBackgroundColor((Color.rgb(Splash.gRed, Splash.gGreen, Splash.gBlue)));

        obtenerEstados();

    }

    public void obtenerEstados() {

        ProgressDialog progressDialog = new ProgressDialog(ObtenerEstados.this, R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String URL_ESTADOS = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/llenarEstados.php";

        RequestQueue requestQueue = Volley.newRequestQueue(this);

            StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_ESTADOS,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Estados");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            estados.add(
                                    new Estados(

                                            jsonObject1.getString("estado_prefactura"),
                                            jsonObject1.getInt("id_estado_prefactura"),
                                            jsonObject1.getString("img_estado")));
                        }

                        adaptador = new AdaptadorEstados(this, estados);
                        rvLista.setAdapter(adaptador);
                        progressDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                    progressDialog.dismiss();
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
    public void onBackPressed(){

    }

}