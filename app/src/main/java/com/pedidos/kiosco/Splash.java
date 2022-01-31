package com.pedidos.kiosco;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Splash extends AppCompatActivity {

    public static String gNombre, gTelefono, gDireccion, gFacebook;
    public static int gNrc, gNit, gCantImagenes, gImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        obtenerEmpresa();

        new Handler().postDelayed(() -> startActivity(new Intent(getApplicationContext(), Login.class)),5000);

    }

    public void obtenerEmpresa(){

        String URL_REPORTES = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerInfoEmpresa.php";

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_REPORTES,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Empresa");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            gNombre = jsonObject1.getString("nombre_empresa");
                            gTelefono = jsonObject1.getString("telefono_empresa");
                            gDireccion = jsonObject1.getString("direccion_empresa");
                            gFacebook = jsonObject1.getString("facebook_empresa");
                            gCantImagenes = jsonObject1.getInt("cnt_imagenes");
                            gNit = jsonObject1.getInt("NIT");
                            gNrc = jsonObject1.getInt("NRC");
                            gImagen = jsonObject1.getInt("imagen");

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, volleyError -> Toast.makeText(getApplicationContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show()
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }

}