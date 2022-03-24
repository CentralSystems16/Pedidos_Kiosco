package com.pedidos.kiosco.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.adapters.AdaptadorClientes;
import com.pedidos.kiosco.adapters.AdaptadorTipoPago;
import com.pedidos.kiosco.desing.Clientes;
import com.pedidos.kiosco.model.Clients;
import com.pedidos.kiosco.model.Pago;
import com.pedidos.kiosco.model.Productos;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.Normalizer;
import java.util.ArrayList;

public class Cliente extends Fragment {

    RecyclerView rvLista;
    ArrayList<Clients> clientes;
    AdaptadorClientes adaptador;

    public static String numero, direccion, dui, nombre;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_cliente, container, false);

        TextView buscador = vista.findViewById(R.id.etBuscadorClientes);
        buscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                filtrar(editable.toString());
            }
        });

        Button cliente = vista.findViewById(R.id.cliente);
        cliente.setOnClickListener(view13 -> {
            FragmentTransaction fr = getFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new RegistroCliente());
            fr.commit();
        });

        rvLista = vista.findViewById(R.id.rvClientes);
        rvLista.setLayoutManager(new LinearLayoutManager(getContext()));

        clientes = new ArrayList<>();

        obtenerClientes();

        return vista;
    }

    public void obtenerClientes() {

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerClientes.php" + "?base=" + VariablesGlobales.dataBase;

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Clientes");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            clientes.add(
                                    new Clients(

                                            jsonObject1.getString("nombre_cliente"),
                                            jsonObject1.getInt("id_cliente"),
                                            jsonObject1.getString("direccion_cliente"),
                                            jsonObject1.getString("dui_cliente"),
                                            jsonObject1.getString("telefono_cliente")));
                        }

                        adaptador = new AdaptadorClientes(getContext(), clientes);
                        rvLista.setAdapter(adaptador);

                        progressDialog.dismiss();

                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }

                }, volleyError -> {
            Toast.makeText(getContext(), "Ocurrio un error inesperado, Error: " + volleyError, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }

    public void filtrar (String texto) {
        ArrayList<Clients> filtrarLista = new ArrayList<>();

        for (Clients clientes : clientes) {
            String cadenaNormalize = Normalizer.normalize(clientes.getNombreCliente(), Normalizer.Form.NFD);
            String cadenaSinAcentos = cadenaNormalize.replaceAll("[^\\p{ASCII}]", "");
            if (cadenaSinAcentos.toLowerCase().contains(texto.toLowerCase()) ) {
                filtrarLista.add(clientes);
            }

        }

        adaptador.filtrarClientes(filtrarLista);
    }


}