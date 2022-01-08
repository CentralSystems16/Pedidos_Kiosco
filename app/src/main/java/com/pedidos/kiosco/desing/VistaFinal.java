package com.pedidos.kiosco.desing;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import androidx.appcompat.app.AppCompatActivity;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.fragments.TicketDatos;;

public class VistaFinal extends AppCompatActivity implements View.OnClickListener {

    Button btnRepetir, btnCerrarSesion, salir;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.vista_final);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        
        ImageButton homeEnd = findViewById(R.id.homeEnd);
        homeEnd.setOnClickListener(v -> {
            TicketDatos.gTotal = 0.00;
            Login.gIdPedido = 0;
            startActivity(new Intent(getApplicationContext(), Principal.class));
        });

        btnRepetir = findViewById(R.id.btnRepetirExamen);
        btnRepetir.setOnClickListener(this);

        btnCerrarSesion = findViewById(R.id.btnCerrarSesion);
        btnCerrarSesion.setOnClickListener(this);

        salir = findViewById(R.id.btnSalir);
        salir.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {

            if (btnRepetir.isPressed()) {

                TicketDatos.gTotal = 0.00;
                Login.gIdPedido = 0;
                Intent i = new Intent(this, Principal.class);
                startActivity(i);

            }

            if (salir.isPressed()) {

                Login.gIdPedido = 0;
                finishAffinity();
                finishActivity(0);
                System.exit(0);

            }
            if (btnCerrarSesion.isPressed()) {
                TicketDatos.gTotal = 0.00;
                Login.gIdPedido = 0;

            }
    }

    @Override
    public void onBackPressed(){

    }
}
