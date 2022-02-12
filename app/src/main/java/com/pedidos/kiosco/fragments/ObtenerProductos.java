package com.pedidos.kiosco.fragments;

import static com.pedidos.kiosco.other.ContadorProductos.GetDataFromServerIntoTextView.gCount;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.pedidos.kiosco.model.Productos;
import com.pedidos.kiosco.other.ContadorProductos;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import pl.droidsonroids.gif.GifImageView;

public class ObtenerProductos extends Fragment {

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.obtener_productos_fragment, container, false);

        ImageButton backprod = vista.findViewById(R.id.backProducts);
        backprod.setOnClickListener(view -> {
            FragmentTransaction fr = getFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new Categorias());
            fr.commit();
        });

        TextView buscador = vista.findViewById(R.id.etBuscador);
        buscador.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filtrar(editable.toString());
            }
        });

        carrito = vista.findViewById(R.id.carrito);
        carrito.setOnClickListener(view -> {

            FragmentTransaction fr = getFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new TicketDatos());
            fr.commit();

        });

        conejo = vista.findViewById(R.id.conejo2);
        conejo.setVisibility(View.INVISIBLE);
        gato = vista.findViewById(R.id.gato2);
        gato.setVisibility(View.INVISIBLE);
        tvCantProductos = vista.findViewById(R.id.tvCantProductos);

        tvCantProductos = vista.findViewById(R.id.tvCantProductos);
        new ContadorProductos.GetDataFromServerIntoTextView(getContext()).execute();
        tvCantProductos.setText(String.valueOf(formatoDecimal.format(gCount)));

        rvLista = vista.findViewById(R.id.rvLista);
        rvLista.setLayoutManager(new GridLayoutManager(getContext(), 3));

        listaProductos = new ArrayList<>();

        adaptador = new AdaptadorProductos(getContext(), listaProductos);
        rvLista.setAdapter(adaptador);

        obtenerProductos();

        return vista;
    }

    public void obtenerProductos() {

        conejo.setVisibility(View.VISIBLE);

        String url = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerProductos.php" + "?id_categoria=" + Login.gIdCategoria;

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

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

                        adaptador = new AdaptadorProductos(getContext(), listaProductos);
                        rvLista.setAdapter(adaptador);

                        conejo.setVisibility(View.INVISIBLE);

                    } catch (JSONException e) {
                        Toast.makeText(getContext(), "Ocurrió un error inesperado, verifica tu conexion a internet o vuelve a intentarlo mas tarde, Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        conejo.setVisibility(View.INVISIBLE);
                        gato.setVisibility(View.VISIBLE);
                    }

                }, volleyError -> {
            Toast.makeText(getContext(), "Ocurrió un error inesperado, verifica tu conexion a internet o vuelve a intentarlo mas tarde, Error: " + volleyError, Toast.LENGTH_LONG).show();
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

    public void filtrar (String texto) {
        ArrayList<Productos> filtrarLista = new ArrayList<>();

        for (Productos usuario : listaProductos) {
            String cadenaNormalize = Normalizer.normalize(usuario.getNombreProducto(), Normalizer.Form.NFD);
            String cadenaSinAcentos = cadenaNormalize.replaceAll("[^\\p{ASCII}]", "");
            if (cadenaSinAcentos.toLowerCase().contains(texto.toLowerCase()) ) {
                filtrarLista.add(usuario);
            }

        }

        adaptador.filtrar(filtrarLista);
    }

    public void onBackPressed() {

    }

}