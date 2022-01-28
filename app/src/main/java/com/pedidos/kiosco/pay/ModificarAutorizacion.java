package com.pedidos.kiosco.pay;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.main.ObtenerReportesFiscal;
import com.pedidos.kiosco.model.Sucursales;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ModificarAutorizacion extends AppCompatActivity {

    String desde, hasta, serie, autorizacion, resolucion, filas, fecha;
    int rgButton;
    EditText desdeet, hastaet, serieet, autorizacionet, resolucionet, filaset, fechaet;

    RadioButton activo, inactivo;

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
        setContentView(R.layout.modificar_autorizacion);

        Button modificar = findViewById(R.id.btnModificarFiscal);
        modificar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                /*String url = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/modificarFiscal.php"
                        + "?nombre_cliente=" + nombre.getText().toString()
                        + "&email_cliente=" + email.getText().toString()
                        + "&id_usuario=" + Login.gIdUsuario
                        + "&id_cliente=" + Login.gIdCliente;
                ejecutarServicio(url);*/
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

                            rgButton = jsonObject1.getInt("activo");
                            System.out.println("Radio: " + rgButton);
                            if (rgButton == 1){
                                activo.setSelected(true);
                            } else {
                                inactivo.setSelected(true);
                            }



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

        ProgressDialog progressDialog = new ProgressDialog(getApplicationContext(), R.style.Custom);
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

}