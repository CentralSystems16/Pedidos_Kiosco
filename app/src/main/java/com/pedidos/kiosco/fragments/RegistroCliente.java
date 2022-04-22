package com.pedidos.kiosco.fragments;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.Login;

import java.util.HashMap;
import java.util.Map;

public class RegistroCliente extends Fragment {

    String usuario;
    EditText regPhoneNo, pas, nom, em;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_registro_cliente, container, false);

        RelativeLayout relativeLayout = vista.findViewById(R.id.linear2);
        relativeLayout.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));

        Button cancelar = vista.findViewById(R.id.btnCancelar);
        cancelar.setOnClickListener(view -> {
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new Cliente());
            fr.commit();
        });

        Button botonRegistrar = vista.findViewById(R.id.btnRegistroCliente);
        botonRegistrar.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));
        regPhoneNo = vista.findViewById(R.id.telefonoCliente);
        pas = vista.findViewById(R.id.direccion);
        nom = vista.findViewById(R.id.nombreCliente3);
        em = vista.findViewById(R.id.dui);

        botonRegistrar.setOnClickListener(v -> {

            String nombre = nom.getText().toString();
            usuario = regPhoneNo.getText().toString();
            String password = pas.getText().toString();
            String dui = em.getText().toString();

            if (nombre.isEmpty()) {
                nom.setError("Agregue su nombre");
                botonRegistrar.setError("Agregue su nombre");
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(nom.getWindowToken(), 0);
            }

            else if (password.isEmpty()) {
                pas.setError("Agregue una dirección.");
                botonRegistrar.setError("Agregue una contraseña");
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pas.getWindowToken(), 0);
            }

            else if (dui.isEmpty()) {
                pas.setError("Agregue su dui.");
                InputMethodManager imm = (InputMethodManager) requireContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(pas.getWindowToken(), 0);
            }

            else {

                ejecutarServicio();

            }
        });

        return vista;

    }

    public void ejecutarServicio() {

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest request = new StringRequest(Request.Method.POST, "http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/registroCliente.php" + "?base=" + VariablesGlobales.dataBase,
                response -> {

                    if(response.equalsIgnoreCase("Usuario registrado")){

                        FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                        fr.replace(R.id.fragment_layout, new Cliente());
                        fr.commit();
                        progressDialog.dismiss();

                    }
                    else{
                        Toast.makeText(getContext(), response, Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }

                }, volleyError -> {
            Toast.makeText(getContext(), "Ocurrió un error inesperado, Error: " + volleyError, Toast.LENGTH_SHORT).show();
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

                params.put("telefono_cliente", usuario);
                params.put("nombre_cliente", nombre);
                params.put("direccion_cliente", password);
                params.put("dui_cliente", email);
                params.put("id_usuario", String.valueOf(Login.gIdUsuario));

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(request);

    }

}