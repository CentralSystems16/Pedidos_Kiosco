package com.pedidos.kiosco.fragments;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.Login;

public class ModificarCliente extends Fragment {

    EditText editNumero, editNombre, editPass, editEmail;
    Button btnModificar, btnCancelarCliente;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.modificar_cliente_fragment, container, false);

        editNumero = vista.findViewById(R.id.obtenerEditarNumero);
        editNumero.setText(Cliente.numero);

        editNombre = vista.findViewById(R.id.obtenerEditarCliente);
        editNombre.setText(Cliente.nombre);

        editPass = vista.findViewById(R.id.obtenerEditarDireccion);
        editPass.setText(Cliente.direccion);

        editEmail = vista.findViewById(R.id.obtenerEditarDui);
        editEmail.setText(Cliente.dui);

        btnModificar = vista.findViewById(R.id.editarCliente);
        btnModificar.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));
        btnModificar.setOnClickListener(v -> ejecutar());

        btnCancelarCliente = vista.findViewById(R.id.btnCancelarCliente);
        btnCancelarCliente.setOnClickListener(view -> {
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new Cliente());
            fr.commit();
        });

        return vista;
    }

    private void ejecutar(){

        String url = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/modificarCliente.php"
                + "?base=" + VariablesGlobales.dataBase
                + "&nombre_cliente=" + editNombre.getText().toString()
                + "&numero_cliente=" + editNumero.getText().toString()
                + "&dui_cliente=" + editEmail.getText().toString()
                + "&direccion_cliente=" + editPass.getText().toString()
                + "&id_cliente=" + Login.gIdCliente;

        ejecutarServicio(url);



    }

    public void ejecutarServicio (String URL){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                    fr.replace(R.id.fragment_layout, new Cliente());
                    fr.commit();},
                error -> {
                    {}
                });
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        requestQueue.add(stringRequest);
    }

}