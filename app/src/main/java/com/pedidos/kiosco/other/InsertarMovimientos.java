package com.pedidos.kiosco.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.main.ObtenerProductos;
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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class InsertarMovimientos extends AsyncTask<String, Void, String> {

    Date d = new Date();
    SimpleDateFormat fecc = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.getDefault());
    String fechacComplString = fecc.format(d);
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat ho = new SimpleDateFormat("h:mm a");
    String horaString = ho.format(d);

    private final WeakReference<Context> context;

    public InsertarMovimientos(Context context) {
        this.context = new WeakReference<>(context);
    }

    protected String doInBackground (String...params){

        String registrar_url = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/insertarMovimientos.php"
                +"?id_tipo_comprobante=1"
                +"&id_cliente=" + Login.gIdCliente
                +"&id_usuario=" + Login.gIdUsuario
                +"&id_sucursal=" + Login.gIdSucursal
                +"&id_prefactura=" + Login.gIdPedido
                +"&fecha_creo=" + fechacComplString + " a las " + horaString
                +"&monto=" + ObtenerProductos.gDetMonto
                +"&monto_iva=" + ObtenerProductos.gDetMontoIva;

        System.out.println(registrar_url);

        String resultado = null;

        try {

            URL url = new URL(registrar_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            String tipoComprobante = "1";
            String idCliente = String.valueOf(Login.gIdCliente);
            String idUsuario = String.valueOf(Login.gIdUsuario);
            String idSucursal = String.valueOf(Login.gIdSucursal);
            String idPrefactura = String.valueOf(Login.gIdPedido);
            String fechaCreo = fechacComplString + " a las " + horaString;
            String monto = String.valueOf(ObtenerProductos.gDetMonto);
            String montoIva = String.valueOf(ObtenerProductos.gDetMontoIva);

            String data = URLEncoder.encode("id_tipo_comprobante", "UTF-8") + "=" + URLEncoder.encode(tipoComprobante, "UTF-8")
                    + "&" + URLEncoder.encode("id_cliente", "UTF-8") + "=" + URLEncoder.encode(idCliente, "UTF-8")
                    + "&" + URLEncoder.encode("id_usuario", "UTF-8") + "=" + URLEncoder.encode(idUsuario, "UTF-8")
                    + "&" + URLEncoder.encode("id_sucursal", "UTF-8") + "=" + URLEncoder.encode((idSucursal), "UTF-8")
                    + "&" + URLEncoder.encode("id_prefactura", "UTF-8") + "=" + URLEncoder.encode(idPrefactura, "UTF-8")
                    + "&" + URLEncoder.encode("fecha_creo", "UTF-8") + "=" + URLEncoder.encode(fechaCreo, "UTF-8")
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

            JSONObject responseJSON = new JSONObject(String.valueOf(stringBuilder));
            Login.gIdMovimiento = Integer.parseInt(responseJSON.getString("last_insert_id()"));

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
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return resultado;
    }
    }