package com.pedidos.kiosco.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class MontoInicial extends Fragment {

    EditText montoInicial;
    public static Double montoInit;
    Date d = new Date();
    SimpleDateFormat fecc = new SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.getDefault());
    String fechacComplString = fecc.format(d);
    SimpleDateFormat ho = new SimpleDateFormat("h:mm a");
    String horaString = ho.format(d);
    int resultado;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.monto_inicial, container, false);

        montoInicial = vista.findViewById(R.id.montoInicial);

        Button aceptar = vista.findViewById(R.id.btnAceptar);

        SharedPreferences preferences2 = requireActivity().getSharedPreferences("preferenciasSucursal", Context.MODE_PRIVATE);
        resultado = preferences2.getInt("sucursal", 0);

        aceptar.setOnClickListener(view -> {
            if (montoInicial != null && montoInicial.length() > 0){
                montoInit = Double.valueOf((montoInicial.getText().toString()));
            }
            ejecutarServicio("http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/insertarCaja.php"
                    + "?id_usuario=" + Login.gIdUsuario
                    + "&id_caja=" + resultado
                    + "&fecha_ini=" + fechacComplString + " a las " + horaString
                    + "&fecha_fin=Activo"
                    + "&state=1"
                    + "&fondo_inicial=" + montoInicial.getText().toString());
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new Home());
            fr.commit();

        });

        return vista;
    }

    public void ejecutarServicio (String URL){

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {

                },
                volleyError -> progressDialog.dismiss()
        );
        RequestQueue requestQueue = Volley.newRequestQueue(requireContext());
        requestQueue.add(stringRequest);
    }

}