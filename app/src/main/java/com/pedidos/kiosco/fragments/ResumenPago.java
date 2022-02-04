package com.pedidos.kiosco.fragments;

import static android.app.Activity.RESULT_OK;
import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;
import static com.pedidos.kiosco.other.ContadorProductos.GetDataFromServerIntoTextView.gCount;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;
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
import com.pedidos.kiosco.desing.EnviandoTicket;
import com.pedidos.kiosco.other.ContadorProductos2;
import com.pedidos.kiosco.other.InsertarDetMovimientos;
import com.pedidos.kiosco.other.InsertarMovimientos;
import com.pedidos.kiosco.pdf.ResponsePOJO;
import com.pedidos.kiosco.pdf.RetrofitClient;
import com.pedidos.kiosco.utils.Numero_a_Letra;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResumenPago extends Fragment {

    TextView totalCompra, totalFinal, cambio;
    EditText etMoney;
    public static TextView cantidad;
    public static Double money, change;
    DecimalFormat formatoDecimal = new DecimalFormat("#");
    Date d = new Date();
    SimpleDateFormat fecc = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.getDefault());
    String fechacComplString = fecc.format(d);
    SimpleDateFormat ho = new SimpleDateFormat("h:mm a");
    String horaString = ho.format(d);
    String gNombre, sucursal, gFecha, gNombreProd, encodedPDF;
    double gTotal, gCantidad, gPrecioUni, gDesc, exento, gravado, noSujeto;
    int gIdFacMovimiento, noCaja,  hasta;
    public static final int PERMISSION_BLUETOOTH = 1;
    StringBuilder sb1 = new StringBuilder("");
    public static int no_comprobante;
    private int REQ_PDF = 21;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View vista = inflater.inflate(R.layout.resumen_pago_fragment, container, false);


        /*File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
        File file = new File(dir, Login.gIdPedido + " Examen.pdf");
        boolean deleted = file.delete();*/

        if(ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,},
                    1000);
        }

        Toolbar toolbar = vista.findViewById(R.id.toolbarPago);
        toolbar.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));

        TextView pedido, pago;

        pedido = vista.findViewById(R.id.tvPedido);
        pago = vista.findViewById(R.id.tvPago);

        pedido.setTextColor((Color.rgb(gRed, gGreen, gBlue)));
        pago.setTextColor((Color.rgb(gRed, gGreen, gBlue)));

        obtenerCaja();

        cantidad = vista.findViewById(R.id.cantidad);
        totalCompra = vista.findViewById(R.id.totalFinalPago);
        totalFinal = vista.findViewById(R.id.totalDef);
        new ContadorProductos2.GetDataFromServerIntoTextView(getContext()).execute();
        cantidad.setText(formatoDecimal.format(gCount));
        totalCompra.setText(String.format("%.2f",TicketDatos.gTotal));
        totalFinal.setText(String.format("%.2f", TicketDatos.gTotal));

        ImageButton back = vista.findViewById(R.id.backArrow);
        back.setOnClickListener(view -> {

            FragmentTransaction fr = getFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new TicketDatos());
            fr.commit();

        });

        etMoney = vista.findViewById(R.id.etMoney);
        cambio = vista.findViewById(R.id.montoCambio);

        etMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (etMoney != null && etMoney.length() > 0) {

                        change = Double.parseDouble(etMoney.getText().toString());
                        money = Double.parseDouble(etMoney.getText().toString());
                        money = money - TicketDatos.gTotal;
                        cambio.setText(String.format("%.2f", money));

                    } else {
                        cambio.setText("0.00");
                    }

                } catch(NumberFormatException ex){ // handle your exception

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Button pagar = vista.findViewById(R.id.btnPagar);
        pagar.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));
        pagar.setOnClickListener(view -> {


            if (etMoney.getText().toString().equals("")){
                Toast.makeText(getContext(), "Por favor, ingresa el monto.", Toast.LENGTH_SHORT).show();
            }
            else {

                obtenerAutFiscal();
                obtenerMovimientos();

                obtenerDetMovimientos();
                encodePDF();
                uploadDocument();
            }
        });

        return vista;
    }

    public void ejecutarServicio (String URL){

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    gCount = 0.00;
                    progressDialog.dismiss();
                },
                volleyError -> progressDialog.dismiss()
        );
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    public void obtenerMovimientos(){

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url_pedido = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerMovimientos.php" + "?id_fac_movimiento=" + Login.gIdMovimiento;
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        System.out.println(url_pedido);
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url_pedido,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Movimientos");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            gNombre = jsonObject1.getString("nombre_cliente");
                            gFecha = jsonObject1.getString("fecha_creo");
                            sucursal = jsonObject1.getString("nombre_sucursal");
                            exento = jsonObject1.getDouble("monto_exento");
                            gravado = jsonObject1.getDouble("monto_gravado");
                            noSujeto = jsonObject1.getDouble("monto_no_sujeto");
                        }

                        progressDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }, Throwable::printStackTrace
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }

    public void obtenerAutFiscal(){

        String url_pedido = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerAutFiscal.php" + "?id_tipo_comprobante=4";
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        StringRequest stringRequest = new StringRequest(Request.Method.GET,url_pedido,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("AutFiscal");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            Login.gIdAutFiscal = jsonObject1.getInt("id_aut_fiscal");
                            hasta = jsonObject1.getInt("hasta");
                        }

                        obtenerNoComprobante();

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

    public void obtenerDetMovimientos(){

        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setMessage("Por favor espera...");
        progressDialog.show();
        progressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        String url_det_pedido = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerDetMovimiento.php"+"?id_fac_movimiento=" + Login.gIdMovimiento;
        System.out.println(url_det_pedido);
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        @SuppressLint("DefaultLocale") StringRequest stringRequest = new StringRequest(Request.Method.GET,url_det_pedido,

                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray jsonArray = jsonObject.getJSONArray("DetMovimiento");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            Double totalFila = jsonObject1.getDouble("monto") + jsonObject1.getDouble("monto_iva");
                            gTotal = gTotal + totalFila;
                            String.format("%.2f", gTotal);
                            jsonObject1.getInt("id_fac_det_movimiento");
                            gNombreProd = jsonObject1.getString("nombre_producto");
                            gCantidad = jsonObject1.getDouble("cantidad");
                            gPrecioUni =  jsonObject1.getDouble("precio_uni");
                            Double total = gCantidad * gPrecioUni;
                            gDesc = jsonObject1.getDouble("monto_desc");
                            gIdFacMovimiento = jsonObject1.getInt("id_fac_movimiento");
                            sb1.append(gNombreProd+"\n" + " $" + gCantidad +  " " + String.format("%.2f", gPrecioUni) + " $" + String.format("%.2f",total) + " G");
                            sb1.append("\n");
                        }

                        Numero_a_Letra NumLetra = new Numero_a_Letra();
                        String numero;
                        numero = String.valueOf(gTotal);

                        try {
                            if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions((Activity) getContext(), new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
                            } else {
                                BluetoothConnection connection = BluetoothPrintersConnections.selectFirstPaired();
                                if (connection != null) {
                                    EscPosPrinter printer = new EscPosPrinter(connection, 203, 48f, 32);

                                    final String text =
                                            "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer,
                                                    getContext().getResources().getDrawableForDensity(R.drawable.logokiosko,
                                                            DisplayMetrics.DENSITY_LOW, getContext().getTheme())) + "</img>\n" +
                                                    "[L]\n" +
                                                    "[C]" + Splash.gNombre + "\n" +
                                                    "[C]" + Splash.gDireccion + "\n" +
                                                    "[C]" + "Sucursal: " + sucursal + "\n" +
                                                    "[C]" + "Teléfono: " + Splash.gTelefono + "\n" +
                                                    "[C]" + "NRC: " + Splash.gNrc + " NIT: " + Splash.gNit + "\n" +
                                                    "[C]" + "Caja: " + noCaja + " Tiquete: " + Login.gIdMovimiento + "\n" +
                                                    "[C]" + "Atendio: " + Login.nombre + "\n" +
                                                    "[L]" + "Fecha: " + gFecha + "\n" +
                                                    "[C]================================\n" +
                                                    "[L]" + sb1.toString() +
                                                    "[R]" + "---------------------------" + "\n" +
                                                    "[R]" + "SubTotal $" + String.format("%.2f",gTotal) + "\n" +
                                                    "[R]" + "Desc $" + String.format("%.2f", gDesc) + "\n" +
                                                    "[R]" + "Exento $" + String.format("%.2f",exento) + "\n" +
                                                    "[R]" + "Gravado $" + String.format("%.2f",gravado) + "\n" +
                                                    "[R]" + "Ventas no sujetas $" + String.format("%.2f",noSujeto) + "\n" +
                                                    "[R]" + "---------------------------" + "\n" +
                                                    "[R]" + "Total a pagar $" + String.format("%.2f",gTotal) + "\n" +
                                                    "[R]" + "Son: " + NumLetra.Convertir(numero, band())+"\n" +
                                                    "[R]" + "Recibido: " + "$"+ String.format("%.2f",change) + "\n" +
                                                    "[R]" + "Cambio: $" + cambio.getText().toString() + "\n\n" +
                                                    "[C]" + "FB: " + Splash.gFacebook + "\n" +
                                                    "[C]Gracias por su compra :)\n";

                                    final String text2 =
                                            "[L]\n" +
                                                    "[C]" + Splash.gNombre + "\n" +
                                                    "[C]" + Splash.gDireccion + "\n" +
                                                    "[C]" + "Sucursal: " + sucursal + "\n" +
                                                    "[C]" + "Teléfono: " + Splash.gTelefono + "\n" +
                                                    "[C]" + "NRC: " + Splash.gNrc + " NIT: " + Splash.gNit + "\n" +
                                                    "[C]" + "Caja: " + noCaja + " Tiquete: " + Login.gIdMovimiento + "\n" +
                                                    "[C]" + "Atendio: " + Login.nombre + "\n" +
                                                    "[L]" + "Fecha: " + gFecha + "\n" +
                                                    "[C]================================\n" +
                                                    "[L]" + "Cant" + " Descripción" + "[R]" + "P/Un" + "[R]" + "Total" + "\n" +
                                                    "[C]================================\n" +
                                                    "[L]" + sb1.toString() +
                                                    "[R]" + "---------------------------" + "\n" +
                                                    "[R]" + "SubTotal $" + String.format("%.2f",gTotal) + "\n" +
                                                    "[R]" + "Desc $" + String.format("%.2f", gDesc) + "\n" +
                                                    "[R]" + "Exento $" + String.format("%.2f",exento) + "\n" +
                                                    "[R]" + "Gravado $" + String.format("%.2f",gravado) + "\n" +
                                                    "[R]" + "Ventas no sujetas $" + String.format("%.2f",noSujeto) + "\n" +
                                                    "[R]" + "---------------------------" + "\n" +
                                                    "[R]" + "Total a pagar $" + String.format("%.2f",gTotal) + "\n" +
                                                    "[R]" + "Son: " + NumLetra.Convertir(numero, band())+"\n" +
                                                    "[R]" + "Recibido: " + "$"+ String.format("%.2f",change) + "\n" +
                                                    "[R]" + "Cambio: $" + cambio.getText().toString() + "\n\n" +
                                                    "[C]" + "FB: " + Splash.gFacebook + "\n" +
                                                    "[C]Gracias por su compra :)\n";

                                    if (Splash.gImagen == 1) {
                                        printer.printFormattedText(text);
                                    }
                                    else {
                                        printer.printFormattedText(text2);
                                    }

                                } else {
                                    Toast.makeText(getContext(), "¡No hay una impresora conectada!", Toast.LENGTH_SHORT).show();
                                }

                            }

                        } catch (Exception e) {
                            Log.e("APP", "No se puede imprimir, error: ", e);
                        }

                        progressDialog.dismiss();

                    } catch (JSONException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                }, Throwable::printStackTrace
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }

    public void obtenerCaja(){

        String URL_REPORTES = "http://" + VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/obtenerCaja.php";

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_REPORTES,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Caja");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            noCaja = jsonObject1.getInt("no_caja");

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, volleyError -> Toast.makeText(getContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show()
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }

    private static boolean band(){
        if ( Math.random() > .5) {
            return true;
        }else{
            return false;
        }
    }

    public void obtenerNoComprobante(){

        String URL_REPORTES = "http://" + VariablesGlobales.host +
                "/android/kiosco/cliente/scripts/scripts_php/obtenerComprobante.php"
                + "?id_aut_fiscal=" + Login.gIdAutFiscal;

        System.out.println(URL_REPORTES);

        RequestQueue requestQueue = Volley.newRequestQueue(getContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_REPORTES,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Comprobante");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            no_comprobante = jsonObject1.getInt("numero_comprobante")+1;

                        }

                        if (no_comprobante == 0){
                            no_comprobante = 1;
                        }

                        if (no_comprobante <= hasta) {
                            new InsertarMovimientos(getContext()).execute();
                            new InsertarDetMovimientos(getContext()).execute();
                            ejecutarServicio("http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/actualizarPrefac.php"
                                    + "?id_estado_prefactura=2"
                                    + "&fecha_finalizo=" + fechacComplString + " a las " + horaString
                                    + "&id_prefactura=" + Login.gIdPedido);
                            ejecutarServicio("http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/actualizarMov.php"
                                    + "?monto_pago=" + etMoney.getText().toString()
                                    + "&monto_cambio=" + cambio.getText().toString()
                                    + "&id_fac_movimiento=" + Login.gIdMovimiento);
                            startActivity(new Intent(getContext(), EnviandoTicket.class));
                        }
                        else {

                            String url = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/actualizarActivoFiscal.php"
                                    + "?id_aut_fiscal=" + Login.gIdAutFiscal;
                            ejecutarServicio(url);

                            new AlertDialog.Builder(getContext())
                                    .setTitle("Autorización finalizada")
                                    .setMessage("La autorización fiscal ha finalizado, por favor comuniquese con su administrador")
                                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                                    })
                                    .setIcon(android.R.drawable.ic_dialog_info)
                                    .show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }, volleyError -> Toast.makeText(getContext(), volleyError.getMessage(), Toast.LENGTH_LONG).show()
        );

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);

    }

    public void ejecutarServicio2 (String URL){

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {

                    progressDialog.dismiss();
                },
                volleyError -> {
                    progressDialog.dismiss();
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(getContext());
        requestQueue.add(stringRequest);
    }

    private void uploadDocument() {

        Call<ResponsePOJO> call = RetrofitClient.getInstance().getAPI().uploadDocument(encodedPDF, Login.gIdPedido, Login.gIdCliente);
        call.enqueue(new Callback<ResponsePOJO>() {
            @Override
            public void onResponse(@NonNull Call<ResponsePOJO> call, @NonNull Response<ResponsePOJO> response) {

            }

            @Override
            public void onFailure(@NonNull Call<ResponsePOJO> call, @NonNull Throwable t) {

            }
        });
    }

    void encodePDF() {
        File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/" + Login.gIdPedido + " Examen.pdf")));
        Uri uri = Uri.fromFile(file);
        System.out.println("URL: " + uri);
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
    
}