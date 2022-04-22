package com.pedidos.kiosco.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.fragments.Cliente;
import com.pedidos.kiosco.fragments.ModificarCliente;
import com.pedidos.kiosco.fragments.TicketDatos;
import com.pedidos.kiosco.model.Clients;
import com.pedidos.kiosco.Login;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorClientes extends RecyclerView.Adapter<AdaptadorClientes.CategoriaViewHolder> {

    Context cContext;
    public static List<Clients> listaClientes;
    public static String clienteActual;

    public AdaptadorClientes(Context cContext, List<Clients> listaClientes) {

        this.cContext = cContext;
        AdaptadorClientes.listaClientes = listaClientes;

    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv_clientes, viewGroup, false);
        return new CategoriaViewHolder(v);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder categoriaViewHolder, int posicion) {


        categoriaViewHolder.tvUsers.setText(listaClientes.get(posicion).getNombreCliente());

        categoriaViewHolder.cheque.setOnClickListener(view -> {

            Login.gIdCliente = listaClientes.get(posicion).getIdCliente();
            clienteActual = listaClientes.get(posicion).getNombreCliente();

            ejecutarServicio("http://" + VariablesGlobales.host + "/android/kiosco/cliente/scripts/scripts_php/actualizarClientePrefac.php"
                    + "?base=" + VariablesGlobales.dataBase
                    + "&id_cliente=" + Login.gIdCliente
                    + "&id_prefactura=" + Login.gIdPedido);

            FragmentTransaction fr = ((AppCompatActivity)cContext).getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new TicketDatos());
            fr.commit();

        });

        categoriaViewHolder.lapiz.setOnClickListener(view -> {
            Login.gIdCliente = listaClientes.get(posicion).getIdCliente();

            Cliente.nombre = listaClientes.get(posicion).getNombreCliente();
            Cliente.direccion = listaClientes.get(posicion).getDireccion();
            Cliente.dui = listaClientes.get(posicion).getDui();
            Cliente.numero = listaClientes.get(posicion).getNumero();

            FragmentTransaction fr = ((AppCompatActivity)cContext).getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new ModificarCliente());
            fr.commit();

        });

    }

    public void ejecutarServicio(String URL) {

        ProgressDialog progressDialog = new ProgressDialog(cContext, R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> progressDialog.dismiss(),
                volleyError -> progressDialog.dismiss()
        );
        RequestQueue requestQueue = Volley.newRequestQueue(cContext);
        requestQueue.add(stringRequest);
    }

    @Override
    public int getItemCount() {
        return listaClientes.size();
    }

    public static class CategoriaViewHolder extends RecyclerView.ViewHolder {

        TextView tvUsers;
        ImageView cheque, lapiz;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);

        tvUsers = itemView.findViewById(R.id.tvClientes);
        cheque = itemView.findViewById(R.id.cheque);
        lapiz = itemView.findViewById(R.id.editar);

        }
    }

    public void filtrarClientes(ArrayList<Clients> filtroClientes) {
        listaClientes = filtroClientes;
        notifyDataSetChanged();
    }

}