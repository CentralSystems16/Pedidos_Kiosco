package com.pedidos.kiosco.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.adapters.AdaptadorCategorias;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import pl.droidsonroids.gif.GifImageView;

public class Categorias extends Fragment {

    RecyclerView rvListaCategorias;
    AdaptadorCategorias adaptadorCat;
    List<com.pedidos.kiosco.model.Categorias> listaCategorias;
    GifImageView conejo, gato;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.fragment_categorias, container, false);

        conejo = vista.findViewById(R.id.conejo);
        conejo.setVisibility(View.INVISIBLE);
        gato = vista.findViewById(R.id.gato);
        gato.setVisibility(View.INVISIBLE);

        Principal.nombreConsumidor.setVisibility(View.GONE);

        rvListaCategorias = vista.findViewById(R.id.rvListaCategorias);
        rvListaCategorias.setLayoutManager(new GridLayoutManager(getContext(), 3));

        listaCategorias = new ArrayList<>();

        adaptadorCat = new AdaptadorCategorias(getContext(), listaCategorias);
        rvListaCategorias.setAdapter(adaptadorCat);

        obtenerCategorias();

        return vista;
    }

    public void obtenerCategorias() {

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String URL_CATEGORIAS = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerCategorias.php"  + "?base=" + VariablesGlobales.dataBase +"&estado_categoria=1";
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CATEGORIAS,

                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Categorias");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            listaCategorias.add(
                                    new com.pedidos.kiosco.model.Categorias(
                                            jsonObject1.getInt("id_categoria"),
                                            jsonObject1.getString("nombre_categoria"),
                                            jsonObject1.getString("img_categoria")
                                    )
                            );
                        }

                        adaptadorCat = new AdaptadorCategorias(getContext(), listaCategorias);
                        rvListaCategorias.setAdapter(adaptadorCat);

                        progressDialog.dismiss();

                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                }, volleyError -> {
            progressDialog.dismiss();
            gato.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Ocurri?? un error inesperado, verifica tu conexion a internet o vuelve a intentarlo mas tarde, Error: " + volleyError, Toast.LENGTH_LONG).show();

        }
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }

}