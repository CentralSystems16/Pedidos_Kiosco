package com.pedidos.kiosco.main;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toolbar;
import com.github.barteksc.pdfviewer.PDFView;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.utils.RecibirPDFReportes;

public class ObtenerManual extends AppCompatActivity {

    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1000;
    PDFView pdfView;
    ProgressBar progressBar;
    Toolbar toolbar;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.obtener_manual);

        pdfView = findViewById(R.id.pdfViewManual);
        progressBar = findViewById(R.id.progressBarManual);

        toolbar = findViewById(R.id.toolbarDetPedidoManual);
        toolbar.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));

        progressDialog = new ProgressDialog(ObtenerManual.this, R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ImageView flechaReturn = findViewById(R.id.returnDetReporteManual);
        flechaReturn.setOnClickListener(v -> {

            startActivity(new Intent(getApplicationContext(), Principal.class));

        });


        boolean granded = checkPermissionForReadExtertalStorage();
        if (!granded) {
            requestPermissionForReadExtertalStorage();
            progressDialog.dismiss();
        } else {
            String urlPdf = "http://34.239.139.117/android/kiosco/cliente/recursos/MANUAL DE USUARIO PARA EASYPOS APP.pdf";

            new RecibirPDFReportes(pdfView, progressBar).execute(urlPdf);
            progressDialog.dismiss();
        }

    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExtertalStorage() {
        try {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}