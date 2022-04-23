package com.pedidos.kiosco.gastos;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;

import android.app.ProgressDialog;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.model.Comprobantes;
import com.pedidos.kiosco.usuarios.UsuarioFragment;

import org.json.JSONArray;
import java.util.ArrayList;
import cz.msebera.android.httpclient.Header;

public class FragmentGastos extends Fragment {

    Spinner spGastos;
    private AsyncHttpClient cliente;
    ArrayList<Comprobantes> comprobantes;
    int gIdComprobanteGastos, gEstadoUs;

    Button btnActivoUser, btnInactivoUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_gastos, container, false);

        EditText montoGastos = vista.findViewById(R.id.montoGastosEdit);
        montoGastos.setText(String.valueOf(ListarGastos.monto));

        btnActivoUser = vista.findViewById(R.id.btnActivoGastos);
        btnActivoUser.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));
        btnActivoUser.setOnClickListener(v -> {

            gEstadoUs = 1;
            btnActivoUser.setVisibility(View.INVISIBLE);
            btnInactivoUser.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Gasto activado nuevamente", Toast.LENGTH_SHORT).show();

        });

        btnInactivoUser = vista.findViewById(R.id.btnInactivoGastos);
        btnInactivoUser.setOnClickListener(v -> {

            gEstadoUs = 0;
            btnActivoUser.setVisibility(View.VISIBLE);
            btnInactivoUser.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "Gasto desactivado", Toast.LENGTH_SHORT).show();

        });

        if (UsuarioFragment.gEstadoUsuario == 0) {
            btnInactivoUser.setVisibility(View.INVISIBLE);
        }

        else {
            btnActivoUser.setVisibility(View.INVISIBLE);
        }

        EditText descripcion = vista.findViewById(R.id.descripcionGastosEdit);
        descripcion.setText(String.valueOf(ListarGastos.descripcion));

        MaterialCardView gastos = vista.findViewById(R.id.cardViewGastosEdit);
        gastos.setStrokeColor(Color.rgb(gRed, gGreen, gBlue));

        EditText fechaCreo = vista.findViewById(R.id.fechaCreo);
        fechaCreo.setText(ListarGastos.fecha);

        spGastos = vista.findViewById(R.id.spGastosEdit);

        cliente = new AsyncHttpClient();

        Button guardar = vista.findViewById(R.id.guardarGastosEdit);
        guardar.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));
        guardar.setOnClickListener(view -> {
            obtenerIdComprobante();
            String url = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/modificarGastos.php"
                    + "?base=" + VariablesGlobales.dataBase
                    + "&monto=" + montoGastos.getText().toString()
                    + "&descripcion=" + descripcion.getText().toString()
                    + "&id_tipo_comprobante=" + gIdComprobanteGastos
                    + "&id_estado_comprobante=" + gEstadoUs
                    + "&id_fac_movimiento=" + ListarGastos.idFacMovimientos;
            System.out.println(url);
            ejecutarServicio(url);
        });

        llenarComprobante();

        return vista;
    }

    private void llenarComprobante(){
        String url ="http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/llenarComprobantes.php" + "?base=" + VariablesGlobales.dataBase;
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){
                    cargarComprobante(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void cargarComprobante(String respuesta){

        comprobantes = new ArrayList<>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){

                Comprobantes c = new Comprobantes();
                c.setComprobante(jsonArreglo.getJSONObject(i).getString("tipo_comprobante"));
                c.setIdComprobante(jsonArreglo.getJSONObject(i).getInt("id_tipo_comprobante"));
                comprobantes.add(c);
            }

            ArrayAdapter<Comprobantes> a  = new ArrayAdapter<>(getContext(), R.layout.spinner_item, comprobantes);
            spGastos.setAdapter(a);
            spGastos.setSelection(ListarGastos.gIdGastos-1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void obtenerIdComprobante(){
        int indice = spGastos.getSelectedItemPosition();
        gIdComprobanteGastos = comprobantes.get(indice).getIdComprobante();

    }

    public void ejecutarServicio (String URL){

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                    fr.replace(R.id.fragment_layout, new ListarGastos());
                    fr.commit();
                },
                volleyError -> progressDialog.dismiss()
        );
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

}