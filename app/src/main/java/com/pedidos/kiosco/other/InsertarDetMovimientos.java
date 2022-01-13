package com.pedidos.kiosco.other;

import static com.pedidos.kiosco.other.ContadorProductos.GetDataFromServerIntoTextView.gCount;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.fragments.TicketDatos;
import com.pedidos.kiosco.main.ObtenerProductos;
import com.pedidos.kiosco.pay.ResumenPago;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class InsertarDetMovimientos extends AsyncTask<String, Void, String> {

    public static boolean exitoInsertProd= false;

private final WeakReference<Context> context;

public InsertarDetMovimientos(Context context) {
    this.context = new WeakReference<>(context);
}

@SuppressLint("WrongThread")
protected String doInBackground (String...params){

    String registrar_url = "http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/insertarDetMovimiento.php"
            +"?id_producto=" + ObtenerProductos.gIdProducto
            +"&id_fac_movimiento=" + Login.gIdMovimiento
            +"&id_tipo_comprobante=1"
            +"&cantidad=1"
            +"&monto=" + ObtenerProductos.gDetMonto
            +"&monto_desc=0.00"
            +"&monto_iva=" + ObtenerProductos.gDetMontoIva
            +"&precio_uni=" + ObtenerProductos.gPrecio
            +"&venta_gravada=0.00";

    String resultado = null;

    try {

        URL url = new URL(registrar_url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setDoOutput(true);
        OutputStream outputStream = httpURLConnection.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

        String idProducto = String.valueOf(ObtenerProductos.gIdProducto);
        String idFacMovimiento = String.valueOf(Login.gIdMovimiento);
        String tipoComprobante= "1";
        String cantidad = String.valueOf(gCount);
        String monto = String.valueOf(ObtenerProductos.gDetMonto);
        String montoDesc = "0.00";
        String montoIva = String.valueOf(ObtenerProductos.gDetMontoIva);
        String precioUni = "0.00";
        String ventaGrav= "0.00";

        String data = URLEncoder.encode("id_producto", "UTF-8") + "=" + URLEncoder.encode(idProducto, "UTF-8")
                + "&" + URLEncoder.encode("id_fac_movimiento", "UTF-8") + "=" + URLEncoder.encode(idFacMovimiento, "UTF-8")
                + "&" + URLEncoder.encode("id_tipo_comprobante", "UTF-8") + "=" + URLEncoder.encode(tipoComprobante, "UTF-8")
                + "&" + URLEncoder.encode("cantidad", "UTF-8") + "=" + URLEncoder.encode(cantidad, "UTF-8")
                + "&" + URLEncoder.encode("monto", "UTF-8") + "=" + URLEncoder.encode(monto, "UTF-8")
                + "&" + URLEncoder.encode("monto_desc", "UTF-8") + "=" + URLEncoder.encode(montoDesc, "UTF-8")
                + "&" + URLEncoder.encode("monto_iva", "UTF-8") + "=" + URLEncoder.encode(montoIva, "UTF-8")
                + "&" + URLEncoder.encode("precio_uni", "UTF-8") + "=" + URLEncoder.encode(precioUni, "UTF-8")
                + "&" + URLEncoder.encode("venta_gravada", "UTF-8") + "=" + URLEncoder.encode(ventaGrav, "UTF-8");

        bufferedWriter.write(data);
        bufferedWriter.flush();
        bufferedWriter.close();
        outputStream.close();

        InputStream inputStream = httpURLConnection.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
        StringBuilder stringBuilder = new StringBuilder();

        String line;
        while ((line = bufferedReader.readLine()) != null) {
            stringBuilder.append(line);
        }

        try {
            JSONObject responseJSON = new JSONObject(String.valueOf(stringBuilder));
            Login.gIdDetMovimiento = responseJSON.getInt("last_insert_id()");
        }catch (JSONException e){
            Log.e("JSON Parser", "Error parsing data " + e.toString());
        }

        if(Login.gIdDetMovimiento > 0) {
            exitoInsertProd = true;
        }else{
            exitoInsertProd = false;
        }

        resultado = stringBuilder.toString();
        bufferedReader.close();
        inputStream.close();
        httpURLConnection.disconnect();
    } catch (MalformedURLException e) {
        Log.d("MiAPP", "Se ha utilizado una URL con formato incorrecto");
        resultado = "Se ha producido un ERROR";
    } catch (IOException e) {
        Log.d("MiAPP", "Error inesperado!, posibles problemas de conexion de red");
        resultado = "Se ha producido un ERROR, comprueba tu conexion a Internet";
    }
    return resultado;
    }
}
