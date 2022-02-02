package com.pedidos.kiosco.main;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.Splash;
import com.pedidos.kiosco.pay.AutFiscal;

public class ObtenerEstadoFiscal extends AppCompatActivity {

    public static int fiscalActivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.obtener_estado_fiscal);

        Toolbar fiscal = findViewById(R.id.toolbarFiscal);
        FloatingActionButton floatingActionButton = findViewById(R.id.floatingFiscal);

        fiscal.setBackgroundColor((Color.rgb(Splash.gRed, Splash.gGreen, Splash.gBlue)));
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));

        ImageButton regresar = findViewById(R.id.regresaraPrincipal);
        regresar.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), Principal.class)));

        floatingActionButton.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), AutFiscal.class)));

        CardView activo = findViewById(R.id.btnFiscalActivo);
        CardView inactivo = findViewById(R.id.btnVerFiscalInactivo);

        activo.setOnClickListener(view -> {
            fiscalActivo = 1;
            startActivity(new Intent(getApplicationContext(), ObtenerReportesFiscal.class));
        });

        inactivo.setOnClickListener(view -> {
            fiscalActivo = 0;
            startActivity(new Intent(getApplicationContext(), ObtenerReportesFiscal.class));
        });

    }

    @Override
    public void onBackPressed() {

    }

}