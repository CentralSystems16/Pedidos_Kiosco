package com.pedidos.kiosco;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import android.app.ProgressDialog;
import android.os.Bundle;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.pedidos.kiosco.fragments.Categorias;
import com.pedidos.kiosco.fragments.Home;
import com.pedidos.kiosco.fragments.TicketDatos;
import com.pedidos.kiosco.fragments.Usuario;
import com.pedidos.kiosco.other.MiPersona;
import java.util.concurrent.ExecutionException;

public class Principal extends AppCompatActivity {

    public static int gIdEstadoCliente, gIdEstado;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);

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