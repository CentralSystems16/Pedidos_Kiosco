package com.pedidos.kiosco.gastos;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.adapters.AdaptadorGastos;
import com.pedidos.kiosco.model.Gastos;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ListarGastos extends Fragment {

    RecyclerView rvLista;
    ArrayList<Gastos> reportes;
    AdaptadorGastos adaptador;

    public static double monto;
    public static String fecha, descripcion;
    public static int gIdGastos, idFacMovimientos, estado = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_listar_gastos, container, false);

        rvLista = vista.findViewById(R.id.listaGastos);
        rvLista.setLayoutManager(new LinearLayoutManager(getContext()));

        reportes = new ArrayList<>();

        obtenerGastos();

        Button crearGastos = vista.findViewById(R.id.crearGasto);
        crearGastos.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));
        crearGastos.setOnClickListener(view -> {
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new CrearGastos());
            fr.commit();
        });
        Button inactivos = vista.findViewById(R.id.gastoInactivos);
        if (estado == 1){
            inactivos.setText("Activos");
        }
        else {
            inactivos.setText("Inactivos");
        }

        inactivos.setOnClickListener(view -> {
            if (estado == 1) {
                estado = 0;
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_layout, new ListarGastos());
                fr.commit();
            }
            else {
                estado = 1;
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_layout, new ListarGastos());
                fr.commit();
            }

        });

        return vista;
    }

    public void obtenerGastos(){

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        new ArrayList<>();

        String url_pedido = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerGastos.php" + "?base=" + VariablesGlobales.dataBase + "&id_usuario=" + Login.gIdUsuario + "&fac_tipo_movimiento=2" + "&id_estado_comprobante=" + estado;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url_pedido,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Movimientos");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            reportes.add(
                                    new Gastos(

                                            jsonObject1.getString("fecha_creo"),
                                            jsonObject1.getDouble("monto"),
                                            jsonObject1.getInt("id_tipo_comprobante"),
                                            jsonObject1.getString("descripcion"),
                                            jsonObject1.getInt("id_fac_movimiento"),
                                            jsonObject1.getInt("id_estado_comprobante")

                                    ));
                        }

                        adaptador = new AdaptadorGastos(getContext(), reportes);
                        rvLista.setAdapter(adaptador);
                        progressDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }, Throwable::printStackTrace
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }

}