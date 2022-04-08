package com.pedidos.kiosco.desing;

import static com.pedidos.kiosco.categorias.CatFragment.MY_DEFAULT_TIMEOUT;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.z.Login;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Administrador extends DialogFragment {

    String gPasswordEmpresa;
    EditText pass;
    Button passCheck, retur;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View vista = inflater.inflate(R.layout.dialog_fragment_pass, container,false);
        vista.setFocusableInTouchMode(false);

        vista.setFocusableInTouchMode(false);

        DatosEmpresa();

        pass = vista.findViewById(R.id.etPassword);
        passCheck = vista.findViewById(R.id.btnContPass);
        passCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pass.getText().toString().equals(gPasswordEmpresa)){
                    ejecutarServicio("http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/actualizarUsuario.php"
                            + "?base=" + VariablesGlobales.dataBase
                            + "&id_usuario=" + Login.gIdUsuario);
                    ejecutarServicio("http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/actualizarDisponibilidad.php" + "?base=" + VariablesGlobales.dataBase);
                    startActivity(new Intent(getContext(), Login.class));
                } else {
                    Toast.makeText(getContext(), "Contraseña incorrecta, por favor intentelo nuevamente.", Toast.LENGTH_SHORT).show();
                }
            }
        });

        retur = vista.findViewById(R.id.btnReturnPass);
        retur.setOnClickListener(view -> getDialog().dismiss());

        return vista;
    }

    public void DatosEmpresa(){

        String URL = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerEmpresa.php" + "?base=" + VariablesGlobales.dataBase;
        System.out.println(URL);
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest request = new StringRequest(Request.Method.POST, URL,

                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Empresa");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            gPasswordEmpresa = jsonObject1.getString("password");

                        }

                        System.out.println("Contraseña: " + gPasswordEmpresa);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, volleyError -> {});

        request.setRetryPolicy(new DefaultRetryPolicy(
                MY_DEFAULT_TIMEOUT * 2,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);
    }

    public void ejecutarServicio (String URL){

        Toast toast = Toast.makeText(getContext(), "¡AHORA ES ADMINISTRADOR!", Toast.LENGTH_LONG);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    toast.setGravity(Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 110);
                    toast.show();

                },
                volleyError -> {
                    String message = null;
                    if (volleyError instanceof NetworkError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof ServerError) {
                        message = "Parece que se ha perdido la conexion a internet";
                    } else if (volleyError instanceof AuthFailureError) {
                        message = "Cannot connect to Internet...Please check your connection!";
                    } else if (volleyError instanceof ParseError) {
                        message = "Parsing error! Please try again after some time!!";
                    } else if (volleyError instanceof TimeoutError) {
                        message = "Connection TimeOut! Please check your internet connection.";
                    }
                    Toast.makeText(getContext(), "" + message, Toast.LENGTH_LONG).show();
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

}
