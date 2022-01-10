package com.pedidos.kiosco.pay;

import static com.pedidos.kiosco.other.ContadorProductos.GetDataFromServerIntoTextView.gCount;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
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
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.desing.EnviandoTicket;
import com.pedidos.kiosco.fragments.TicketDatos;
import com.pedidos.kiosco.pdf.ResponsePOJO;
import com.pedidos.kiosco.pdf.RetrofitClient;
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

public class ResumenPago extends AppCompatActivity {

    TextView cantidad, totalCompra, totalFinal, cambio;
    EditText etMoney;
    public static Double money;
    DecimalFormat formatoDecimal = new DecimalFormat("#");
    Date d = new Date();
    SimpleDateFormat fecc = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.getDefault());
    String fechacComplString = fecc.format(d);
    SimpleDateFormat ho = new SimpleDateFormat("h:mm a");
    String horaString = ho.format(d);
    String gNombre, sucursal, gFecha, gNombreProd, encodedPDF;
    Double gTotal, gCantidad, gPrecioUni;
    public static final int PERMISSION_BLUETOOTH = 1;
    StringBuilder sb2 = new StringBuilder("");
    private int REQ_PDF = 21;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resumen_pago);

        cantidad = findViewById(R.id.cantidad);
        totalCompra = findViewById(R.id.totalFinalPago);
        totalFinal = findViewById(R.id.totalDef);
        cantidad.setText(formatoDecimal.format(gCount));
        totalCompra.setText(String.format("%.2f",TicketDatos.gTotal));
        totalFinal.setText(String.format("%.2f", TicketDatos.gTotal));

        ImageButton back = findViewById(R.id.backArrow);
        back.setOnClickListener(view -> {

        });

        etMoney = findViewById(R.id.etMoney);
        cambio = findViewById(R.id.montoCambio);

        etMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (etMoney != null && etMoney.length() > 0) {

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

        Button pagar = findViewById(R.id.btnPagar);
        pagar.setOnClickListener(view -> {


                ejecutarServicio("http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/actualizarPrefac.php"
                        + "?id_estado_prefactura=2"
                        + "&fecha_finalizo=" + fechacComplString + " a las " + horaString
                        + "&id_prefactura=" + Login.gIdPedido);

                String url = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/actualizarMov.php"
                        +"?monto_pago="+ etMoney.getText().toString()
                        +"&monto_cambio=" + cambio.getText().toString()
                        +"&id_fac_movimiento="+Login.gIdMovimiento;
                ejecutarServicio2(url);

        });

        obtenerMovimientos();
        obtenerDetMovimientos();

    }

    public void ejecutarServicio (String URL){

        ProgressDialog progressDialog = new ProgressDialog(ResumenPago.this, R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    Login.gIdPedido = 0;
                    gCount = 0.00;
                    startActivity(new Intent(getApplicationContext(), EnviandoTicket.class));
            progressDialog.dismiss();
                },
                volleyError -> progressDialog.dismiss()
        );
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void obtenerMovimientos(){

        ProgressDialog progressDialog = new ProgressDialog(ResumenPago.this, R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url_pedido = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerMovimientos.php" + "?id_fac_movimiento=" + Login.gIdMovimiento;
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

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

    public void obtenerDetMovimientos(){

        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Por favor espera...");
        progressDialog.show();
        progressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        String url_det_pedido = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerDetMovimiento.php"+"?id_prefactura=" + Login.gIdPedido;

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url_det_pedido,

                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray jsonArray = jsonObject.getJSONArray("DetMovimiento");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            Double totalFila = jsonObject1.getDouble("monto") + jsonObject1.getDouble("monto_iva");
                            gTotal = gTotal + totalFila;

                                            jsonObject1.getInt("id_fac_det_movimiento");
                            gNombreProd = jsonObject1.getString("nombre_producto");
                            gCantidad = jsonObject1.getDouble("cantidad");
                            gPrecioUni =  jsonObject1.getDouble("precio_uni");

                        }

                        sb2.append(gNombreProd + "        Cant. " + gCantidad + "    PRECIO $" + gPrecioUni);
                        sb2.append("\n");
                        sb2.append("\n");

                        try {
                            if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions((Activity) getApplicationContext(), new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
                            } else {
                                BluetoothConnection connection = BluetoothPrintersConnections.selectFirstPaired();
                                if (connection != null) {
                                    EscPosPrinter printer = new EscPosPrinter(connection, 203, 48f, 32);

                                    final String text =
                                            "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer,
                                                    this.getApplicationContext().getResources().getDrawableForDensity(R.drawable.logokiosko,
                                                            DisplayMetrics.DENSITY_LOW, getApplicationContext().getTheme())) + "</img>\n" +
                                                    "[L]\n" +
                                                    "[L]" + gFecha + "\n\n" +
                                                    "[L]" + "Cliente: " + gNombre + "\n" +
                                                    "[C]================================\n" +

                                                    "[L]<b>"+ sb2.toString() +"</b>\n" +

                                                    "[C]--------------------------------\n" +
                                                    "[L]TOTAL $" + gTotal + "\n" +
                                                    "[C]--------------------------------\n" +
                                                    "[C]<barcode type='ean13' height='10'>202105160005</barcode>\n" +
                                                    "[C]--------------------------------\n" +
                                                    "[C]Gracias por su compra :)\n";

                                    printer.printFormattedText(text);
                                } else {
                                    Toast.makeText(getApplicationContext(), "¡No hay una impresora conectada!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        } catch (Exception e) {
                            Log.e("APP", "No se puede imprimir.", e);
                        }

                        uploadDocument();
                        encodePDF();

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

    public void ejecutarServicio2 (String URL){

        ProgressDialog progressDialog = new ProgressDialog(ResumenPago.this, R.style.Custom);
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
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
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
        try {
            InputStream inputStream = ResumenPago.this.getContentResolver().openInputStream(uri);
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
                InputStream inputStream = ResumenPago.this.getContentResolver().openInputStream(path);
                byte[] pdfInBytes = new byte[inputStream.available()];
                inputStream.read(pdfInBytes);
                encodedPDF = Base64.encodeToString(pdfInBytes, Base64.DEFAULT);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}