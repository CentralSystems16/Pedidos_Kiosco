package com.pedidos.kiosco.other;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.AsyncTask;

import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.VariablesGlobales;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class SumaMontoMultipleIva extends AsyncTask<Void, Void, Void>{

        @SuppressLint("StaticFieldLeak")
        public Context context;
        HttpResponse httpResponse;
        JSONArray jsonObject = null;
        String StringHolder = "" ;
        String contador_url = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/obtenerSumaMontoIva.php"
                + "?id_prefactura=" + Login.gIdPedido;

        public static String sumaMontoMultipleIva;

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
                sumaMontoMultipleIva = String.valueOf((responseJSON.getJSONArray("voto").getJSONObject(0).getDouble("count")));
                new ActualizarPedidoMultiple(context).execute();

            } catch (JSONException e) {

                e.printStackTrace();
            }
        }
}