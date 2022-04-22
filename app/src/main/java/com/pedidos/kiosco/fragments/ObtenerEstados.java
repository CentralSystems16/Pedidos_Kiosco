package com.pedidos.kiosco.fragments;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;
import android.widget.Toolbar;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.Splash;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.adapters.AdaptadorEstados;
import com.pedidos.kiosco.model.Estados;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ObtenerEstados extends Fragment {

    RecyclerView rvLista;
    ArrayList<Estados> estados;
    AdaptadorEstados adaptador;
    ImageButton regresar;
    public static String estadosNombre;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_obtener_estados, container, false);

        regresar = vista.findViewById(R.id.regresara);
        regresar.setOnClickListener(view -> {

            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new Home());
            fr.commit();

        });

        rvLista = vista.findViewById(R.id.listaEstados);
        rvLista.setLayoutManager(new GridLayoutManager(getContext(), 3));

        estados = new ArrayList<>();

        Toolbar estado = vista.findViewById(R.id.toolbarEstados);
        estado.setBackgroundColor((Color.rgb(Splash.gRed, Splash.gGreen, Splash.gBlue)));

        obtenerEstados();

        return vista;
    }

    public void obtenerEstados() {

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String URL_ESTADOS = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/llenarEstados.php" + "?base=" + VariablesGlobales.dataBase;

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

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

                        adaptador = new AdaptadorEstados(getContext(), estados);
                        rvLista.setAdapter(adaptador);
                        progressDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                    progressDialog.dismiss();
                }, volleyError -> {
            Toast.makeText(getContext(), "Ocurrió un error inesperado, Error: " + volleyError, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }
    
}