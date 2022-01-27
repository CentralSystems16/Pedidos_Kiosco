package com.pedidos.kiosco.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.pedidos.kiosco.R;

public class ObtenerEstadoFiscal extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.obtener_estado_fiscal);

        CardView activo = findViewById(R.id.btnFiscalActivo);
        CardView inactivo = findViewById(R.id.btnVerFiscalInactivo);

        activo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ObtenerReportesFiscal.class));
            }
        });

    }
}