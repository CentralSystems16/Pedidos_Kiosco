package com.pedidos.kiosco.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.categorias.CatFragment;

public class ModificarCategorias extends Fragment {

    Button btnGuardar, btnSubir, activo, inactivo;
    ImageView imgCambio;
    @SuppressLint("StaticFieldLeak")
    EditText modifNomCat;
    public static int gEstadoAct = 1;
    RequestQueue requestQueue;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.modificar_categorias_fragment, container, false);

        modifNomCat = vista.findViewById(R.id.etModifCat);
        modifNomCat.setText(CatFragment.gNombreCat);

        imgCambio = vista.findViewById(R.id.imgCambio);
        Glide.with(this).load(CatFragment.gImagen).into(imgCambio);

        activo = vista.findViewById(R.id.btnActivo);
        activo.setEnabled(false);
        activo.setOnClickListener(v -> {

            gEstadoAct = 1;
            activo.setEnabled(false);
            inactivo.setEnabled(true);
            Toast.makeText(getContext(), "Categoría activada nuevamente", Toast.LENGTH_SHORT).show();
        });

        inactivo = vista.findViewById(R.id.btnInactivo);
        inactivo.setOnClickListener(v -> {

            gEstadoAct = 0;
            activo.setEnabled(true);
            inactivo.setEnabled(false);
            Toast.makeText(getContext(), "La categoría a sido desactivada", Toast.LENGTH_SHORT).show();
        });


        btnGuardar = vista.findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(v -> {
            String url = "http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/ActualizarCategoria.php"
                    + "?nombre_categoria=" + modifNomCat.getText().toString()
                    + "&estado_categoria=" + gEstadoAct
                    + "&id_categoria=" + CatFragment.gIdCategoria;
            ejecutarServicio(url);

            Intent i = new Intent(getContext(), Principal.class);
            startActivity(i);

        });

        btnSubir = vista.findViewById(R.id.cargarImagen);
        btnSubir.setOnClickListener(v -> Toast.makeText(getContext(), "Temporalmente desabilitada", Toast.LENGTH_SHORT).show());

        return vista;
    }

    public void ejecutarServicio (String URL){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> Toast.makeText(getContext(), "CATEGORÍA ACTUALIZADA CON ÉXITO", Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show());
        requestQueue = Volley.newRequestQueue(requireActivity());
        requestQueue.add(stringRequest);
    }

}