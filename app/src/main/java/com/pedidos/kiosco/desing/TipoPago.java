package com.pedidos.kiosco.desing;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.adapters.AdaptadorTipoPago;
import com.pedidos.kiosco.model.Pago;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class TipoPago extends Fragment {

    public static int idTipoPago;
    public static String tipoPago;

    RecyclerView rvLista;
    ArrayList<Pago> pago;
    AdaptadorTipoPago adaptador;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View vista = inflater.inflate(R.layout.tipo_pago, container,false);
        vista.setFocusableInTouchMode(false);

        rvLista = vista.findViewById(R.id.rvListaPago);
        rvLista.setLayoutManager(new GridLayoutManager(getContext(), 3));

        pago = new ArrayList<>();

        obtenerTipoPago();

        return vista;
    }

    public void obtenerTipoPago() {

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerTipoPago.php";

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("TipoPago");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            pago.add(
                                    new Pago(

                                            jsonObject1.getInt("id_tipo_pago"),
                                            jsonObject1.getString("tipo_pago"),
                                            jsonObject1.getInt("activo"),
                                            jsonObject1.getString("imagen")));
                        }

                        adaptador = new AdaptadorTipoPago(getContext(), pago);
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

}
