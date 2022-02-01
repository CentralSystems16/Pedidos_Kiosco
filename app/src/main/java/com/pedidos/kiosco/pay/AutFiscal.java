package com.pedidos.kiosco.pay;

import androidx.appcompat.app.AppCompatActivity;
import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.main.ObtenerEstadoFiscal;
import com.pedidos.kiosco.model.Caja;
import com.pedidos.kiosco.model.Comprobantes;
import com.pedidos.kiosco.model.Sucursales;
import org.json.JSONArray;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class AutFiscal extends AppCompatActivity {

    EditText desde, hasta, serie, autorizacion, resolucion, numeroFilas, mDisplayDate;
    RadioGroup activos;
    Button continuar;
    DatePickerDialog.OnDateSetListener mDateSetListener;

    int gIdComprobante, gIdSucursal, gIdCaja;

    private AsyncHttpClient cliente;
    private AsyncHttpClient cliente2;
    private AsyncHttpClient cliente3;

    private Spinner spComprobante;
    ArrayList<Comprobantes> comprobantes;

    private Spinner spSucursal;
    ArrayList<Sucursales> sucursal;

    private Spinner spCaja;
    ArrayList<Caja> caja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autenticacion_fiscal);

        ImageButton agregar = findViewById(R.id.agregar);
        agregar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ObtenerEstadoFiscal.class));
            }
        });

        desde = findViewById(R.id.etDesde);
        hasta = findViewById(R.id.etHasta);
        serie = findViewById(R.id.etSerie);
        autorizacion = findViewById(R.id.etAuth);
        resolucion = findViewById(R.id.etRes);
        numeroFilas = findViewById(R.id.editFilas);
        mDisplayDate = findViewById(R.id.editFecha);
        activos = findViewById(R.id.rgActivo);
        continuar = findViewById(R.id.btnRegistrarFiscal);

        cliente = new AsyncHttpClient();
        cliente2 = new AsyncHttpClient();
        cliente3 = new AsyncHttpClient();
        spComprobante = findViewById(R.id.spinnerComprobante);
        spSucursal = findViewById(R.id.spinnerSucursal);
        spCaja = findViewById(R.id.spinnerCajas);

        llenarCaja();
        llenarComprobante();
        llenarSucursal();

        mDisplayDate.setOnClickListener(v -> {

            @SuppressLint("SimpleDateFormat")
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date date = null;
            try {
                date = sdf.parse("2003/07/01");
            } catch (ParseException e) {
                e.printStackTrace();
            }

            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    getApplicationContext(),
                    android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth,
                    mDateSetListener,
                    year, month, day);
            dialog.getDatePicker().setMaxDate(cal.getTimeInMillis());
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            dialog.show();
        });

        mDateSetListener = new DatePickerDialog.OnDateSetListener() {
            private static final String TAG = "";

            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;

                Log.d(TAG, "onDateSet: d/MMMM/yyyy: " + day + month + year);

                String date = day + "/" + month + "/" + year;
                mDisplayDate.setText(date);
            }
        };

        continuar.setOnClickListener(view -> {
            if (desde.getText().toString().equals("") || hasta.getText().toString().equals("") || serie.getText().toString().equals("") || autorizacion.getText().toString().equals("")
                || resolucion.getText().toString().equals("") || numeroFilas.getText().toString().equals("") || mDisplayDate.getText().toString().equals("")){

                Toast.makeText(getApplicationContext(), "Hay campos vacios", Toast.LENGTH_SHORT).show();

            }

            else {
                ejecutarServicio();
            }

        });

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

    public void ejecutarServicio() {

        obtenerIdSucursal();
        obtenerIdCaja();
        obtenerIdComprobante();

        ProgressDialog progressDialog = new ProgressDialog(AutFiscal.this, R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, "http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/registroFiscal.php",
                response -> {

                    if(response.equalsIgnoreCase("Usuario registrado")){

                        startActivity(new Intent(getApplicationContext(), ObtenerEstadoFiscal.class));
                        progressDialog.dismiss();

                    }
                    else{
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                }, volleyError -> {
            Toast.makeText(getApplicationContext(), "Ocurrió un error inesperado, Error: " + volleyError, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        ){
            @Override
            protected Map<String, String> getParams() {

                String desde1 = desde.getText().toString();
                String hasta1 = hasta.getText().toString();
                String serie1 = serie.getText().toString();
                String autorizacion1 = autorizacion.getText().toString();
                String resolucion1 = resolucion.getText().toString();
                String numeroFilas1 = numeroFilas.getText().toString();
                String mDisplayDate1 = mDisplayDate.getText().toString();
                String sucursal = String.valueOf(gIdSucursal);
                String caja = String.valueOf(gIdCaja);
                String comprobante = String.valueOf(gIdComprobante);


                Map<String,String> params = new HashMap<>();

                params.put("desde", desde1);
                params.put("hasta", hasta1);
                params.put("serie", serie1);
                params.put("no_autorizacion", autorizacion1);
                params.put("resolucion", resolucion1);
                params.put("no_filas", numeroFilas1);
                params.put("fecha_autorizacion", mDisplayDate1);
                params.put("id_sucursal", sucursal);
                params.put("id_caja", caja);
                params.put("id_tipo_comprobante", comprobante);
                params.put("id_usuario", String.valueOf(Login.gIdUsuario));

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(request);

    }

    @Override
    public void onBackPressed() {

    }

}