package com.pedidos.kiosco.productos;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.categorias.Categorias;
import org.json.JSONArray;
import java.util.ArrayList;
import cz.msebera.android.httpclient.Header;

public class ModificarProductos extends AppCompatActivity {

    Button btnGuardarProd, btnActivo, btnInactivo, cargarImagen;
    EditText modifProd;
    EditText modifPrec;
    RequestQueue requestQueue;
    public static int gEstadoProd = 1;
    private AsyncHttpClient cliente;
    private Spinner spProductos;
    ArrayList<Categorias> lista;
    int gOpcionUser2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.modificar_productos);

        cliente = new AsyncHttpClient();
        spProductos = findViewById(R.id.spinnerCategoria);

        cargarImagen = findViewById(R.id.cargarImagenProd);
        cargarImagen.setOnClickListener(v -> Toast.makeText(ModificarProductos.this, "Temporalmente desabilitada", Toast.LENGTH_LONG).show());

        modifProd = findViewById(R.id.etModifProd);
        modifProd.setText(ProdFragment.gNombreProd);

        modifPrec = findViewById(R.id.etModifPrec);
        modifPrec.setText(String.valueOf(ProdFragment.gPrecio));

        btnActivo = findViewById(R.id.btnActivoProducto);
        btnActivo.setEnabled(false);
        btnActivo.setOnClickListener(v -> {

            gEstadoProd = 1;
            btnActivo.setEnabled(false);
            btnInactivo.setEnabled(true);
            Toast.makeText(ModificarProductos.this, "Producto activado nuevamente", Toast.LENGTH_SHORT).show();
        });

        btnInactivo = findViewById(R.id.btnInactivoProducto);
        btnInactivo.setOnClickListener(v -> {

            gEstadoProd = 0;
            btnActivo.setEnabled(true);
            btnInactivo.setEnabled(false);
            Toast.makeText(ModificarProductos.this, "Producto desactivado", Toast.LENGTH_SHORT).show();

        });

        llenarSpinner();

        btnGuardarProd = findViewById(R.id.btnGuardarProd);
        btnGuardarProd.setOnClickListener(v -> ejecutar());


    }

    private void ejecutar (){

        obtenerIdCategoria();

        String url = "http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/ActualizarPorducto.php"
                                       + "?nombre_producto=" + modifProd.getText().toString()
                                       + "&precio_producto=" + modifPrec.getText().toString()
                                       + "&estado_producto=" + gEstadoProd
                                       + "&id_categoria=" + gOpcionUser2
                                       + "&id_producto=" + ProdFragment.gIdProducto;

        ejecutarServicio(url);

        System.out.println(url);

        Intent i = new Intent(this, Principal.class);
        startActivity(i);

    }

    public void ejecutarServicio (String URL){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> Toast.makeText(getApplicationContext(), "PRODUCTO ACTUALIZADO CON ÉXITO", Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show());
        requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void llenarSpinner(){
        String url ="http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/LlenarCategorias.php";
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

            ArrayAdapter<Categorias> a  = new ArrayAdapter<>(this, R.layout.spinner_item, lista);
            spProductos.setAdapter(a);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void obtenerIdCategoria(){
        int indice = spProductos.getSelectedItemPosition();
        gOpcionUser2 = lista.get(indice).getIdCategoria();

    }

}