package com.pedidos.kiosco.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.main.ObtenerEstados;
import com.smarteist.autoimageslider.DefaultSliderView;
import com.smarteist.autoimageslider.IndicatorAnimations;
import com.smarteist.autoimageslider.SliderAnimations;
import com.smarteist.autoimageslider.SliderLayout;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Home extends Fragment {

    private SliderLayout sliderLayout;
    CardView hacerPedido, verPedido;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        obtenerPedidosAct();
        obtenerPedidosAct2();

        //fiscal = view.findViewById(R.id.btnFiscal);
        hacerPedido = view.findViewById(R.id.btnPedidos);
        verPedido = view.findViewById(R.id.btnVerPedidos);
        verPedido.setOnClickListener(view12 -> startActivity(new Intent(getContext(), ObtenerEstados.class)));

        /*fiscal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });*/

        hacerPedido.setOnClickListener(view1 -> {

            FragmentTransaction fr = getFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new Categorias());
            fr.commit();
            Login.gIdPedido = 0;

        });

        sliderLayout = view.findViewById(R.id.slider);
        sliderLayout.setIndicatorAnimation(IndicatorAnimations.FILL);
        sliderLayout.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION);
        sliderLayout.setScrollTimeInSec(3);

        setSliderViews();

        return view;
    }

    private void setSliderViews() {
        for (int i = 0; i < 5; i++){
            DefaultSliderView sliderView = new DefaultSliderView(getContext());

            switch (i){
                case 0:
                    sliderView.setImageUrl("https://scontent.fsal3-1.fna.fbcdn.net/v/t1.6435-9/127645803_2913214262115686_5758257802602160430_n.jpg?_nc_cat=110&ccb=1-5&_nc_sid=730e14&_nc_ohc=0cY51CDEJpkAX9OzjbI&_nc_ht=scontent.fsal3-1.fna&oh=00_AT9UAzG05VAKs7buwytQSH8xJbGtZt9ebnR2LZe2EPt-ew&oe=61FA105A");
                    break;
                case 1:
                    sliderView.setImageUrl("https://scontent.fsal3-1.fna.fbcdn.net/v/t1.6435-9/126214047_2905606326209813_3469125596645758691_n.jpg?_nc_cat=104&ccb=1-5&_nc_sid=730e14&_nc_ohc=5PuMrvFN4PAAX-uEp9i&tn=nd7MaUoWwAqaMK3g&_nc_ht=scontent.fsal3-1.fna&oh=00_AT-A7QbHg5HXE1JOULrFOGoJ5XJ5f7qyz9UumNOYqpJRrg&oe=61FAA677");
                    break;
                case 2:
                    sliderView.setImageUrl("https://scontent.fsal3-1.fna.fbcdn.net/v/t1.6435-9/123359791_2849571731813273_5616569268018996732_n.jpg?_nc_cat=103&ccb=1-5&_nc_sid=730e14&_nc_ohc=dB7ReAW1QgAAX_5xBo3&_nc_ht=scontent.fsal3-1.fna&oh=00_AT_pF6TBYbKWs_DT9dvzGThwd2fWnIrI5etqqb3O_MATCw&oe=61FA27FA");
                    break;
                case 3:
                    sliderView.setImageUrl("https://scontent.fsal3-1.fna.fbcdn.net/v/t1.6435-9/123257170_2848711515232628_9050946330856909175_n.jpg?_nc_cat=104&ccb=1-5&_nc_sid=730e14&_nc_ohc=0ewZhYZaipEAX94rs4r&_nc_ht=scontent.fsal3-1.fna&oh=00_AT9alJvSmKu2E7NbFGqK-uGtPuosy6Yy9lkFNB2thMZSGg&oe=61FA5D65");
                    break;
                case 4:
                    sliderView.setImageUrl("https://scontent.fsal3-1.fna.fbcdn.net/v/t1.6435-9/118087703_2641712985932483_9073627628336970857_n.jpg?_nc_cat=109&ccb=1-5&_nc_sid=730e14&_nc_ohc=yXmBHpVwHMcAX9ky48G&_nc_ht=scontent.fsal3-1.fna&oh=00_AT_4CXX6IHhVHXFD_jlkWYOfLXQOhKBpvPBcyLrdlenw9A&oe=61FAD330");
                    break;
            }

            sliderView.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
            sliderLayout.addSliderView(sliderView);

        }
    }

    public void obtenerPedidosAct() {

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerPedidosActivos.php" + "?id_estado_prefactura=1" + "&id_usuario=" + Login.gIdUsuario + "&id_cliente=" + Login.gIdCliente;
        System.out.println(url);
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("PedidosAct");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            Login.gIdPedido = jsonObject1.getInt("id_prefactura");

                            if (Login.gIdPedido == 0){
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(requireContext(), "¡Tienes un pedido abierto!", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }

                        }
                        progressDialog.dismiss();

                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                }, volleyError -> {
            Toast.makeText(getContext(), "Ocurrio un error inesperado, Error: " + volleyError, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }

    public void obtenerPedidosAct2() {

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerPedidosActivos2.php" + "?id_estado_comprobante=1" + "&id_usuario=" + Login.gIdUsuario + "&id_cliente=" + Login.gIdCliente;

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("PedidosAct");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            Login.gIdMovimiento = jsonObject1.getInt("id_fac_movimiento");

                        }
                        progressDialog.dismiss();

                    } catch (JSONException e) {
                        progressDialog.dismiss();
                        e.printStackTrace();
                    }
                }, volleyError -> {
            Toast.makeText(getContext(), "Ocurrio un error inesperado, Error: " + volleyError, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();

        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }

}