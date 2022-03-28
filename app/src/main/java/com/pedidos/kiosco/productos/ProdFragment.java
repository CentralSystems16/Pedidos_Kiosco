package com.pedidos.kiosco.productos;

import android.app.ProgressDialog;
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
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.categorias.CatFragment;
import com.pedidos.kiosco.categorias.Categorias;
import com.pedidos.kiosco.fragments.ProductosInactivos;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class ProdFragment extends Fragment {

    RecyclerView rvLista;
    ArrayList<Categorias>  categorias;
    AdaptadorCatProd adaptador;
    public static int gIdProducto, gOpciones, estado;
    public static double gPrecio, gDetMonto, gDetMontoIva;
    public static String gNombreProd;
    Button inactivos;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        obtenerProductos();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_prod, container, false);
        categorias = new ArrayList<>();
        rvLista = vista.findViewById(R.id.rvListaCategoriasProd);

        rvLista.setLayoutManager(new GridLayoutManager(getContext(), 3));

        inactivos = vista.findViewById(R.id.btnInactivos);
        inactivos.setOnClickListener(v -> {
            FragmentTransaction fr = getFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new ProductosInactivos());
            fr.commit();
        });

        return vista;
    }

    public void obtenerProductos() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Por favor espera...");
        progressDialog.show();
        progressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        String URL_PRODUCTOS = "http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerCategorias.php"
                + "?base=" + VariablesGlobales.dataBase
                + "&estado_categoria=1";

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_PRODUCTOS,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Categorias");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            categorias.add(
                                    new Categorias(
                                            jsonObject1.getInt("id_categoria"),
                                            jsonObject1.getString("nombre_categoria"),
                                            jsonObject1.getString("img_categoria"),
                                            jsonObject1.getInt("estado_categoria")));
                        }

                        adaptador = new AdaptadorCatProd(getContext(), categorias);
                        rvLista.setAdapter(adaptador);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }, Throwable::printStackTrace
        ) {
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                CatFragment.MY_DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }
}