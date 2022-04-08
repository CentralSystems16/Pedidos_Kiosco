package com.pedidos.kiosco.other;

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
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class MiPersona extends AsyncTask<String, Void, String>{

    public static Boolean exito = false;

        private final WeakReference<Context> context;
        int insertId = 0;

        public MiPersona(Context context) {
            this.context = new WeakReference<>(context);
        }

        protected String doInBackground (String...params){

            String registrar_url = "http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/clientes.php" + "?base=" + VariablesGlobales.dataBase;
            String resultado = null;

            try {

                URL url = new URL(registrar_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));

                String nombre = Login.nombre;
                String email = Login.email;
                int usuario = Login.gIdUsuario;

                String data = URLEncoder.encode("nombre_cliente", "UTF-8") + "=" + URLEncoder.encode(nombre, "UTF-8")
                        + "&" + URLEncoder.encode("email_cliente", "UTF-8") + "=" + URLEncoder.encode(email, "UTF-8")
                        + "&" + URLEncoder.encode("id_usuario", "UTF-8") + "=" + URLEncoder.encode(String.valueOf(usuario), "UTF-8");

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
                    JSONObject responseJSON = new JSONObject(String.valueOf((stringBuilder)));
                    Login.gIdCliente = responseJSON.getInt("last_insert_id()");
                    insertId = responseJSON.getInt(("last_insert_id()"));
                } catch (JSONException e) {
                    Log.e("JSON Parser", "Error parsing data " + e.toString());
                }

                if(insertId > 0) {
                    Login.gIdCliente = insertId;
                    exito = true;
                }else{
                    exito = false;
                }

                resultado = stringBuilder.toString();
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
            } catch (IOException ignored) {}

            return resultado;

        }
    }

