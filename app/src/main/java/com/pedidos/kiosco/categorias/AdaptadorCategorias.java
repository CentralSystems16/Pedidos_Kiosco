package com.pedidos.kiosco.categorias;

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
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pedidos.kiosco.R;

import java.util.List;

public class AdaptadorCategorias extends RecyclerView.Adapter<AdaptadorCategorias.CategoriaViewHolder> {

    Context cContext;
   public static List<Categorias> listaCategorias;

    public AdaptadorCategorias(Context cContext, List<Categorias> listaCategorias) {

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
        Glide.with(cContext)
                .load(categorias.getImgCategoria())
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(categoriaViewHolder.imageView);

        categoriaViewHolder.itemView.setOnClickListener(v -> {

            CatFragment.gNombreCat = listaCategorias.get(posicion).getNombreCategoria();
            CatFragment.gImagen = listaCategorias.get(posicion).getImgCategoria();
            CatFragment.gIdCategoria = listaCategorias.get(posicion).getIdCategoria();
            FragmentTransaction fr = ((AppCompatActivity)cContext).getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new ModificarCategorias());
            fr.commit();

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
        imageView = itemView.findViewById(R.id.imgItemCategorias);

        }
    }
}