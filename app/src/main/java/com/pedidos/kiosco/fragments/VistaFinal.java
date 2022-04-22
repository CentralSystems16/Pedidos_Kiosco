package com.pedidos.kiosco.fragments;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import static com.pedidos.kiosco.Splash.gRed;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toolbar;
import com.google.android.material.button.MaterialButton;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.other.SumaMonto;
import com.pedidos.kiosco.other.SumaMontoDevolucion;

public class VistaFinal extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.fragment_vista_final, container, false);


        new SumaMonto().execute();
        new SumaMontoDevolucion().execute();

        Toolbar toolbar = vista.findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(Color.rgb(gRed, gGreen, gBlue));

        MaterialButton nuevo = vista.findViewById(R.id.btnRepetirPedido);
        nuevo.setStrokeColor(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));

        MaterialButton ver = vista.findViewById(R.id.btnVerOrdenes);
        ver.setStrokeColor(ColorStateList.valueOf(Color.rgb(gRed, gGreen, gBlue)));

        ImageButton homeEnd = vista.findViewById(R.id.homeEnd);
        homeEnd.setOnClickListener(v -> {
            TicketDatos.gTotal = 0.00;
            Login.gIdPedido = 0;
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new Home());
            fr.commit();
        });

        nuevo.setOnClickListener(view -> {
            TicketDatos.gTotal = 0.00;
            Login.gIdPedido = 0;
            Login.gIdMovimiento = 0;
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new Home());
            fr.commit();
        });

        ver.setOnClickListener(view -> {
            TicketDatos.gTotal = 0.00;
            Login.gIdPedido = 0;
            Principal.gIdEstadoCliente = 2;
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new ObtenerMovimientos());
            fr.commit();
        });

        return vista;
    }
}