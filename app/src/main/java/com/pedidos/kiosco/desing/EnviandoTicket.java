package com.pedidos.kiosco.desing;

import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.fragments.ObtenerMovimientos;

public class EnviandoTicket extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enviando_ticket);

        new Handler().postDelayed(() -> {
            FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new ObtenerMovimientos());
            fr.commit();
                finish();
            }, 5000);
        }

    @Override
    public void onBackPressed(){

    }
    }