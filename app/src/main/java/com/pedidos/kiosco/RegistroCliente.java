package com.pedidos.kiosco;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pedidos.kiosco.model.Sucursales;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegistroCliente extends AppCompatActivity {

    String usuario;
    EditText regPhoneNo, pas, nom, em;
    int gIdSucursal;
    CheckBox politica;
    Spinner spSucursales;
    AsyncHttpClient cliente;
    ArrayList<Sucursales> lista = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro_cliente);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        RelativeLayout relativeLayout = findViewById(R.id.linear2);
        relativeLayout.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));

        Button botonRegistrar = findViewById(R.id.btnRegistroCliente);
        botonRegistrar.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));
        regPhoneNo = findViewById(R.id.telefonoCliente);
        pas = findViewById(R.id.direccion);
        nom = findViewById(R.id.nombreCliente3);
        em = findViewById(R.id.dui);

        botonRegistrar.setOnClickListener(v -> {

            obtenerSucursales();

            String nombre = nom.getText().toString();
            usuario = regPhoneNo.getText().toString();
            String password = pas.getText().toString();
            String dui = em.getText().toString();

            if (usuario.isEmpty()) {
                regPhoneNo.setError("Agregue su número de teléfono");
                botonRegistrar.setError("Agregue su número de teléfono");
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(regPhoneNo.getWindowToken(), 0);
            }

            else if (nombre.isEmpty()) {
                nom.setError("Agregue su nombre");
                botonRegistrar.setError("Agregue su nombre");
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(nom.getWindowToken(), 0);
            }

            else if (password.isEmpty()) {
                pas.setError("Agregue una dirección.");
                botonRegistrar.setError("Agregue una contraseña");
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pas.getWindowToken(), 0);
            }

            else if (dui.isEmpty()) {
                pas.setError("Agregue su dui.");
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pas.getWindowToken(), 0);
            }

            else {

                ejecutarServicio();

            }
        });
    }

    public void ejecutarServicio() {

        ProgressDialog progressDialog = new ProgressDialog(RegistroCliente.this, R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, "http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/registroCliente.php" + "?base=" + VariablesGlobales.dataBase,
                response -> {

                    if(response.equalsIgnoreCase("Usuario registrado")){

                        Intent i = new Intent(getApplicationContext(), Principal.class);
                        startActivity(i);
                        progressDialog.dismiss();

                    }
                    else{
                        Toast.makeText(RegistroCliente.this, response, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                }, volleyError -> {
            Toast.makeText(getApplicationContext(), "Ocurrió un error inesperado, Error: " + volleyError, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        }

        ){
            @Override
            protected Map<String, String> getParams() {

                String usuario = regPhoneNo.getText().toString();
                String password = pas.getText().toString();
                String nombre = nom.getText().toString();
                String email = em.getText().toString();


                Map<String,String> params = new HashMap<>();

                params.put("numero_cliente", usuario);
                params.put("nombre_cliente", nombre);
                params.put("direccion_cliente", password);
                params.put("dui_cliente", email);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(RegistroCliente.this);
        requestQueue.add(request);

    }

    private void llenarSpinner(){
        String url = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerSucursales.php" + "?base=" + VariablesGlobales.dataBase;
        System.out.println(url);
        cliente.post(url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (statusCode == 200){
                    cargarSpinner(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getApplicationContext(), "Ocurrió un error inesperado", Toast.LENGTH_SHORT).show();
            }

        });
    }

    public void obtenerSucursales(){
        int indice = spSucursales.getSelectedItemPosition();
        gIdSucursal = lista.get(indice).getIdSucursal();

    }

    private void cargarSpinner(String respuesta){

        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){

                Sucursales c = new Sucursales();
                c.setIdSucursal(jsonArreglo.getJSONObject(i).getInt("id_sucursal"));
                c.setNomSucursal(jsonArreglo.getJSONObject(i).getString("nombre_sucursal"));
                lista.add(c);
            }

            ArrayAdapter<Sucursales> a  = new ArrayAdapter<>(this, R.layout.spinner_item, lista);
            spSucursales.setAdapter(a);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

}