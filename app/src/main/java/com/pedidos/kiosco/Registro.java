package com.pedidos.kiosco;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
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

public class Registro extends AppCompatActivity {

    String usuario;
    EditText regPhoneNo, pas, nom, pr, em;
    int gIdSucursal;
    CheckBox politica;
    Spinner spSucursales;
    AsyncHttpClient cliente;
    ArrayList<Sucursales> lista = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registro);

        RelativeLayout relativeLayout = findViewById(R.id.linear2);
        relativeLayout.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));

        MaterialCardView registro = findViewById(R.id.cardViewRegistro);
        registro.setStrokeColor(Color.rgb(gRed, gGreen, gBlue));

        politica = findViewById(R.id.cbPolitica);

        cliente = new AsyncHttpClient();
        spSucursales = findViewById(R.id.spinnerSucursales);
        llenarSpinner();

        Button botonRegistrar = findViewById(R.id.btnRegistro);
        botonRegistrar.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));
        regPhoneNo = findViewById(R.id.telefono);
        pas = findViewById(R.id.pass);
        nom = findViewById(R.id.nombre);
        pr = findViewById(R.id.repeatPass);
        em = findViewById(R.id.email);

        botonRegistrar.setOnClickListener(v -> {

            obtenerSucursales();

            String nombre = nom.getText().toString();
            usuario = regPhoneNo.getText().toString();
            String password = pas.getText().toString();
            String passwordRepeat = pr.getText().toString();

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
                pas.setError("Agregue una contraseña.");
                botonRegistrar.setError("Agregue una contraseña");
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pas.getWindowToken(), 0);
            }

            else if (passwordRepeat.isEmpty()) {
                pr.setError("Repita la contraseña");
                botonRegistrar.setError("Repita la contraseña");
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pr.getWindowToken(), 0);
            }

            else if (password.length() < 6) {
                pas.setError("La contraseña debe contener al menos 6 caracteres");
                botonRegistrar.setError("La contraseña debe contener al menos 6 caracteres");
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pas.getWindowToken(), 0);
            }

            else if (!password.equals(passwordRepeat)) {
                pas.setError("Las contraseñas no coiciden");
                pr.setError("Las contraseñas no coiciden");
                botonRegistrar.setError("Las contraseñas no coiciden");
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pas.getWindowToken(), 0);

            }  else if (regPhoneNo.length() < 8 || regPhoneNo.length() > 8) {
                regPhoneNo.setError("El número ingresado no es correcto, asegurese que sea un número válido");
                botonRegistrar.setError("El número ingresado no es correcto, asegurese que sea un número válido");
                InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(regPhoneNo.getWindowToken(), 0);
            }

            else if (!politica.isChecked()){
                Toast.makeText(getApplicationContext(), "Por favor, acepte los terminos y las politicas de privacidad", Toast.LENGTH_SHORT).show();
            }

            else {

                ejecutarServicio();

            }
        });
    }

    public void ejecutarServicio() {

        ProgressDialog progressDialog = new ProgressDialog(Registro.this, R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, "http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/registroUsuario.php" + "?base=" + VariablesGlobales.dataBase,
                response -> {

                    if(response.equalsIgnoreCase("Usuario registrado")){

                        String phoneNo = regPhoneNo.getText().toString();
                        Intent i = new Intent(getApplicationContext(), Login.class);
                        i.putExtra("phoneNo", phoneNo);
                        startActivity(i);
                        progressDialog.dismiss();

                    }
                    else{
                        Toast.makeText(Registro.this, response, Toast.LENGTH_SHORT).show();
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
                String passwordRepeat = pr.getText().toString();
                String email = em.getText().toString();
                String sucursal = String.valueOf(gIdSucursal);

                Map<String,String> params = new HashMap<>();

                params.put("login_usuario", usuario);
                params.put("nombre_usuario", nombre);
                params.put("password_usuarios", password);
                params.put("password_repeat_usuario", passwordRepeat);
                params.put("email_usuario", email);
                params.put("current_number", usuario);
                params.put("id_sucursal", sucursal);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(Registro.this);
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