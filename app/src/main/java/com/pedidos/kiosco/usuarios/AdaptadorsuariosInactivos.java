package com.pedidos.kiosco.usuarios;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.fragments.ModificarUsuario;

import java.util.List;

public class AdaptadorsuariosInactivos extends RecyclerView.Adapter<AdaptadorsuariosInactivos.CategoriaViewHolder> {

    Context cContext;
   public static List<Usuarios> listaUsuarios;
    RequestQueue requestQueue;

    public AdaptadorsuariosInactivos(Context cContext, List<Usuarios> listaUsuarios) {

        this.cContext = cContext;
        AdaptadorsuariosInactivos.listaUsuarios = listaUsuarios;

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

            ModificarUsuario.gEstadoUs = 1;

            ejecutarServicio("http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/modificarUsuariosInactivos.php"
                    + "?estado_usuario=" + ModificarUsuario.gEstadoUs
                    + "&id_usuario=" + UsuarioFragment.gIdUsuario);

            Intent i = new Intent(categoriaViewHolder.itemView.getContext(), Principal.class);
            categoriaViewHolder.itemView.getContext().startActivity(i);

        });
    }

    public void ejecutarServicio (String URL){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> Toast.makeText(cContext, "USUARIO ACTIVADO CON ÉXITO", Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(cContext, error.toString(), Toast.LENGTH_SHORT).show());
        requestQueue = Volley.newRequestQueue(cContext);
        requestQueue.add(stringRequest);
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