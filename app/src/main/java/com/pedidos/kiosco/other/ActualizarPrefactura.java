package com.pedidos.kiosco.other;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.VariablesGlobales;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ActualizarPrefactura extends AsyncTask<String, Void, String> {

    Date d = new Date();
    SimpleDateFormat fecc = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.getDefault());
    String fechacComplString = fecc.format(d);
    SimpleDateFormat ho = new SimpleDateFormat("h:mm a");
    String horaString = ho.format(d);

    private final WeakReference<Context> context;
    int estado = 2;

    public ActualizarPrefactura(Context context) {
        this.context = new WeakReference<>(context);
    }

    protected String doInBackground (String...params){

                String actualizar_url = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/actualizarPrefac.php"
                + "?id_estado_prefactura=" + estado
                + "&fecha_finalizo=" + fechacComplString + " a las " + horaString
                + "&id_prefactura=" + Login.gIdPedido;

        String resultado;

        System.out.println(actualizar_url);

        try {
            URL url = new URL(actualizar_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            String idDetPrefactura = String.valueOf(Login.gIdPedido);
            String idEstadoPrefactura = String.valueOf(estado);
            String fechaFinalizo = fechacComplString + " a las " + horaString;

            String data = URLEncoder.encode("id_prefactura", "UTF-8") + "=" + URLEncoder.encode(idDetPrefactura, "UTF-8")
                    + "&" + URLEncoder.encode("id_estado_prefactura", "UTF-8") + "=" + URLEncoder.encode(idEstadoPrefactura, "UTF-8")
                    + "&" + URLEncoder.encode("fecha_finalizo", "UTF-8") + "=" + URLEncoder.encode(fechaFinalizo, "UTF-8");

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
