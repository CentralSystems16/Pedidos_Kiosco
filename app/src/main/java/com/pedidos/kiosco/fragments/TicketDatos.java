package com.pedidos.kiosco.fragments;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gFacebook;
import static com.pedidos.kiosco.Splash.gFoto;
import static com.pedidos.kiosco.Splash.gGif;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gImagenSplah;
import static com.pedidos.kiosco.Splash.gRecBlue;
import static com.pedidos.kiosco.Splash.gRecGreen;
import static com.pedidos.kiosco.Splash.gRecRed;
import static com.pedidos.kiosco.Splash.gRed;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.borders.GrooveBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.VerticalAlignment;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.Splash;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.model.DetReporte;
import com.pedidos.kiosco.adapters.AdapProdReport;
import com.pedidos.kiosco.other.ContadorProductos;
import com.pedidos.kiosco.pay.ResumenPago;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import pl.droidsonroids.gif.GifImageView;

public class TicketDatos extends Fragment {

    TextView fechaReporte, totalItem, horaReporte, nombreTicket, etBuscador;
    RecyclerView rvProductos;
    AdapProdReport adaptador;
    List<DetReporte> listaProdReport;
    ImageView btnConfirmarEnviar;
    @SuppressLint("StaticFieldLeak")
    public static TextView totalFinal;
    public static Double gTotal = 0.00;
    public static String gNombre, sucursal;
    String email;
    Date d = new Date();
    SimpleDateFormat fecc = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.getDefault());
    String fechacComplString = fecc.format(d);
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat ho = new SimpleDateFormat("h:mm a");
    String horaString = ho.format(d);
    GifImageView gato;
    TextView noHay, anular;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_carrito, container, false);

        nombreTicket = vista.findViewById(R.id.nombreReporte);

        etBuscador = vista.findViewById(R.id.etBuscadorTicket);
        etBuscador.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));
        etBuscador.setOnClickListener(view -> {
            FragmentTransaction fr = getFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new Categorias());
            fr.commit();
        });

        LinearLayout ln1 = vista.findViewById(R.id.linearTicket);
        LinearLayout ln2 = vista.findViewById(R.id.linearTicket2);
        Toolbar ln3 = vista.findViewById(R.id.toolbarTicket);
        TextView total = vista.findViewById(R.id.tvPagar);

        total.setTextColor((Color.rgb(gRed, gGreen, gBlue)));
        ln1.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));
        ln2.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));
        ln3.setBackgroundColor(Color.rgb(gRecRed, gRecGreen, gRecBlue));

        anular = vista.findViewById(R.id.anularPedido);

        anular.setOnClickListener(view -> new AlertDialog.Builder(requireContext())
                .setTitle("Confirmación")
                .setMessage("¿Seguro que quieres anular el pedido?")
                .setPositiveButton("Si", (dialog, which) -> ejecutarServicio("http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/eliminarPedido.php" + "?id_prefactura=" + Login.gIdPedido))
                .setNegativeButton("Cancelar", (dialog, which) -> {
                })
                .setIcon(android.R.drawable.ic_dialog_info)
                .show());

        gato = vista.findViewById(R.id.gato3);
        Glide.with(TicketDatos.this).load(gGif).into(gato);
        gato.setVisibility(View.GONE);
        noHay = vista.findViewById(R.id.noHay);
        noHay.setVisibility(View.GONE);

        totalFinal = vista.findViewById(R.id.TotalFinal);
        totalItem = vista.findViewById(R.id.totalItem);
        fechaReporte = vista.findViewById(R.id.fechaReporte);
        horaReporte = vista.findViewById(R.id.horaReporte);
        fechaReporte.setText((fechacComplString));
        horaReporte.setText(horaString);

        btnConfirmarEnviar = vista.findViewById(R.id.cerdo);
        Glide.with(TicketDatos.this).load(gFoto).into(btnConfirmarEnviar);
        btnConfirmarEnviar.setOnClickListener(view -> {
            try {
                createPDF();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            startActivity(new Intent(getContext(), ResumenPago.class));
        });

        rvProductos = vista.findViewById(R.id.rvProductos);
        rvProductos.setLayoutManager(new LinearLayoutManager(getContext()));

        listaProdReport = new ArrayList<>();

        adaptador = new AdapProdReport(getContext(), listaProdReport);
        rvProductos.setAdapter(adaptador);

        generarPedido();
        generarDetPedido();

        if (Login.gIdPedido == 0){
            gato.setVisibility(View.VISIBLE);
            noHay.setVisibility(View.VISIBLE);
            btnConfirmarEnviar.setVisibility(View.INVISIBLE);
        }

        return vista;
    }

    public void ejecutarServicio (String URL){

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    ContadorProductos.GetDataFromServerIntoTextView.gCount = 0.00;
                    gTotal = 0.0;
                    Login.gIdPedido = 0;
                    totalFinal.setText("0.00");
                    listaProdReport.clear();
                    adaptador.notifyDataSetChanged();
                    if (listaProdReport.isEmpty()) {
                        gato.setVisibility(View.VISIBLE);
                        noHay.setVisibility(View.VISIBLE);
                        btnConfirmarEnviar.setVisibility(View.INVISIBLE);
                        btnConfirmarEnviar.setVisibility(View.INVISIBLE);
                        anular.setEnabled(false);
                        progressDialog.dismiss();
                    }
                },
                error -> progressDialog.dismiss());
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

    public void generarPedido() {

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url_pedido = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerPedido.php" + "?id_prefactura=" + Login.gIdPedido;
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url_pedido,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Reporte");

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            gNombre = jsonObject1.getString("nombre_cliente");
                            nombreTicket.setText(gNombre);
                            jsonObject1.getString("fecha_creo");
                            email = jsonObject1.getString("email_cliente");
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

    public void generarDetPedido() {

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String url_det_pedido = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerDetPedido.php"+"?id_prefactura=" + Login.gIdPedido;

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET,url_det_pedido,

                response -> {

                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        JSONArray jsonArray = jsonObject.getJSONArray("Detreporte");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            Double totalFila = jsonObject1.getDouble("monto") + jsonObject1.getDouble("monto_iva");
                            gTotal = gTotal + totalFila;

                            totalFinal.setText(String.format("%.2f", gTotal));

                            listaProdReport.add(

                                    new DetReporte(

                                            Login.gIdFacDetPedido = jsonObject1.getInt("id_det_prefactura"),
                                            jsonObject1.getString("nombre_producto"),
                                            jsonObject1.getDouble("cantidad_producto"),
                                            jsonObject1.getDouble("precio_venta"),
                                            jsonObject1.getDouble("monto"),
                                            jsonObject1.getDouble("monto_iva")));

                        }

                        adaptador = new AdapProdReport(getContext(), listaProdReport);
                        rvProductos.setAdapter(adaptador);

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

    public void createPDF() throws FileNotFoundException {

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {

                String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                File file = new File(pdfPath, Login.gIdPedido + " Examen.pdf");

                PdfWriter writer = null;
                try {
                    writer = new PdfWriter(file);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                assert writer != null;
                PdfDocument pdfDocument = new PdfDocument(writer);
                com.itextpdf.layout.Document document = new Document(pdfDocument);

                Drawable d = getActivity().getDrawable(R.drawable.logokiosko);
                Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byte[] bitmapData = stream.toByteArray();

                ImageData imageData = ImageDataFactory.create(bitmapData);
                com.itextpdf.layout.element.Image image = new Image(imageData);
                image.setHeight(100);
                image.setWidth(100);

                Drawable d2 = getActivity().getDrawable(R.drawable.logotaqueriapdf);
                Bitmap bitmap2 = ((BitmapDrawable)d2).getBitmap();
                ByteArrayOutputStream stream2 = new ByteArrayOutputStream();
                bitmap2.compress(Bitmap.CompressFormat.PNG, 100, stream2);
                byte[] bitmapData2 = stream2.toByteArray();

                ImageData  imageData2 = ImageDataFactory.create(bitmapData2);
                Image image2 = new Image(imageData2);
                image2.setHeight(532);
                image2.setWidth(532);

                Drawable d3 = getActivity().getDrawable(R.drawable.barcodepdf);
                Bitmap bitmap3 = ((BitmapDrawable)d3).getBitmap();
                ByteArrayOutputStream stream3 = new ByteArrayOutputStream();
                bitmap3.compress(Bitmap.CompressFormat.PNG, 100, stream3);
                byte[] bitmapData3 = stream3.toByteArray();

                ImageData imageData3 = ImageDataFactory.create(bitmapData3);
                Image image3 = new Image(imageData3);
                image3.setHeight(50);
                image3.setWidth(100);
                image3.setMarginLeft(398);
                image3.setMarginTop(-40);

                StringBuffer sb = new StringBuffer(18);
                for ( int i = 0; i < 7 ;i++) {
                    sb.append("0");
                }
                String codigo = sb.toString();

                Paragraph pedido = new Paragraph( "Número de orden: " + codigo + Login.gIdPedido);

                Paragraph fecha = new Paragraph( "Fecha y hora de la orden: " + fechacComplString + " a las " + horaString);

                Paragraph sucursal1 = new Paragraph("Sucursal: " + sucursal);

                float[] medidaCeldas = {0.78f, 2.40f, 1.40f, 0.63f};
                Table table = new Table(medidaCeldas);
                Border border1 = new GrooveBorder(2);

                table.addCell(new Cell().setBackgroundColor(new DeviceRgb(244,167,29)).setBorder(border1).add(new Paragraph("CANTIDAD").setTextAlignment(TextAlignment.CENTER)));
                table.addCell(new Cell().setBackgroundColor(new DeviceRgb(244,167,29)).setBorder(border1).add(new Paragraph("PRODUCTO").setTextAlignment(TextAlignment.CENTER)));
                table.addCell(new Cell().setBackgroundColor(new DeviceRgb(244,167,29)).setBorder(border1).add(new Paragraph("PRECIO UNITARIO").setTextAlignment(TextAlignment.CENTER)));
                table.addCell(new Cell().setBackgroundColor(new DeviceRgb(244,167,29)).setBorder(border1).add(new Paragraph("MONTO").setTextAlignment(TextAlignment.CENTER)));

                for(int i = 0 ; i < listaProdReport.size(); i++) {

                    table.addCell(new Cell()
                            .add(new Paragraph(String.valueOf((AdapProdReport.lCantProducto = listaProdReport.get(i).getCantiProd())))).setTextAlignment(TextAlignment.CENTER));

                    table.addCell(new Cell()
                            .add(new Paragraph(listaProdReport.get(i).getNombreProducto()).setTextAlignment(TextAlignment.LEFT)));

                    table.addCell(new Cell()
                            .add(new Paragraph("$ " + String.format("%.2f", AdapProdReport.lPrecioVta = listaProdReport.get(i).getPrecioVenta())).setTextAlignment(TextAlignment.RIGHT)));

                    table.addCell(new Cell()
                            .add(new Paragraph("$ " + String.format("%.2f",AdapProdReport.lDetMontoFinal = listaProdReport.get(i).getMonto() + listaProdReport.get(i).getMontoIva())).setTextAlignment(TextAlignment.RIGHT)));

                }

                table.addFooterCell(new Cell(0,6).add(new Paragraph("TOTAL: $ " + String.format("%.2f",gTotal)).setTextAlignment(TextAlignment.RIGHT)));


                Paragraph linea = new Paragraph("----------------------------------------------------------------------------------------------------------------------------------");
                linea.setBackgroundColor(new DeviceRgb(244,167,29));
                linea.setFontColor(new DeviceRgb(244,167,29));
                linea.setMarginTop(300f);
                Paragraph direcion = new Paragraph("Dirección: ");

                Paragraph telefono = new Paragraph("Teléfono: 7702-2123");
                Paragraph sitio = new Paragraph("Facebook: Dulces Tipicos La fiesta");

                document.add(image2.setFixedPosition(170,-10));
                document.add(image.setFixedPosition(420,710));
                document.add(pedido);
                document.add(fecha);
                document.add(sucursal1);
                document.add(table.setMarginTop(30f).setHorizontalAlignment(HorizontalAlignment.CENTER).setVerticalAlignment(VerticalAlignment.MIDDLE));
                document.add(linea);
                document.add(direcion);
                document.add(telefono);
                document.add(sitio);
                document.add(image3);
                document.close();

            }
        });

    }
}