package com.pedidos.kiosco.fragments;

import static android.app.Activity.RESULT_OK;
import static com.pedidos.kiosco.fragments.ResumenPago.PERMISSION_BLUETOOTH;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.os.Environment;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.dantsu.escposprinter.EscPosPrinter;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.Splash;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.adapters.AdaptadorCorteCaja;
import com.pedidos.kiosco.pdf.ResponsePOJO;
import com.pedidos.kiosco.pdf.RetrofitClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import pl.droidsonroids.gif.GifImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CrearReporteCierreCaja extends Fragment {

    private Double monto, montoFisico, montoDevolucion, montoDiferencia, montoInicial;
    private int idTipoPago, numeroCaja;
    private String gTipoPago;
    private String nombreCajero, fechaInicio, fechaFin;
    private double ventaTotal = 0.00, montoDevolucionTotal = 0.00, gastos = 0.00, gastosTotal = 0.00,
            entregarTotal = 0.00, montoFisicoTotal = 0.00 , montoDiferenciaTotal = 0.00, montoInicialTotal = 0.00;
    private int cierre;
    ArrayList arrayList = new ArrayList();
    String detalle = "", resultado = "";
    GifImageView imprimiendo;
    String encodedPDF;
    int REQ_PDF;
    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1000;

    public CrearReporteCierreCaja(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle datosRecuperados = getArguments();
        if (datosRecuperados == null) {
            return;
        }

        cierre = datosRecuperados.getInt("cierre");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         View vista =  inflater.inflate(R.layout.crear_reporte_cierre_caja, container, false);
         obtenerTipoPagoFacTipoPagoCaja(cierre);
         imprimiendo = vista.findViewById(R.id.imprimiendo);
         return vista;

    }

    public void obtenerTipoPagoFacTipoPagoCaja(int idCierreCaja) {

        String url = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerTipoPagoFacTipoPagoCaja.php" + "?base=" + VariablesGlobales.dataBase + "&id_cierre_caja=" + idCierreCaja;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("TipoPago");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            idTipoPago = jsonObject1.getInt("id_tipo_pago");

                            obtenerVentasTipoPago(idTipoPago, cierre);


                        }

                    } catch (JSONException e) {

                        e.printStackTrace();
                    }

                }, volleyError -> Toast.makeText(getContext(), "Ocurrio un error inesperado, Error: " + volleyError, Toast.LENGTH_SHORT).show());

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }

    public void obtenerVentasTipoPago(int idTipoPagoParam, int idCierreCaja) {

        String url = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerVentasTipoPago.php" + "?base=" + VariablesGlobales.dataBase + "&id_tipo_pago="
                + idTipoPagoParam + "&id_cierre_caja=" + idCierreCaja;

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("TipoPago");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            gTipoPago = jsonObject1.getString("tipo_pago");
                            monto = jsonObject1.getDouble("monto");
                            montoDevolucion = jsonObject1.getDouble("monto_devolucion");
                            montoDiferencia = jsonObject1.getDouble("monto_diferencia");
                            montoInicial= jsonObject1.getDouble("monto_inicial");
                            montoFisico = jsonObject1.getDouble("monto_fisico");
                            numeroCaja = jsonObject1.getInt("no_caja");
                            nombreCajero = jsonObject1.getString("nombre_usuario");
                            fechaInicio  = jsonObject1.getString("fecha_ini");
                            fechaFin = jsonObject1.getString("fecha_fin");

                            detalle = gTipoPago + "\n" +
                                    "[R ]================================\n" +
                                    "[R]" + "Venta(+) $" + String.format("%.2f", monto) + "\n" +
                                    "[R]" + "Devolución(-) $" + String.format("%.2f", montoDevolucion) + "\n" +
                                    "[R]" + "Encontrado(-) $" + String.format("%.2f", montoFisico) + "\n" +
                                    "[R]" + "Monto diferencia(=) $" + String.format("%.2f", montoDiferencia) + "\n" +
                                    "[R]================================\n";

                            arrayList.add(detalle);

                        }

                        ventaTotal = ventaTotal + monto;

                        montoDevolucionTotal = montoDevolucionTotal + montoDevolucion;

                        gastos = 0.00;

                        montoInicialTotal = montoInicialTotal + montoInicial;

                        entregarTotal = montoInicialTotal + ventaTotal - gastosTotal;

                        montoFisicoTotal = montoFisicoTotal + montoFisico;

                        montoDiferenciaTotal = montoDiferenciaTotal + montoDiferencia;

                        if (montoDiferenciaTotal == 0) {
                            resultado = "[C]================================" +
                                    "[C]" + "CAJA CUADRADA" +
                                    "[C]================================";
                        } else {
                            if (montoDiferenciaTotal > 0) {
                                resultado = "[R]" + "Faltante $" + String.format("%.2f", montoDiferenciaTotal) + "\n" +
                                        "[C]================================" +
                                        "[C]" + "CAJA DESCUADRADA" +
                                        "[C]================================";
                            } else if (montoDiferenciaTotal < 0){
                                resultado = "[R]" + "Sobrante $" + String.format("%.2f", montoDiferenciaTotal) + "\n" +
                                        "[C]================================" + "\n" +
                                        "[C]" + "CAJA DESCUADRADA" + "\n" +
                                        "[C]================================";
                            }
                        }

                        for (int i = 0; i < arrayList.size(); i++) {
                            if (arrayList.size() == 2){
                            try {
                                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
                                } else {
                                    BluetoothConnection connection = BluetoothPrintersConnections.selectFirstPaired();
                                    if (connection != null) {
                                        EscPosPrinter printer = new EscPosPrinter(connection, 203, 48f, 32);
                                        @SuppressLint("DefaultLocale") final String text =
                                                "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer,
                                                        getContext().getResources().getDrawableForDensity(R.drawable.logochicharroneria,
                                                                DisplayMetrics.DENSITY_LOW, getContext().getTheme())) + "</img>\n" +
                                                        "[L]\n" +
                                                        "[C]" + Splash.gNombre + "\n" +
                                                        "[C]" + Splash.gDireccion + "\n" +
                                                        "[C]" + "Departamento" + "\n" +
                                                        "[C]" + "NRC: " + Splash.gNrc + " NIT: " + Splash.gNit + "\n" +
                                                        "[C]================================\n" +
                                                        "[L]" + "Corte de cajero" +
                                                        "[C]================================\n" +
                                                        "[C]" + "No Caja: " + numeroCaja + "\n" +
                                                        "[C]" + "Cajero: " + nombreCajero + "\n" +
                                                        "[C]" + "Fecha negocio: " + fechaInicio + "\n" +
                                                        "[C]" + "Fecha Sistema: " + fechaFin + "\n" +
                                                        "[C]================================" +
                                                        "[R]" + "Monto inicial(+) $" + String.format("%.2f", montoInicialTotal) + "\n" +
                                                        "[L]" + arrayList + "\n" +
                                                        "[C]================================\n" +
                                                        "[C]" + "Totales " +
                                                        "[C]================================\n" +
                                                        "[R]" + "Venta Total " + "$" + String.format("%.2f", ventaTotal) + "\n" +
                                                        "[R]" + "Devolución total $" + String.format("%.2f", montoDevolucionTotal) + "\n" +
                                                        "[R]" + "Gastos $" +  String.format("%.2f", gastos) + "\n" +
                                                        "[R]" + "Total gastos $" +  String.format("%.2f", gastosTotal) + "\n" +
                                                        "[R]" + "Total a Entregar $" + String.format("%.2f", entregarTotal) + "\n" +
                                                        "[R]" + "Monto Declarado $" + String.format("%.2f", montoFisicoTotal) + "\n" +
                                                        "[C]================================\n" +
                                                        "[R]" + resultado + "\n" +
                                                        "[C]================================";

                                        if (AdaptadorCorteCaja.noImprimir == 0) {
                                            printer.printFormattedText(text);
                                            Fragment fragmento = new Home();
                                            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                            fragmentTransaction.replace(R.id.fragment_layout, fragmento);
                                            fragmentTransaction.addToBackStack(null);
                                            fragmentTransaction.commit();

                                        }

                                    } else {
                                        if (AdaptadorCorteCaja.noImprimir == 1) {
                                            boolean granded = checkPermissionForReadExtertalStorage();
                                            if (!granded) {
                                                requestPermissionForReadExtertalStorage();
                                            } else {
                                                createPDF();
                                            }
                                        }
                                        Toast.makeText(getContext(), "¡No hay una impresora conectada!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            } catch (Exception e) {
                                Log.e("APP", "No se puede imprimir, error: ", e);
                            }

                            }

                        }
                    } catch (JSONException e) {

                        e.printStackTrace();
                    }

                }, volleyError -> Toast.makeText(getContext(), "Ocurrio un error inesperado, Error: " + volleyError, Toast.LENGTH_SHORT).show());

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }

    public void createPDF() throws FileNotFoundException{

        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
        File file = new File(pdfPath, "ComprobanteCorteCaja.pdf");

            PdfWriter writer = new PdfWriter(file);
            PdfDocument pdfDocument = new PdfDocument(writer);
            Document document = new Document(pdfDocument);
            PageSize pageSize = new PageSize(300, 1200);
            pdfDocument.setDefaultPageSize(pageSize);

        Paragraph nombre = new Paragraph(Splash.gNombre + "\n").setTextAlignment(TextAlignment.CENTER);
        Paragraph direccion = new Paragraph(Splash.gDireccion + "\n").setTextAlignment(TextAlignment.CENTER);
        Paragraph departamento = new Paragraph("Departamento" + "\n").setTextAlignment(TextAlignment.CENTER);
        Paragraph nitnrc = new Paragraph("NRC: " + Splash.gNrc + " NIT: " + Splash.gNit + "\n").setTextAlignment(TextAlignment.CENTER);
        Paragraph linea1 = new Paragraph("================================" + "\n").setTextAlignment(TextAlignment.CENTER);
        Paragraph corte = new Paragraph("Corte de cajero" + "\n").setTextAlignment(TextAlignment.CENTER);
        Paragraph linea2 = new Paragraph("================================" + "\n").setTextAlignment(TextAlignment.CENTER);
        Paragraph noCaja = new Paragraph("No Caja: " + numeroCaja + "\n").setTextAlignment(TextAlignment.CENTER);
        Paragraph cajero = new Paragraph("Cajero: " + nombreCajero + "\n").setTextAlignment(TextAlignment.CENTER);
        Paragraph fecNegocio = new Paragraph("Fecha negocio: " + fechaInicio + "\n").setTextAlignment(TextAlignment.CENTER);
        Paragraph fecSistema = new Paragraph("Fecha Sistema: " + fechaFin + "\n").setTextAlignment(TextAlignment.CENTER);
        Paragraph linea3 = new Paragraph("======                                                                                                                                                                                                                                                                                                                                                                           ==========================" + "\n").setTextAlignment(TextAlignment.CENTER);
        Paragraph montoInit = new Paragraph("Monto inicial(+) $" + String.format("%.2f", montoInicialTotal) + "\n").setTextAlignment(TextAlignment.LEFT);
        Paragraph datos = new Paragraph(arrayList + "\n").setTextAlignment(TextAlignment.RIGHT);
        Paragraph linea4 = new Paragraph("================================" + "\n").setTextAlignment(TextAlignment.RIGHT);
        Paragraph totales = new Paragraph("Totales " + "\n").setTextAlignment(TextAlignment.RIGHT);
        Paragraph linea5 = new Paragraph("================================" + "\n").setTextAlignment(TextAlignment.RIGHT);
        Paragraph vTotal = new Paragraph("Venta Total " + "$" + String.format("%.2f", ventaTotal) + "\n").setTextAlignment(TextAlignment.RIGHT);
        Paragraph devTotal = new Paragraph("Devolución total $" + String.format("%.2f", montoDevolucionTotal) + "\n").setTextAlignment(TextAlignment.RIGHT);
        Paragraph gastos1 = new Paragraph("Gastos $" + String.format("%.2f", gastos) + "\n").setTextAlignment(TextAlignment.RIGHT);
        Paragraph tGastos = new Paragraph("Total gastos $" + String.format("%.2f", gastosTotal) + "\n").setTextAlignment(TextAlignment.RIGHT);
        Paragraph tEntregar = new Paragraph("Total a Entregar $" + String.format("%.2f", entregarTotal) + "\n").setTextAlignment(TextAlignment.RIGHT);
        Paragraph mDeclarado = new Paragraph("Monto Declarado $" + String.format("%.2f", montoFisicoTotal) + "\n").setTextAlignment(TextAlignment.RIGHT);
        Paragraph linea6 = new Paragraph("================================" + "\n").setTextAlignment(TextAlignment.RIGHT);
        Paragraph resultado1 = new Paragraph(resultado + "\n").setTextAlignment(TextAlignment.RIGHT);
        Paragraph linea7 = new Paragraph("================================" + "\n").setTextAlignment(TextAlignment.RIGHT);

        document.add(nombre);
        document.add(direccion);
        document.add(departamento);
        document.add(nitnrc);
        document.add(linea1);
        document.add(corte);
        document.add(linea2);
        document.add(noCaja);
        document.add(cajero);
        document.add(fecNegocio);
        document.add(fecSistema);
        document.add(montoInit);
        document.add(linea3);
        document.add(datos);
        document.add(linea4);
        document.add(totales);
        document.add(linea5);
        document.add(vTotal);
        document.add(devTotal);
        document.add(gastos1);
        document.add(tGastos);
        document.add(tEntregar);
        document.add(mDeclarado);
        document.add(linea6);
        document.add(resultado1);
        document.add(linea7);

        document.close();

        encodePDF();
        uploadDocument();

    }

    private void uploadDocument() {
        Call<ResponsePOJO> call = RetrofitClient.getInstance().getAPI().uploadDocument(VariablesGlobales.dataBase, encodedPDF);
        call.enqueue(new Callback<ResponsePOJO>() {
            @Override
            public void onResponse(@NonNull Call<ResponsePOJO> call, @NonNull Response<ResponsePOJO> response) {
                FragmentTransaction fr = getFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_layout, new ObtenerDetReporte());
                fr.commit();
            }

            @Override
            public void onFailure(@NonNull Call<ResponsePOJO> call, @NonNull Throwable t) {

            }
        });
    }

    void encodePDF() {
        File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/" + "ComprobanteCorteCaja.pdf")));
        Uri uri = Uri.fromFile(file);
        try {
            InputStream inputStream = getContext().getContentResolver().openInputStream(uri);
            byte[] pdfInBytes = new byte[inputStream.available()];
            inputStream.read(pdfInBytes);
            encodedPDF = Base64.encodeToString(pdfInBytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_PDF && resultCode == RESULT_OK && data != null){

            Uri path = data.getData();

            try {
                InputStream inputStream = getContext().getContentResolver().openInputStream(path);
                byte[] pdfInBytes = new byte[inputStream.available()];
                inputStream.read(pdfInBytes);
                encodedPDF = Base64.encodeToString(pdfInBytes, Base64.DEFAULT);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }


    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result =getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExtertalStorage() {
        try {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}