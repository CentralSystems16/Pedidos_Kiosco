package com.pedidos.kiosco;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import pl.droidsonroids.gif.GifImageView;

public class Splash extends AppCompatActivity {

    public static String gNombre, gTelefono, gDireccion, gFacebook, gImagenSplah, gAnimacion;
    public static int gNrc, gNit, gCantImagenes, gImagen, gRed, gGreen, gBlue;

    TextView tvEmpresa;
    ImageView imgEmpresa;
    GifImageView animEmpresa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        Animation animacion1 = AnimationUtils.loadAnimation(this,R.anim.desplazamiento_arriba);
        Animation animacion2 = AnimationUtils.loadAnimation(this,R.anim.desplazamiento_abajo2);

        tvEmpresa = findViewById(R.id.tvEmpresa);
        imgEmpresa = findViewById(R.id.imgEmpresa);
        animEmpresa = findViewById(R.id.animacionEmpresa);
        tvEmpresa.setAnimation(animacion2);
        imgEmpresa.setAnimation(animacion1);

        obtenerEmpresa();

        new Handler().postDelayed(() -> startActivity(new Intent(getApplicationContext(), Login.class)),10000);

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