package com.pedidos.kiosco.other;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.fragments.MontoInicial;
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

public class InsertarCierreMovCaja extends AsyncTask<String, Void, String> {

    private final WeakReference<Context> context;

    public InsertarCierreMovCaja(Context context) {
        this.context = new WeakReference<>(context);
    }

    protected String doInBackground (String...params){



        String registrar_url = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/insertarCierreMovCaja.php"
                +"?base=" + VariablesGlobales.dataBase
                +"&id_cierre_caja=" + VariablesGlobales.gIdCierreCaja
                +"&id_usuario=" + Login.gIdUsuario
                +"&monto_inicial=0.00"
                +"&monto_venta=0.00"
                +"&monto_gastos=" + ContadorGastos.GetDataFromServerIntoTextView.totalCierre
                +"&monto_fisico=0.00"
                +"&diferencia=0.00"
                +"&monto_devolucion=0.00"
                +"&fecha_movimiento=1/1/1";

        String resultado = null;

        try {
            System.out.println(registrar_url);
            URL url = new URL(registrar_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            String cierreCaja = String.valueOf(VariablesGlobales.gIdCierreCaja);
            String usuario = String.valueOf(Login.gIdUsuario);
            String inicial = String.valueOf(MontoInicial.montoInit);
            String venta = "0.00";
            String gastos = String.valueOf(ContadorGastos.GetDataFromServerIntoTextView.totalCierre);
            String fisico = "0.00";
            String diferencia = "0.00";
            String devolucion = "0.00";
            String fecMovimiento = "1/1/1";

            String data = URLEncoder.encode("id_cierre_caja", "UTF-8") + "=" + URLEncoder.encode(cierreCaja, "UTF-8")
                    + "&" + URLEncoder.encode("id_usuario", "UTF-8") + "=" + URLEncoder.encode(usuario, "UTF-8")
                    + "&" + URLEncoder.encode("monto_inicial", "UTF-8") + "=" + URLEncoder.encode(inicial, "UTF-8")
                    + "&" + URLEncoder.encode("monto_venta", "UTF-8") + "=" + URLEncoder.encode(venta, "UTF-8")
                    + "&" + URLEncoder.encode("monto_gastos", "UTF-8") + "=" + URLEncoder.encode(gastos, "UTF-8")
                    + "&" + URLEncoder.encode("monto_fisico", "UTF-8") + "=" + URLEncoder.encode((fisico), "UTF-8")
                    + "&" + URLEncoder.encode("diferencia", "UTF-8") + "=" + URLEncoder.encode((diferencia), "UTF-8")
                    + "&" + URLEncoder.encode("monto_devolucion", "UTF-8") + "=" + URLEncoder.encode(devolucion, "UTF-8")
                    + "&" + URLEncoder.encode("fecha_movimiento", "UTF-8") + "=" + URLEncoder.encode(fecMovimiento, "UTF-8");

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