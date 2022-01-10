package com.pedidos.kiosco.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.ProgressBar;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.github.barteksc.pdfviewer.PDFView;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.utils.RecibirPDFReportes;

public class ObtenerDetReporte extends AppCompatActivity {

    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1000;
    PDFView pdfView;
    ProgressBar progressBar;

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.obtener_det_reporte);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        pdfView = findViewById(R.id.pdfView);
        progressBar = findViewById(R.id.progressBar);

        ImageView flechaReturn = findViewById(R.id.flechaReturn);
        flechaReturn.setOnClickListener(v -> {

                startActivity(new Intent(getApplicationContext(), ObtenerReportes.class));

        });

        boolean granded = checkPermissionForReadExtertalStorage();
        if (!granded) {
            requestPermissionForReadExtertalStorage();
        } else {
            String urlPdf = "http://34.239.139.117/android/res/cliente/pedidos/Cliente"+ Login.gIdClienteReporte + "/" + Login.gIdPedidoReporte +" Examen.pdf";
            new RecibirPDFReportes(pdfView, progressBar).execute(urlPdf);
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

    @Override
    public void onBackPressed() {

    }
}