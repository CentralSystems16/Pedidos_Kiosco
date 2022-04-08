package com.pedidos.kiosco.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.z.Login;

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

public class InsertarFacDetMovimientos extends AsyncTask<String, Void, String> {

    public static boolean exitoInsertProd= false;

    private final WeakReference<Context> context;

    public InsertarFacDetMovimientos(Context context) {
    this.context = new WeakReference<>(context);
}

    @SuppressLint("WrongThread")
    protected String doInBackground (String...params){

    String registrar_url = "http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/insertarDetMovimiento.php"
            +"?base=" + VariablesGlobales.dataBase
            +"&id_fac_movimiento=" + Login.gIdMovimiento
            +"&id_prefactura=" + Login.gIdPedido;

    String resultado = null;

    try {

        URL url = new URL(registrar_url);
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setRequestMethod("GET");
        httpURLConnection.setDoOutput(true);
        OutputStream outputStream = httpURLConnection.getOutputStream();
        BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

        String idPedido = String.valueOf(Login.gIdPedido);

        String data = URLEncoder.encode("id_prefactura", "UTF-8") + "=" + URLEncoder.encode(idPedido, "UTF-8");


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
