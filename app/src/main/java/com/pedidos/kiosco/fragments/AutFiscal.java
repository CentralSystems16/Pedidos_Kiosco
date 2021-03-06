package com.pedidos.kiosco.fragments;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;
import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;
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
import com.pedidos.kiosco.Login;
import org.json.JSONArray;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import cz.msebera.android.httpclient.Header;

public class AutFiscal extends Fragment {

    EditText desde, hasta, serie, autorizacion, resolucion, numeroFilas, mDisplayDate;
    RadioGroup activos;
    Button continuar, btnCancelar;

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

    private int ultimoAnio, ultimoMes, ultimoDiaDelMes;

    private DatePickerDialog.OnDateSetListener listenerDeDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int anio, int mes, int diaDelMes) {

            ultimoAnio = anio;
            ultimoMes = mes;
            ultimoDiaDelMes = diaDelMes;

            refrescarFechaEnEditText();

        }
    };

    public void refrescarFechaEnEditText() {

        String fecha = String.format(Locale.getDefault(), "%02d-%02d-%02d", ultimoAnio, ultimoMes+1, ultimoDiaDelMes);
        mDisplayDate.setText(fecha);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_aut_fiscal, container, false);

        mDisplayDate = vista.findViewById(R.id.editFecha);

        final Calendar calendario = Calendar.getInstance();
        ultimoAnio = calendario.get(Calendar.YEAR);
        ultimoMes = calendario.get(Calendar.MONTH);
        ultimoDiaDelMes = calendario.get(Calendar.DAY_OF_MONTH);

        btnCancelar = vista.findViewById(R.id.btncancelarAuth);
        btnCancelar.setOnClickListener(view -> {
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new ObtenerEstadoFiscal());
            fr.commit();
        });

        refrescarFechaEnEditText();

        mDisplayDate.setOnClickListener(v -> {

            DatePickerDialog dialogoFecha = new DatePickerDialog(getContext(), listenerDeDatePicker, ultimoAnio, ultimoMes, ultimoDiaDelMes);

            dialogoFecha.show();
        });

        Toolbar toolbar = vista.findViewById(R.id.toolbarAdd);
        toolbar.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));

        ImageButton agregar = vista.findViewById(R.id.agregar);
        agregar.setOnClickListener(view -> {
            startActivity(new Intent(getContext(), ObtenerEstadoFiscal.class));
        });

        MaterialCardView registro = vista.findViewById(R.id.cardView1Add);
        registro.setStrokeColor(Color.rgb(gRed, gGreen, gBlue));

        MaterialCardView registro2 = vista.findViewById(R.id.cardView2Add);
        registro2.setStrokeColor(Color.rgb(gRed, gGreen, gBlue));

        MaterialCardView registro3 = vista.findViewById(R.id.cardView3Add);
        registro3.setStrokeColor(Color.rgb(gRed, gGreen, gBlue));

        desde = vista.findViewById(R.id.etDesde);
        hasta = vista.findViewById(R.id.etHasta);
        serie = vista.findViewById(R.id.etSerie);
        autorizacion = vista.findViewById(R.id.etAuth);
        resolucion = vista.findViewById(R.id.etRes);
        numeroFilas = vista.findViewById(R.id.editFilas);

        activos = vista.findViewById(R.id.rgActivo);
        continuar = vista.findViewById(R.id.btnRegistrarFiscal);

        cliente = new AsyncHttpClient();
        cliente2 = new AsyncHttpClient();
        cliente3 = new AsyncHttpClient();
        spComprobante = vista.findViewById(R.id.spinnerComprobante);
        spSucursal = vista.findViewById(R.id.spinnerSucursal);
        spCaja = vista.findViewById(R.id.spinnerCajas);

        llenarCaja();
        llenarComprobante();
        llenarSucursal();

        continuar.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));
        continuar.setOnClickListener(view -> {
            if (desde.getText().toString().equals("") || hasta.getText().toString().equals("") || serie.getText().toString().equals("") || autorizacion.getText().toString().equals("")
                    || resolucion.getText().toString().equals("") || numeroFilas.getText().toString().equals("") || mDisplayDate.getText().toString().equals("")){

                Toast.makeText(getContext(), "Hay campos vacios", Toast.LENGTH_SHORT).show();

            }

            else {
                ejecutarServicio();
            }

        });

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

    public void ejecutarServicio() {

        obtenerIdSucursal();
        obtenerIdCaja();
        obtenerIdComprobante();

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, "http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/registroFiscal.php" + "?base=" + VariablesGlobales.dataBase,
                response -> {

                    if(response.equalsIgnoreCase("Usuario registrado")){

                        FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                        fr.replace(R.id.fragment_layout, new ObtenerEstadoFiscal());
                        fr.commit();
                        progressDialog.dismiss();

                    }
                    else{
                        Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                }, volleyError -> {
            Toast.makeText(getContext(), "Ocurri?? un error inesperado, Error: " + volleyError, Toast.LENGTH_SHORT).show();
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

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);

    }

}