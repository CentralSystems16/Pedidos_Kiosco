package com.pedidos.kiosco.desing;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.fragments.TicketDatos;
import com.pedidos.kiosco.main.ObtenerMovimientos;
import com.pedidos.kiosco.other.SumaMonto;
import com.pedidos.kiosco.other.SumaMontoDevolucion;
import com.pedidos.kiosco.pdf.ResponsePOJO;
import com.pedidos.kiosco.pdf.RetrofitClient;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VistaFinal extends AppCompatActivity {

    String encodedPDF;
    int REQ_PDF;
    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.vista_final);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        new SumaMonto().execute();
        new SumaMontoDevolucion().execute();

        //encodePDF();
        //uploadDocument();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));

        MaterialButton nuevo = findViewById(R.id.btnRepetirPedido);
        nuevo.setStrokeColor(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));

        MaterialButton ver = findViewById(R.id.btnVerOrdenes);
        ver.setStrokeColor(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));
        
        ImageButton homeEnd = findViewById(R.id.homeEnd);
        homeEnd.setOnClickListener(v -> {
            TicketDatos.gTotal = 0.00;
            Login.gIdPedido = 0;
            startActivity(new Intent(getApplicationContext(), Principal.class));
        });

        nuevo.setOnClickListener(view -> {
            TicketDatos.gTotal = 0.00;
            Login.gIdPedido = 0;
            Login.gIdMovimiento = 0;
            Intent i = new Intent(getApplicationContext(), Principal.class);
            startActivity(i);
        });

        ver.setOnClickListener(view -> {
            TicketDatos.gTotal = 0.00;
            Login.gIdPedido = 0;
            Principal.gIdEstadoCliente = 2;
            startActivity(new Intent(getApplicationContext(), ObtenerMovimientos.class));
        });

    }

    private void uploadDocument() {

        Call<ResponsePOJO> call = RetrofitClient.getInstance().getAPI().uploadDocument(encodedPDF);
        call.enqueue(new Callback<ResponsePOJO>() {
            @Override
            public void onResponse(@NonNull Call<ResponsePOJO> call, @NonNull Response<ResponsePOJO> response) {

            }

            @Override
            public void onFailure(@NonNull Call<ResponsePOJO> call, @NonNull Throwable t) {

            }
        });
    }

    void encodePDF() {
        File file = new File(String.valueOf(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS + "/" + Login.gIdPedido + " Examen.pdf")));
        Uri uri = Uri.fromFile(file);
        try {
            InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(uri);
            byte[] pdfInBytes = new byte[inputStream.available()];
            inputStream.read(pdfInBytes);
            encodedPDF = Base64.encodeToString(pdfInBytes, Base64.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == REQ_PDF && resultCode == RESULT_OK && data != null){

            Uri path = data.getData();

            try {
                InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(path);
                byte[] pdfInBytes = new byte[inputStream.available()];
                inputStream.read(pdfInBytes);
                encodedPDF = Base64.encodeToString(pdfInBytes, Base64.DEFAULT);

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void onBackPressed(){

    }
}
