package com.pedidos.kiosco.usuarios;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import com.pedidos.kiosco.R;
import com.pedidos.kiosco.fragments.ModificarUsuario;
import com.pedidos.kiosco.fragments.TicketDatos;

import java.util.List;

public class Adaptadorsuarios extends RecyclerView.Adapter<Adaptadorsuarios.CategoriaViewHolder> {

    Context cContext;
    public static List<Usuarios> listaUsuarios;

    public Adaptadorsuarios(Context cContext, List<Usuarios> listaUsuarios) {

        this.cContext = cContext;
        Adaptadorsuarios.listaUsuarios = listaUsuarios;

    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv_usuarios, viewGroup, false);
        return new CategoriaViewHolder(v);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder categoriaViewHolder, int posicion) {

        categoriaViewHolder.tvUsers.setText(listaUsuarios.get(posicion).getNombreUsuario());

        categoriaViewHolder.itemView.setOnClickListener(v -> {

            UsuarioFragment.gIdUsuario = listaUsuarios.get(posicion).getIdUsuario();
            UsuarioFragment.gNombreUsuario = listaUsuarios.get(posicion).getNombreUsuario();
            UsuarioFragment.gLoginUusario = listaUsuarios.get(posicion).getLoginUsuario();
            UsuarioFragment.gPasswordUsuario = listaUsuarios.get(posicion).getPasswordUsuarios();
            UsuarioFragment.gPasswordRepeatUsuario = listaUsuarios.get(posicion).getPasswordRepeatUsuarios();
            UsuarioFragment.gEmailUsuario = listaUsuarios.get(posicion).getEmailUsuario();
            UsuarioFragment.gIdCargo = listaUsuarios.get(posicion).getIdCargo();
            UsuarioFragment.gEstadoUsuario = listaUsuarios.get(posicion).getIdEstado();

            FragmentManager fragmentManager = ((FragmentActivity) cContext).getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.add(R.id.fragment_layout, new ModificarUsuario());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();

        });
    }

    @Override
    public int getItemCount() {
        return listaUsuarios.size();
    }

    public static class CategoriaViewHolder extends RecyclerView.ViewHolder {

   TextView tvUsers;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);

        tvUsers = itemView.findViewById(R.id.tvNombreUsers);

        }
    }
}