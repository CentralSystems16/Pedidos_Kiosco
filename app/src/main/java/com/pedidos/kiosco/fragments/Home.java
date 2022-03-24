package com.pedidos.kiosco.fragments;

import static com.pedidos.kiosco.Splash.gBlue3;
import static com.pedidos.kiosco.Splash.gGreen3;
import static com.pedidos.kiosco.Splash.gRecBlue2;
import static com.pedidos.kiosco.Splash.gRecGreen2;
import static com.pedidos.kiosco.Splash.gRecRed2;
import static com.pedidos.kiosco.Splash.gRed3;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.pedidos.kiosco.main.ObtenerEstados;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLOutput;

public class Home extends Fragment {

    private SliderLayout sliderLayout;
    CardView hacerPedido, verPedido, abrirCaja;
    int resultado, state;
    TextView cierreCaja;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        verifyStoragePermissions(getActivity());

        cierreCaja = view.findViewById(R.id.txtCierreCaja);
        obtenerCierreCaja();
        obtenerPedidosAct();
        obtenerPedidosAct2();

        hacerPedido = view.findViewById(R.id.btnPedidos);
        verPedido = view.findViewById(R.id.btnVerPedidos);
        verPedido.setOnClickListener(view12 -> startActivity(new Intent(getContext(), ObtenerEstados.class)));

        abrirCaja = view.findViewById(R.id.btnCrearCaja);

        TextView nombre = view.findViewById(R.id.nombrePrincipal);
        nombre.setText(Login.nombre);

        hacerPedido.setOnClickListener(view1 -> {

                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_layout, new Categorias());
                fr.commit();
                Login.gIdPedido = 0;

        });

        hacerPedido.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRecRed2, gRecGreen2, gRecBlue2)));
        verPedido.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed3, gGreen3, gBlue3)));

        sliderLayout = view.findViewById(R.id.slider);
        sliderLayout.setIndicatorAnimation(IndicatorAnimations.FILL);
        sliderLayout.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderLayout.setScrollTimeInSec(3);

        setSliderViews();

        return view;

    }

    public void obtenerCierreCaja(){

        SharedPreferences preferences2 = requireActivity().getSharedPreferences("preferenciasSucursal", Context.MODE_PRIVATE);
        resultado = preferences2.getInt("sucursal", 0);

        String url_pedido = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerCierreCaja.php"  + "?base=" + VariablesGlobales.dataBase + "&id_usuario=" + Login.gIdUsuario + "&id_caja=" + resultado;
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url_pedido,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Caja");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            VariablesGlobales.gIdCierreCaja = jsonObject1.getInt("id_cierre_caja");
                            jsonObject1.getInt("id_caja");
                            jsonObject1.getString("fecha_ini");
                            jsonObject1.getString("fecha_fin");
                            jsonObject1.getDouble("fondo_inicial");
                            state = jsonObject1.getInt("state");

                        }

                        if (state == 1){
                            cierreCaja.setText("Cerrar caja");
                        }

                        abrirCaja.setOnClickListener(view12 -> {

                            if (state == 0) {

                                FragmentTransaction fr = getFragmentManager().beginTransaction();
                                fr.replace(R.id.fragment_layout, new MontoInicial());
                                fr.commit();

                            } else {

                                FragmentTransaction fr = getFragmentManager().beginTransaction();
                                fr.replace(R.id.fragment_layout, new CierreCaja());
                                fr.commit();

                            }
                        });

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

    private void setSliderViews() {
        for (int i = 0; i < 3; i++){
            DefaultSliderView sliderView = new DefaultSliderView(getContext());

            switch (i){
                case 0:
                    sliderView.setImageUrl("https://www.comedera.com/wp-content/uploads/2017/06/recetas-de-comida-mexicana.jpg");
                    break;
                case 1:
                    sliderView.setImageUrl("https://tipsparatuviaje.com/wp-content/uploads/2018/09/tacos-comida-mexicana.jpg");
                    break;
                case 2:
                    sliderView.setImageUrl("https://www.mylatinatable.com/wp-content/uploads/2019/03/Entomatadas-5.jpg");
                    break;
            }

            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            sliderLayout.addSliderView(sliderView);

        }
    }

    public void obtenerPedidosAct() {

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerPedidosActivos.php"  + "?base=" + VariablesGlobales.dataBase + "&id_estado_prefactura=1" + "&id_usuario=" + "&id_cliente=" + Login.gIdCliente;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("PedidosAct");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            Login.gIdPedido = jsonObject1.getInt("id_prefactura");

                            if (Login.gIdPedido == 0){
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(requireContext(), "¡Tienes un pedido abierto!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }

                        }
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

    public void obtenerPedidosAct2() {

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerPedidosActivos2.php"  + "?base=" + VariablesGlobales.dataBase + "&id_estado_comprobante=1" + "&id_usuario=" + Login.gIdUsuario + "&id_cliente=" + Login.gIdCliente;

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("PedidosAct");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            Login.gIdMovimiento = jsonObject1.getInt("id_fac_movimiento");

                        }
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

    // Storage Permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static final String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {

        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

}