package com.pedidos.kiosco.other;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.fragments.TicketDatos;

public class Ayuda extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ayuda);

        FragmentTransaction fr = getSupportFragmentManager().beginTransaction();
        fr.replace(R.id.ayuda, new TicketDatos());
        fr.commit();

    }
}