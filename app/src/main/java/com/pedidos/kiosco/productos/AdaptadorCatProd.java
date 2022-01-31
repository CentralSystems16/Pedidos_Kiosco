package com.pedidos.kiosco.productos;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.categorias.CatFragment;
import com.pedidos.kiosco.categorias.Categorias;
import java.util.List;

public class AdaptadorCatProd extends RecyclerView.Adapter<AdaptadorCatProd.CategoriaViewHolder> {

    Context cContext;
    public static List<Categorias> listaCategorias;

    public AdaptadorCatProd(Context cContext, List<Categorias> listaCategorias) {

        this.cContext = cContext;
        this.listaCategorias = listaCategorias;

    }

    @NonNull
    @Override
    public CategoriaViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv_categorias, viewGroup, false);
        return new CategoriaViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoriaViewHolder categoriaViewHolder, int posicion) {

        Categorias categorias = listaCategorias.get(posicion);

        categoriaViewHolder.tvCategorias.setText(listaCategorias.get(posicion).getNombreCategoria());


        Glide.with(cContext).load(categorias.getImgCategoria()).into(categoriaViewHolder.imageView);

        categoriaViewHolder.itemView.setOnClickListener(v -> {

            CatFragment.gNombreCat = listaCategorias.get(posicion).getNombreCategoria();
            CatFragment.gImagen = listaCategorias.get(posicion).getImgCategoria();

            CatFragment.gIdCategoria = listaCategorias.get(posicion).getIdCategoria();
            Intent i = new Intent(cContext, ObtenerProductos.class);
            categoriaViewHolder.itemView.getContext().startActivity(i);

        });
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
            imageView = itemView.findViewById(R.id.imgItem);

        }
    }
}
