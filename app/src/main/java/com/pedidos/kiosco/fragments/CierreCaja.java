package com.pedidos.kiosco.fragments;

import static com.pedidos.kiosco.fragments.ResumenPago.PERMISSION_BLUETOOTH;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.Splash;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.adapters.AdaptadorCierreCaja;
import com.pedidos.kiosco.model.Pago;
import com.pedidos.kiosco.other.SumaMontoDevolucion;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class CierreCaja extends Fragment {

    public static Double montoFisico,
            gMonto, gMontoFisico, gMontoDevolucion, gMontoDiferencia, gMontoInicial,
    gMonto1, gMontoFisico1, gMontoDevolucion1, gMontoDiferencia1, gMontoInicial1;
    public static int lIdTipoPago;
    public static String lTipoPago, gTipoPago, gTipoPago1;
    RecyclerView rvLista;
    ArrayList<Pago> pago;
    AdaptadorCierreCaja adaptador;
    View vista;
    Date d = new Date();
    SimpleDateFormat fecc = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.getDefault());
    String fechacComplString = fecc.format(d);
    SimpleDateFormat ho = new SimpleDateFormat("h:mm a");
    String horaString = ho.format(d);
    public static Button aceptar;
    public static Double fondoInit;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        vista = inflater.inflate(R.layout.cierre_caja_fragment, container, false);

        obtenerCierreCaja();

        rvLista = vista.findViewById(R.id.rvListaPagoCierre);
        rvLista.setLayoutManager(new LinearLayoutManager(getActivity()));

        pago = new ArrayList<>();

        aceptar = vista.findViewById(R.id.btnAceptarTipoPago);
        aceptar.setEnabled(false);
        aceptar.setOnClickListener(view -> {

            new AlertDialog.Builder(getContext())
                    .setTitle("Confirmación de cierre")
                    .setMessage("Esta seguro de cerrar la caja")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {

                        ejecutarServicio("http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/actualizarCierreCaja.php"
                                + "?fecha_fin=" + fechacComplString + " a las " + horaString
                                + "&state=2"
                                + "&id_cierre_caja=" + VariablesGlobales.gIdCierreCaja);

                        ejecutarServicio("http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/insertarCierreMovCaja.php"
                                + "?id_cierre_caja=" +VariablesGlobales.gIdCierreCaja
                                + "&id_usuario=" + Login.gIdUsuario
                                + "&monto_inicial=" + fondoInit
                                + "&monto_venta=0.00"
                                + "&monto_gastos=0.00"
                                + "&monto_fisico=0.00"
                                + "&diferencia=0.00"
                                + "&monto_devolucion=" + SumaMontoDevolucion.sumaMontoDevolucion
                                + "&fecha_movimiento=" + fechacComplString + " a las " + horaString);

                    }).setNegativeButton(android.R.string.no, (dialog, which) ->{})
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();

        });

        Button cerrar = vista.findViewById(R.id.btnCancelarTipoPago);
        cerrar.setOnClickListener(view -> {

            ejecutarServicio("http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/eliminarFacTipoPago.php" + "?id_cierre_caja=" + VariablesGlobales.gIdCierreCaja);

        });

        obtenerTipoPagoLiquidar();

        return vista;
    }

    public void obtenerTipoPagoLiquidar() {

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerTipoPagoLiquidar.php";

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("TipoPago");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            pago.add(
                                    new Pago(

                            jsonObject1.getInt("id_tipo_pago"),
                            jsonObject1.getString("tipo_pago"),
                            jsonObject1.getInt("activo"),
                            jsonObject1.getString("imagen")));

                        }

                        adaptador = new AdaptadorCierreCaja(getContext(), pago);
                        rvLista.setAdapter(adaptador);

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

    public void obtenerFacTipoPagoCaja() {

        String url = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerTipoPagoCaja.php" + "?id_tipo_pago=1";
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
                            gMontoInicial = jsonObject1.getDouble("monto_inicial");
                            gMontoFisico = jsonObject1.getDouble("monto_fisico");

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

    public void obtenerFacTipoPago() {

        String url = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerTipoPagoCaja.php" + "?id_tipo_pago=2";
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("TipoPago");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            gTipoPago1 = jsonObject1.getString("tipo_pago");
                            gMonto1 = jsonObject1.getDouble("monto");
                            gMontoDevolucion1 = jsonObject1.getDouble("monto_devolucion");
                            gMontoDiferencia1 = jsonObject1.getDouble("monto_diferencia");
                            gMontoInicial1 = jsonObject1.getDouble("monto_inicial");
                            gMontoFisico1 = jsonObject1.getDouble("monto_fisico");

                        }

                        double ventTotal = gMonto+gMonto1;
                        double devTotal = gMontoDevolucion+gMontoDevolucion1;

                        try {
                            if (ContextCompat.checkSelfPermission(requireActivity(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
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
                                                    "[C]" + "La Chicharroneria Mix" + "\n" +
                                                    "[C]" + "Dirección exacta" + "\n" +
                                                    "[C]" + "Departamento" + "\n" +
                                                    "[C]" + "NRC: " + Splash.gNrc + " NIT: " + Splash.gNit + "\n" +
                                                    "[C]================================\n" +
                                                    "[L]" + "Corte de cajero" +
                                                    "[C]================================\n" +
                                                    "[C]" + "No Caja: " + "1" + "\n" +
                                                    "[C]" + "Cajero: " + Login.nombre + "\n" +
                                                    "[C]" + "Fecha negocio: " + "Fecha negocio" + "\n" +
                                                    "[C]" + "Fecha Sistema: " + fechacComplString + " a las " + horaString + "\n" +
                                                    "[C]================================" +
                                                    "[C]" + "Efectivo" + "\n" +
                                                    "[R]" + "Monto inicial(+) $" + String.format("%.2f", gMontoInicial) +
                                                    "[C]================================\n" +
                                                    "[R]" + "Venta(+) $" + String.format("%.2f", gMonto) + "\n" +
                                                    "[R]" + "Anticipo en corte(+) $" + "0.00" + "\n" +
                                                    "[R]" + "Anticipo Actual S/Vta(-) $" + "0.00" + "\n" +
                                                    "[R]" + "Anticipo anterior S/Vta(-) $" + "0.00" + "\n" +
                                                    "[R]" + "Devolución(-) $" + String.format("%.2f", gMontoDevolucion) + "\n" +
                                                    "[R]" + "Encontrado(-) $" + "0.00" +
                                                    "[C]================================\n" +
                                                    "[R]" + "Diferencia Efectivo(=) $" + String.format("%.2f", gMontoDiferencia) + "\n" +
                                                    "[C]" + "Targeta " + "\n" +
                                                    "[R]" + "Venta(+) " + "$" + String.format("%.2f", gMonto1) + "\n" +
                                                    "[R]" + "Devolución(-) $" + String.format("%.2f", gMontoDevolucion1) + "\n" +
                                                    "[R]" + "Encontrado(-) $" + "0.00" + "\n" +
                                                    "[R]" + "Diferencia de tarjeta(=) $" + String.format("%.2f", gMontoDiferencia1) + "\n" +
                                                    "[C]" + "Totales " +
                                                    "[C]================================\n" +
                                                    "[R]" + "Venta Total " + "$" + String.format("%.2f", ventTotal) + "\n" +
                                                    "[R]" + "Devolución total $" + String.format("%.2f", devTotal) + "\n" +
                                                    "[R]" + "Gastos $" + "0.00" + "\n" +
                                                    "[R]" + "Total gastos $" + "0.00" + "\n" +
                                                    "[R]" + "Monto a Entregar (Efectivo + Tarjeta) $" + "0.00" + "\n" +
                                                    "[R]" + "Monto Declarado $" + "0.00" + "\n" +
                                                    "[C]================================\n" +
                                                    "[R]" + "Sobrante $" + "0.00" + "\n"+
                                                    "[C]================================" +
                                                    "[C]" + "CAJA DESCUADRADA"+
                                                    "[C]================================";

                                    printer.printFormattedText(text);
                                    System.out.println(text);

                                    FragmentTransaction fr = getActivity().getSupportFragmentManager().beginTransaction();
                                    fr.replace(R.id.fragment_layout, new Home());
                                    fr.commit();

                                } else {
                                    Toast.makeText(getContext(), "¡No hay una impresora conectada!", Toast.LENGTH_SHORT).show();
                                }

                            }

                        } catch (Exception e) {
                            Log.e("APP", "No se puede imprimir, error: ", e);
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

    public void obtenerCierreCaja(){

        String url_pedido = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerIdCierre.php" + "?id_usuario=" + Login.gIdUsuario;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url_pedido,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Caja");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            fondoInit = jsonObject1.getDouble("fondo_inicial");

                        }

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

    public void ejecutarServicio (String URL){

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    obtenerFacTipoPagoCaja();
                    obtenerFacTipoPago();
                    progressDialog.dismiss();
                },
                volleyError -> {
                    progressDialog.dismiss();
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        requestQueue.add(stringRequest);
    }
}