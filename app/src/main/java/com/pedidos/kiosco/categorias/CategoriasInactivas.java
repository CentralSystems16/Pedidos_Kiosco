package com.pedidos.kiosco.categorias;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class CategoriasInactivas extends AppCompatActivity {

    RecyclerView rvLista;
    ArrayList<Categorias> categorias;
    int MY_DEFAULT_TIMEOUT = 15000;
    String URL_CATEGORIAS = "http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerCategorias.php"+ "?id_categoria=" + CatFragment.gIdCategoria + "?estado_producto=0";
    AdaptadorCategoriasInvalidas adaptadorCat;
    int gIdCategoria;
    String gNombreCat;
    String gImagen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.categorias_inactivas);

        obtenerCategorias();

        categorias = new ArrayList<>();

        rvLista = findViewById(R.id.rvListaCategoriasInactivas);
        rvLista.setLayoutManager(new GridLayoutManager(this, 2));

    }

    public void obtenerCategorias() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CATEGORIAS,

                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Categorias");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            categorias.add(
                                    new Categorias(
                                            gIdCategoria = jsonObject1.getInt("id_categoria"),
                                            gNombreCat = jsonObject1.getString("nombre_categoria"),
                                            gImagen = jsonObject1.getString("img_categoria")));
                        }

                        adaptadorCat = new AdaptadorCategoriasInvalidas(this, categorias);
                        rvLista.setAdapter(adaptadorCat);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }
}