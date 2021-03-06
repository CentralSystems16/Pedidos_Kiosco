package com.pedidos.kiosco.fragments;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.model.Caja;
import com.pedidos.kiosco.model.Comprobantes;
import com.pedidos.kiosco.model.Sucursales;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ModificarAutorizacion extends Fragment {

    String desde, hasta, serie, autorizacion, resolucion, filas, fecha, activoEdit;
    EditText desdeet, hastaet, serieet, autorizacionet, resolucionet, filaset, fechaet;
    RadioButton activo, inactivo;
    Button btnActivo, btnInactivo;

    int gEstadoProd = 1;

    private AsyncHttpClient cliente;
    private AsyncHttpClient cliente2;
    private AsyncHttpClient cliente3;

    private Spinner spComprobante;
    ArrayList<Comprobantes> comprobantes;

    private Spinner spSucursal;
    ArrayList<Sucursales> sucursal;

    private Spinner spCaja;
    ArrayList<Caja> caja;

    int gIdComprobante, gIdSucursal, gIdCaja;

    ImageView regresar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_modificar_autorizacion, container, false);

        getInfoUser();

        TextView fiscal = vista.findViewById(R.id.numeroAut);
        fiscal.setText(String.valueOf(ObtenerReportesFiscal.numeroAut));

        Toolbar toolbar = vista.findViewById(R.id.toolbarModif);
        toolbar.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));

        MaterialCardView registro = vista.findViewById(R.id.cardView1);
        registro.setStrokeColor(Color.rgb(gRed, gGreen, gBlue));

        MaterialCardView registro2 = vista.findViewById(R.id.cardView2);
        registro2.setStrokeColor(Color.rgb(gRed, gGreen, gBlue));

        MaterialCardView registro3 = vista.findViewById(R.id.cardView3);
        registro3.setStrokeColor(Color.rgb(gRed, gGreen, gBlue));

        regresar = vista.findViewById(R.id.returnmodif);
        regresar.setOnClickListener(view -> {
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new ObtenerEstadoFiscal());
            fr.commit();
        });

        cliente = new AsyncHttpClient();
        cliente2 = new AsyncHttpClient();
        cliente3 = new AsyncHttpClient();
        spComprobante = vista.findViewById(R.id.EditSpinnerComprobante);
        spSucursal = vista.findViewById(R.id.editSpinnerSucursal);
        spCaja = vista.findViewById(R.id.EditSpinnerCajas);

        btnActivo = vista.findViewById(R.id.btnActivoProducto);
        btnActivo.setOnClickListener(v -> {

            gEstadoProd = 1;
            btnActivo.setVisibility(View.INVISIBLE);
            btnInactivo.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Activado nuevamente", Toast.LENGTH_SHORT).show();
        });

        btnInactivo = vista.findViewById(R.id.btnInactivoProducto);
        btnInactivo.setOnClickListener(v -> {

            gEstadoProd = 0;
            btnActivo.setVisibility(View.VISIBLE);
            btnInactivo.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "Desactivado", Toast.LENGTH_SHORT).show();

        });

        Button modificar = vista.findViewById(R.id.btnModificarFiscal);
        modificar.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));
        modificar.setOnClickListener(view -> {

            String url = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/modificarFiscal.php"
                    + "?desde=" + desdeet.getText().toString()
                    + "&hasta=" + hastaet.getText().toString()
                    + "&serie=" + serieet.getText().toString()
                    + "&no_autorizacion=" + autorizacionet.getText().toString()
                    + "&resolucion=" + resolucionet.getText().toString()
                    + "&no_filas=" + filaset.getText().toString()
                    + "&fecha_autorizacion=" + fechaet.getText().toString()
                    + "&activo=" + gEstadoProd
                    + "&id_aut_fiscal=" + ObtenerReportesFiscal.idAutFiscal;
            ejecutarServicio(url);
            System.out.println(url);
            startActivity(new Intent(getContext(), ObtenerEstadoFiscal.class));
        });

        desdeet = vista.findViewById(R.id.editDesde);
        hastaet = vista.findViewById(R.id.editHasta);
        serieet = vista.findViewById(R.id.editSerie);
        autorizacionet = vista.findViewById(R.id.editAuth);
        resolucionet = vista.findViewById(R.id.editRes);
        filaset = vista.findViewById(R.id.etNumFilas);
        fechaet = vista.findViewById(R.id.tvFechaFiscal);
        activo = vista.findViewById(R.id.si);
        inactivo = vista.findViewById(R.id.no);

        return vista;
    }

    private void llenarComprobante(){
        String url ="http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/llenarComprobantes.php" + "?base=" + VariablesGlobales.dataBase;
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){
                    cargarComprobante(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void cargarComprobante(String respuesta){

        comprobantes = new ArrayList<>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){

                Comprobantes c = new Comprobantes();
                c.setComprobante(jsonArreglo.getJSONObject(i).getString("tipo_comprobante"));
                c.setIdComprobante(jsonArreglo.getJSONObject(i).getInt("id_tipo_comprobante"));
                comprobantes.add(c);
            }

            ArrayAdapter<Comprobantes> a  = new ArrayAdapter<>(getContext(), R.layout.spinner_item, comprobantes);
            spComprobante.setAdapter(a);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void obtenerIdComprobante(){
        int indice = spComprobante.getSelectedItemPosition();
        gIdComprobante = comprobantes.get(indice).getIdComprobante();

    }

    private void llenarSucursal(){
        String url ="http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/llenarSucursales.php" + "?base=" + VariablesGlobales.dataBase;
        cliente2.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){
                    cargarSucursal(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void cargarSucursal(String respuesta){

        sucursal = new ArrayList<>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){

                Sucursales c = new Sucursales();
                c.setNomSucursal(jsonArreglo.getJSONObject(i).getString("nombre_sucursal"));
                c.setIdSucursal(jsonArreglo.getJSONObject(i).getInt("id_sucursal"));
                sucursal.add(c);
            }

            ArrayAdapter<Sucursales> a  = new ArrayAdapter<>(getContext(), R.layout.spinner_item, sucursal);
            spSucursal.setAdapter(a);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void obtenerIdSucursal(){
        int indice = spSucursal.getSelectedItemPosition();
        gIdSucursal = sucursal.get(indice).getIdSucursal();

    }

    private void llenarCaja(){
        String url ="http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/llenarCajasAdd.php" + "?base=" + VariablesGlobales.dataBase;
        cliente3.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){
                    cargarCaja(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void cargarCaja(String respuesta){

        caja = new ArrayList<>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){

                Caja c = new Caja();
                c.setNombreCaja(jsonArreglo.getJSONObject(i).getString("nombre_caja"));
                c.setIdCaja(jsonArreglo.getJSONObject(i).getInt("id_caja"));
                caja.add(c);
            }

            ArrayAdapter<Caja> a  = new ArrayAdapter<>(getContext(), R.layout.spinner_item, caja);
            spCaja.setAdapter(a);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void obtenerIdCaja(){
        int indice = spCaja.getSelectedItemPosition();
        gIdCaja = caja.get(indice).getIdCaja();

    }

    public void getInfoUser() {

        String URL_USUARIOS = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerFiscal.php" + "?base=" + VariablesGlobales.dataBase + "&id_aut_fiscal=" + ObtenerReportesFiscal.idAutFiscal;
        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_USUARIOS,

                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Fiscal");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            desde = jsonObject1.getString("desde");
                            desdeet.setText(desde);

                            hasta = jsonObject1.getString("hasta");
                            hastaet.setText(hasta);

                            serie = jsonObject1.getString("serie");
                            serieet.setText(serie);

                            autorizacion = jsonObject1.getString("no_autorizacion");
                            autorizacionet.setText(autorizacion);

                            resolucion = jsonObject1.getString("resolucion");
                            resolucionet.setText(resolucion);

                            filas = jsonObject1.getString("no_filas");
                            filaset.setText(filas);

                            fecha = jsonObject1.getString("fecha_autorizacion");
                            fechaet.setText(fecha);

                            activoEdit = jsonObject1.getString("activo");
                            if (activoEdit.equals("1")){
                                btnInactivo.setVisibility(View.VISIBLE);
                                btnActivo.setVisibility(View.INVISIBLE);
                            }
                            else {
                                btnActivo.setVisibility(View.VISIBLE);
                                btnInactivo.setVisibility(View.INVISIBLE);
                            }

                            llenarCaja();
                            llenarComprobante();
                            llenarSucursal();

                        }

                        progressDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }

                }, VolleyError -> progressDialog.dismiss()
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
                response -> progressDialog.dismiss(),
                volleyError -> progressDialog.dismiss()
        );
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

}