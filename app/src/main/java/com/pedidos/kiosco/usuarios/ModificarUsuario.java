package com.pedidos.kiosco.usuarios;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;

public class ModificarUsuario extends AppCompatActivity {

    EditText editUsuario, editNombre, editPass, editRepeatPass, editEmail, editDui, editEdad;
    RequestQueue requestQueue;
    TextView editFechaNac;
    Button modificarUsuario, btnActivoUser, btnInactivoUser, btnNotSi, btnNotiNo;
    public static int gEstadoUs = 1, gNotiUs = 1;

    @SuppressLint("WrongViewCast")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modificar_usuario);

        btnNotiNo = findViewById(R.id.btnNoUsuario);
        btnNotSi = findViewById(R.id.btnSiUsuario);

        btnNotiNo.setOnClickListener(v -> {

            gNotiUs = 0;
            btnNotSi.setEnabled(true);
            btnNotiNo.setEnabled(false);
            Toast.makeText(ModificarUsuario.this, "Este usuario dejará de recibir notificaciones", Toast.LENGTH_SHORT).show();

        });

        btnNotSi.setEnabled(false);
        btnNotSi.setOnClickListener(v -> {

         gNotiUs = 1;
         btnNotSi.setEnabled(false);
         btnNotiNo.setEnabled(true);
         Toast.makeText(ModificarUsuario.this, "Este usuario recibirá notificaciones", Toast.LENGTH_SHORT).show();

        });

        btnActivoUser = findViewById(R.id.btnActivoUsuario);
        btnActivoUser.setEnabled(false);
        btnActivoUser.setOnClickListener(v -> {

            gEstadoUs = 1;
            System.out.println("Estado actual: " + gEstadoUs);
            btnActivoUser.setEnabled(false);
            btnInactivoUser.setEnabled(true);
            Toast.makeText(ModificarUsuario.this, "Usuario activado nuevamente", Toast.LENGTH_SHORT).show();

        });

        btnInactivoUser = findViewById(R.id.btnInactivoUsuario);
        btnInactivoUser.setOnClickListener(v -> {

            gEstadoUs = 0;
            System.out.println("Estado actual: " + gEstadoUs);
            btnActivoUser.setEnabled(true);
            btnInactivoUser.setEnabled(false);
            Toast.makeText(ModificarUsuario.this, "Usuario desactivado", Toast.LENGTH_SHORT).show();

        });

        editUsuario = findViewById(R.id.obtenerEditarUsuario);
        editUsuario.setText(UsuarioFragment.gLoginUusario);

        editNombre = findViewById(R.id.obtenerEditarNombre);
        editNombre.setText(UsuarioFragment.gNombreUsuario);

        editPass = findViewById(R.id.obtenerEditarPass);
        editPass.setText(UsuarioFragment.gPasswordUsuario);

        editRepeatPass = findViewById(R.id.obtenerEditarPassRepeat);
        editRepeatPass.setText(UsuarioFragment.gPasswordRepeatUsuario);

        editEmail = findViewById(R.id.obtenerEditarEmail);
        editEmail.setText(UsuarioFragment.gEmailUsuario);

        editEdad = findViewById(R.id.obtenerEditarEdad);
        editEdad.setText(String.valueOf(UsuarioFragment.gEdadUsuario));

        editDui = findViewById(R.id.obtenerEditarDui);
        editDui.setText(String.valueOf(UsuarioFragment.gDuiUsuario));

        editFechaNac = findViewById(R.id.obtenerEditarFecha);
        editFechaNac.setText(String.valueOf(UsuarioFragment.gFechaNacimiento));

        modificarUsuario = findViewById(R.id.editarUsuario);
        modificarUsuario.setOnClickListener(v -> ejecutar());

    }

    private void ejecutar(){

        String url = "http://" + VariablesGlobales.host + "/android/LCB/administrador/scripts/scripts_php/modificarUsuario.php"
                + "?login_usuario=" + editUsuario.getText().toString()
                + "&nombre_usuario=" + editNombre.getText().toString()
                + "&email_usuario=" + editEmail.getText().toString()
                + "&nacimiento_usuario=" + editFechaNac.getText().toString()
                + "&edad_usuario=" + editEdad.getText().toString()
                + "&dui_usuario=" + editDui.getText().toString()
                + "&password_usuarios=" + editPass.getText().toString()
                + "&password_repeat_usuario=" + editRepeatPass.getText().toString()
                + "&estado_usuario=" + gEstadoUs
                + "&notificacion=" + gNotiUs
                + "&id_usuario=" + Login.gIdUsuario;

        ejecutarServicio(url);

        System.out.println(url);

        Intent i = new Intent(this, Principal.class);
        startActivity(i);

    }

    public void ejecutarServicio (String URL){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> Toast.makeText(getApplicationContext(), "USUARIO ACTUALIZADO CON ÉXITO", Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show());
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

}