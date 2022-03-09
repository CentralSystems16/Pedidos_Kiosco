package com.pedidos.kiosco.categorias;

import static com.pedidos.kiosco.fragments.ModificarCategorias.gEstadoAct;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.fragments.ModificarCategorias;

import java.util.List;

public class AdaptadorCategoriasInvalidas extends RecyclerView.Adapter<AdaptadorCategoriasInvalidas.CategoriaViewHolder> {

    Context cContext;
   public static List<Categorias> listaCategorias;
    RequestQueue requestQueue;

    public AdaptadorCategoriasInvalidas(Context cContext, List<Categorias> listaCategorias) {

        this.cContext = cContext;
        this.listaCategorias = listaCategorias;

    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv_categorias, viewGroup, false);
        return new CategoriaViewHolder(v);

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder categoriaViewHolder, int posicion) {

        Categorias categorias = listaCategorias.get(posicion);

    categoriaViewHolder.tvCategorias.setText(listaCategorias.get(posicion).getNombreCategoria());

     Glide.with(cContext).load(categorias.getImgCategoria()).into(categoriaViewHolder.imageView);

        categoriaViewHolder.itemView.setOnClickListener(v -> {

            CatFragment.gNombreCat = listaCategorias.get(posicion).getNombreCategoria();
            CatFragment.gImagen = listaCategorias.get(posicion).getImgCategoria();

            CatFragment.gIdCategoria = listaCategorias.get(posicion).getIdCategoria();

            gEstadoAct = 1;

            ejecutarservicio("http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/actualizarCategoriasInactivas.php"
                    + "?base=" + VariablesGlobales.dataBase
                    + "&estado_categoria=" + gEstadoAct
                    + "&id_categoria=" + CatFragment.gIdCategoria);

            FragmentTransaction fr = ((AppCompatActivity)cContext).getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new CatFragment());
            fr.commit();

        });
    }

    public void ejecutarservicio(String URL){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> Toast.makeText(cContext, "CATEGORIA ACTIVADA CON ÉXITO", Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(cContext, error.toString(), Toast.LENGTH_SHORT).show());
                requestQueue = Volley.newRequestQueue(cContext);
                requestQueue.add(stringRequest);

    }

    @Override
    public int getItemCount() {
        return listaCategorias.size();
    }

    public static class CategoriaViewHolder extends RecyclerView.ViewHolder {

   TextView tvCategorias;
   ImageView imageView;

        public CategoriaViewHolder(@NonNull View itemView) {
            super(itemView);

        tvCategorias = itemView.findViewById(R.id.tvNombreCat);
        imageView = itemView.findViewById(R.id.imgItemCategorias);

        }
    }
}