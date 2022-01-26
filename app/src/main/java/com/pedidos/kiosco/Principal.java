package com.pedidos.kiosco;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pedidos.kiosco.categorias.CatFragment;
import com.pedidos.kiosco.fragments.Categorias;
import com.pedidos.kiosco.fragments.Home;
import com.pedidos.kiosco.fragments.TicketDatos;
import com.pedidos.kiosco.fragments.Usuario;
import com.pedidos.kiosco.other.MiPersona;
import com.pedidos.kiosco.pay.AutFiscal;
import com.pedidos.kiosco.productos.ProdFragment;

import java.util.concurrent.ExecutionException;

public class Principal extends AppCompatActivity {

    Animation rotateOpen;
    Animation rotateClose;
    Animation fromBottom;
    Animation toBottom;
    Boolean clicked = false;
    FloatingActionButton addButton, list, product, user;

    public static int gIdEstadoCliente, gIdEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);

        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        addButton = findViewById(R.id.floatingActionButton);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onAddButtonClickListener();
                startActivity(new Intent(getApplicationContext(), AutFiscal.class));
            }
        });

        list = findViewById(R.id.floatingActionButton2);
        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_layout, new CatFragment());
                fr.commit();

            }
        });

        product = findViewById(R.id.floatingActionButton3);
        product.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_layout, new ProdFragment());
                fr.commit();

            }
        });

        user = findViewById(R.id.floatingActionButton4);
        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        if (Login.gIdCliente == 0) {

            try {
                new MiPersona(this).execute().get();

            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }

            if (MiPersona.exito) {
                ejecutarServicio("http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/actualizarClienteUsuario.php" + "?id_cliente=" + Login.gIdCliente + "&id_usuario=" + Login.gIdUsuario);
            }
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new Home()).commit();

    }

    private void onAddButtonClickListener() {
        setVisibility(clicked);
        setAnimation(clicked);
        clicked = !clicked;
    }

    private void setAnimation(Boolean clicked) {

        if (!clicked){
            list.startAnimation(fromBottom);
            product.startAnimation(fromBottom);
            user.startAnimation(fromBottom);
            addButton.startAnimation(rotateOpen);
        }
        else {
            list.startAnimation(toBottom);
            product.startAnimation(toBottom);
            user.startAnimation(toBottom);
            addButton.startAnimation(rotateClose);
        }
        
    }

    private void setVisibility(Boolean clicked) {

        if (!clicked){
            list.setVisibility(View.VISIBLE);
            product.setVisibility(View.VISIBLE);
            user.setVisibility(View.VISIBLE);
        }
        else {
            list.setVisibility(View.INVISIBLE);
            product.setVisibility(View.INVISIBLE);
            user.setVisibility(View.INVISIBLE);
        }

    }

    public void ejecutarServicio (String URL){
        ProgressDialog progressDialog = new ProgressDialog(Principal.this, R.style.Custom);
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

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = item -> {
        Fragment selectedFragment = null;

        if (item.getItemId() == R.id.nav_home) {
            selectedFragment = new Home();
        }

        if (item.getItemId() == R.id.nav_cat) {
            selectedFragment = new Categorias();
        }

        if (item.getItemId() == R.id.nav_user) {
            selectedFragment = new Usuario();
        }

        if (item.getItemId() == R.id.nav_shop) {
            selectedFragment = new TicketDatos();
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, selectedFragment).commit();

    return  true;
    };
}