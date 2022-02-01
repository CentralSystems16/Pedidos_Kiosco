package com.pedidos.kiosco;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gImagenSplah;
import static com.pedidos.kiosco.Splash.gRed;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

public class Login extends AppCompatActivity {

    public static int gIdCliente, cargo, gIdUsuario, gVerificacion, gIdCategoria,
            gIdPedido, gIdFacDetPedido, gIdSucursal, gIdMovimiento, gIdDetMovimiento,
            gIdPedidoReporte, gIdClienteReporte, gIdAutFiscal;

    public static String nombre, email, repeatContra, usuario, contra;

    EditText user, password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

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
                                gIdCliente = jsonResponse.getInt("id_cliente");
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