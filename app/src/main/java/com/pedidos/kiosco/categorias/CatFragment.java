package com.pedidos.kiosco.categorias;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;

import android.app.ProgressDialog;
import android.graphics.Color;
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
import com.pedidos.kiosco.fragments.AgregarCategorias;
import com.pedidos.kiosco.fragments.CategoriasInactivas;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;

public class CatFragment extends Fragment {

    RecyclerView rvLista;
    public static ArrayList<Categorias> categorias;
    public static final int MY_DEFAULT_TIMEOUT = 15000;
    AdaptadorCategorias adaptadorCat;
    Button inactivas;
    public static int gIdCategoria, estadoCategoria;
    public static String gNombreCat, gImagen;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        obtenerCategorias();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_cat, container, false);
        categorias = new ArrayList<>();

        Button crearCat = vista.findViewById(R.id.btnCrearCat);
        crearCat.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));
        crearCat.setOnClickListener(view -> {
            FragmentTransaction fr = getFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new AgregarCategorias());
            fr.commit();
        });

        rvLista = vista.findViewById(R.id.rvListaCategorias);

        inactivas = vista.findViewById(R.id.btnInactivas);
        inactivas.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));
        inactivas.setOnClickListener(v -> {

            FragmentTransaction fr = getFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new CategoriasInactivas());
            fr.commit();

        });

        rvLista.setLayoutManager(new GridLayoutManager(getContext(), 3));
        return vista;

    }

    public void obtenerCategorias() {

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Por favor espera...");
        progressDialog.show();
        progressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        String URL_CATEGORIAS = "http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerCategorias.php"
                +"?base=" + VariablesGlobales.dataBase
                +"&estado_categoria=1";

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_CATEGORIAS,

                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Categorias");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                            categorias.add(
                                    new Categorias(
                                            gIdCategoria = jsonObject1.getInt("id_categoria"),
                                            gNombreCat = jsonObject1.getString("nombre_categoria"),
                                            gImagen = jsonObject1.getString("img_categoria"),
                                            estadoCategoria = jsonObject1.getInt("estado_categoria")));

                        }

                        adaptadorCat = new AdaptadorCategorias(getContext(), categorias);
                        rvLista.setAdapter(adaptadorCat);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }, Throwable::printStackTrace
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                MY_DEFAULT_TIMEOUT,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }
}