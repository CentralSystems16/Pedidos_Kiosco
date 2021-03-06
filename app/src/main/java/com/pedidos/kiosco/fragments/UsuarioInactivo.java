package com.pedidos.kiosco.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.usuarios.AdaptadorsuariosInactivos;
import com.pedidos.kiosco.usuarios.Usuarios;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class UsuarioInactivo extends Fragment {

    RecyclerView rvLista;
    public static ArrayList<Usuarios> usuarios;
    AdaptadorsuariosInactivos adaptadorUsers;
    public static final String URL_USERS = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerUsuariosInactivos.php" + "?base=" + VariablesGlobales.dataBase + "&estado_usuario=0";
    public static String gNombreUsuario, gLoginUusario, gPasswordUsuario, gPasswordRepeatUsuario, gEmailUsuario;
    public static int gIdUsuario, gIdCargo, gEstado;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_usuario_inactivo, container, false);

        usuarios = new ArrayList<>();

        rvLista = vista.findViewById(R.id.rvListaUsuariosInactivo);
        rvLista.setLayoutManager(new GridLayoutManager(getContext(), 1));

        obtenerUsuarios();

        return vista;
    }

    public void obtenerUsuarios() {

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
                                            gEstado = jsonObject1.getInt("estado_usuario")));

                        }

                        adaptadorUsers = new AdaptadorsuariosInactivos(getContext(), usuarios);
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