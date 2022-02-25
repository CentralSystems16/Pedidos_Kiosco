package com.pedidos.kiosco.usuarios;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class UsuarioInactivos extends AppCompatActivity {

    RecyclerView rvLista;
    public static ArrayList<Usuarios> usuarios;
    AdaptadorsuariosInactivos adaptadorUsers;
    public static final String URL_USERS = "http://" + VariablesGlobales.host +"/android/LCB/administrador/scripts/scripts_php/loginInactivos.php";
    public static String gNombreUsuario, gLoginUusario, gPasswordUsuario, gPasswordRepeatUsuario, gEmailUsuario, gFechaNacimiento, gSexoUsuario;
    public static int gIdUsuario, gIdCargo, gEstado, gEdadUsuario, gDuiUsuario;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.usuarios_inactivos);
        usuarios = new ArrayList<>();

        rvLista = findViewById(R.id.rvListaUsuariosInactivo);
        rvLista.setLayoutManager(new GridLayoutManager(this, 1));

        obtenerUsuarios();

    }

    public void obtenerUsuarios() {

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_USERS,

                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Usuarios");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            usuarios.add(
                                    new Usuarios(
                                            gIdUsuario = jsonObject1.getInt("id_usuario"),
                                            gNombreUsuario = jsonObject1.getString("nombre_usuario"),
                                            gLoginUusario = jsonObject1.getString("login_usuario"),
                                            gPasswordUsuario = jsonObject1.getString("password_usuarios"),
                                            gPasswordRepeatUsuario = jsonObject1.getString("password_repeat_usuario"),
                                            gEmailUsuario = jsonObject1.getString("email_usuario"),
                                            gIdCargo = jsonObject1.getInt("id_cargo"),
                                            gEstado = jsonObject1.getInt("estado_usuario")));

                                        }

                        adaptadorUsers = new AdaptadorsuariosInactivos(this, usuarios);
                        rvLista.setAdapter(adaptadorUsers);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }
}