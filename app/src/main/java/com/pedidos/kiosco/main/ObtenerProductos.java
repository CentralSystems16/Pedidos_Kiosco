package com.pedidos.kiosco.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.categorias.CatFragment;
import com.pedidos.kiosco.productos.AdaptadorProductos;
import com.pedidos.kiosco.productos.AgregarProducto;
import com.pedidos.kiosco.productos.Productos;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ObtenerProductos extends Fragment {

    RecyclerView rvLista = null;
    @SuppressLint("StaticFieldLeak")
    public static AdaptadorProductos adaptador = null;
    public static List<Productos> listaProductos;
    RequestQueue requestQueue11;
    public static final String URL_PRODUCTOS = "http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerProductos.php";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       View vista = inflater.inflate(R.layout.fragment_obtener_productos, container, false);

        requestQueue11 = Volley.newRequestQueue(getContext());

        rvLista = vista.findViewById(R.id.rvListaProductos);
        rvLista.setLayoutManager(new GridLayoutManager(getContext(), 3));

        listaProductos = new ArrayList<>();

        adaptador = new AdaptadorProductos(getContext(), listaProductos);
        rvLista.setAdapter(adaptador);

        obtenerProductos();

        FloatingActionButton fab = vista.findViewById(R.id.fabProd);
        fab.setOnClickListener(view -> {
            Intent i = new Intent(getContext(), AgregarProducto.class);
            startActivity(i);
        });

       return vista;
    }

    public void obtenerProductos() {

        String url = URL_PRODUCTOS  + "?base=" + VariablesGlobales.dataBase + "&id_categoria=" + CatFragment.gIdCategoria;

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

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
                                            jsonObject1.getString("img_producto"),
                                            jsonObject1.getInt("estado_producto")));
                        }

                        adaptador = new AdaptadorProductos(getContext(), listaProductos);
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