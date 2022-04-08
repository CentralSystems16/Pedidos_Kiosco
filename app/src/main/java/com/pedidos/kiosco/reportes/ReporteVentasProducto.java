package com.pedidos.kiosco.reportes;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;
import static com.pedidos.kiosco.reportes.BuscarReportes.sFecFinal;
import static com.pedidos.kiosco.reportes.BuscarReportes.sFecInicial;
import static com.pedidos.kiosco.reportes.BuscarReportes.sHoraFinal;
import static com.pedidos.kiosco.reportes.BuscarReportes.sHoraInicial;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.other.ContadorCantidad;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

public class ReporteVentasProducto extends Fragment {

    TextView nombreProducto, cantProducto;
    public static TextView cantTotal;

    EditText fechaInicial, horaInicial, fechaFinal, horaFinal;

    private static final String CERO = "0";
    private static final String DOS_PUNTOS = ":";

    private int dia, mes, anio;

    public final Calendar cClock = Calendar.getInstance();

    final int horaClock = cClock.get(Calendar.HOUR_OF_DAY);
    final int minutoClock = cClock.get(Calendar.MINUTE);

    StringBuilder sb, sb2;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.reporte_ventas_producto, container, false);

        Toolbar toolbar = vista.findViewById(R.id.toolbarReporteProducto);
        toolbar.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));

        obtenerReporteProductos();

        Button mostrarPorCorte = vista.findViewById(R.id.btnMostrarPorCorte);
        mostrarPorCorte.setOnClickListener(view -> {

            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new ReporteVentasProductoCorte());
            fr.commit();

        });

        nombreProducto = vista.findViewById(R.id.nombreProducto);
        cantProducto = vista.findViewById(R.id.cantProducto);
        cantTotal = vista.findViewById(R.id.cantTotal);

        fechaInicial = vista.findViewById(R.id.fechaInicialP);

        fechaInicial.setOnClickListener(view -> showDatePickerDialog());
        fechaInicial.setText(sFecInicial);

        horaInicial = vista.findViewById(R.id.horaInicialP);
        horaInicial.setText(sHoraInicial);
        horaInicial.setOnClickListener(view -> showTimePickerDialog());

        fechaFinal = vista.findViewById(R.id.fechaFinalP);
        fechaFinal.setText(sFecFinal);
        fechaFinal.setOnClickListener(view -> showDatePickerDialogFinal());

        horaFinal = vista.findViewById(R.id.horaFinalP);
        horaFinal.setText(sHoraFinal);
        horaFinal.setOnClickListener(view -> showTimePickerDialogFinal());

        new ContadorCantidad.GetDataFromServerIntoTextView(getContext()).execute();

        return vista;
    }


    private void showDatePickerDialog() {
        final Calendar c= Calendar.getInstance();
        dia=c.get(Calendar.DAY_OF_MONTH);
        mes=c.get(Calendar.MONTH);
        anio=c.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view1, year, monthOfYear, dayOfMonth) -> {
            fechaInicial.setText(dayOfMonth+"-"+(monthOfYear+1)+"-"+year);
            sFecInicial = fechaInicial.getText().toString();
            sFecFinal = fechaFinal.getText().toString();
            sHoraInicial = horaInicial.getText().toString();
            sHoraFinal = horaFinal.getText().toString();
            nombreProducto.setText("");
            cantProducto.setText("");
            cantTotal.setText("");
            obtenerReporteProductos();

        }
                ,anio,mes,dia);
        datePickerDialog.getDatePicker();
        datePickerDialog.show();

    }

    private void showTimePickerDialog() {
        TimePickerDialog recogerHora = new TimePickerDialog(getContext(), (view12, hourOfDay, minute) -> {

            String horaFormateada =  (hourOfDay < 10)? CERO + hourOfDay : String.valueOf(hourOfDay);

            String minutoFormateado = (minute < 10)? CERO + minute :String.valueOf(minute);

            horaInicial.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + "00");
            sFecInicial = fechaInicial.getText().toString();
            sFecFinal = fechaFinal.getText().toString();
            sHoraInicial = horaInicial.getText().toString();
            sHoraFinal = horaFinal.getText().toString();
            nombreProducto.setText("");
            cantProducto.setText("");
            cantTotal.setText("");
            obtenerReporteProductos();


        }, horaClock, minutoClock, false);

        recogerHora.show();
    }

    private void showDatePickerDialogFinal() {

        final Calendar c= Calendar.getInstance();
        dia=c.get(Calendar.DAY_OF_MONTH);
        mes=c.get(Calendar.MONTH);
        anio=c.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view1, year, monthOfYear, dayOfMonth) -> {
            fechaFinal.setText(dayOfMonth+"-"+(monthOfYear+1)+"-"+year);
            sFecInicial = fechaInicial.getText().toString();
            sFecFinal = fechaFinal.getText().toString();
            sHoraInicial = horaInicial.getText().toString();
            sHoraFinal = horaFinal.getText().toString();
            nombreProducto.setText("");
            cantProducto.setText("");
            cantTotal.setText("");
            obtenerReporteProductos();

        }
                ,anio,mes,dia);
        datePickerDialog.getDatePicker();
        datePickerDialog.show();

    }

    private void showTimePickerDialogFinal() {
        TimePickerDialog recogerHora = new TimePickerDialog(getContext(), (view12, hourOfDay, minute) -> {

            String horaFormateada =  (hourOfDay < 10)? CERO + hourOfDay : String.valueOf(hourOfDay);

            String minutoFormateado = (minute < 10)? CERO + minute :String.valueOf(minute);

            horaFinal.setText(horaFormateada + DOS_PUNTOS + minutoFormateado + "59");
            sFecInicial = fechaInicial.getText().toString();
            sFecFinal = fechaFinal.getText().toString();
            sHoraInicial = horaInicial.getText().toString();
            sHoraFinal = horaFinal.getText().toString();
            nombreProducto.setText("");
            cantProducto.setText("");
            cantTotal.setText("");
            obtenerReporteProductos();


        }, horaClock, minutoClock, false);

        recogerHora.show();
    }

    public void obtenerReporteProductos() {

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        sb=new StringBuilder("");
        sb2=new StringBuilder("");

        String URL_REPORTE = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerReporteProductos.php"
                + "?base=" + VariablesGlobales.dataBase
                + "&fecha_inicio=" + sFecInicial + " " + BuscarReportes.sHoraInicial
                + "&fecha_fin=" + BuscarReportes.sFecFinal + " " + BuscarReportes.sHoraFinal;

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_REPORTE,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Reporte");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                             sb.append(jsonObject1.getString("nombre_producto")+"\n\n");
                             sb2.append(jsonObject1.getDouble("cantidad") + "0" +"\n\n");

                        }

                        cantProducto.setText(sb2.toString());
                        nombreProducto.setText(sb.toString());

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