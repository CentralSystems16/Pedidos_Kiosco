package com.pedidos.kiosco;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;
import static com.pedidos.kiosco.categorias.CatFragment.MY_DEFAULT_TIMEOUT;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pedidos.kiosco.desing.Clientes;
import com.pedidos.kiosco.reportes.BuscarReportes;
import com.pedidos.kiosco.categorias.CatFragment;
import com.pedidos.kiosco.fragments.Categorias;
import com.pedidos.kiosco.fragments.Home;
import com.pedidos.kiosco.fragments.TicketDatos;
import com.pedidos.kiosco.fragments.Usuario;
import com.pedidos.kiosco.main.CorteCaja;
import com.pedidos.kiosco.main.ObtenerEstadoFiscal;
import com.pedidos.kiosco.productos.ProdFragment;
import com.pedidos.kiosco.usuarios.UsuarioFragment;
import com.pedidos.kiosco.z.Login;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Principal extends AppCompatActivity {

    Animation rotateOpen;
    Animation rotateClose;
    Animation fromBottom;
    Animation toBottom;
    Boolean clicked = false;
    ExtendedFloatingActionButton list, product, user, fiscal, comprobante, reportes;
    FloatingActionButton addButton;
    public static ExtendedFloatingActionButton nombreConsumidor;
    public static BottomNavigationView bottomNavigationView;
    public static int gIdEstadoCliente, gIdEstado, maximo, actual;

    private long startTime=1*60*1000; // 15 MINS IDLE TIME
    private final long interval = 1 * 1000;
    CountDownTimer countDownTimer;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        countDownTimer = new MyCountDownTimer(startTime, interval);

        DatosEmpresa();

        rotateOpen = AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim);
        rotateClose = AnimationUtils.loadAnimation(this, R.anim.rotate_close_anim);
        fromBottom = AnimationUtils.loadAnimation(this, R.anim.from_bottom_anim);
        toBottom = AnimationUtils.loadAnimation(this, R.anim.to_bottom_anim);

        addButton = findViewById(R.id.floatingActionButton);
        addButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));
        if(Login.cargo == 1 || Login.cargo == 2){
            addButton.setVisibility(View.VISIBLE);
        }

        nombreConsumidor = findViewById(R.id.floatingActionButton8);
        nombreConsumidor.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));
        nombreConsumidor.setOnClickListener(view -> {
            Clientes myDialogFragment = new Clientes();
            myDialogFragment.show(getSupportFragmentManager(), "MyFragment");
            myDialogFragment.setCancelable(false);
        });

        addButton.setOnClickListener(view -> onAddButtonClickListener());

        list = findViewById(R.id.floatingActionButton2);
        list.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));
        list.setOnClickListener(view -> {

            FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new CatFragment());
            fr.commit();

            list.startAnimation(toBottom);
            product.startAnimation(toBottom);
            user.startAnimation(toBottom);
            fiscal.startAnimation(toBottom);
            comprobante.startAnimation(toBottom);
            reportes.startAnimation(toBottom);
            addButton.startAnimation(rotateClose);

            list.setVisibility(View.GONE);
            product.setVisibility(View.GONE);
            user.setVisibility(View.GONE);
            fiscal.setVisibility(View.GONE);
            comprobante.setVisibility(View.GONE);
            reportes.setVisibility(View.GONE);

            setClicleable(clicked);

        });

        product = findViewById(R.id.floatingActionButton3);
        product.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));
        product.setOnClickListener(view -> {

            FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new ProdFragment());
            fr.commit();

            list.startAnimation(toBottom);
            product.startAnimation(toBottom);
            user.startAnimation(toBottom);
            fiscal.startAnimation(toBottom);
            comprobante.startAnimation(toBottom);
            reportes.startAnimation(toBottom);
            addButton.startAnimation(rotateClose);

            list.setVisibility(View.GONE);
            product.setVisibility(View.GONE);
            user.setVisibility(View.GONE);
            fiscal.setVisibility(View.GONE);
            comprobante.setVisibility(View.GONE);
            reportes.setVisibility(View.GONE);

            setClicleable(clicked);

        });

        user = findViewById(R.id.floatingActionButton4);
        user.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));
        user.setOnClickListener(view -> {

            FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new UsuarioFragment());
            fr.commit();

            list.startAnimation(toBottom);
            product.startAnimation(toBottom);
            user.startAnimation(toBottom);
            fiscal.startAnimation(toBottom);
            comprobante.startAnimation(toBottom);
            reportes.startAnimation(toBottom);
            addButton.startAnimation(rotateClose);

            list.setVisibility(View.GONE);
            product.setVisibility(View.GONE);
            user.setVisibility(View.GONE);
            fiscal.setVisibility(View.GONE);
            comprobante.setVisibility(View.GONE);
            reportes.setVisibility(View.GONE);

            setClicleable(clicked);

        });

        fiscal = findViewById(R.id.floatingActionButton5);
        fiscal.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));
        fiscal.setOnClickListener(view -> {
            startActivity(new Intent(getApplicationContext(), ObtenerEstadoFiscal.class));
        });

        comprobante = findViewById(R.id.floatingActionButton6);
        comprobante.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));
        comprobante.setOnClickListener(view -> {

            FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new CorteCaja());
            fr.commit();

            list.startAnimation(toBottom);
            product.startAnimation(toBottom);
            user.startAnimation(toBottom);
            fiscal.startAnimation(toBottom);
            comprobante.startAnimation(toBottom);
            reportes.startAnimation(toBottom);
            addButton.startAnimation(rotateClose);

            list.setVisibility(View.GONE);
            product.setVisibility(View.GONE);
            user.setVisibility(View.GONE);
            fiscal.setVisibility(View.GONE);
            comprobante.setVisibility(View.GONE);
            reportes.setVisibility(View.GONE);

            setClicleable(clicked);

        });

        reportes = findViewById(R.id.floatingActionButton7);
        reportes.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));
        reportes.setOnClickListener(view -> {

            FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new BuscarReportes());
            fr.commit();

            list.startAnimation(toBottom);
            product.startAnimation(toBottom);
            user.startAnimation(toBottom);
            fiscal.startAnimation(toBottom);
            comprobante.startAnimation(toBottom);
            reportes.startAnimation(toBottom);
            addButton.startAnimation(rotateClose);

            list.setVisibility(View.GONE);
            product.setVisibility(View.GONE);
            user.setVisibility(View.GONE);
            fiscal.setVisibility(View.GONE);
            comprobante.setVisibility(View.GONE);
            reportes.setVisibility(View.GONE);

            setClicleable(clicked);

        });

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setBackgroundColor((Color.rgb(Splash.gRed, Splash.gGreen, Splash.gBlue)));

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new com.pedidos.kiosco.z.Login()).commit();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onAddButtonClickListener() {
        setVisibility(clicked);
        setAnimation(clicked);
        setClicleable(clicked);
        clicked = !clicked;
    }

    private void setAnimation(Boolean clicked) {

        if (!clicked){
            list.startAnimation(fromBottom);
            product.startAnimation(fromBottom);
            user.startAnimation(fromBottom);
            fiscal.startAnimation(fromBottom);
            comprobante.startAnimation(fromBottom);
            reportes.startAnimation(fromBottom);
            addButton.startAnimation(rotateOpen);

        }

        else {
            list.startAnimation(toBottom);
            product.startAnimation(toBottom);
            user.startAnimation(toBottom);
            fiscal.startAnimation(toBottom);
            comprobante.startAnimation(toBottom);
            reportes.startAnimation(toBottom);
            addButton.startAnimation(rotateClose);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setVisibility(Boolean clicked) {

        if (!clicked){
            list.setVisibility(View.VISIBLE);
            product.setVisibility(View.VISIBLE);
            user.setVisibility(View.VISIBLE);
            fiscal.setVisibility(View.VISIBLE);
            comprobante.setVisibility(View.VISIBLE);
            reportes.setVisibility(View.VISIBLE);

        }

        else {
            list.setVisibility(View.GONE);
            product.setVisibility(View.GONE);
            user.setVisibility(View.GONE);
            fiscal.setVisibility(View.GONE);
            comprobante.setVisibility(View.GONE);
            reportes.setVisibility(View.GONE);

        }
    }

    public void setClicleable(Boolean clicked){

        if (!clicked){
            list.setClickable(true);
            product.setClickable(true);
            user.setClickable(true);
            fiscal.setClickable(true);
            comprobante.setClickable(true);
            reportes.setClickable(true);

        }

        else {

            list.setClickable(false);
            product.setClickable(false);
            user.setClickable(false);
            fiscal.setClickable(false);
            comprobante.setClickable(false);
            reportes.setClickable(false);

        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Toast.makeText(getApplicationContext(), "onRestar", Toast.LENGTH_SHORT).show();
        DatosEmpresa();
    }



    public void DatosEmpresa(){

        String URL = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerEmpresa.php" + "?base=" + VariablesGlobales.dataBase;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, URL,

                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Empresa");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            maximo = jsonObject1.getInt("maximo");
                            actual  = jsonObject1.getInt("actual");

                        }


                        if (actual == maximo){
                            new AlertDialog.Builder(Principal.this)
                                    .setTitle("Limite de usuarios")
                                    .setMessage("El limite de usuarios permitidos esta completo. Para poder ingresar cierre sesion en otro dispositivo")
                                    .setPositiveButton(android.R.string.yes, (dialog, which) -> startActivity(new Intent(getApplicationContext(), Login.class)))
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .show();
                        }

                        else {
                            ejecutarServicio("http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/actualizarDisponibles.php" + "?base=" + VariablesGlobales.dataBase);

                        }

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

    @Override
    public void onUserInteraction(){

        super.onUserInteraction();

        //Reset the timer on user interaction...
        countDownTimer.cancel();
        countDownTimer.start();
    }

    public class MyCountDownTimer extends CountDownTimer {
        public MyCountDownTimer(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            //DO WHATEVER YOU WANT HERE
            // CIERRA LA APP MATANDO EL PROCESO Y VUELVE A ABRIRLO.
            ejecutarServicio2("http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/actualizarDisponibilidad.php" + "?base=" + VariablesGlobales.dataBase);

        }

        @Override
        public void onTick(long millisUntilFinished) {
        }
    }

    public void ejecutarServicio (String URL){
        ProgressDialog progressDialog = new ProgressDialog(Principal.this, R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {

            progressDialog.dismiss();
                },
                volleyError -> progressDialog.dismiss()
        );

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void ejecutarServicio2 (String URL){
        Toast.makeText(getApplicationContext(), "Entro al metodo", Toast.LENGTH_SHORT).show();
        ProgressDialog progressDialog = new ProgressDialog(Principal.this, R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    Toast.makeText(getApplicationContext(), "Se ejecuto el metodo", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                },
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

    @Override
    public void onBackPressed() {

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            Toast.makeText(getApplicationContext(), "Finalizo", Toast.LENGTH_SHORT).show();
            ejecutarServicio2("http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/actualizarDisponibilidad.php" + "?base=" + VariablesGlobales.dataBase);
        }
    }


}