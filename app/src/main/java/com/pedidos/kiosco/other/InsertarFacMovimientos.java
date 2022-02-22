package com.pedidos.kiosco.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.desing.TipoPago;
import com.pedidos.kiosco.fragments.ObtenerProductos;
import com.pedidos.kiosco.fragments.ResumenPago;
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

public class InsertarFacMovimientos extends AsyncTask<String, Void, String> {

    Date d = new Date();
    SimpleDateFormat fecc = new SimpleDateFormat("d'-'MMMM'-'yyyy", Locale.getDefault());
    String fechacComplString = fecc.format(d);
    @SuppressLint("SimpleDateFormat")
    SimpleDateFormat ho = new SimpleDateFormat("h:mm a");
    String horaString = ho.format(d);

    private final WeakReference<Context> context;


    public InsertarFacMovimientos(Context context) {
        this.context = new WeakReference<>(context);
    }

    protected String doInBackground (String...params){

        String registrar_url = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/insertarMovimientos.php"
                +"?id_cliente=" + Login.gIdCliente
                +"&id_tipo_comprobante=4"
                +"&id_usuario=" + Login.gIdUsuario
                +"&id_forma_pago=1"
                +"&id_estado_comprobante=1"
                +"&id_sucursal=" + Login.gIdSucursal
                +"&id_aut_fiscal=" + Login.gIdAutFiscal
                +"&id_prefactura=" + Login.gIdPedido
                +"&id_tipo_pago=" + TipoPago.idTipoPago
                +"&fecha=" + "1/1/1"
                +"&fecha_creo=" + fechacComplString + " " + horaString
                +"&fecha_mod=" + "1/1/1"
                +"&monto=" + SumaMontoMultiple.sumaMontoMultiple
                +"&monto_iva=" + SumaMontoMultipleIva.sumaMontoMultipleIva
                +"&fac_tipo_movimiento=1"
                +"&monto_desc=" + "0.00"
                +"&monto_pago=" + "0.00"
                +"&monto_cambio=" + "0.00"
                +"&monto_exento=" + "0.00"
                +"&monto_gravado=" + "0.00"
                +"&monto_no_sujeto=" + "0.00"
                +"&numero_comprobante=" + ResumenPago.no_comprobante
                +"&id_cierre_caja=" + VariablesGlobales.gIdCierreCaja;

        System.out.println(registrar_url);

        String resultado = null;

        try {

            URL url = new URL(registrar_url);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setDoOutput(true);
            OutputStream outputStream = httpURLConnection.getOutputStream();
            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

            String idCliente = String.valueOf(Login.gIdCliente);
            String tipoComprobante = "1";
            String idUsuario = String.valueOf(Login.gIdUsuario);
            String idFormaPago = "1";
            String idEstadoComprobante = "1";
            String idSucursal = String.valueOf(Login.gIdSucursal);
            String idAutFiscal = "1";
            String idPrefactura = String.valueOf(Login.gIdPedido);
            String idTipoPago = "1";
            String fecha = "1/1/1";
            String fechaCreo = fechacComplString + " a las " + horaString;
            String fechaMod = "11/11/11";
            String monto = String.valueOf(ObtenerProductos.gDetMonto);
            String montoIva = String.valueOf(ObtenerProductos.gDetMontoIva);
            String facTipoMov = "1";
            String montoDesc = "0.00";
            String montoPago = "0.00";
            String montoCambio = "0.00";

            String data = URLEncoder.encode("id_cliente", "UTF-8") + "=" + URLEncoder.encode(idCliente, "UTF-8")
                    + "&" + URLEncoder.encode("id_tipo_comprobante", "UTF-8") + "=" + URLEncoder.encode(tipoComprobante, "UTF-8")
                    + "&" + URLEncoder.encode("id_usuario", "UTF-8") + "=" + URLEncoder.encode(idUsuario, "UTF-8")
                    + "&" + URLEncoder.encode("id_forma_pago", "UTF-8") + "=" + URLEncoder.encode(idFormaPago, "UTF-8")
                    + "&" + URLEncoder.encode("id_estado_comprobante", "UTF-8") + "=" + URLEncoder.encode(idEstadoComprobante, "UTF-8")
                    + "&" + URLEncoder.encode("id_sucursal", "UTF-8") + "=" + URLEncoder.encode((idSucursal), "UTF-8")
                    + "&" + URLEncoder.encode("id_aut_fiscal", "UTF-8") + "=" + URLEncoder.encode((idAutFiscal), "UTF-8")
                    + "&" + URLEncoder.encode("id_prefactura", "UTF-8") + "=" + URLEncoder.encode(idPrefactura, "UTF-8")
                    + "&" + URLEncoder.encode("id_tipo_pago", "UTF-8") + "=" + URLEncoder.encode(idTipoPago, "UTF-8")
                    + "&" + URLEncoder.encode("fecha", "UTF-8") + "=" + URLEncoder.encode(fecha, "UTF-8")
                    + "&" + URLEncoder.encode("fecha_creo", "UTF-8") + "=" + URLEncoder.encode(fechaCreo, "UTF-8")
                    + "&" + URLEncoder.encode("fecha_mod", "UTF-8") + "=" + URLEncoder.encode(fechaMod, "UTF-8")
                    + "&" + URLEncoder.encode("monto", "UTF-8") + "=" + URLEncoder.encode(monto, "UTF-8")
                    + "&" + URLEncoder.encode("monto_iva", "UTF-8") + "=" + URLEncoder.encode(montoIva, "UTF-8")
                    + "&" + URLEncoder.encode("fac_tipo_movimiento", "UTF-8") + "=" + URLEncoder.encode(facTipoMov, "UTF-8")
                    + "&" + URLEncoder.encode("monto_desc", "UTF-8") + "=" + URLEncoder.encode(montoDesc, "UTF-8")
                    + "&" + URLEncoder.encode("monto_pago", "UTF-8") + "=" + URLEncoder.encode(montoPago, "UTF-8")
                    + "&" + URLEncoder.encode("monto_cambio", "UTF-8") + "=" + URLEncoder.encode(montoCambio, "UTF-8");

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
            System.out.println("ID movimiento al insertar: " + Login.gIdMovimiento);
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