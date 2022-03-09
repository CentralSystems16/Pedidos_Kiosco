package com.pedidos.kiosco.adapters.reportes;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pedidos.kiosco.R;

public class ReporteVentatotal extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.reporte_ventatotal, container, false);



        return vista;
    }
}