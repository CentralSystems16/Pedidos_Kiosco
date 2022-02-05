package com.pedidos.kiosco.usuarios;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;

import org.json.JSONArray;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import cz.msebera.android.httpclient.Header;

public class AgregarUsuario extends AppCompatActivity {

    private AsyncHttpClient cliente;
    private Spinner spProductos;
    RequestQueue requestQueue;
    String select;
    private DatePickerDialog.OnDateSetListener mDateSetListener;
    EditText regPhoneNo, pas, nom, pr, em, ed, dui, mes;
    private TextView mDisplayDate, errorPass, errorEdad2;
    RadioGroup rg;
    public static int gusuarioR;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agregar_usuario);
        //TODO: Bloquear orientación de pantalla.
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);

        errorPass = findViewById(R.id.errorPass);
        errorEdad2 = findViewById(R.id.errorEdad);

    final Button botonRegistrar = findViewById(R.id.btnRegistrarUsuario);

     regPhoneNo = findViewById(R.id.campoTelefono);
     pas = findViewById(R.id.campoPass);
     nom = findViewById(R.id.campoNombre);
     pr = findViewById(R.id.campoPassRepeat);
     em = findViewById(R.id.campoEmail);
     ed = findViewById(R.id.campoEdad);
     dui = findViewById(R.id.campoDui);
     mes = findViewById(R.id.campoMeses);
     mDisplayDate = findViewById(R.id.tvDate);

        ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String errorEdad = ed.getText().toString();

                if (errorEdad.equals("0") || errorEdad.equals("1") || errorEdad.equals("2") || errorEdad.equals("3") || errorEdad.equals("4") || errorEdad.equals("5") || errorEdad.equals("6") || errorEdad.equals("7") || errorEdad.equals("8") || errorEdad.equals("9") || errorEdad.equals("10") || errorEdad.equals("11") || errorEdad.equals("12") || errorEdad.equals("13") || errorEdad.equals("14") || errorEdad.equals("15") || errorEdad.equals("16") || errorEdad.equals("17")){
                    errorEdad2.setText("Error");
                } else if (errorEdad.equals(".") || errorEdad.equals("-")){
                    errorEdad2.setText("Carácteres inválidos");
                } else {
                    errorEdad2.setText("");
                }

            }
        });

        pr.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                String password = pas.getText().toString();
                String passwordRepeat = pr.getText().toString();

                if (password.equals(passwordRepeat)) {
                    errorPass.setText("Coicide");
                } else {
                    errorPass.setText("No coicide");
                }

            }
        });

        mDisplayDate.setOnClickListener(v -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
            Date date = null;
            try {
                date = sdf.parse("2003/01/01");
            } catch (ParseException e) {
                e.printStackTrace();
            }
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            int year = cal.get(Calendar.YEAR);
            int month = cal.get(Calendar.MONTH);
            int day = cal.get(Calendar.DAY_OF_MONTH);

            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    android.R.style.Theme_Holo_Light_Dialog_MinWidth,
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

                Log.d(TAG, "onDateSet: d/MMMM/yyyy: " + day + month  + year);

                String date  = day + "/" + month + "/" + year;
                mDisplayDate.setText(date);
            }
        };

    spProductos = findViewById(R.id.spinnerCargo);
    cliente = new AsyncHttpClient();
        llenarSpinner();


        botonRegistrar.setOnClickListener(v->{

            String nombre = nom.getText().toString();
            String edad = ed.getText().toString();
            String fechaNacimiento = mDisplayDate.getText().toString();
            String usuario = regPhoneNo.getText().toString();
            String password = pas.getText().toString();
            String passwordRepeat = pr.getText().toString();
            String id = dui.getText().toString();
            String me = mes.getText().toString();

            int radiogroupSexo = rg.getCheckedRadioButtonId();
            if (radiogroupSexo < 0){

            } else {
                View rbM = rg.findViewById(radiogroupSexo);
                int idx = rg.indexOfChild(rbM);
                RadioButton r = (RadioButton) rg.getChildAt(idx);
                select = r.getText().toString();
            }

            if (nombre.equals("")){
                Toast.makeText(this, "Campos vacíos!", Toast.LENGTH_SHORT).show();
            }

            else if (id.equals("")){
                Toast.makeText(this, "Campos vacíos!", Toast.LENGTH_SHORT).show();
            }

            else if (me.equals("")){
                Toast.makeText(this, "Campos vacíos!", Toast.LENGTH_SHORT).show();
            }

            else if (usuario.equals("")){
                Toast.makeText(this, "Campos vacíos!", Toast.LENGTH_SHORT).show();
            }

            else if (password.equals("")){
                Toast.makeText(this, "Campos vacíos!", Toast.LENGTH_SHORT).show();
            }

            else if (passwordRepeat.equals("")){
                Toast.makeText(this, "Campos vacíos!", Toast.LENGTH_SHORT).show();
            }

            else if (password.length() < 6){
                Toast.makeText(this, "La contraseña debe contener al menos 6 carácteres", Toast.LENGTH_SHORT).show();
            }

            else if (!password.equals(passwordRepeat)){
                Toast.makeText(this, "Las contraseñas no coiciden", Toast.LENGTH_SHORT).show();
            }

            else if (edad.isEmpty()){
                Toast.makeText(this, "Campos vacíos!", Toast.LENGTH_SHORT).show();
            }

            else if (fechaNacimiento.isEmpty()){
                Toast.makeText(this, "Campos vacíos!", Toast.LENGTH_SHORT).show();
            }

            else if (select.isEmpty()){
                Toast.makeText(this, "Campos vacíos!", Toast.LENGTH_SHORT).show();
            } else {
                ejecutar();
            }
        });
    }


private void ejecutar(){

    ejecutarServicio("http://"+ VariablesGlobales.host +"/android/LCB/administrador/scripts/scripts_php/registro.php");
    Intent i = new Intent(this, Principal.class);
    startActivity(i);

}


    private void llenarSpinner(){
        String url ="http://"+ VariablesGlobales.host +"/android/LCB/administrador/scripts/scripts_php/llenarCargos.php";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){
                    cargarSpinner(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void cargarSpinner(String respuesta){

        ArrayList<Cargos> lista = new ArrayList<>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){

                Cargos c = new Cargos();
                c.setNomCargo(jsonArreglo.getJSONObject(i).getString("nombre_cargo"));
                c.setIdCargo(jsonArreglo.getJSONObject(i).getInt("id_cargo"));
                lista.add(c);
            }

            ArrayAdapter<Cargos> a  = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, lista);
            spProductos.setAdapter(a);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void ejecutarServicio(String URL) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, response -> {

        },
                error -> Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show()){
            @Override
            protected Map<String, String> getParams() {

                String usuario = regPhoneNo.getText().toString();
                String password = pas.getText().toString();
                String nombre = nom.getText().toString();
                String fechaNacimiento = mDisplayDate.getText().toString();
                String passwordRepeat = pr.getText().toString();
                String email = em.getText().toString();
                String edad = ed.getText().toString();
                String id = dui.getText().toString();
                String me = mes.getText().toString();

                int radiogroupSexo = rg.getCheckedRadioButtonId();
                if (radiogroupSexo < 0){

                } else {
                    View rbM = rg.findViewById(radiogroupSexo);
                    int idx = rg.indexOfChild(rbM);
                    RadioButton r = (RadioButton) rg.getChildAt(idx);
                    select = r.getText().toString();
                }

                Map<String, String> parametros = new HashMap<>();

                parametros.put("login_usuario", usuario);
                parametros.put("id_usuario", String.valueOf(gusuarioR));
                parametros.put("nombre_usuario", nombre);
                parametros.put("password_usuarios", password);
                parametros.put("password_repeat_usuario", passwordRepeat);
                parametros.put("email_usuario", email);
                parametros.put("edad_usuario", edad);
                parametros.put("nacimiento_usuario", fechaNacimiento);
                parametros.put("dui_usuario", id);
                parametros.put("meses_usuario", me);
                parametros.put("sexo_usuario", select);

                return parametros;
            }
        };

        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);

    }
}

