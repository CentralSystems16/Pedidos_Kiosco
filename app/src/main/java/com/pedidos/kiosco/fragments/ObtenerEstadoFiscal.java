package com.pedidos.kiosco.fragments;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.Splash;

public class ObtenerEstadoFiscal extends Fragment {

    public static int fiscalActivo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_obtener_estado_fiscal, container, false);

        Toolbar fiscal = vista.findViewById(R.id.toolbarFiscal);
        FloatingActionButton floatingActionButton = vista.findViewById(R.id.floatingFiscal);

        fiscal.setBackgroundColor((Color.rgb(Splash.gRed, Splash.gGreen, Splash.gBlue)));
        floatingActionButton.setBackgroundTintList(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));

        ImageButton regresar = vista.findViewById(R.id.regresaraPrincipal);
        regresar.setOnClickListener(view -> {
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new Home());
            fr.commit();
        });

        floatingActionButton.setOnClickListener(view -> {
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new AutFiscal());
            fr.commit();
        });

        CardView activo = vista.findViewById(R.id.btnFiscalActivo);
        CardView inactivo = vista.findViewById(R.id.btnVerFiscalInactivo);

        activo.setOnClickListener(view -> {
            fiscalActivo = 1;
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new ObtenerReportesFiscal());
            fr.commit();
        });

        inactivo.setOnClickListener(view -> {
            fiscalActivo = 0;
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new ObtenerReportesFiscal());
            fr.commit();
        });

        return vista;

    }
}