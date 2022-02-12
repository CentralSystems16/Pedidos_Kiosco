package com.pedidos.kiosco.fragments;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.adapters.AdaptadorCierreCaja;
import com.pedidos.kiosco.model.Pago;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CierreCaja extends Fragment {

    public static Double etCaja;
    public static Double fondoInit;
    public static int lIdTipoPago;
    public static String lTipoPago;
    RecyclerView rvLista;
    ArrayList<Pago> pago;
    AdaptadorCierreCaja adaptador;
    View vista;
    Date d = new Date();
    SimpleDateFormat fecc = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.getDefault());
    String fechacComplString = fecc.format(d);
    SimpleDateFormat ho = new SimpleDateFormat("h:mm a");
    String horaString = ho.format(d);
    public static Button aceptar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.cierre_caja_fragment, container, false);

        obtenerCierreCaja();

        rvLista = vista.findViewById(R.id.rvListaPagoCierre);
        rvLista.setLayoutManager(new LinearLayoutManager(getActivity()));

        pago = new ArrayList<>();

        aceptar = vista.findViewById(R.id.btnAceptarTipoPago);
        aceptar.setEnabled(false);
        aceptar.setOnClickListener(view -> {

            new AlertDialog.Builder(getContext())
                    .setTitle("Confirmación de cierre")
                    .setMessage("Esta seguro de cerrar la caja")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {

                        ejecutarServicio("http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/actualizarCierreCaja.php"
                                + "?fecha_fin=" + fechacComplString + " a las " + horaString
                                + "&state=2"
                                + "&id_cierre_caja=" + VariablesGlobales.gIdCierreCaja);

                        ejecutarServicio("http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/insertarCierreMovCaja.php"
                                + "?id_cierre_caja=" +VariablesGlobales.gIdCierreCaja
                                + "&id_usuario=" + Login.gIdUsuario
                                + "&monto_inicial=0.00"
                                + "&monto_venta=0.00"
                                + "&monto_gastos=0.00"
                                + "&monto_fisico=0.00"
                                + "&diferencia=0.00"
                                + "&monto_devolucion=0.00"
                                + "&fecha_movimiento=" + fechacComplString + " a las " + horaString);

                    }).setNegativeButton(android.R.string.no, (dialog, which) ->{})
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();

        });

        Button cerrar = vista.findViewById(R.id.btnCancelarTipoPago);
        cerrar.setOnClickListener(view -> {

            ejecutarServicio("http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/eliminarFacTipoPago.php" + "?id_cierre_caja=" + VariablesGlobales.gIdCierreCaja);

        });

        obtenerTipoPagoLiquidar();

        return vista;
    }

    public void obtenerTipoPagoLiquidar() {

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerTipoPagoLiquidar.php";

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

                        adaptador = new AdaptadorCierreCaja(getContext(), pago);
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

    public void obtenerCierreCaja(){

        String url_pedido = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerIdCierre.php" + "?id_usuario=" + Login.gIdUsuario;
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url_pedido,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Caja");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            fondoInit = jsonObject1.getDouble("fondo_inicial");

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();

                    }
                }, Throwable::printStackTrace
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }

    public void ejecutarServicio (String URL){

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    FragmentTransaction fr = getActivity().getSupportFragmentManager().beginTransaction();
                    fr.replace(R.id.fragment_layout, new Home());
                    fr.commit();
                    progressDialog.dismiss();
                },
                volleyError -> {
                    progressDialog.dismiss();
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        requestQueue.add(stringRequest);
    }


}