package com.pedidos.kiosco.fragments;

import static com.pedidos.kiosco.fragments.ResumenPago.PERMISSION_BLUETOOTH;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
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
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.Splash;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.adapters.AdaptadorCorteCaja;
import com.pedidos.kiosco.main.CorteCaja;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import pl.droidsonroids.gif.GifImageView;

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

        String url = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerTipoPagoFacTipoPagoCaja.php" + "?id_cierre_caja=" + idCierreCaja;
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

        String url = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerVentasTipoPago.php" + "?id_tipo_pago="
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
                            if (arrayList.size() == arrayList.size()){
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

                                        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                                        File file = new File(pdfPath, CorteCaja.idCorteCaja + " CorteCaja.pdf");

                                        PdfWriter writer = null;
                                        try {
                                            writer = new PdfWriter(file);
                                        } catch (FileNotFoundException e) {
                                            e.printStackTrace();
                                        }

                                        assert writer != null;
                                        PdfDocument pdfDocument = new PdfDocument(writer);

                                        pdfDocument.setDefaultPageSize(PageSize.A3);

                                        Document document = new Document(pdfDocument);

                                        Paragraph nombre = new Paragraph(Splash.gNombre + "\n").setTextAlignment(TextAlignment.RIGHT);
                                        Paragraph direccion = new Paragraph(Splash.gDireccion + "\n").setTextAlignment(TextAlignment.RIGHT);
                                        Paragraph departamento = new Paragraph("Departamento" + "\n").setTextAlignment(TextAlignment.RIGHT);
                                        Paragraph nitnrc = new Paragraph("NRC: " + Splash.gNrc + " NIT: " + Splash.gNit + "\n").setTextAlignment(TextAlignment.RIGHT);
                                        Paragraph linea1 = new Paragraph("================================" + "\n").setTextAlignment(TextAlignment.RIGHT);
                                        Paragraph corte = new Paragraph("Corte de cajero" + "\n").setTextAlignment(TextAlignment.RIGHT);
                                        Paragraph linea2 = new Paragraph("================================" + "\n").setTextAlignment(TextAlignment.RIGHT);
                                        Paragraph noCaja = new Paragraph("No Caja: " + numeroCaja + "\n").setTextAlignment(TextAlignment.RIGHT);
                                        Paragraph cajero = new Paragraph("Cajero: " + nombreCajero + "\n").setTextAlignment(TextAlignment.RIGHT);
                                        Paragraph fecNegocio = new Paragraph("Fecha negocio: " + fechaInicio + "\n").setTextAlignment(TextAlignment.RIGHT);
                                        Paragraph fecSistema = new Paragraph("Fecha Sistema: " + fechaFin + "\n").setTextAlignment(TextAlignment.RIGHT);
                                        Paragraph linea3 = new Paragraph("================================" + "\n").setTextAlignment(TextAlignment.RIGHT);
                                        Paragraph montoInit = new Paragraph("Monto inicial(+) $" + String.format("%.2f", montoInicialTotal) + "\n").setTextAlignment(TextAlignment.RIGHT);
                                        Paragraph datos = new Paragraph(arrayList + "\n").setTextAlignment(TextAlignment.RIGHT);
                                        Paragraph linea4 = new Paragraph("================================" + "\n").setTextAlignment(TextAlignment.RIGHT);
                                        Paragraph totales = new Paragraph("Totales " + "\n").setTextAlignment(TextAlignment.RIGHT);
                                        Paragraph linea5 = new Paragraph("================================" + "\n").setTextAlignment(TextAlignment.RIGHT);
                                        Paragraph vTotal = new Paragraph("Venta Total " + "$" + String.format("%.2f", ventaTotal) + "\n").setTextAlignment(TextAlignment.RIGHT);
                                        Paragraph devTotal = new Paragraph("Devolución total $" + String.format("%.2f", montoDevolucionTotal) + "\n").setTextAlignment(TextAlignment.RIGHT);
                                        Paragraph gastos1 = new Paragraph("Gastos $" +  String.format("%.2f", gastos) + "\n").setTextAlignment(TextAlignment.RIGHT);
                                        Paragraph tGastos = new Paragraph("Total gastos $" +  String.format("%.2f", gastosTotal) + "\n").setTextAlignment(TextAlignment.RIGHT);
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


}