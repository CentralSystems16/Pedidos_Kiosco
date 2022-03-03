package com.pedidos.kiosco.fragments;

import android.annotation.SuppressLint;
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
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.categorias.CatFragment;
import com.pedidos.kiosco.productos.AdaptadorProductosInactivos;
import com.pedidos.kiosco.productos.Productos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProductosInactivos extends Fragment {

    RecyclerView rvLista = null;
    @SuppressLint("StaticFieldLeak")
    public static AdaptadorProductosInactivos adaptador = null;
    public static List<Productos> listaProductos;
    RequestQueue requestQueue11;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_productos_inactivos, container, false);

        requestQueue11 = Volley.newRequestQueue(getContext());

        rvLista = vista.findViewById(R.id.rvListaProductosInactivos);
        rvLista.setLayoutManager(new GridLayoutManager(getContext(), 3));

        listaProductos = new ArrayList<>();

        adaptador = new AdaptadorProductosInactivos(getContext(), listaProductos);
        rvLista.setAdapter(adaptador);

        obtenerProductos();

        return vista;
    }

    public void obtenerProductos() {
        String URL_PRODUCTOS = "http://"+ VariablesGlobales.host
                + "/android/kiosco/cliente/scripts/scripts_php/obtenerProductosInactivos.php"
                + "?estado_producto=0";
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        System.out.println(URL_PRODUCTOS);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_PRODUCTOS,

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

                        adaptador = new AdaptadorProductosInactivos(getContext(), listaProductos);
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