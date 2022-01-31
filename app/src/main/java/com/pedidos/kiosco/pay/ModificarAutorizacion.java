package com.pedidos.kiosco.pay;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.main.ObtenerEstadoFiscal;
import com.pedidos.kiosco.main.ObtenerReportesFiscal;
import com.pedidos.kiosco.model.Caja;
import com.pedidos.kiosco.model.Sucursales;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class ModificarAutorizacion extends AppCompatActivity {

    String desde, hasta, serie, autorizacion, resolucion, filas, fecha;
    int rgButton;
    EditText desdeet, hastaet, serieet, autorizacionet, resolucionet, filaset, fechaet;
    RadioGroup activoEdit;
    RadioButton activo, inactivo;
    Button btnActivo, btnInactivo;

    int gEstadoProd;

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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modificar_autorizacion);

        regresar = findViewById(R.id.returnmodif);
        regresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ObtenerReportesFiscal.class));
            }
        });

        cliente = new AsyncHttpClient();
        cliente2 = new AsyncHttpClient();
        cliente3 = new AsyncHttpClient();
        spComprobante = findViewById(R.id.EditSpinnerComprobante);
        spSucursal = findViewById(R.id.editSpinnerSucursal);
        spCaja = findViewById(R.id.EditSpinnerCajas);

        btnActivo = findViewById(R.id.btnActivoProducto);
        btnActivo.setOnClickListener(v -> {

            gEstadoProd = 1;
            btnActivo.setEnabled(false);
            btnInactivo.setEnabled(true);
            Toast.makeText(ModificarAutorizacion.this, "Activado nuevamente", Toast.LENGTH_SHORT).show();
        });

        btnInactivo = findViewById(R.id.btnInactivoProducto);
        btnInactivo.setOnClickListener(v -> {

            gEstadoProd = 0;
            btnActivo.setEnabled(true);
            btnInactivo.setEnabled(false);
            Toast.makeText(ModificarAutorizacion.this, "Desactivado", Toast.LENGTH_SHORT).show();

        });

        Button modificar = findViewById(R.id.btnModificarFiscal);
        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

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
                startActivity(new Intent(getApplicationContext(), ObtenerEstadoFiscal.class));
            }
        });

        desdeet = findViewById(R.id.editDesde);
        hastaet = findViewById(R.id.editHasta);
        serieet = findViewById(R.id.editSerie);
        autorizacionet = findViewById(R.id.editAuth);
        resolucionet = findViewById(R.id.editRes);
        filaset = findViewById(R.id.etNumFilas);
        fechaet = findViewById(R.id.tvFechaFiscal);
        activo = findViewById(R.id.si);
        inactivo = findViewById(R.id.no);

        getInfoUser();
    }

    private void llenarComprobante(){
        String url ="http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/llenarComprobantes.php";
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

            ArrayAdapter<Comprobantes> a  = new ArrayAdapter<>(this, R.layout.spinner_item, comprobantes);
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
        String url ="http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/llenarSucursales.php";
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

            ArrayAdapter<Sucursales> a  = new ArrayAdapter<>(this, R.layout.spinner_item, sucursal);
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
        String url ="http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/llenarCajas.php";
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

            ArrayAdapter<Caja> a  = new ArrayAdapter<>(this, R.layout.spinner_item, caja);
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

        String URL_USUARIOS = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerFiscal.php" + "?id_aut_fiscal=" + ObtenerReportesFiscal.idAutFiscal;

        ProgressDialog progressDialog = new ProgressDialog(ModificarAutorizacion.this, R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

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

        ProgressDialog progressDialog = new ProgressDialog(ModificarAutorizacion.this, R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> progressDialog.dismiss(),
                volleyError -> progressDialog.dismiss()
        );
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    @Override
    public void onBackPressed() {

    }

}