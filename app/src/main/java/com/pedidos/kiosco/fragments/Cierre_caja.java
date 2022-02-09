package com.pedidos.kiosco.fragments;

import android.app.ProgressDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Cierre_caja extends Fragment {

    public static String cierreCaja1;
    Date d = new Date();
    SimpleDateFormat fecc = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.getDefault());
    String fechacComplString = fecc.format(d);
    SimpleDateFormat ho = new SimpleDateFormat("h:mm a");
    String horaString = ho.format(d);
    Double fondoInit;

    TextView cierre, monto;
    ImageView caja;
    EditText montoFinal;
    Button aceptarCredit, regresar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.cierre_caja_fragment, container, false);

        EditText cierreCaja = vista.findViewById(R.id.montoFinal);
        obtenerCierreCaja();

        cierreCaja1 = cierreCaja.getText().toString();
        Button aceptar = vista.findViewById(R.id.btnAceptarRegresar);

        cierre = vista.findViewById(R.id.txtCierre);
        monto = vista.findViewById(R.id.txtMonto);
        caja = vista.findViewById(R.id.imgCierre);
        montoFinal = vista.findViewById(R.id.montoTargeta);
        aceptarCredit = vista.findViewById(R.id.btnAceptarCredit);
        regresar = vista.findViewById(R.id.btnRegresarCredit);


        aceptar.setOnClickListener(view -> {

            String url = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/insertarCierreCaja.php"
                    + "?id_cierre_caja=" +VariablesGlobales.gIdCierreCaja
                    + "&id_usuario=" + Login.gIdUsuario
                    + "&monto_inicial=" + fondoInit
                    + "&monto_venta=0.00"
                    + "&monto_gastos=0.00"
                    + "&monto_fisico=" + cierreCaja.getText().toString()
                    + "&diferencia=0.00"
                    + "&monto_devolucion=0.00"
                    + "&fecha_movimiento=" + fechacComplString + " a las " + horaString;

            ejecutarServicio2("http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/actualizarCierreCaja.php"
                    + "?fecha_fin=" + fechacComplString + " a las " + horaString
                    + "&state=2"
                    + "&id_cierre_caja=" + VariablesGlobales.gIdCierreCaja);

            ejecutarServicio2("http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/actualizarCierreMovCaja.php"
                    + "?monto_fisico=" + cierreCaja1
                    + "&id_cierre_caja=" + VariablesGlobales.gIdCierreCaja);

            ejecutarServicio(url);

        });

        return vista;
    }
    public void obtenerCierreCaja(){

        String url_pedido = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerIdCierre.php" + "?id_usuario=" + Login.gIdUsuario;
        System.out.println(url_pedido);
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

                        Toast.makeText(getContext(), ""+VariablesGlobales.gIdCierreCaja, Toast.LENGTH_SHORT).show();

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


                    /*progressDialog.dismiss();
                    FragmentTransaction fr = getFragmentManager().beginTransaction();
                    fr.replace(R.id.fragment_layout, new Home());
                    fr.commit();*/


                },
                volleyError -> {
                    progressDialog.dismiss();
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        requestQueue.add(stringRequest);
    }

    public void ejecutarServicio2 (String URL){

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {

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