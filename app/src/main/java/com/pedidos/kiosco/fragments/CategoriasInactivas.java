package com.pedidos.kiosco.fragments;

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
import com.pedidos.kiosco.categorias.AdaptadorCategoriasInvalidas;
import com.pedidos.kiosco.categorias.CatFragment;
import com.pedidos.kiosco.categorias.Categorias;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class CategoriasInactivas extends Fragment {

    RecyclerView rvLista;
    ArrayList<Categorias> categorias;
    int MY_DEFAULT_TIMEOUT = 15000;
    String URL_CATEGORIAS = "http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerCategorias.php" + "?base=" + VariablesGlobales.dataBase + "&id_categoria=" + CatFragment.gIdCategoria + "&estado_producto=0";
    AdaptadorCategoriasInvalidas adaptadorCat;
    int gIdCategoria, estadoCategoria;
    String gNombreCat;
    String gImagen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista =  inflater.inflate(R.layout.fragment_categorias_inactivas, container, false);

        obtenerCategorias();

        categorias = new ArrayList<>();

        rvLista = vista.findViewById(R.id.rvListaCategoriasInactivas);
        rvLista.setLayoutManager(new GridLayoutManager(getContext(), 2));

        return vista;
    }

    public void obtenerCategorias() {

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

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
                                            gImagen = jsonObject1.getString("img_categoria"),
                                            estadoCategoria = jsonObject1.getInt("estado_categoria")));
                        }

                        adaptadorCat = new AdaptadorCategoriasInvalidas(getContext(), categorias);
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