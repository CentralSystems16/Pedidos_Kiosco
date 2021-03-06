package com.pedidos.kiosco.fragments;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.usuarios.Cargos;
import com.pedidos.kiosco.usuarios.UsuarioFragment;
import com.pedidos.kiosco.Login;

import org.json.JSONArray;
import java.util.ArrayList;
import cz.msebera.android.httpclient.Header;

public class ModificarUsuario extends Fragment {

    EditText editUsuario, editNombre, editPass, editRepeatPass, editEmail;
    RequestQueue requestQueue;
    Button modificarUsuario, btnActivoUser, btnInactivoUser, btnCancelar;
    public static int gEstadoUs = 1;

    Spinner spCargos;
    AsyncHttpClient cliente;
    ArrayList<Cargos> lista = new ArrayList<>();

    int gCargoUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.modificar_usuario_fragment, container, false);

        spCargos = vista.findViewById(R.id.spinnerCargo);
        cliente = new AsyncHttpClient();
        llenarSpinner();

        btnCancelar = vista.findViewById(R.id.cancelarUsuario);
        btnCancelar.setOnClickListener(view -> {
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new UsuarioFragment());
            fr.commit();
        });

        btnActivoUser = vista.findViewById(R.id.btnActivoUsuario);
        btnActivoUser.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));
        btnActivoUser.setOnClickListener(v -> {

            gEstadoUs = 1;
            btnActivoUser.setVisibility(View.INVISIBLE);
            btnInactivoUser.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Usuario activado nuevamente", Toast.LENGTH_SHORT).show();

        });

        btnInactivoUser = vista.findViewById(R.id.btnInactivoUsuario);
        btnInactivoUser.setOnClickListener(v -> {

            gEstadoUs = 0;
            btnActivoUser.setVisibility(View.VISIBLE);
            btnInactivoUser.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "Usuario desactivado", Toast.LENGTH_SHORT).show();

        });

        if (UsuarioFragment.gEstadoUsuario == 0) {
            btnInactivoUser.setVisibility(View.INVISIBLE);
        }

        else {
            btnActivoUser.setVisibility(View.INVISIBLE);
        }

        editUsuario = vista.findViewById(R.id.obtenerEditarUsuario);
        editUsuario.setText(UsuarioFragment.gLoginUusario);

        editNombre = vista.findViewById(R.id.obtenerEditarNombre);
        editNombre.setText(UsuarioFragment.gNombreUsuario);

        editPass = vista.findViewById(R.id.obtenerEditarPass);
        editPass.setText(UsuarioFragment.gPasswordUsuario);

        editRepeatPass = vista.findViewById(R.id.obtenerEditarPassRepeat);
        editRepeatPass.setText(UsuarioFragment.gPasswordRepeatUsuario);

        editEmail = vista.findViewById(R.id.obtenerEditarEmail);
        editEmail.setText(UsuarioFragment.gEmailUsuario);

        modificarUsuario = vista.findViewById(R.id.editarUsuario);
        modificarUsuario.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));
        modificarUsuario.setOnClickListener(v -> ejecutar());

        return vista;
    }

    private void ejecutar(){

        gModifUser();

        String url = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/modificarUsuario.php"
                + "?base=" + VariablesGlobales.dataBase
                + "&login_usuario=" + editUsuario.getText().toString()
                + "&nombre_usuario=" + editNombre.getText().toString()
                + "&email_usuario=" + editEmail.getText().toString()
                + "&password_usuarios=" + editPass.getText().toString()
                + "&password_repeat_usuario=" + editRepeatPass.getText().toString()
                + "&estado_usuario=" + gEstadoUs
                + "&cargo_usuario=" + gCargoUser
                + "&id_usuario=" + UsuarioFragment.gIdUsuario;

        ejecutarServicio(url);

        if (gCargoUser != UsuarioFragment.gIdCargo){
            startActivity(new Intent(getContext(), Login.class));
            Toast.makeText(getContext(), "Su cargo ha cambiado por favor inicie sesion nuevamente.", Toast.LENGTH_SHORT).show();
        }

        else {
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new UsuarioFragment());
            fr.commit();
        }
    }

    private void llenarSpinner(){
        String url ="http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/llenarCargos.php" + "?base=" + VariablesGlobales.dataBase;
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){
                    cargarSpinner(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void cargarSpinner(String respuesta){

        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){

                Cargos c = new Cargos();
                c.setNomCargo(jsonArreglo.getJSONObject(i).getString("nombre_cargo"));
                c.setIdCargo(jsonArreglo.getJSONObject(i).getInt("id_cargo"));
                lista.add(c);
            }

            ArrayAdapter<Cargos> a  = new ArrayAdapter<>(getContext(), R.layout.spinner_item, lista);
            spCargos.setAdapter(a);
            spCargos.setSelection(UsuarioFragment.gIdCargo-1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void gModifUser(){
        int idUser = spCargos.getSelectedItemPosition();
        gCargoUser = lista.get(idUser).getIdCargo();
    }

    public void ejecutarServicio (String URL){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> Toast.makeText(getContext(), "USUARIO ACTUALIZADO CON ??XITO", Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show());
        requestQueue = Volley.newRequestQueue(requireActivity());
        requestQueue.add(stringRequest);
    }

}