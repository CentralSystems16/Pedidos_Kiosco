package com.pedidos.kiosco.fragments;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.Toolbar;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.TextAlignment;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.Splash;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.adapters.reportes.BuscarReportes;
import com.pedidos.kiosco.adapters.reportes.ReporteVentasProducto;
import com.pedidos.kiosco.other.SumaMontoEfectivo;
import com.pedidos.kiosco.other.SumaMontoTarjeta;
import com.pedidos.kiosco.pdf.ResponsePOJO;
import com.pedidos.kiosco.pdf.RetrofitClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReporteVentas extends Fragment {

    StringBuilder sb = new StringBuilder("");

    int numeroComprobante;
    String fecha, tComprobante, tPago;
    Double monto;
    String encodedPDF;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.reporte_ventas, container, false);

        Toolbar toolbar = vista.findViewById(R.id.toolbarReportesVentas);
        toolbar.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));

        obtenerReporteProductos();

        return vista;
    }

    public void obtenerReporteProductos() {

        new SumaMontoEfectivo().execute();
        new SumaMontoTarjeta().execute();

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        String URL_ESTADOS = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerVentasProductoTotal.php"
                + "?base=" + VariablesGlobales.dataBase
                + "&fecha_inicio=" + BuscarReportes.sFecInicial + " " + BuscarReportes.sHoraInicial
                + "&fecha_fin=" + BuscarReportes.sFecFinal + " " + BuscarReportes.sHoraFinal;

        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());

        StringRequest stringRequest = new StringRequest(Request.Method.GET, URL_ESTADOS,

                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        JSONArray jsonArray = jsonObject.getJSONArray("Reporte");

                        for (int i = 0; i < jsonArray.length(); i++) {

                            JSONObject jsonObject1 = jsonArray.getJSONObject(i);

                            numeroComprobante = jsonObject1.getInt("numero_comprobante");
                            monto = jsonObject1.getDouble("monto_pago");
                            fecha = jsonObject1.getString("fecha_creo");
                            tPago = jsonObject1.getString("tipo_pago");
                            tComprobante = jsonObject1.getString("tipo_comprobante");

                            sb.append(fecha + "        " + tComprobante + "        " + numeroComprobante + "        " + tPago + "        " + "Adiel" + "        " + monto + "        " + "0.00" + "        " + "Activo" + "\n");

                        }

                        String pdfPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString();
                        File file = new File(pdfPath, "ComprobanteCorteCaja.pdf");

                        PdfWriter writer = new PdfWriter(file);
                        PdfDocument pdfDocument = new PdfDocument(writer);
                        Document document = new Document(pdfDocument);
                        PageSize pageSize = new PageSize(PageSize.A4);
                        pdfDocument.setDefaultPageSize(pageSize);

                        @SuppressLint("UseCompatLoadingForDrawables") Drawable d = requireContext().getDrawable(R.drawable.logocentral);
                        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
                        ByteArrayOutputStream stream = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
                        byte[] bitmapData = stream.toByteArray();

                        ImageData imageData = ImageDataFactory.create(bitmapData);
                        com.itextpdf.layout.element.Image image = new Image(imageData);
                        image.setHeight(100);
                        image.setWidth(100);
                        if (SumaMontoTarjeta.sumaMontoTargeta == null){
                            SumaMontoTarjeta.sumaMontoTargeta = 0.00;
                        }

                        double totalFinal = SumaMontoEfectivo.sumaMontoEfectivo + SumaMontoTarjeta.sumaMontoTargeta;

                        Paragraph nombre = new Paragraph(Splash.gNombre + "\n").setTextAlignment(TextAlignment.CENTER);
                        Paragraph direccion = new Paragraph("Venta total" + "\n").setTextAlignment(TextAlignment.CENTER);
                        Paragraph departamento = new Paragraph("Desde: " + "Hasta: " + "\n").setTextAlignment(TextAlignment.CENTER);
                        Paragraph linea1 = new Paragraph("Fecha" + "      " + "Tipo comp" + "      " + "No" + "      " + "Tipo pago" + "      " + "Nombre del cliente" + "      " + "Monto" + "      " + "Propina" + "      "  + "Estado" + "\n").setTextAlignment(TextAlignment.CENTER);
                        Paragraph cajero = new Paragraph("================================================================" + "\n").setTextAlignment(TextAlignment.CENTER);
                        Paragraph noCaja = new Paragraph(sb.toString() + "\n").setTextAlignment(TextAlignment.CENTER);
                        Paragraph datos = new Paragraph("================================================================" + "\n").setTextAlignment(TextAlignment.CENTER);
                        Paragraph totales = new Paragraph("Tipo pago    " + "Venta " + "Propina " + "SubTotal " + "\n").setTextAlignment(TextAlignment.RIGHT);
                        Paragraph datos2 = new Paragraph("================================" + "\n").setTextAlignment(TextAlignment.RIGHT);
                        Paragraph linea5 = new Paragraph("Efectivo" + "          " + SumaMontoEfectivo.sumaMontoEfectivo + "      " + "0.00" + "      " + SumaMontoEfectivo.sumaMontoEfectivo + "\n").setTextAlignment(TextAlignment.RIGHT);
                        Paragraph vTotal = new Paragraph("Tarjeta " + "          " + SumaMontoTarjeta.sumaMontoTargeta + "          " + "0.00" + "          " + SumaMontoTarjeta.sumaMontoTargeta + "\n").setTextAlignment(TextAlignment.RIGHT);
                        Paragraph datos3 = new Paragraph("================================" + "\n").setTextAlignment(TextAlignment.RIGHT);
                        Paragraph devTotal = new Paragraph("Total final " + totalFinal + "\n").setTextAlignment(TextAlignment.RIGHT);

                        document.add(nombre);
                        document.add(direccion);
                        document.add(departamento);
                        document.add(linea1);
                        document.add(cajero);
                        document.add(noCaja);
                        document.add(datos);
                        document.add(totales);
                        document.add(datos2);
                        document.add(linea5);
                        document.add(vTotal);
                        document.add(datos3);
                        document.add(devTotal);

                        document.close();

                        encodePDF();
                        uploadDocument();

                        progressDialog.dismiss();

                    } catch (JSONException | FileNotFoundException e) {
                        e.printStackTrace();
                        progressDialog.dismiss();
                    }
                    progressDialog.dismiss();
                }, volleyError -> {
            Toast.makeText(getContext(), "Ocurrió un error inesperado, Error: " + volleyError, Toast.LENGTH_SHORT).show();
            progressDialog.dismiss();
        });

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                0,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
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
            InputStream inputStream = getActivity().getContentResolver().openInputStream(uri);
            byte[] pdfInBytes = new byte[inputStream.available()];
            inputStream.read(pdfInBytes);
            encodedPDF = Base64.encodeToString(pdfInBytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}