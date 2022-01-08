package com.pedidos.kiosco.main;

import static com.pedidos.kiosco.other.ContadorProductos.GetDataFromServerIntoTextView.gCount;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.adapters.AdaptadorProductos;
import com.pedidos.kiosco.fragments.TicketDatos;
import com.pedidos.kiosco.model.Productos;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import pl.droidsonroids.gif.GifImageView;

public class ObtenerProductos extends AppCompatActivity {

    RecyclerView rvLista = null;
    AdaptadorProductos adaptador = null;
    List<Productos> listaProductos;

    public static double gPrecio, gDetMonto, gDetMontoIva;
    public static int gIdProducto, cantidad, maximo, minimo;
    public static String gNombreProd;

    GifImageView conejo, gato, carrito;

    public static TextView tvCantProductos;
    DecimalFormat formatoDecimal = new DecimalFormat("#");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_productos);

        carrito = findViewById(R.id.carrito);
        carrito.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), TicketDatos.class)));

        conejo = findViewById(R.id.conejo2);
        conejo.setVisibility(View.INVISIBLE);
        gato = findViewById(R.id.gato2);
        gato.setVisibility(View.INVISIBLE);
        tvCantProductos = findViewById(R.id.tvCantProductos);

        tvCantProductos = findViewById(R.id.tvCantProductos);
        tvCantProductos.setText(String.valueOf(formatoDecimal.format(gCount)));

        rvLista = findViewById(R.id.rvLista);
        rvLista.setLayoutManager(new GridLayoutManager(this, 3));

        listaProductos = new ArrayList<>();

        adaptador = new AdaptadorProductos(this, listaProductos);
        rvLista.setAdapter(adaptador);

        obtenerProductos();

    }

    public void obtenerProductos() {

        conejo.setVisibility(View.VISIBLE);

        String url = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerProductos.php" + "?id_categoria=" + Login.gIdCategoria;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Productos");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            listaProductos.add(
                                    new Productos(
                                            jsonObject1.getInt("id_producto"),
                                            jsonObject1.getString("nombre_producto"),
                                            jsonObject1.getDouble("precio_producto"),
                                            jsonObject1.getString("img_producto"),
                                            jsonObject1.getInt("cantidad"),
                                            jsonObject1.getInt("minimo"),
                                            jsonObject1.getInt("maximo")));
                        }

                        adaptador = new AdaptadorProductos(this, listaProductos);
                        rvLista.setAdapter(adaptador);

                        conejo.setVisibility(View.INVISIBLE);

                    } catch (JSONException e) {
                        Toast.makeText(this, "Ocurrió un error inesperado, verifica tu conexion a internet o vuelve a intentarlo mas tarde, Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        conejo.setVisibility(View.INVISIBLE);
                        gato.setVisibility(View.VISIBLE);
                    }

                }, volleyError -> {
            Toast.makeText(this, "Ocurrió un error inesperado, verifica tu conexion a internet o vuelve a intentarlo mas tarde, Error: " + volleyError, Toast.LENGTH_LONG).show();
            conejo.setVisibility(View.INVISIBLE);
            gato.setVisibility(View.VISIBLE);
        }
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }

}