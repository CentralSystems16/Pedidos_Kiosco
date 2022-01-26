package com.pedidos.kiosco.productos;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.categorias.CatFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ObtenerProductos extends AppCompatActivity {

    RecyclerView rvLista = null;
    @SuppressLint("StaticFieldLeak")
    public static AdaptadorProductos adaptador = null;
    public static List<Productos> listaProductos;
    RequestQueue requestQueue11;
    public static final String URL_PRODUCTOS = "http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerProductos.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.obtener_productos);

        requestQueue11 = Volley.newRequestQueue(getApplicationContext());

        rvLista = findViewById(R.id.rvListaProductos);
        rvLista.setLayoutManager(new GridLayoutManager(this, 3));

        listaProductos = new ArrayList<>();

        adaptador = new AdaptadorProductos(ObtenerProductos.this, listaProductos);
        rvLista.setAdapter(adaptador);

        obtenerProductos();

        FloatingActionButton fab = findViewById(R.id.fabProd);
        fab.setOnClickListener(view -> {
            Intent i = new Intent(getApplicationContext(), AgregarProducto.class);
            startActivity(i);
        });

    }

    public void obtenerProductos() {

        String url = URL_PRODUCTOS  + "?id_categoria=" + CatFragment.gIdCategoria;

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Productos");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            listaProductos.add(
                                    new Productos(
                                            jsonObject1.getInt("id_producto"),
                                            jsonObject1.getString("nombre_producto"),
                                            jsonObject1.getDouble("precio_producto"),
                                            jsonObject1.getInt("opciones"),
                                            jsonObject1.getString("img_producto")));
                        }

                        adaptador = new AdaptadorProductos(ObtenerProductos.this, listaProductos);
                        rvLista.setAdapter(adaptador);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace
        ) {
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                CatFragment.MY_DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }
}