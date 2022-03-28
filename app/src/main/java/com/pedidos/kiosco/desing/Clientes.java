package com.pedidos.kiosco.desing;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentTransaction;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.adapters.AdaptadorClientes;
import com.pedidos.kiosco.fragments.Cliente;

public class Clientes extends DialogFragment {

    public static TextView clienteActual;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View vista = inflater.inflate(R.layout.clientes, container,false);
        vista.setFocusableInTouchMode(false);

        clienteActual = vista.findViewById(R.id.clienteActual);

        if (AdaptadorClientes.clienteActual != null){
            Clientes.clienteActual.setText(AdaptadorClientes.clienteActual);
        }

        CardView mantener = vista.findViewById(R.id.btnMantener);
        mantener.setOnClickListener(view -> {
            getDialog().dismiss();
        });

        CardView registrar = vista.findViewById(R.id.btnRegistrar);
        registrar.setOnClickListener(view -> {
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new Cliente());
            fr.commit();
            getDialog().dismiss();
        });

        return vista;

    }
}
