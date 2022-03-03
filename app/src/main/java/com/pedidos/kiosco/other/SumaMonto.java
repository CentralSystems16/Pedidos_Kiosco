package com.pedidos.kiosco.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.fragments.CierreCaja;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.IOException;

public class SumaMonto extends AsyncTask<Void, Void, Void>{

        @SuppressLint("StaticFieldLeak")
        public Context context;
        HttpResponse httpResponse;
        JSONArray jsonObject = null;
        String StringHolder = "" ;

        String contador_url = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerMonto.php"
                + "?base=" + VariablesGlobales.dataBase
                + "&id_cierre_caja=" + VariablesGlobales.gIdCierreCaja
                + "&id_tipo_pago=" + CierreCaja.lIdTipoPago;

        public static Double sumaMonto;

        @Override
        protected void onPreExecute()
        {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... arg0) {

            System.out.println(contador_url);

            HttpClient httpClient = new DefaultHttpClient();
            HttpPost httpPost = new HttpPost(contador_url);

            try {
                httpResponse = httpClient.execute(httpPost);

                StringHolder = EntityUtils.toString(httpResponse.getEntity(), "UTF-8");

            } catch (IOException e) {
                e.printStackTrace();
            }

            try{

                JSONArray jsonArray = new JSONArray(StringHolder);
                jsonObject = jsonArray.getJSONArray(0);

            } catch ( Exception e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(Void result) {

            try {

                    JSONObject responseJSON = new JSONObject(String.valueOf(StringHolder));
                    sumaMonto = responseJSON.getJSONArray("voto").getJSONObject(0).getDouble("count");

            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
}