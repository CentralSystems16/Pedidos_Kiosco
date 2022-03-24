package com.pedidos.kiosco;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gImagenSplah;
import static com.pedidos.kiosco.Splash.gRed;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    public static int gIdCliente = 1, cargo, gIdUsuario, gVerificacion, gIdCategoria,
            gIdPedido, gIdFacDetPedido, gIdSucursal, gIdMovimiento, gIdDetMovimiento,
            gIdPedidoReporte, gIdAutFiscal;

    public static String nombre, email, repeatContra, usuario, contra;

    EditText user, password;

    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        HashMap<String, Object> defaultsRate = new HashMap<>();
        defaultsRate.put("new_version_code", String.valueOf(getVersionCode()));

        mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
        FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                .setMinimumFetchIntervalInSeconds(10)
                .build();

        mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);
        mFirebaseRemoteConfig.setDefaultsAsync(defaultsRate);

        mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                final String new_version_code = mFirebaseRemoteConfig.getString("nueva_version_disponible");

                if(Integer.parseInt(new_version_code) > getVersionCode())
                    showTheDialog();
            }
            else Log.e("MYLOG", "mFirebaseRemoteConfig.fetchAndActivate() NOT Successful");

        });

        LinearLayout linearLayout = findViewById(R.id.linearLogin);
        linearLayout.setBackgroundColor(Color.rgb(244, 57, 44));

        ImageView logoLogin = findViewById(R.id.imageViewLogin);
        TextView txtBinevenido = findViewById(R.id.tctBienvenido);

        Glide.with(Login.this).load(gImagenSplah).into(logoLogin);
        txtBinevenido.setTextColor(Color.rgb(gRed, gGreen, gBlue));

        user = findViewById(R.id.user);
        password = findViewById(R.id.pass);

        recuperarDatos();

        TextView btnRegistrar = findViewById(R.id.crearCuenta);
        btnRegistrar.setOnClickListener(v -> {
            Intent i = new Intent(getApplicationContext(), Registro.class);
            startActivity(i);
        });

        Button btnEntrar = findViewById(R.id.btnEntrar);
        btnEntrar.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));

        btnEntrar.setOnClickListener(v -> {

                ProgressDialog progressDialog = new ProgressDialog(Login.this, R.style.Custom);
                progressDialog.setMessage("Por favor, espera...");
                progressDialog.setCancelable(false);
                progressDialog.show();

                usuario = user.getText().toString();
                contra = password.getText().toString();

                if (usuario.isEmpty() || contra.isEmpty()) {
                    user.setError("Campos vacíos");
                    progressDialog.dismiss();
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

                                startActivity(new Intent(getApplicationContext(), Principal.class));

                                progressDialog.dismiss();

                            } else {
                                Toast.makeText(Login.this, "Usuario y/o contraseña incorrectos o usuario inactivo y/o sin permisos, por favor intentalo nuevamente.", Toast.LENGTH_LONG).show();
                                progressDialog.dismiss();
                            }

                        } catch (JSONException e) {
                            Toast.makeText(getApplicationContext(), "Ocurrió un error inesperado, intentalo de nuevo más tarde, Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                            progressDialog.dismiss();
                        }
                    };

                    LoginRequest loginRequest = new LoginRequest(usuario, contra, responseListener);
                    RequestQueue queue = Volley.newRequestQueue(Login.this);
                    queue.add(loginRequest);
                }
        });

    }

    private void showTheDialog(){
        final AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle("ACTUALIZACIÓN")
                .setMessage("¡Hay una nueva versión disponible, por favor actualiza tu app a la última versión para recibir nuevas y mejores funcionalidades!")
                .setPositiveButton("ACTUALIZAR", null)
                .show();

        dialog.setCancelable(false);

        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
        positiveButton.setOnClickListener(v -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("market://details?id=" + "com.pedidos.kiosco")));
            }
            catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://play.google.com/store/apps/details?id=" + "com.pedidos.kiosco")));
            }
        });
    }

    private PackageInfo pInfo;
    public int getVersionCode() {
        pInfo = null;
        try {
            pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            Log.i("MYLOG", "NameNotFoundException: "+e.getMessage());
        }
        return pInfo.versionCode;
    }

    private void guardarPreferencias(){
        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor =  preferences.edit();
        editor.putString("login_usuario", usuario);
        editor.putString("password_usuarios", contra);
        editor.putBoolean("sesion", true);
        editor.apply();
    }

    private void recuperarDatos(){
        SharedPreferences preferences = getSharedPreferences("preferenciasLogin", Context.MODE_PRIVATE);
        user.setText(preferences.getString("login_usuario", ""));
        password.setText(preferences.getString("password_usuarios", ""));
    }

    @Override
    public void onBackPressed() {

    }

}