package com.pedidos.kiosco;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pedidos.kiosco.model.Caja;
import com.pedidos.kiosco.model.Sucursales;
import com.pedidos.kiosco.pdf.Api;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Map;

import pl.droidsonroids.gif.GifImageView;

public class Splash extends AppCompatActivity {

    public static String gNombre, gTelefono, gDireccion, gFacebook, gImagenSplah, gAnimacion, gGif, gFoto, gGif2, gFoto2, gGif3, gFoto3;
    public static int gNrc, gNit, gCantImagenes, gImagen, gRed, gGreen, gBlue, gRecRed, gRecGreen, gRecBlue, gRecRed2, gRecGreen2, gRecBlue2, gRed3, gGreen3, gBlue3;

    TextView tvEmpresa;
    ImageView imgEmpresa;
    GifImageView animEmpresa;
    AsyncHttpClient datos;
    AsyncHttpClient datos2;
    Spinner spDatos;
    ArrayList<Sucursales> lista = new ArrayList<>();
    Spinner spDatos2;
    ArrayList<Caja> lista2 = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        datos = new AsyncHttpClient();
        datos2 = new AsyncHttpClient();
        showAlertWithTextInputLayout(getApplicationContext());

    }

    private void llenarSpinner(){

        String url ="http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/llenarSucursales.php";

        datos.post(url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (statusCode == 200){
                    cargarSpinner(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "Ocurrió un error inesperado", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void cargarSpinner(String respuesta){

        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){

                Sucursales e = new Sucursales();
                e.setNomSucursal(jsonArreglo.getJSONObject(i).getString("nombre_sucursal"));
                lista.add(e);
            }

            ArrayAdapter<Sucursales> a = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, lista);
            spDatos.setAdapter(a);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void llenarSpinner2(){

        String url ="http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/llenarCajas.php";

        datos2.post(url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (statusCode == 200){
                    cargarSpinner2(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "Ocurrió un error inesperado", Toast.LENGTH_SHORT).show();
            }

        });
    }

    private void cargarSpinner2(String respuesta){

        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){

                Caja e = new Caja();
                e.setNombreCaja(jsonArreglo.getJSONObject(i).getString("nombre_caja"));
                lista2.add(e);
            }

            ArrayAdapter<Caja> a = new ArrayAdapter<>(getApplicationContext(), R.layout.spinner_item, lista2);
            spDatos2.setAdapter(a);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    private void showAlertWithTextInputLayout(Context context) {

         spDatos = new Spinner(context);
         llenarSpinner();

        AlertDialog dialog = new AlertDialog.Builder(Splash.this)
                .setTitle("Sucursal")
                .setMessage("Por favor, seleccione la sucursal a la que pertenece")
                .setView(spDatos)
                .setPositiveButton("Realizado", (dialogInterface, i) -> {

                    showAlertWithTextInputLayout2(context);

                })
                .create();

        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        dialog.show();

    }

    private void showAlertWithTextInputLayout2(Context context) {

        llenarSpinner2();
        spDatos2 = new Spinner(context);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("Cajas")
                .setMessage("Por favor, seleccione el número de caja")
                .setView(spDatos2)
                .setPositiveButton("Realizado", (dialogInterface, i) -> {
                    ejecutarServicio("http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/insertarCaja.php");
                })
                .create();

        dialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);
        dialog.show();

    }

    public void ejecutarServicio (String URL) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {
            Toast.makeText(getApplicationContext(), "¡Datos registrados correctamente!", Toast.LENGTH_SHORT).show();
            Animation animacion1 = AnimationUtils.loadAnimation(this,R.anim.desplazamiento_arriba);
            Animation animacion2 = AnimationUtils.loadAnimation(this,R.anim.desplazamiento_abajo2);

            tvEmpresa = findViewById(R.id.tvEmpresa);
            imgEmpresa = findViewById(R.id.imgEmpresa);
            animEmpresa = findViewById(R.id.animacionEmpresa);
            tvEmpresa.setAnimation(animacion2);
            imgEmpresa.setAnimation(animacion1);

            obtenerEmpresa();
            obtenerRecursos();
            obtenerRecursos2();
            obtenerRecursos3();

            new Handler().postDelayed(() -> startActivity(new Intent(getApplicationContext(), Login.class)),10000);

        }, error -> Toast.makeText(getApplicationContext(), "Ocurrió un error: " + error.getMessage(), Toast.LENGTH_SHORT).show()) {
            @Override
            protected Map<String, String> getParams() {

                Map<String, String> params = new Hashtable<>();
                params.put("nombre_sucursal", String.valueOf(spDatos.getSelectedItemPosition()));
                params.put("nombre_caja", String.valueOf(spDatos2.getSelectedItemPosition()));

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

    }

    public void obtenerRecursos(){

        String url = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerRecursos.php" + "?id_recurso=1";
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url
                ,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Empresa");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            gGif = jsonObject1.getString("gif_recurso");
                            gFoto = jsonObject1.getString("img_recurso");
                            gRecRed = jsonObject1.getInt("red_recurso");
                            gRecBlue = jsonObject1.getInt("green_recurso");
                            gRecGreen = jsonObject1.getInt("blue_recurso");

                        }
                    } catch (JSONException ignored) {}
                }, volleyError ->{}
        );
        requestQueue.add(stringRequest);
    }

    public void obtenerRecursos2(){

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerRecursos.php" + "?id_recurso=2",

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Empresa");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            gGif2 = jsonObject1.getString("gif_recurso");
                            gFoto2 = jsonObject1.getString("img_recurso");
                            gRecRed2 = jsonObject1.getInt("red_recurso");
                            gRecGreen2 = jsonObject1.getInt("green_recurso");
                            gRecBlue2 = jsonObject1.getInt("blue_recurso");

                        }
                    } catch (JSONException ignored) {}
                }, volleyError ->{}
        );
        requestQueue.add(stringRequest);
    }

    public void obtenerRecursos3(){

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,
                "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerRecursos.php" + "?id_recurso=3",

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Empresa");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            gGif3 = jsonObject1.getString("gif_recurso");
                            gFoto3 = jsonObject1.getString("img_recurso");
                            gRed3 = jsonObject1.getInt("red_recurso");
                            gGreen3 = jsonObject1.getInt("green_recurso");
                            gBlue3  = jsonObject1.getInt("blue_recurso");

                        }
                    } catch (JSONException ignored) {}
                }, volleyError ->{}
        );
        requestQueue.add(stringRequest);
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
                            tvEmpresa.setText(gNombre);
                            gTelefono = jsonObject1.getString("telefono_empresa");
                            gDireccion = jsonObject1.getString("direccion_empresa");
                            gFacebook = jsonObject1.getString("facebook_empresa");
                            gCantImagenes = jsonObject1.getInt("cnt_imagenes");
                            gNit = jsonObject1.getInt("NIT");
                            gNrc = jsonObject1.getInt("NRC");
                            gImagen = jsonObject1.getInt("imagen");
                            gImagenSplah = jsonObject1.getString("img_empresa");
                            Glide.with(Splash.this).load(gImagenSplah).into(imgEmpresa);
                            gAnimacion = jsonObject1.getString("animacion_empresa");
                            Glide.with(Splash.this).load(gAnimacion).into(animEmpresa);
                            gRed = jsonObject1.getInt("red_empresa");
                            gGreen = jsonObject1.getInt("green_empresa");
                            gBlue = jsonObject1.getInt("blue_empresa");

                            tvEmpresa.setTextColor(Color.rgb(gRed, gGreen, gBlue));

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