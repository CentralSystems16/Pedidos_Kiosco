package com.pedidos.kiosco.other;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.adapters.AdapProdReport;

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

public class ActualizarPedido extends AsyncTask<String, Void, String> {

    private final WeakReference<Context> context;

    public ActualizarPedido (Context context) {
        this.context = new WeakReference<>(context);
    }

    protected String doInBackground (String...params){
                String actualizar_url = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/actualizarPedido.php"
                + "?id_prefactura=" + Login.gIdPedido
                + "&monto=" + AdapProdReport.monto
                + "&monto_iva=" + AdapProdReport.montoIva;

        String resultado;

        try {

            URL url = new URL(actualizar_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            String idDetPrefactura = String.valueOf(Login.gIdPedido);
            String monto = String.valueOf(AdapProdReport.lNewDetMonto);
            String montoIva = String.valueOf(AdapProdReport.lNewDetMontoIva);

            String data = URLEncoder.encode("id_prefactura", "UTF-8") + "=" + URLEncoder.encode(idDetPrefactura, "UTF-8")
                    + "&" + URLEncoder.encode("monto", "UTF-8") + "=" + URLEncoder.encode(monto, "UTF-8")
                    + "&" + URLEncoder.encode("monto_iva", "UTF-8") + "=" + URLEncoder.encode(montoIva, "UTF-8");

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
