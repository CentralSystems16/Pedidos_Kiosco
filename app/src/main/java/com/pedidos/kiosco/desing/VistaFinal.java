package com.pedidos.kiosco.desing;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.fragments.TicketDatos;
import com.pedidos.kiosco.main.ObtenerMovimientos;
import com.pedidos.kiosco.other.SumaMonto;
import com.pedidos.kiosco.other.SumaMontoDevolucion;

public class VistaFinal extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.vista_final);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        new SumaMonto().execute();
        new SumaMontoDevolucion().execute();

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

    @Override
    public void onBackPressed(){

    }
}
