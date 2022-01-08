package com.pedidos.kiosco.pay;

import static com.pedidos.kiosco.other.ContadorProductos.GetDataFromServerIntoTextView.gCount;
import androidx.appcompat.app.AppCompatActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.desing.EnviandoTicket;
import com.pedidos.kiosco.fragments.TicketDatos;
import com.pedidos.kiosco.other.InsertarDetMovimientos;
import com.pedidos.kiosco.other.InsertarMovimientos;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

public class ResumenPago extends AppCompatActivity {

    TextView cantidad, totalCompra, totalFinal, cambio;
    EditText etMoney;
    public static Double money;
    DecimalFormat formatoDecimal = new DecimalFormat("#");
    Date d = new Date();
    SimpleDateFormat fecc = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.getDefault());
    String fechacComplString = fecc.format(d);
    SimpleDateFormat ho = new SimpleDateFormat("h:mm a");
    String horaString = ho.format(d);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resumen_pago);

        cantidad = findViewById(R.id.cantidad);
        totalCompra = findViewById(R.id.totalFinalPago);
        totalFinal = findViewById(R.id.totalDef);
        cantidad.setText(formatoDecimal.format(gCount));
        totalCompra.setText(String.format("%.2f",TicketDatos.gTotal));
        totalFinal.setText(String.format("%.2f", TicketDatos.gTotal));

        ImageButton back = findViewById(R.id.backArrow);
        back.setOnClickListener(view -> {

        });

        etMoney = findViewById(R.id.etMoney);
        cambio = findViewById(R.id.montoCambio);

        etMoney.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (etMoney != null && etMoney.length() > 0) {

                        money = Double.parseDouble(etMoney.getText().toString());
                        money = money - TicketDatos.gTotal;
                        cambio.setText(String.format("%.2f", money));

                    } else {
                        cambio.setText("0.00");
                    }

                } catch(NumberFormatException ex){ // handle your exception

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        Button pagar = findViewById(R.id.btnPagar);
        pagar.setOnClickListener(view -> {


                ejecutarServicio("http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/actualizarPrefac.php"
                        + "?id_estado_prefactura=2"
                        + "&fecha_finalizo=" + fechacComplString + " a las " + horaString
                        + "&id_prefactura=" + Login.gIdPedido);

                String url = "http://"+ VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/actualizarMov.php"
                        +"?monto_pago="+ etMoney.getText().toString()
                        +"&monto_cambio=" + cambio.getText().toString()
                        +"&id_fac_movimiento="+Login.gIdMovimiento;
                ejecutarServicio2(url);
            System.out.println(url);


        });
    }

    public void ejecutarServicio (String URL){

        ProgressDialog progressDialog = new ProgressDialog(ResumenPago.this, R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    Login.gIdPedido = 0;
                    gCount = 0.00;
                    startActivity(new Intent(getApplicationContext(), EnviandoTicket.class));
            progressDialog.dismiss();
                },
                volleyError -> progressDialog.dismiss()
        );
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

    public void ejecutarServicio2 (String URL){

        ProgressDialog progressDialog = new ProgressDialog(ResumenPago.this, R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {

                    progressDialog.dismiss();
                },
                volleyError -> {
            progressDialog.dismiss();
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);
    }

}