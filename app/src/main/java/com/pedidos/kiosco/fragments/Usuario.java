package com.pedidos.kiosco.fragments;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

public class Usuario extends Fragment {

    EditText numero, nombre, email, password, repeatPassword;
    Button completeEdit, completeEditDef;
    String gLogin, gNombre, gEmail, gContra, gRepatContra;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("ResourceAsColor")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_usuario, container, false);

        getInfoUser();

        Button cerrarSesion = vista.findViewById(R.id.cerrarSesion);
        cerrarSesion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), Login.class));
            }
        });

        Button esAmin = vista.findViewById(R.id.esAdmin);

        esAmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        if(Login.cargo == 1 || Login.cargo == 2){
            esAmin.setVisibility(View.INVISIBLE);
        }



        numero = vista.findViewById(R.id.EditNumero);
        numero.setEnabled(false);

        nombre = vista.findViewById(R.id.EditNombre);
        nombre.setEnabled(false);

        email = vista.findViewById(R.id.EditEmail);
        email.setEnabled(false);

        password = vista.findViewById(R.id.EditPass);
        password.setEnabled(false);

        repeatPassword = vista.findViewById(R.id.EditRepeatPass);
        repeatPassword.setEnabled(false);

        completeEditDef = vista.findViewById(R.id.completeEditDef);
        completeEditDef.setVisibility(View.INVISIBLE);
        completeEditDef.setOnClickListener(v -> {

            String name = nombre.getText().toString();
            String pass = password.getText().toString();
            String passRepeat = repeatPassword.getText().toString();

            if (name.equals("") || pass.equals("") || passRepeat.equals("")) {
                Toast toast = Toast.makeText(getContext(), "No puede dejar campos vacíos!", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 110);
                toast.show();
            } else if (!pass.equals(passRepeat)) {
                Toast toast = Toast.makeText(getContext(), "Asegurese que las contraseñas sean iguales.", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 110);
                toast.show();
            } else {

                String url = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/modificarUsuario.php"
                        + "?base=" + VariablesGlobales.dataBase
                        + "&nombre_cliente=" + nombre.getText().toString()
                        + "&email_cliente=" + email.getText().toString()
                        + "&id_usuario=" + Login.gIdUsuario
                        + "&id_cliente=" + Login.gIdCliente;

                String url2 = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/modificarClienteUsuario.php"
                        + "?base=" + VariablesGlobales.dataBase
                        + "&login_usuario=" + numero.getText().toString()
                        + "&nombre_usuario=" + nombre.getText().toString()
                        + "&email_usuario=" + email.getText().toString()
                        + "&password_usuarios=" + password.getText().toString()
                        + "&password_repeat_usuario=" + repeatPassword.getText().toString()
                        + "&id_usuario=" + Login.gIdUsuario;

                ejecutarServicio(url);
                ejecutarServicio(url2);

                completeEditDef.setVisibility(View.INVISIBLE);
                completeEdit.setVisibility(View.VISIBLE);
                nombre.setEnabled(false);
                email.setEnabled(false);
                password.setEnabled(false);
                repeatPassword.setEnabled(false);

            }
        });

        completeEdit = vista.findViewById(R.id.completeEdit);
        completeEdit.setOnClickListener(v -> {

            completeEdit.setVisibility(View.INVISIBLE);
            completeEditDef.setVisibility(View.VISIBLE);

            nombre.setEnabled(true);
            nombre.setTextColor(R.color.teal_200);
            email.setEnabled(true);
            email.setTextColor(R.color.teal_200);
            password.setEnabled(true);
            password.setTextColor(R.color.teal_200);
            repeatPassword.setEnabled(true);
            repeatPassword.setTextColor(R.color.teal_200);
        });

        return vista;

    }

    public void getInfoUser() {

        String URL_USUARIOS = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerUsuarios.php" + "?base=" + VariablesGlobales.dataBase + "&id_usuario=" + Login.gIdUsuario;

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_USUARIOS,

                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Usuarios");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            gLogin = jsonObject1.getString("login_usuario");
                            numero.setText(gLogin);

                            gNombre = jsonObject1.getString("nombre_usuario");
                            nombre.setText(gNombre);

                            gEmail = jsonObject1.getString("email_usuario");
                            email.setText(gEmail);

                            gContra = jsonObject1.getString("password_usuarios");
                            password.setText(gContra);

                            gRepatContra = jsonObject1.getString("password_repeat_usuario");
                            repeatPassword.setText(gRepatContra);

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
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }
}