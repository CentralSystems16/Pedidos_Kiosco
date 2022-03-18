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
    SimpleDateFormat fecc = new SimpleDateFormat("d '-' MMMM '-' yyyy", Locale.getDefault());
    String fechacComplString = fecc.format(d);
    SimpleDateFormat ho = new SimpleDateFormat("h:mm a");
    String horaString = ho.format(d);
    int resultado;
    Button cant1, cant2, cant3, cant4, cant5, cant6;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.monto_inicial, container, false);

        montoInicial = vista.findViewById(R.id.montoInicial);

        Button aceptar = vista.findViewById(R.id.btnAceptar);
        cant1 = vista.findViewById(R.id.btnCant1);
        cant2 = vista.findViewById(R.id.btnCant2);
        cant3 = vista.findViewById(R.id.btnCant3);
        cant4 = vista.findViewById(R.id.btnCant4);
        cant5 = vista.findViewById(R.id.btnCant5);
        cant6 = vista.findViewById(R.id.btnCant6);

        Button regresar = vista.findViewById(R.id.btnRegresar);
        regresar.setOnClickListener(view -> {
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new Home());
            fr.commit();
        });

        cant1.setOnClickListener(view -> {
            String[] parts = cant1.getText().toString().split(" ");
            String part1 = parts[0];
            String part2 = parts[1];
            montoInicial.setText(part2);
        });

        cant2.setOnClickListener(view -> {
            String[] parts = cant2.getText().toString().split(" ");
            String part1 = parts[0];
            String part2 = parts[1];
            montoInicial.setText(part2);
        });

        cant3.setOnClickListener(view -> {
            String[] parts = cant3.getText().toString().split(" ");
            String part1 = parts[0];
            String part2 = parts[1];
            montoInicial.setText(part2);
        });

        cant4.setOnClickListener(view -> {
            String[] parts = cant4.getText().toString().split(" ");
            String part1 = parts[0];
            String part2 = parts[1];
            montoInicial.setText(part2);
        });

        cant5.setOnClickListener(view -> {
            String[] parts = cant5.getText().toString().split(" ");
            String part1 = parts[0];
            String part2 = parts[1];
            montoInicial.setText(part2);
        });

        cant6.setOnClickListener(view -> {
            String[] parts = cant6.getText().toString().split(" ");
            String part1 = parts[0];
            String part2 = parts[1];
            montoInicial.setText(part2);
        });

        SharedPreferences preferences2 = requireActivity().getSharedPreferences("preferenciasSucursal", Context.MODE_PRIVATE);
        resultado = preferences2.getInt("sucursal", 0);

        aceptar.setOnClickListener(view -> {
            if (montoInicial != null && montoInicial.length() > 0){
                montoInit = Double.valueOf((montoInicial.getText().toString()));
            }
            ejecutarServicio("http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/insertarCaja.php"
                    + "?base=" + VariablesGlobales.dataBase
                    + "&id_usuario=" + Login.gIdUsuario
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