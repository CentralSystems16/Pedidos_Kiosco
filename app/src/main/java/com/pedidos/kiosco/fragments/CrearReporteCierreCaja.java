package com.pedidos.kiosco.fragments;

import static com.pedidos.kiosco.fragments.ResumenPago.PERMISSION_BLUETOOTH;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

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
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.Splash;
import com.pedidos.kiosco.VariablesGlobales;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CrearReporteCierreCaja extends Fragment {

    private Double gMonto, gMontoFisico, gMontoDevolucion, gMontoDiferencia, gMontoInicial;
    private int idTipoPago, numeroCaja;
    private String gTipoPago;
    private String nombreCajero, fechaInicio, fechaFin;
    private double ventTotal = 0.00, devTotal = 0.00, montoInicial = 0.00;
    private int cierre;
    private  String pago;
    ArrayList arrayList = new ArrayList();
    ArrayList arrayList2 = new ArrayList();
    String detalle = "";

    public CrearReporteCierreCaja(){
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle datosRecuperados = getArguments();
        if (datosRecuperados == null) {
            // No hay datos, manejar excepción
            return;
        }

        cierre = datosRecuperados.getInt("cierre");

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         obtenerTipoPagoFacTipoPagoCaja(cierre);
         return inflater.inflate(R.layout.crear_reporte_cierre_caja, container, false);

    }

    public void obtenerTipoPagoFacTipoPagoCaja(int idCierreCaja) {

        System.out.println("Entro al metodo");

        String url = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerTipoPagoFacTipoPagoCaja.php" + "?id_cierre_caja=" + idCierreCaja;
        System.out.println(url);
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
                            gMonto = jsonObject1.getDouble("monto");
                            gMontoDevolucion = jsonObject1.getDouble("monto_devolucion");
                            gMontoDiferencia = jsonObject1.getDouble("monto_diferencia");
                            gMontoInicial= jsonObject1.getDouble("monto_inicial");
                            gMontoFisico = jsonObject1.getDouble("monto_fisico");
                            numeroCaja = jsonObject1.getInt("no_caja");
                            nombreCajero = jsonObject1.getString("nombre_usuario");
                            fechaInicio  = jsonObject1.getString("fecha_ini");
                            fechaFin = jsonObject1.getString("fecha_fin");

                            detalle = gTipoPago + "\n" +
                                    "[C]================================\n" +
                                    "[R]" + "Venta(+) $" + gMonto + "\n" +
                                    "[R]" + "Devolución(-) $" + String.format("%.2f", gMontoDevolucion) + "\n" +
                                    "[R]" + "Encontrado(-) $" + String.format("%.2f", gMontoFisico) + "\n" +
                                    "[R]" + "Monto diferencia(-) $" + String.format("%.2f", gMontoDiferencia) + "\n" +
                                    "[C]================================\n";

                            arrayList.add(detalle);
                            System.out.println(arrayList);
                            Toast.makeText(getContext(), ""+arrayList.size(), Toast.LENGTH_SHORT).show();

                        }

                        ventTotal = ventTotal + gMonto;
                        devTotal = devTotal + gMontoDevolucion;
                        montoInicial = montoInicial + gMontoInicial;
                        for (int i = 0; i < arrayList.size(); i++) {
                            if (arrayList.size() == 2){
                            try {
                                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                                    ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
                                } else {
                                    BluetoothConnection connection = BluetoothPrintersConnections.selectFirstPaired();
                                    if (connection != null) {
                                        EscPosPrinter printer = new EscPosPrinter(connection, 203, 48f, 32);
                                        final String text =
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
                                                        "[R]" + "Monto inicial(+) $" + String.format("%.2f", montoInicial) + "\n" +
                                                        "[L]" + arrayList + "\n" +
                                                        "[C]================================\n" +
                                                        "[C]" + "Totales " +
                                                        "[C]================================\n" +
                                                        "[R]" + "Venta Total " + "$" + String.format("%.2f", ventTotal) + "\n" +
                                                        "[R]" + "Devolución total $" + String.format("%.2f", devTotal) + "\n" +
                                                        "[R]" + "Gastos $" + "0.00" + "\n" +
                                                        "[R]" + "Total gastos $" + "0.00" + "\n" +
                                                        "[R]" + "Monto a Entregar (Efectivo + Tarjeta) $" + "0.00" + "\n" +
                                                        "[R]" + "Monto Declarado $" + "0.00" + "\n" +
                                                        "[C]================================\n" +
                                                        "[R]" + "Sobrante $" + "0.00" + "\n" +
                                                        "[C]================================" +
                                                        "[C]" + "CAJA DESCUADRADA" +
                                                        "[C]================================";

                                        printer.printFormattedText(text);
                                        System.out.println(text);

                                    } else {
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