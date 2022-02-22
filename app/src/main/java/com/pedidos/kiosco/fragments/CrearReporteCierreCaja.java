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
            // No hay datos, manejar excepción
            return;
        }

        cierre = datosRecuperados.getInt("cierre");
        System.out.println("Corte en reimpresion: " + cierre);

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
                                    "[C]================================\n" +
                                    "[R]" + "Venta(+) $" + String.format("%.2f", monto) + "\n" +
                                    "[R]" + "Devolución(-) $" + String.format("%.2f", montoDevolucion) + "\n" +
                                    "[R]" + "Encontrado(-) $" + String.format("%.2f", montoFisico) + "\n" +
                                    "[R]" + "Monto diferencia(=) $" + String.format("%.2f", montoDiferencia) + "\n" +
                                    "[C]================================\n";

                            arrayList.add(detalle);
                            System.out.println(arrayList);
                            Toast.makeText(getContext(), ""+arrayList.size(), Toast.LENGTH_SHORT).show();

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

                                        printer.printFormattedText(text);
                                        System.out.println(text);
                                        Fragment fragmento = new Home();
                                        FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                        fragmentTransaction.replace(R.id.fragment_layout, fragmento);
                                        fragmentTransaction.addToBackStack(null);
                                        fragmentTransaction.commit();

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