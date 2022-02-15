package com.pedidos.kiosco.desing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;

import java.io.File;

public class EnviandoTicket extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enviando_ticket);

        /*File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).toString());
        File file = new File(dir, Login.gIdPedido + " Examen.pdf");
        boolean deleted = file.delete();*/

        new Handler().postDelayed(() -> {
                startActivity(new Intent(getApplicationContext(), VistaFinal.class));
                finish();
            }, 10000);
        }

    @Override
    public void onBackPressed(){

    }
    }