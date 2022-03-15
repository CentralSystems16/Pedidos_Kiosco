package com.pedidos.kiosco.adapters.reportes;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;
import static com.pedidos.kiosco.fragments.TicketDatos.gTotal;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.card.MaterialCardView;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.Splash;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.adapters.reportes.ReporteVentasProducto;
import com.pedidos.kiosco.fragments.ReporteVentas;
import com.pedidos.kiosco.model.Reporte;
import com.pedidos.kiosco.other.SumaMontoEfectivo;
import com.pedidos.kiosco.other.SumaMontoTarjeta;
import com.pedidos.kiosco.utils.Numero_a_Letra;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class BuscarReportes extends Fragment {

    private static final String CERO = "0";
    private static final String DOS_PUNTOS = ":";

    private int dia, mes, anio;

    public final Calendar cClock = Calendar.getInstance();

    final int horaClock = cClock.get(Calendar.HOUR_OF_DAY);
    final int minutoClock = cClock.get(Calendar.MINUTE);

    EditText fechaInicial, horaInicial, fechaFinal, horaFinal;

    public static String sFecInicial, sFecFinal;
    public static String sHoraInicial, sHoraFinal;

    AsyncHttpClient datos;
    ArrayList<Reporte> lista = new ArrayList<>();
    Spinner spinner;

    Date d = new Date();
    SimpleDateFormat fecc = new SimpleDateFormat("d'-'M'-'yyyy", Locale.getDefault());
    String fechacComplString = fecc.format(d);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.buscar_reportes, container, false);

        datos = new AsyncHttpClient();

        MaterialCardView registro = vista.findViewById(R.id.cardViewReportes);
        registro.setStrokeColor(Color.rgb(gRed, gGreen, gBlue));

        Toolbar toolbar = vista.findViewById(R.id.toolbarReportes);
        toolbar.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));

        spinner = vista.findViewById(R.id.spinnerReportes);

        Button mostrarReporte = vista.findViewById(R.id.btnMostrarRep);
        mostrarReporte.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));
        mostrarReporte.setOnClickListener(view -> {
            if (spinner.getSelectedItemPosition() == 0) {
                sFecInicial = fechaInicial.getText().toString();
                sFecFinal = fechaFinal.getText().toString();
                sHoraInicial = horaInicial.getText().toString();
                sHoraFinal = horaFinal.getText().toString();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_layout, new ReporteVentasProducto());
                fr.commit();
            }

            else if (spinner.getSelectedItemPosition() == 1){
                sFecInicial = fechaInicial.getText().toString();
                sFecFinal = fechaFinal.getText().toString();
                sHoraInicial = horaInicial.getText().toString();
                sHoraFinal = horaFinal.getText().toString();
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_layout, new ReporteVentas());
                fr.commit();
            }

        });

        llenarSpinner();

        fechaInicial = vista.findViewById(R.id.fechaInicial);

        fechaInicial.setOnClickListener(view -> showDatePickerDialog());
        fechaInicial.setText(fechacComplString);

        horaInicial = vista.findViewById(R.id.horaInicial);
        horaInicial.setText("00:00:00");
        horaInicial.setOnClickListener(view -> showTimePickerDialog());

        fechaFinal = vista.findViewById(R.id.fechaFinal);
        fechaFinal.setText(fechacComplString);
        fechaFinal.setOnClickListener(view -> showDatePickerDialogFinal());

        horaFinal = vista.findViewById(R.id.horaFinal);
        horaFinal.setText("23:59:59");
        horaFinal.setOnClickListener(view -> showTimePickerDialogFinal());

        return vista;
    }

    private void llenarSpinner(){

        String url ="http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/llenarReportes.php" + "?base=" + VariablesGlobales.dataBase;

        datos.post(url, new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody) {
                if (statusCode == 200){
                    cargarSpinner(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, cz.msebera.android.httpclient.Header[] headers, byte[] responseBody, Throwable error) {
                Toast.makeText(getContext(), "Ocurrió un error inesperado", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void cargarSpinner(String respuesta){

        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){

                Reporte e = new Reporte();
                e.setNomReporte(jsonArreglo.getJSONObject(i).getString("nombre_reporte"));
                lista.add(e);
            }

            ArrayAdapter<Reporte> a = new ArrayAdapter<>(getContext(), R.layout.spinner_item, lista);
            spinner.setAdapter(a);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void showDatePickerDialog() {
        final Calendar c= Calendar.getInstance();
        dia=c.get(Calendar.DAY_OF_MONTH);
        mes=c.get(Calendar.MONTH);
        anio=c.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(), (view1, year, monthOfYear, dayOfMonth) -> {
            fechaInicial.setText(dayOfMonth+"-"+(monthOfYear+1)+"-"+year);

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


        }, horaClock, minutoClock, false);

        recogerHora.show();
    }

}