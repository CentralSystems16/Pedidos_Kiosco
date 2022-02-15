package com.pedidos.kiosco.other;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.adapters.AdaptadorCierreCaja;
import com.pedidos.kiosco.fragments.CierreCaja;
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

public class InsertarFacTipoPagoCaja extends AsyncTask<String, Void, String> {

    private final WeakReference<Context> context;

    public InsertarFacTipoPagoCaja(Context context) {
        this.context = new WeakReference<>(context);
    }

    protected String doInBackground (String...params){

        System.out.println("Monto incial: " + CierreCaja.fondoInit);
        System.out.println("Monto: " + SumaMonto.sumaMonto);
        System.out.println("Monto devolucion: " + SumaMontoDevolucion.sumaMontoDevolucion);
        System.out.println("Monto fisico: " + CierreCaja.montoFisico);

        double montoDif = CierreCaja.fondoInit + SumaMonto.sumaMonto - SumaMontoDevolucion.sumaMontoDevolucion - CierreCaja.montoFisico;
        System.out.println("Monto diferencia: " + montoDif);

        String registrar_url = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/insertarTipoPagoCaja.php"
                +"?id_cierre_caja=" + VariablesGlobales.gIdCierreCaja
                +"&tipo_pago=" + CierreCaja.lTipoPago
                +"&monto=" + SumaMonto.sumaMonto
                +"&monto_devolucion=" + SumaMontoDevolucion.sumaMontoDevolucion
                +"&monto_diferencia=" + montoDif
                +"&liquidar=1"
                +"&id_tipo_pago=" + CierreCaja.lIdTipoPago
                +"&monto_inicial=" + CierreCaja.fondoInit
                +"&monto_fisico=" + CierreCaja.montoFisico;

        String resultado = null;

        System.out.println(registrar_url);

        try {

            URL url = new URL(registrar_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            String idCierreCaja = String.valueOf(VariablesGlobales.gIdCierreCaja);
            String tipoPago = CierreCaja.lTipoPago;
            String monto = String.valueOf(SumaMonto.sumaMonto);
            String montoDevolucion = String.valueOf(0.00);
            String montoDiferencia = String.valueOf(0.00);
            String liquidar = "1";
            String idTipoPago = String.valueOf(CierreCaja.lIdTipoPago);
            String montoInicial = String.valueOf(CierreCaja.fondoInit);
            String montoFisico = String.valueOf(CierreCaja.montoFisico);

            String data = URLEncoder.encode("id_cierre_caja", "UTF-8") + "=" + URLEncoder.encode(idCierreCaja, "UTF-8")
                    + "&" + URLEncoder.encode("tipo_pago", "UTF-8") + "=" + URLEncoder.encode(tipoPago, "UTF-8")
                    + "&" + URLEncoder.encode("monto", "UTF-8") + "=" + URLEncoder.encode(monto, "UTF-8")
                    + "&" + URLEncoder.encode("monto_devolucion", "UTF-8") + "=" + URLEncoder.encode((montoDevolucion), "UTF-8")
                    + "&" + URLEncoder.encode("monto_diferencia", "UTF-8") + "=" + URLEncoder.encode((montoDiferencia), "UTF-8")
                    + "&" + URLEncoder.encode("liquidar", "UTF-8") + "=" + URLEncoder.encode(liquidar, "UTF-8")
                    + "&" + URLEncoder.encode("id_tipo_pago", "UTF-8") + "=" + URLEncoder.encode(idTipoPago, "UTF-8")
                    + "&" + URLEncoder.encode("monto_inicial", "UTF-8") + "=" + URLEncoder.encode(montoInicial, "UTF-8")
                    + "&" + URLEncoder.encode("monto_fisico", "UTF-8") + "=" + URLEncoder.encode(montoFisico, "UTF-8");

            bufferedWriter.write(data);
            bufferedWriter.flush();
            bufferedWriter.close();
            outputStream.close();

            SumaMonto.sumaMonto = 0.00;
            SumaMontoDevolucion.sumaMontoDevolucion = 0.00;

            if (CierreCaja.lIdTipoPago == 1){
                CierreCaja.fondoInit = 0.00;
            }

            CierreCaja.montoFisico = 0.00;

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