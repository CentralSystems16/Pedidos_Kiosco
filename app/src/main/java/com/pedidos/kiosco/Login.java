package com.pedidos.kiosco;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gImagenSplah;
import static com.pedidos.kiosco.Splash.gRed;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.pedidos.kiosco.desing.acercaDe;
import com.pedidos.kiosco.fragments.Home;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

public class Login extends Fragment {

    public static int gIdCliente = 1, cargo, gIdUsuario, gVerificacion, gIdCategoria,
            gIdPedido, gIdFacDetPedido, gIdSucursal, gIdMovimiento, gIdDetMovimiento,
            gIdPedidoReporte, gIdAutFiscal, idUser;

    public static String nombre, email, repeatContra, usuario, contra;

    ArrayList arrayList = new ArrayList();

    EditText user, password;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    Date d = new Date();
    SimpleDateFormat fecc = new SimpleDateFormat("yyyy'-'MM'-'dd", Locale.getDefault());
    String fechacComplString = fecc.format(d);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_login, container, false);

        Principal.bottomNavigationView.setVisibility(View.GONE);
        Principal.nombreConsumidor.setVisibility(View.GONE);

        ImageButton info = vista.findViewById(R.id.info);
        info.setOnClickListener(view -> startActivity(new Intent(getContext(), acercaDe.class)));

        Button btnEntrar = vista.findViewById(R.id.btnEntrar);

        HashMap<String, Object> defaultsRate = new HashMap<>();
        defaultsRate.put("nueva_version_disponible", String.valueOf(getVersionCode()));

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10)
                .build();

        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(defaultsRate);

        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                final String new_version_code = mFirebaseRemoteConfig.getString("nueva_version_disponible");
                System.out.println("Version en firebase: " + new_version_code);
                if(Integer.parseInt(new_version_code) > getVersionCode())
                    mostrarDialogo();
            }
            else Log.e("MYLOG", "mFirebaseRemoteConfig.fetchAndActivate() NOT Successful");

        });

        obtenerSesiones();

        LinearLayout linearLayout = vista.findViewById(R.id.linearLogin);
        linearLayout.setBackgroundColor(Color.rgb(244, 57, 44));

        LinearLayout linearLayout2 = vista.findViewById(R.id.linearxd);
        linearLayout2.setBackgroundColor(Color.rgb(244, 57, 44));

        ImageView logoLogin = vista.findViewById(R.id.imageViewLogin);
        TextView txtBinevenido = vista.findViewById(R.id.tctBienvenido);

        Glide.with(requireContext()).load(gImagenSplah).into(logoLogin);
        txtBinevenido.setTextColor(Color.rgb(gRed, gGreen, gBlue));

        user = vista.findViewById(R.id.user);
        password = vista.findViewById(R.id.pass);

        recuperarDatos();

        TextView btnRegistrar = vista.findViewById(R.id.crearCuenta);
        btnRegistrar.setOnClickListener(v -> {
            Intent i = new Intent(getContext(), Registro.class);
            startActivity(i);
        });

        btnEntrar.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));

        btnEntrar.setOnClickListener(v -> {

            usuario = user.getText().toString();
            contra = password.getText().toString();

            if (usuario.isEmpty() || contra.isEmpty()) {
                user.setError("Campos vacíos");

            } else {

                Response.Listener<String> responseListener = response -> {

                    try {

                        JSONObject jsonResponse = new JSONObject(response);
                        boolean succes = jsonResponse.getBoolean("success");

                        if (succes) {
                            guardarPreferencias();
                            gIdUsuario = jsonResponse.getInt("id_usuario");
                            nombre = jsonResponse.getString("nombre_usuario");
                            email = jsonResponse.getString("email_usuario");
                            repeatContra = jsonResponse.getString("password_repeat_usuario");
                            cargo = jsonResponse.getInt("cargo_usuario");
                            gVerificacion = jsonResponse.getInt("verificacion_usuario");
                            gIdSucursal = jsonResponse.getInt("id_sucursal");

                            if (idUser != gIdUsuario){
                                ejecutarServicio("http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/registroSesion.php"
                                        + "?base=" + VariablesGlobales.dataBase
                                        + "&ingreso=" + fechacComplString
                                        + "&egreso=" + "null"
                                        + "&id_usuario=" + gIdUsuario);
                            }
                            else {
                                ejecutarServicio("http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/actualizarIngreso.php"
                                        + "?base=" + VariablesGlobales.dataBase
                                        + "&ingreso=" + fechacComplString
                                        + "&id_usuario=" + gIdUsuario);
                            }

                            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                            fr.replace(R.id.fragment_layout, new Home());
                            fr.commit();

                        } else {
                            Toast.makeText(getContext(), "Usuario y/o contraseña incorrectos o usuario inactivo y/o sin permisos, por favor intentalo nuevamente.", Toast.LENGTH_LONG).show();
                             }

                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Ocurrió un error inesperado, intentalo de nuevo más tarde, Error: " + e.getMessage(), Toast.LENGTH_LONG).show();

                    }
                };

                LoginRequest loginRequest = new LoginRequest(usuario, contra, responseListener);
                RequestQueue queue = Volley.newRequestQueue(requireActivity());
                queue.add(loginRequest);
            }
        });

        return vista;
    }

    public void ejecutarServicio (String URL){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                },
                volleyError -> {
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

    private void mostrarDialogo() {

        startActivity(new Intent(getContext(), UpdateApp.class));

    }

    private PackageInfo pInfo;
    public int getVersionCode() {
        pInfo = null;
        try {
            pInfo = getContext().getPackageManager().getPackageInfo(getContext().getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("MYLOG", "NameNotFoundException: "+e.getMessage());
        }
        return pInfo.versionCode;
    }

    private void guardarPreferencias(){
        SharedPreferences preferences = getContext().getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  preferences.edit();
        editor.putString("login_usuario", usuario);
        editor.putString("password_usuarios", contra);
        editor.putBoolean("sesion", true);
        editor.apply();
    }

    private void recuperarDatos(){
        SharedPreferences preferences = getContext().getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        user.setText(preferences.getString("login_usuario", ""));
        password.setText(preferences.getString("password_usuarios", ""));
    }

    public void obtenerSesiones() {

        String url = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerSesiones.php" + "?base=" + VariablesGlobales.dataBase;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Sesiones");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            idUser = jsonObject1.getInt("id_usuario");

                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }
                }, volleyError -> {
            Toast.makeText(getContext(), "Ocurrio un error inesperado, Error: " + volleyError, Toast.LENGTH_SHORT).show();

        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }

}