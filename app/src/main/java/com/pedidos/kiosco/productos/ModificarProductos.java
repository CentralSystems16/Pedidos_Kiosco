package com.pedidos.kiosco.productos;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;
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
import com.pedidos.kiosco.categorias.CatFragment;
import com.pedidos.kiosco.categorias.Categorias;
import com.pedidos.kiosco.fragments.ObtenerEstados;
import com.pedidos.kiosco.fragments.ObtenerProductos;

import org.json.JSONArray;
import java.util.ArrayList;
import cz.msebera.android.httpclient.Header;

public class ModificarProductos extends Fragment {

    Button btnGuardarProd, btnActivo, btnInactivo, cargarImagen, cancelar;
    EditText modifProd;
    EditText modifPrec;
    RequestQueue requestQueue;
    public static int gEstadoProd = 1;
    private AsyncHttpClient cliente;
    private Spinner spProductos;
    ArrayList<Categorias> lista;
    int gOpcionUser2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_modificar_productos, container, false);

        cliente = new AsyncHttpClient();
        spProductos = vista.findViewById(R.id.spinnerCategoria);

        cargarImagen = vista.findViewById(R.id.cargarImagenProd);
        cargarImagen.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));
        cargarImagen.setOnClickListener(v -> Toast.makeText(getContext(), "Temporalmente desabilitada", Toast.LENGTH_LONG).show());

        modifProd = vista.findViewById(R.id.etModifProd);
        modifProd.setText(ProdFragment.gNombreProd);

        modifPrec = vista.findViewById(R.id.etModifPrec);
        modifPrec.setText(String.valueOf(ProdFragment.gPrecio));

        cancelar = vista.findViewById(R.id.btnCancelarGuard);
        cancelar.setOnClickListener(view -> {
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new ProdFragment());
            fr.commit();
        });

        btnActivo = vista.findViewById(R.id.btnActivoProducto);
        btnActivo.setOnClickListener(v -> {

            gEstadoProd = 1;
            btnActivo.setVisibility(View.INVISIBLE);
            btnInactivo.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Producto activado nuevamente", Toast.LENGTH_SHORT).show();
        });

        btnInactivo = vista.findViewById(R.id.btnInactivoProducto);
        btnInactivo.setOnClickListener(v -> {

            gEstadoProd = 0;
            btnActivo.setVisibility(View.VISIBLE);
            btnInactivo.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "Producto desactivado", Toast.LENGTH_SHORT).show();

        });

        if (ProdFragment.estado == 0) {
            btnInactivo.setVisibility(View.INVISIBLE);
        }

        else {
            btnActivo.setVisibility(View.INVISIBLE);
        }

        llenarSpinner();

        btnGuardarProd = vista.findViewById(R.id.btnGuardarProd);
        btnGuardarProd.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));
        btnGuardarProd.setOnClickListener(v -> ejecutar());


        return vista;
    }

public void ejecutar(){

    obtenerIdCategoria();

    String url = "http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/ActualizarPorducto.php"
            + "?base=" + VariablesGlobales.dataBase
            + "&nombre_producto=" + modifProd.getText().toString()
            + "&precio_producto=" + modifPrec.getText().toString()
            + "&estado_producto=" + gEstadoProd
            + "&id_categoria=" + gOpcionUser2
            + "&id_producto=" + ProdFragment.gIdProducto;

    ejecutarServicio(url);

}

    public void ejecutarServicio (String URL){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    FragmentTransaction fr = getFragmentManager().beginTransaction();
                    fr.replace(R.id.fragment_layout, new ProdFragment());
                    fr.commit();
            Toast.makeText(requireContext(), "PRODUCTO ACTUALIZADO CON ÉXITO", Toast.LENGTH_LONG).show();
                },
                error -> Toast.makeText(getContext(), error.toString(), Toast.LENGTH_SHORT).show());
        requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

    private void llenarSpinner(){
        String url ="http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/LlenarCategorias.php"+ "?base=" + VariablesGlobales.dataBase;
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

        lista = new ArrayList<>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){

                Categorias c = new Categorias();
                c.setNombreCategoria(jsonArreglo.getJSONObject(i).getString("nombre_categoria"));
                c.setIdCategoria(jsonArreglo.getJSONObject(i).getInt("id_categoria"));
                lista.add(c);
            }

            ArrayAdapter<Categorias> a  = new ArrayAdapter<>(getContext(), R.layout.spinner_item, lista);
            spProductos.setAdapter(a);
            spProductos.setSelection(CatFragment.gIdCategoria-1);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void obtenerIdCategoria(){
        int indice = spProductos.getSelectedItemPosition();
        gOpcionUser2 = lista.get(indice).getIdCategoria();

    }

}