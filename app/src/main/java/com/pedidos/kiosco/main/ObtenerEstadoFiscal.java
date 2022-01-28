package com.pedidos.kiosco.main;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.pay.AutFiscal;

public class ObtenerEstadoFiscal extends AppCompatActivity {

    public static int fiscalActivo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.obtener_estado_fiscal);

        FloatingActionButton addButton = findViewById(R.id.floatingFiscal);
        addButton.setOnClickListener(view -> startActivity(new Intent(getApplicationContext(), AutFiscal.class)));

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
}