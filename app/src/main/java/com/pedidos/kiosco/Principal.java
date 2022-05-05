package com.pedidos.kiosco;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pedidos.kiosco.categorias.CatFragment;
import com.pedidos.kiosco.desing.Clientes;
import com.pedidos.kiosco.fragments.Categorias;
import com.pedidos.kiosco.fragments.Home;
import com.pedidos.kiosco.fragments.ObtenerEstadoFiscal;
import com.pedidos.kiosco.fragments.TicketDatos;
import com.pedidos.kiosco.fragments.Usuario;
import com.pedidos.kiosco.gastos.ListarGastos;
import com.pedidos.kiosco.main.CorteCaja;
import com.pedidos.kiosco.productos.ProdFragment;
import com.pedidos.kiosco.reportes.BuscarReportes;
import com.pedidos.kiosco.usuarios.UsuarioFragment;
import com.pedidos.kiosco.utils.RecibirPDFReportes;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Principal extends AppCompatActivity {

    Animation rotateOpen;
    Animation rotateClose;
    Animation fromBottom;
    Animation toBottom;
    Boolean clicked = false;
    ExtendedFloatingActionButton list, product, user, fiscal, comprobante, reportes, gastos;
    public static FloatingActionButton addButton;
    public static ExtendedFloatingActionButton nombreConsumidor;
    public static BottomNavigationView bottomNavigationView;
    public static int gIdEstadoCliente, gIdEstado, maximo, actual;
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1000;

    /*private long startTime=10*60*1000; // 15 MINS IDLE TIME
    private final long interval = 1 * 1000;
    CountDownTimer countDownTimer;*/

    Date d = new Date();
    SimpleDateFormat fecc = new SimpleDateFormat("yyyy'-'MM'-'dd", Locale.getDefault());
    String fechacComplString = fecc.format(d);

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.principal);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        //countDownTimer = new MyCountDownTimer(startTime, interval);

        //DatosEmpresa();

        boolean granded = checkPermissionForReadExtertalStorage();
        if (!granded) {
            requestPermissionForReadExtertalStorage();
        }

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
            gastos.startAnimation(toBottom);
            addButton.startAnimation(rotateClose);

            list.setVisibility(View.GONE);
            product.setVisibility(View.GONE);
            user.setVisibility(View.GONE);
            fiscal.setVisibility(View.GONE);
            comprobante.setVisibility(View.GONE);
            reportes.setVisibility(View.GONE);
            gastos.setVisibility(View.GONE);

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
            gastos.startAnimation(toBottom);
            addButton.startAnimation(rotateClose);

            list.setVisibility(View.GONE);
            product.setVisibility(View.GONE);
            user.setVisibility(View.GONE);
            fiscal.setVisibility(View.GONE);
            comprobante.setVisibility(View.GONE);
            reportes.setVisibility(View.GONE);
            gastos.setVisibility(View.GONE);

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
            gastos.startAnimation(toBottom);
            addButton.startAnimation(rotateClose);

            list.setVisibility(View.GONE);
            product.setVisibility(View.GONE);
            user.setVisibility(View.GONE);
            fiscal.setVisibility(View.GONE);
            comprobante.setVisibility(View.GONE);
            reportes.setVisibility(View.GONE);
            gastos.setVisibility(View.GONE);

            setClicleable(clicked);

        });

        fiscal = findViewById(R.id.floatingActionButton5);
        fiscal.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));
        fiscal.setOnClickListener(view -> {
            FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new ObtenerEstadoFiscal());
            fr.commit();

            list.startAnimation(toBottom);
            product.startAnimation(toBottom);
            user.startAnimation(toBottom);
            fiscal.startAnimation(toBottom);
            comprobante.startAnimation(toBottom);
            reportes.startAnimation(toBottom);
            gastos.startAnimation(toBottom);
            addButton.startAnimation(rotateClose);

            list.setVisibility(View.GONE);
            product.setVisibility(View.GONE);
            user.setVisibility(View.GONE);
            fiscal.setVisibility(View.GONE);
            comprobante.setVisibility(View.GONE);
            reportes.setVisibility(View.GONE);
            gastos.setVisibility(View.GONE);

            setClicleable(clicked);

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
            gastos.startAnimation(toBottom);
            addButton.startAnimation(rotateClose);

            list.setVisibility(View.GONE);
            product.setVisibility(View.GONE);
            user.setVisibility(View.GONE);
            fiscal.setVisibility(View.GONE);
            comprobante.setVisibility(View.GONE);
            reportes.setVisibility(View.GONE);
            gastos.setVisibility(View.GONE);

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
            gastos.startAnimation(toBottom);
            addButton.startAnimation(rotateClose);

            list.setVisibility(View.GONE);
            product.setVisibility(View.GONE);
            user.setVisibility(View.GONE);
            fiscal.setVisibility(View.GONE);
            comprobante.setVisibility(View.GONE);
            reportes.setVisibility(View.GONE);
            gastos.setVisibility(View.GONE);

            setClicleable(clicked);

        });

        gastos = findViewById(R.id.floatingActionButton9);
        gastos.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));
        gastos.setOnClickListener(view -> {

            FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new ListarGastos());
            fr.commit();

            list.startAnimation(toBottom);
            product.startAnimation(toBottom);
            user.startAnimation(toBottom);
            fiscal.startAnimation(toBottom);
            comprobante.startAnimation(toBottom);
            reportes.startAnimation(toBottom);
            gastos.startAnimation(toBottom);
            addButton.startAnimation(rotateClose);

            list.setVisibility(View.GONE);
            product.setVisibility(View.GONE);
            user.setVisibility(View.GONE);
            fiscal.setVisibility(View.GONE);
            comprobante.setVisibility(View.GONE);
            reportes.setVisibility(View.GONE);
            gastos.setVisibility(View.GONE);

            setClicleable(clicked);

        });

        bottomNavigationView = findViewById(R.id.bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        bottomNavigationView.setBackgroundColor((Color.rgb(Splash.gRed, Splash.gGreen, Splash.gBlue)));

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_layout, new Login()).commit();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void onAddButtonClickListener() {
        setVisibility(clicked);
        setAnimation(clicked);
        setClicleable(clicked);
        clicked = !clicked;
    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = getApplicationContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExtertalStorage() {
        try {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setAnimation(Boolean clicked) {

        if (!clicked){
            list.startAnimation(fromBottom);
            product.startAnimation(fromBottom);
            user.startAnimation(fromBottom);
            fiscal.startAnimation(fromBottom);
            comprobante.startAnimation(fromBottom);
            reportes.startAnimation(fromBottom);
            gastos.setAnimation(fromBottom);
            addButton.startAnimation(rotateOpen);

        }

        else {
            list.startAnimation(toBottom);
            product.startAnimation(toBottom);
            user.startAnimation(toBottom);
            fiscal.startAnimation(toBottom);
            comprobante.startAnimation(toBottom);
            reportes.startAnimation(toBottom);
            gastos.startAnimation(toBottom);
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
            gastos.setVisibility(View.VISIBLE);

        }

        else {
            list.setVisibility(View.GONE);
            product.setVisibility(View.GONE);
            user.setVisibility(View.GONE);
            fiscal.setVisibility(View.GONE);
            comprobante.setVisibility(View.GONE);
            reportes.setVisibility(View.GONE);
            gastos.setVisibility(View.GONE);

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
            gastos.setClickable(true);

        }

        else {

            list.setClickable(false);
            product.setClickable(false);
            user.setClickable(false);
            fiscal.setClickable(false);
            comprobante.setClickable(false);
            reportes.setClickable(false);
            gastos.setClickable(false);

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

    /*public void DatosEmpresa(){

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
                                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                        FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
                                        fr.replace(R.id.fragment_layout, new Login());
                                        fr.commit();
                                    })
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
    }*/

    /*@Override
    public void onUserInteraction(){

        super.onUserInteraction();

        //Reset the timer on user interaction...
        countDownTimer.cancel();
        countDownTimer.start();
    }*/

    /*public class MyCountDownTimer extends CountDownTimer {
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
    }*/

    /*public void ejecutarServicio2 (String URL){
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
    }*/

    /*@Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            Toast.makeText(getApplicationContext(), "Finalizo", Toast.LENGTH_SHORT).show();
            ejecutarServicio2("http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/actualizarDisponibilidad.php" + "?base=" + VariablesGlobales.dataBase);
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        DatosEmpresa();
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (isFinishing()) {
            Toast.makeText(getApplicationContext(), "Finalizo", Toast.LENGTH_SHORT).show();
        }
    }*/

    protected void onPause() {
        super.onPause();
        ejecutarServicio("http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/actualizarSesiones.php"
                + "?base=" + VariablesGlobales.dataBase
                + "&egreso=" + fechacComplString
                + "&id_usuario=" + Login.gIdUsuario);
    }
}