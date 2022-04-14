package com.pedidos.kiosco.fragments;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;
import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toolbar;
import com.github.barteksc.pdfviewer.PDFView;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.main.CorteCaja;
import com.pedidos.kiosco.utils.RecibirPDFReportes;
import java.io.File;

public class ObtenerDetReporte extends Fragment {

    private static final int READ_STORAGE_PERMISSION_REQUEST_CODE = 1000;
    PDFView pdfView;
    ProgressBar progressBar;
    Toolbar toolbar;
    ProgressDialog progressDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_obtener_det_reporte, container, false);

        pdfView = vista.findViewById(R.id.pdfView);
        progressBar = vista.findViewById(R.id.progressBar);

        toolbar = vista.findViewById(R.id.toolbarDetPedido);
        toolbar.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));

        progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        ImageView flechaReturn = vista.findViewById(R.id.returnDetReporte);
        flechaReturn.setOnClickListener(v -> {
            File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
            File file = new File(dir, "ComprobanteCorteCaja.pdf");
            boolean deleted = file.delete();
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new Home());
            fr.commit();
        });

        boolean granded = checkPermissionForReadExtertalStorage();
        if (!granded) {
            requestPermissionForReadExtertalStorage();
            progressDialog.dismiss();
        } else {
            String urlPdf = "http://34.239.139.117/android/kiosco/cliente/pedidos/ComprobanteCorteCaja.pdf";

            new RecibirPDFReportes(pdfView, progressBar).execute(urlPdf);
            progressDialog.dismiss();
        }

        return vista;

    }

    public boolean checkPermissionForReadExtertalStorage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int result =getContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
            return result == PackageManager.PERMISSION_GRANTED;
        }
        return false;
    }

    public void requestPermissionForReadExtertalStorage() {
        try {
            ActivityCompat.requestPermissions(requireActivity(), new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, READ_STORAGE_PERMISSION_REQUEST_CODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}