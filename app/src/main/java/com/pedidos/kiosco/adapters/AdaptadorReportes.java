package com.pedidos.kiosco.adapters;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
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
import androidx.appcompat.app.AlertDialog;
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
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.Splash;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.main.ObtenerReportes;
import com.pedidos.kiosco.model.Reportes;
import com.pedidos.kiosco.other.Ayuda;
import com.pedidos.kiosco.utils.Numero_a_Letra;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdaptadorReportes extends RecyclerView.Adapter<AdaptadorReportes.ReportesViewHolder> {

    Context cContext;
    public static List<Reportes> listaReportes;
    public static final int PERMISSION_BLUETOOTH = 1;
    String gNombre, sucursal, gFecha, gNombreProd, encodedPDF;
    double gTotal, gCantidad, gPrecioUni, gDesc, exento, gravado, noSujeto;
    int gIdFacMovimiento, noCaja,  hasta;
    StringBuilder sb1 = new StringBuilder("");
    public static int no_comprobante;
    Date d = new Date();
    SimpleDateFormat fecc = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.getDefault());
    String fechacComplString = fecc.format(d);
    SimpleDateFormat ho = new SimpleDateFormat("h:mm a");
    String horaString = ho.format(d);

    public AdaptadorReportes(Context cContext, List<Reportes> listaReportes) {

        this.cContext = cContext;
        AdaptadorReportes.listaReportes = listaReportes;
    }

    @NonNull
    @Override
    public ReportesViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv_reportes, viewGroup, false);
        return new ReportesViewHolder(v);

    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ReportesViewHolder reportesViewHolder, @SuppressLint("RecyclerView") int posicion) {

        reportesViewHolder.tvNombre.setText(listaReportes.get(posicion).getNombre());
        reportesViewHolder.tvFecha.setText(listaReportes.get(posicion).getFechaFinalizo());
        reportesViewHolder.tvPedido.setText(Integer.toString(listaReportes.get(posicion).getPedido()));

        reportesViewHolder.ver.setOnClickListener(v -> {

            Login.gIdPedido = listaReportes.get(posicion).getPedido();

            cContext.startActivity(new Intent(cContext, Ayuda.class));


        });

        if (Principal.gIdEstadoCliente == 2){
            reportesViewHolder.anular.setVisibility(View.VISIBLE);
            reportesViewHolder.reeImprimir.setVisibility(View.VISIBLE);
        }

        reportesViewHolder.reeImprimir.setOnClickListener(view -> {


        });

        reportesViewHolder.anular.setOnClickListener(view -> new AlertDialog.Builder(cContext)
                .setTitle("Confirmación")
                .setMessage("¿Esta seguro que desea anular el pedido?, ¡Esta acción ya no se puede revertir!")

                .setPositiveButton("CONFIRMAR", (dialog, which) -> {

                    Login.gIdPedidoReporte = listaReportes.get(posicion).getPedido();
                    ejecutarServicio("http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/actualizarEstado.php" + "?id_estado_prefactura=3" + "&id_prefactura=" + Login.gIdPedidoReporte);
                    Toast.makeText(cContext, "El pedido se ha anulado", Toast.LENGTH_SHORT).show();
                    cContext.startActivity(new Intent(cContext, ObtenerReportes.class));

                })
                .setNegativeButton("CANCELAR",
                        (dialog, which) -> dialog.dismiss()
                )
                .setIcon(android.R.drawable.ic_dialog_info)
                .show());


    }

    public abstract class numeroUno(){

    }

    public void obtenerDetMovimientos(){

        final ProgressDialog progressDialog = new ProgressDialog(cContext);
        progressDialog.setMessage("Por favor espera...");
        progressDialog.show();
        progressDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL);

        String url_det_pedido = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerDetMovimiento.php"+"?id_fac_movimiento=" + Login.gIdMovimiento;
        System.out.println(url_det_pedido);
        RequestQueue requestQueue = Volley.newRequestQueue(cContext);

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
                                                    //"[R]" + "Son: " + NumLetra.Convertir(numero, band())+"\n" +
                                                    //"[R]" + "Recibido: " + "$"+ String.format("%.2f",change) + "\n" +
                                                    //"[R]" + "Cambio: $" + cambio.getText().toString() + "\n\n" +
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
                                                    //"[R]" + "Son: " + NumLetra.Convertir(numero, band())+"\n" +
                                                    //"[R]" + "Recibido: " + "$"+ String.format("%.2f",change) + "\n" +
                                                    //"[R]" + "Cambio: $" + cambio.getText().toString() + "\n\n" +
                                                    "[C]" + "FB: " + Splash.gFacebook + "\n" +
                                                    "[C]Gracias por su compra :)\n";

                                    if (Splash.gImagen == 1) {
                                        printer.printFormattedText(text);
                                    }
                                    else {
                                        printer.printFormattedText(text2);
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

    public void ejecutarServicio (String URL){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {},
                error -> {});
        RequestQueue requestQueue = Volley.newRequestQueue(cContext);
        requestQueue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return listaReportes.size();
    }

    public static class ReportesViewHolder extends RecyclerView.ViewHolder {

   TextView tvNombre, tvFecha, tvPedido;
   Button reeImprimir, ver, anular;

        public ReportesViewHolder(@NonNull View itemView) {
            super(itemView);

        tvNombre = itemView.findViewById(R.id.nombreCliente);
        tvFecha = itemView.findViewById(R.id.tvFecha);
        tvPedido = itemView.findViewById(R.id.numeroPedido);
        reeImprimir = itemView.findViewById(R.id.reimprimir);
        ver = itemView.findViewById(R.id.verReporte);
        anular = itemView.findViewById(R.id.anular);

        }
    }

}