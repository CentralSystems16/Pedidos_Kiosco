package com.pedidos.kiosco.usuarios;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.fragments.CierreCaja;
import com.pedidos.kiosco.fragments.UsuarioInactivo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class UsuarioFragment extends Fragment {

    RecyclerView rvLista;
    public static ArrayList<Usuarios> usuarios;
    Adaptadorsuarios adaptadorUsers;
    FloatingActionButton fab;
    public static final String URL_USERS = "http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerUsers.php" + "?estado_usuario=1";
    public static String gNombreUsuario, gLoginUusario, gPasswordUsuario, gPasswordRepeatUsuario, gEmailUsuario, gFechaNacimiento, gSexoUsuario;
    public static int gIdUsuario, gIdCargo, gEstadoUsuario, gEdadUsuario, gDuiUsuario;
    public Button usuariosInactivos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        obtenerUsuarios();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.usuario_frament, container, false);

        usuarios = new ArrayList<>();

        rvLista = vista.findViewById(R.id.rvListaUsuarios);
        rvLista.setLayoutManager(new GridLayoutManager(getContext(), 1));

        usuariosInactivos = vista.findViewById(R.id.usuariosInactivos);
        usuariosInactivos.setOnClickListener(v -> {
            FragmentTransaction fr = getFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new UsuarioInactivo());
            fr.commit();
        });

        fab = vista.findViewById(R.id.fabUser);
        fab.setOnClickListener(v -> {
            Intent i = new Intent(requireActivity(), AgregarUsuario.class);
            startActivity(i);
        });

        return vista;

    }

    public void obtenerUsuarios() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Por favor espera...");
        progressDialog.show();
        progressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

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
                                    gIdCargo = jsonObject1.getInt("cargo_usuario"),
                                    gEstadoUsuario = jsonObject1.getInt("estado_usuario")));

                        }

                        adaptadorUsers = new Adaptadorsuarios(getContext(), usuarios);
                        rvLista.setAdapter(adaptadorUsers);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }, Throwable::printStackTrace
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }
}