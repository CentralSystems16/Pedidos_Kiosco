package com.pedidos.kiosco.productos;

import static com.pedidos.kiosco.fragments.ModificarProductos.gEstadoProd;

import android.annotation.SuppressLint;
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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.categorias.CatFragment;
import com.pedidos.kiosco.fragments.ModificarProductos;

import java.util.ArrayList;
import java.util.List;

public class AdaptadorProductosInactivos extends RecyclerView.Adapter<AdaptadorProductosInactivos.ProductosViewHolder> {

    Context context;
    public static List<Productos> listaProductos;
    @SuppressLint("StaticFieldLeak")
    RequestQueue requestQueue;

    public AdaptadorProductosInactivos(Context context, List<Productos> listaProductos) {
        this.context = context;
        AdaptadorProductosInactivos.listaProductos = listaProductos;
    }

    @NonNull
    @Override
    public ProductosViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_rv_productos, viewGroup, false);
        return new ProductosViewHolder(v);
    }

    @SuppressLint("SetTextI18n")
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBindViewHolder(@NonNull ProductosViewHolder productosViewHolder, int i) {

        //final Productos user = listaProductos.get(i);

        //TODO: Obtiene el nombre y el precio del modelado (Getter y Setter) En este caso solo usamos el getter.
        productosViewHolder.tvNombre.setText(listaProductos.get(i).getNombreProducto());
        productosViewHolder.tvPrecio.setText(Double.toString(listaProductos.get(i).getPrecioProducto()));

        //TODO: Se le asigna un color al cardview cuando posteriormente sea seleccionado
        //productosViewHolder.view.setBackgroundTintList(ColorStateList.valueOf(user.isSelect() ? Color.GRAY : Color.rgb(0, 151, 167)));

        productosViewHolder.view.setOnClickListener(v -> {

            //TODO: Si el boton es seleccionado que cambie de color
            //user.setSelect(!user.isSelect());
            //productosViewHolder.view.setBackgroundTintList(ColorStateList.valueOf(user.isSelect() ? Color.GRAY : Color.rgb(0,151,167)));

            //TODO: Guardar el estado del carrito

            //TODO: Cantidad de productos que se actualizan graficamente en el carrito


            /*if (user.isSelect){
                tvCantProductos.setText(""+(Integer.parseInt(tvCantProductos.getText().toString().trim()) - 1));
                carroCompra.add(listaProductos.get(i));
            }*/

            //TODO: Obtiene elos productos para posteriormente mostrarlos en cada cardView (En este caso solo muestra el nombre y el precio.
            ProdFragment.gNombreProd = listaProductos.get(i).getNombreProducto();
            ProdFragment.gIdProducto = listaProductos.get(i).getIdProducto();
            ProdFragment.gPrecio = listaProductos.get(i).getPrecioProducto();
            ProdFragment.gDetMonto = ProdFragment.gPrecio / 1.13;
            ProdFragment.gDetMontoIva = ProdFragment.gDetMonto * 0.13;
            ProdFragment.gOpciones = listaProductos.get(i).getOpciones();

            gEstadoProd = 1;

            ejecutarservicio(
                    "http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/actualizarProductosInactivos.php"
                            + "?estado_producto=" + gEstadoProd
                            + "&id_producto=" + ProdFragment.gIdProducto);

            FragmentTransaction fr = ((AppCompatActivity)context).getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new ProdFragment());
            fr.commit();

             });
        }

    public void ejecutarservicio(String URL){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> Toast.makeText(context, "PRODUCTO ACTIVADO CON ÉXITO", Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(context, error.toString(), Toast.LENGTH_SHORT).show());
        requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);

    }

    @Override
    public int getItemCount() {
            return listaProductos == null ? 0 : listaProductos.size();
    }

    public static class ProductosViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre, tvPrecio;
        public View view;

        public ProductosViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;

            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
        }
    }

    public void filtrar(ArrayList<Productos> filtroUsuarios) {
        listaProductos = filtroUsuarios;
        notifyDataSetChanged();
    }
}
