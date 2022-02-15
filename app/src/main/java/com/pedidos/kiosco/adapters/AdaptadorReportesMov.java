package com.pedidos.kiosco.adapters;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;
import static com.pedidos.kiosco.fragments.ResumenPago.PERMISSION_BLUETOOTH;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
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
import com.pedidos.kiosco.main.ObtenerDetReporte;
import com.pedidos.kiosco.main.ObtenerMovimientos;
import com.pedidos.kiosco.model.Movimientos;
import com.pedidos.kiosco.utils.Numero_a_Letra;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;

public class AdaptadorReportesMov extends RecyclerView.Adapter<AdaptadorReportesMov.ReportesViewHolder> {

    Context cContext;
    public static List<Movimientos> listaReportes;
    String sucursal, gFecha, gNombreProd;
    double gTotal, gCantidad, gPrecioUni, gDesc, exento, gravado, noSujeto, cambio, change;
    StringBuilder sb1 = new StringBuilder("");
    int gIdFacMovimiento, noCaja;

    public AdaptadorReportesMov(Context cContext, List<Movimientos> listaReportes) {

        this.cContext = cContext;
        AdaptadorReportesMov.listaReportes = listaReportes;
    }

    @NonNull
    @Override
    public ReportesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv_reportes_mov, viewGroup, false);
        return new ReportesViewHolder(v);

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ReportesViewHolder reportesViewHolder, @SuppressLint("RecyclerView") int posicion) {

        reportesViewHolder.tvNombre.setText(listaReportes.get(posicion).getNombreCliente());
        listaReportes.get(posicion).getNombreCliente();
        reportesViewHolder.tvFecha.setText(listaReportes.get(posicion).getFechaCreo());
        reportesViewHolder.tvComprobante.setText(listaReportes.get(posicion).getNombreSucursal());
        reportesViewHolder.numeroComprobante.setText(listaReportes.get(posicion).getNumeroComprobante());
        reportesViewHolder.tipoPago.setText(listaReportes.get(posicion).getTipoPago());

        reportesViewHolder.editar.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));
        reportesViewHolder.editar2.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));
        reportesViewHolder.editar3.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));

        //ver
        reportesViewHolder.editar3.setOnClickListener(view -> {
            Login.gIdPedidoReporte = listaReportes.get(posicion).getIdPrefactura();
            Login.gIdClienteReporte = listaReportes.get(posicion).getIdCliente();
            cContext.startActivity(new Intent(cContext, ObtenerDetReporte.class));

        });

        //reimprimir
        reportesViewHolder.editar.setOnClickListener(v -> {
            ObtenerMovimientos.idMov = listaReportes.get(posicion).getIdMov();
            obtenerDetMovimientos();
        });

        //anular
        reportesViewHolder.editar2.setOnClickListener(view -> {
            ObtenerMovimientos.idMov = listaReportes.get(posicion).getIdMov();
            String url = "http://34.239.139.117/android/kiosco/cliente/scripts/scripts_php/anularFacMovimientos.php" + "?id_fac_movimiento=" + ObtenerMovimientos.idMov;
            ejecutarServicio(url);
            System.out.println(url);
        });
    }

    public void ejecutarServicio (String URL){

        ProgressDialog progressDialog = new ProgressDialog(cContext, R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    Toast.makeText(cContext, "Comprobante anulado", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                },
                volleyError -> progressDialog.dismiss()
        );
        RequestQueue requestQueue = Volley.newRequestQueue(cContext);
        requestQueue.add(stringRequest);
    }


    public void obtenerDetMovimientos(){

        final ProgressDialog progressDialog = new ProgressDialog(cContext);
        progressDialog.setMessage("Por favor espera...");
        progressDialog.show();
        progressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        String url_det_pedido = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerDetMovimiento.php"+"?id_fac_movimiento=" + ObtenerMovimientos.idMov;
        RequestQueue requestQueue = Volley.newRequestQueue(cContext);

        @SuppressLint("DefaultLocale") StringRequest stringRequest = new StringRequest(Request.Method.GET,url_det_pedido,

                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray jsonArray = jsonObject.getJSONArray("DetMovimiento");
                        sb1 = new StringBuilder("");
                        gTotal = 0.00;
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
                            if (ContextCompat.checkSelfPermission(cContext, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions((Activity) cContext, new String[]{Manifest.permission.BLUETOOTH}, PERMISSION_BLUETOOTH);
                            } else {
                                BluetoothConnection connection = BluetoothPrintersConnections.selectFirstPaired();
                                if (connection != null) {
                                    EscPosPrinter printer = new EscPosPrinter(connection, 203, 48f, 32);

                                    final String text =
                                            "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer,
                                                    cContext.getResources().getDrawableForDensity(R.drawable.logokiosko,
                                                            DisplayMetrics.DENSITY_LOW, cContext.getTheme())) + "</img>\n" +
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
                                                    "[R]" + "Son: " + NumLetra.Convertir(numero, true)+"\n" +
                                                    "[R]" + "Recibido: " + "$"+ change + "\n" +
                                                    "[R]" + "Cambio: $" + cambio + "\n\n" +
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
                                                    "[R]" + "Son: " + NumLetra.Convertir(numero, true)+"\n" +
                                                    "[R]" + "Recibido: " + "$"+ String.format("%.2f",change) + "\n" +
                                                    "[R]" + "Cambio: $" + cambio + "\n\n" +
                                                    "[C]" + "FB: " + Splash.gFacebook + "\n" +
                                                    "[C]Gracias por su compra :)\n";

                                    if (Splash.gImagen == 1) {
                                        printer.printFormattedText(text);
                                        System.out.println(text);
                                    }
                                    else {
                                        printer.printFormattedText(text2);
                                        System.out.println(text2);
                                    }

                                } else {
                                    Toast.makeText(cContext, "¡No hay una impresora conectada!", Toast.LENGTH_SHORT).show();
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

    @Override
    public int getItemCount() {
        return listaReportes.size();
    }

    public static class ReportesViewHolder extends RecyclerView.ViewHolder {

   TextView tvNombre, tvFecha, tvComprobante, numeroComprobante, tipoPago;
   Button editar, editar2, editar3;

        public ReportesViewHolder(@NonNull View itemView) {
            super(itemView);

        tvNombre = itemView.findViewById(R.id.nombreMov);
        tvFecha = itemView.findViewById(R.id.tvFechaMov);
        tvComprobante = itemView.findViewById(R.id.comprobanteMov);
        editar = itemView.findViewById(R.id.reimprimir2);
        editar2 = itemView.findViewById(R.id.reimprimir1);
        editar3 = itemView.findViewById(R.id.reimprimir3);
        numeroComprobante = itemView.findViewById(R.id.comprobante);
        tipoPago = itemView.findViewById(R.id.tipoPago);

        }
    }

}