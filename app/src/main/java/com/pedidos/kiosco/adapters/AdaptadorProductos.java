package com.pedidos.kiosco.adapters;

import static com.pedidos.kiosco.Splash.gBlue;
import static com.pedidos.kiosco.Splash.gGreen;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.google.android.material.snackbar.Snackbar;
import com.pedidos.kiosco.Login;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.Splash;
import com.pedidos.kiosco.fragments.ObtenerProductos;
import com.pedidos.kiosco.model.Productos;
import com.pedidos.kiosco.other.ActualizarPedido;
import com.pedidos.kiosco.other.ActualizarPedidoMultiple;
import com.pedidos.kiosco.other.ContadorProductos;
import com.pedidos.kiosco.other.InsertarDetPedido;
import com.pedidos.kiosco.other.InsertarPedido;
import com.pedidos.kiosco.other.SumaMontoMultiple;
import com.pedidos.kiosco.other.SumaMontoMultipleIva;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class AdaptadorProductos extends RecyclerView.Adapter<AdaptadorProductos.ProductosViewHolder> {

    Context context;
    public static List<Productos> listaProductos;

    public AdaptadorProductos(Context context, List<Productos> listaUsuarios) {
        this.context = context;
        listaProductos = listaUsuarios;
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
    public void onBindViewHolder(@NonNull ProductosViewHolder productosViewHolder, @SuppressLint("RecyclerView") int i) {

        final Productos user = listaProductos.get(i);
        //TODO: Obtiene el nombre y el precio del modelado (Getter y Setter) En este caso solo usamos el getter.
        productosViewHolder.tvNombre.setText(listaProductos.get(i).getNombreProducto());
        productosViewHolder.tvPrecio.setText(Double.toString(listaProductos.get(i).getPrecioProducto()));
        Glide.with(context).load(user.getImgProducto()).into(productosViewHolder.imgItem);

        productosViewHolder.view.setOnClickListener(v -> {

            ProgressDialog progressDialog = new ProgressDialog(context, R.style.Custom);
            progressDialog.setMessage("Por favor, espera...");
            progressDialog.setCancelable(false);
            progressDialog.show();

                //TODO: Obtiene elos productos para posteriormente mostrarlos en cada cardView (En este caso solo muestra el nombre y el precio)
                ObtenerProductos.gNombreProd = listaProductos.get(i).getNombreProducto();
                ObtenerProductos.gIdProducto = listaProductos.get(i).getIdProducto();
                ObtenerProductos.gPrecio = listaProductos.get(i).getPrecioProducto();
                ObtenerProductos.gDetMonto = ObtenerProductos.gPrecio / 1.13;
                ObtenerProductos.gDetMontoIva = ObtenerProductos.gDetMonto * 0.13;
                ObtenerProductos.cantidad = listaProductos.get(i).getCantidad();
                ObtenerProductos.maximo = listaProductos.get(i).getMaximo();
                ObtenerProductos.minimo = listaProductos.get(i).getMinimo();

                if (ObtenerProductos.gPrecio == 0.0) {
                    Toast.makeText(context, "No se puede agregar este producto.", Toast.LENGTH_SHORT).show();
                    productosViewHolder.view.setEnabled(true);
                    progressDialog.dismiss();

                } else {

                            if (Login.gIdPedido == 0) {
                                new InsertarPedido(context).execute();
                            }

                            try {
                                new InsertarDetPedido(context).execute().get();
                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }

                            if (InsertarDetPedido.exitoInsertProd) {
                                AgregarProducto(v);
                                new ContadorProductos.GetDataFromServerIntoTextView(context).execute();
                                progressDialog.dismiss();
                            } else {
                                Toast.makeText(context, "Ocurrió un error inesperado...", Toast.LENGTH_SHORT).show();
                                progressDialog.dismiss();
                            }
                            new SumaMontoMultiple().execute();
                            new SumaMontoMultipleIva().execute();
                            new ActualizarPedidoMultiple(context).execute();

                }
        });
    }

    public void AgregarProducto (View view){

        Snackbar snack = Snackbar.make(view, "Producto agregado correctamente!",1000);
        view = snack.getView();
        FrameLayout.LayoutParams params =(FrameLayout.LayoutParams)view.getLayoutParams();
        params.gravity = Gravity.TOP;
        params.height = 120;
        view.setLayoutParams(params);
        snack.setBackgroundTint(Color.rgb(Splash.gRed,gGreen,gBlue));
        snack.show();
    }

    @Override
    public int getItemCount() {
        return listaProductos == null ? 0 : listaProductos.size();
    }

    public static class ProductosViewHolder extends RecyclerView.ViewHolder {

        TextView tvNombre, tvPrecio;
        public View view;
        ImageView imgItem;

        public ProductosViewHolder(@NonNull View itemView) {
            super(itemView);

            view = itemView;

            tvNombre = itemView.findViewById(R.id.tvNombre);
            tvPrecio = itemView.findViewById(R.id.tvPrecio);
            imgItem = itemView.findViewById(R.id.imgItemProd);
        }
    }

        public void filtrar(ArrayList<Productos> filtroProductos) {
        listaProductos = filtroProductos;
        notifyDataSetChanged();
    }
}
