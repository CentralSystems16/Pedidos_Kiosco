package com.pedidos.kiosco.desing;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import com.pedidos.kiosco.R;

public class EnviandoTicket extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enviando_ticket);

        new Handler().postDelayed(() -> {
                startActivity(new Intent(getApplicationContext(), VistaFinal.class));
                finish();
            }, 5000);
        }

    @Override
    public void onBackPressed(){

    }
    }