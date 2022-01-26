package com.pedidos.kiosco.categorias;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;

public class ModificarCategorias extends AppCompatActivity {

    Button btnGuardar, btnSubir, activo, inactivo;
    ImageView imgCambio;
    @SuppressLint("StaticFieldLeak")
    EditText modifNomCat;
    public static int gEstadoAct = 1;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modificar_categorias);

        modifNomCat = findViewById(R.id.etModifCat);
        modifNomCat.setText(CatFragment.gNombreCat);

        imgCambio = findViewById(R.id.imgCambio);
        Glide.with(this).load(CatFragment.gImagen).into(imgCambio);

        activo = findViewById(R.id.btnActivo);
        activo.setEnabled(false);
        activo.setOnClickListener(v -> {

            gEstadoAct = 1;
            activo.setEnabled(false);
            inactivo.setEnabled(true);
            Toast.makeText(ModificarCategorias.this, "Categoría activada nuevamente", Toast.LENGTH_SHORT).show();
        });

        inactivo = findViewById(R.id.btnInactivo);
        inactivo.setOnClickListener(v -> {

            gEstadoAct = 0;
            activo.setEnabled(true);
            inactivo.setEnabled(false);
            Toast.makeText(ModificarCategorias.this, "La categoría a sido desactivada", Toast.LENGTH_SHORT).show();
        });


        btnGuardar = findViewById(R.id.btnGuardar);
        btnGuardar.setOnClickListener(v -> {
            String url = "http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/ActualizarCategoria.php"
                    + "?nombre_categoria=" + modifNomCat.getText().toString()
                    + "&estado_categoria=" + gEstadoAct
                    + "&id_categoria=" + CatFragment.gIdCategoria;
            ejecutarServicio(url);

            Intent i = new Intent(this, Principal.class);
            startActivity(i);

        });

        btnSubir = findViewById(R.id.cargarImagen);
        btnSubir.setOnClickListener(v -> Toast.makeText(this, "Temporalmente desabilitada", Toast.LENGTH_SHORT).show());
    }



    public void ejecutarServicio (String URL){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> Toast.makeText(getApplicationContext(), "CATEGORÍA ACTUALIZADA CON ÉXITO", Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show());
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}