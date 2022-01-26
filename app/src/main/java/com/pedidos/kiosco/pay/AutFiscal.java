package com.pedidos.kiosco.pay;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.categorias.Categorias;
import com.pedidos.kiosco.model.Sucursales;

import org.json.JSONArray;

import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;

public class AutFiscal extends AppCompatActivity {

    int gIdComprobante, gIdSucursal;

    private AsyncHttpClient cliente;

    private Spinner spComprobante;
    ArrayList<Comprobantes> comprobantes;

    private Spinner spSucursal;
    ArrayList<Sucursales> sucursal;

    private Spinner spCaja;
    ArrayList<Caja> caja;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.autenticacion_fiscal);

        cliente = new AsyncHttpClient();
        spComprobante = findViewById(R.id.spinnerComprobante);
        spSucursal = findViewById(R.id.spinnerSucursales);
        spCaja = findViewById(R.id.spinnerCajas);

        llenarCaja();
        llenarComprobante();
        llenarSucursal();

    }

    private void llenarComprobante(){
        String url ="http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/llenarComprobantes.php";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){
                    cargarComprobante(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void cargarComprobante(String respuesta){

        comprobantes = new ArrayList<>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){

                Comprobantes c = new Comprobantes();
                c.setComprobante(jsonArreglo.getJSONObject(i).getString("tipo_comprobante"));
                c.setIdComprobante(jsonArreglo.getJSONObject(i).getInt("id_tipo_comprobante"));
                comprobantes.add(c);
            }

                ArrayAdapter<Comprobantes> a  = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, comprobantes);
            spComprobante.setAdapter(a);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void obtenerIdComprobante(){
        int indice = spComprobante.getSelectedItemPosition();
        gIdComprobante = comprobantes.get(indice).getIdComprobante();

    }

    private void llenarSucursal(){
        String url ="http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/llenarSucursal.php";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){
                    cargarSucursal(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void cargarSucursal(String respuesta){

        sucursal = new ArrayList<>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){

                Sucursales c = new Sucursales();
                c.setNomSucursal(jsonArreglo.getJSONObject(i).getString("nombre_sucursal"));
                c.setIdSucursal(jsonArreglo.getJSONObject(i).getInt("id_sucursal"));
                sucursal.add(c);
            }

            ArrayAdapter<Sucursales> a  = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sucursal);
            spSucursal.setAdapter(a);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void obtenerIdSucursal(){
        int indice = spSucursal.getSelectedItemPosition();
        gIdSucursal = sucursal.get(indice).getIdSucursal();

    }

    private void llenarCaja(){
        String url ="http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/LlenarCajas.php";
        cliente.post(url, new AsyncHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                if (statusCode == 200){
                    cargarCaja(new String(responseBody));
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {

            }
        });
    }

    private void cargarCaja(String respuesta){

        caja = new ArrayList<>();
        try {
            JSONArray jsonArreglo = new JSONArray(respuesta);
            for (int i = 0; i < jsonArreglo.length(); i++){

                Caja c = new Caja();
                c.setNombreCaja(jsonArreglo.getJSONObject(i).getString("nombre_caja"));
                c.setIdCaja(jsonArreglo.getJSONObject(i).getInt("id_caja"));
                caja.add(c);
            }

            ArrayAdapter<Caja> a  = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, caja);
            spCaja.setAdapter(a);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void obtenerIdCaja(){
        int indice = spCaja.getSelectedItemPosition();
        gIdComprobante = caja.get(indice).getIdCaja();

    }

}