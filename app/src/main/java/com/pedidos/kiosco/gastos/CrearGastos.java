package com.pedidos.kiosco.gastos;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Spinner;
import com.android.volley.DefaultRetryPolicy;
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
import com.pedidos.kiosco.other.InsertarGastos;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class CrearGastos extends Fragment {

    EditText monto, descripcion, Afecha, noComprobante;
    RadioGroup activo;
    Button continuar, cancelar;

    Spinner spGastos;

    private AsyncHttpClient cliente;

    ArrayList<Comprobantes> comprobantes;

    public static String Sfecha, Smonto, Sdescripcion;

    public static int gIdComprobanteGastos, numeroComprobante;

    private int ultimoAnio, ultimoMes, ultimoDiaDelMes;

    private DatePickerDialog.OnDateSetListener listenerDeDatePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int anio, int mes, int diaDelMes) {

            ultimoAnio = anio;
            ultimoMes = mes;
            ultimoDiaDelMes = diaDelMes;

            refrescarFechaEnEditText();

        }
    };

    public void refrescarFechaEnEditText() {

        String fecha = String.format(Locale.getDefault(), "%02d-%02d-%02d", ultimoAnio, ultimoMes+1, ultimoDiaDelMes);
        Afecha.setText(fecha);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_crear_gastos, container, false);

        monto = vista.findViewById(R.id.montoGastos);
        descripcion = vista.findViewById(R.id.descripcionGastos);
        Afecha = vista.findViewById(R.id.fechaGastos);
        noComprobante = vista.findViewById(R.id.numeroComprobante);

        Afecha.setOnClickListener(v -> {

            DatePickerDialog dialogoFecha = new DatePickerDialog(getContext(), listenerDeDatePicker, ultimoAnio, ultimoMes, ultimoDiaDelMes);

            dialogoFecha.show();
        });

        activo = vista.findViewById(R.id.activoGastos);

        MaterialCardView gastos = vista.findViewById(R.id.cardViewGastos);
        gastos.setStrokeColor(Color.rgb(gRed, gGreen, gBlue));

        continuar = vista.findViewById(R.id.guardarGastos);
        continuar.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));
        continuar.setOnClickListener(view -> {

            if (VariablesGlobales.gIdCierreCaja == 0) {
                new AlertDialog.Builder(getContext())
                        .setTitle("Caja cerrada")
                        .setMessage("Para continuar debe abrir caja")
                        .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        })
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .show();
            }
            else {
                obtenerIdComprobante();
                obtenerAutFiscal();
                Smonto = monto.getText().toString();
                Sdescripcion = descripcion.getText().toString();
                Sfecha = Afecha.getText().toString();
                numeroComprobante = Integer.parseInt(noComprobante.getText().toString());

                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_layout, new ListarGastos());
                fr.commit();
            }

        });

        cancelar = vista.findViewById(R.id.cancelarGastos);

        spGastos = vista.findViewById(R.id.spGastos);

        cliente = new AsyncHttpClient();

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

    public void obtenerAutFiscal() {

        String url_pedido = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerAutFiscal.php" + "?base=" + VariablesGlobales.dataBase + "&id_tipo_comprobante=4";
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        Login.gIdAutFiscal = 0;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url_pedido,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray jsonArray = jsonObject.getJSONArray("AutFiscal");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            Login.gIdAutFiscal = jsonObject1.getInt("id_aut_fiscal");
                        }

                        new InsertarGastos(getContext()).execute();


                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }, Throwable::printStackTrace
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

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
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void obtenerIdComprobante(){
        int indice = spGastos.getSelectedItemPosition();
        gIdComprobanteGastos = comprobantes.get(indice).getIdComprobante();

    }


}