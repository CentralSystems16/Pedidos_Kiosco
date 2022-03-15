package com.pedidos.kiosco.adapters.reportes;

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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ReporteVentasProducto extends Fragment {

    TextView nombreProducto, cantProducto, cantTotal;
    double cantidadTotal = 0.00;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.reporte_ventas_producto, container, false);

        Toolbar toolbar = vista.findViewById(R.id.toolbarReporteProducto);
        toolbar.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));

        obtenerReporteProductos();

        Button mostrarPorCorte = vista.findViewById(R.id.btnMostrarPorCorte);
        mostrarPorCorte.setOnClickListener(view -> {

            FragmentTransaction fr = getFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new ReporteVentasProductoCorte());
            fr.commit();

        });

        nombreProducto = vista.findViewById(R.id.nombreProducto);
        cantProducto = vista.findViewById(R.id.cantProducto);
        cantTotal = vista.findViewById(R.id.cantTotal);

        TextView desde = vista.findViewById(R.id.fDesde);
        desde.setText(BuscarReportes.sFecInicial + " " + BuscarReportes.sHoraInicial);
        TextView hasta = vista.findViewById(R.id.fHasta);
        hasta.setText(BuscarReportes.sFecFinal + " " + BuscarReportes.sHoraFinal);

        return vista;
    }

    public void obtenerReporteProductos() {

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String URL_REPORTE = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerReporteProductos.php"
                + "?base=" + VariablesGlobales.dataBase
                + "&fecha_inicio=" + BuscarReportes.sFecInicial + " " + BuscarReportes.sHoraInicial
                + "&fecha_fin=" + BuscarReportes.sFecFinal + " " + BuscarReportes.sHoraFinal;

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_REPORTE,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Reporte");
                        StringBuilder sb=new StringBuilder("");
                        StringBuilder sb2=new StringBuilder("");
                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                             sb.append(jsonObject1.getString("nombre_producto")+"\n\n");
                             sb2.append(jsonObject1.getDouble("cantidad") + "0" +"\n\n");
                             jsonObject1.getString("nombre_cliente");

                             cantidadTotal = jsonObject1.getDouble("cantidad");
                             cantTotal.setText(cantidadTotal + "0");

                        }
                        nombreProducto.setText(sb.toString());
                        cantProducto.setText(sb2.toString());

                        progressDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                    progressDialog.dismiss();
                }, volleyError -> {
            Toast.makeText(getContext(), "Ocurrió un error inesperado, Error: " + volleyError, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

}