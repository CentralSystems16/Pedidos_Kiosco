package com.pedidos.kiosco.categorias;

import static android.app.Activity.RESULT_OK;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.imgtoserver.ResponsePOJO;
import com.pedidos.kiosco.imgtoserver.RetroClient2;
import com.pedidos.kiosco.categorias.CatFragment;
import com.pedidos.kiosco.usuarios.UsuarioFragment;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ModificarCategorias extends Fragment {

    int IMG_REQUEST = 21;
    Bitmap bitmap;
    ImageView imageView;
    Button btnSelectImage, btnUploadImage, btnActiva, btnInactiva, btnCancelar;
    private EditText nombreImagen;
    public static int gEstadoAct = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View vista = inflater.inflate(R.layout.modificar_categorias_fragment, container, false);

        imageView = vista.findViewById(R.id.imageViewEdit);
        btnSelectImage = vista.findViewById(R.id.btnSelectEditImage);
        btnUploadImage = vista.findViewById(R.id.btnSelectUploadImage);

        btnCancelar = vista.findViewById(R.id.btncancelaract);
        btnCancelar.setOnClickListener(view -> {
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new CatFragment());
            fr.commit();
        });

        btnActiva = vista.findViewById(R.id.btnActivo);
        btnInactiva = vista.findViewById(R.id.btnInactivo);

        if (CatFragment.estadoCategoria == 0) {
            btnInactiva.setVisibility(View.INVISIBLE);
        }

        else {
            btnActiva.setVisibility(View.INVISIBLE);
        }

        nombreImagen = vista.findViewById(R.id.nombreImagenEdit);
        nombreImagen.setText(CatFragment.gNombreCat);

        Glide.with(this).load(CatFragment.gImagen).into(imageView);
        Glide.with(getContext())
                .load(CatFragment.gImagen)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageView);

        btnSelectImage.setOnClickListener(v -> {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, IMG_REQUEST);

        });

        btnActiva.setOnClickListener(v -> {

            gEstadoAct = 1;
            btnActiva.setVisibility(View.INVISIBLE);
            btnInactiva.setVisibility(View.VISIBLE);
            Toast.makeText(getContext(), "Categor??a activada nuevamente", Toast.LENGTH_SHORT).show();
        });


        btnInactiva.setOnClickListener(v -> {

            gEstadoAct = 0;
            btnActiva.setVisibility(View.VISIBLE);
            btnInactiva.setVisibility(View.INVISIBLE);
            Toast.makeText(getContext(), "La categor??a a sido desactivada", Toast.LENGTH_SHORT).show();
        });

        btnUploadImage.setOnClickListener(v -> uploadImage());

        return vista;
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == IMG_REQUEST && resultCode == RESULT_OK && data != null){

            Uri path = data.getData();

            try {
                bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(),path);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {
        String encodedImage = null;
        if (bitmap != null) {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteArrayOutputStream);
            byte[] imageInByte = byteArrayOutputStream.toByteArray();
            encodedImage = Base64.encodeToString(imageInByte, Base64.DEFAULT);


            Call<ResponsePOJO> call = RetroClient2.getInstance().getApi2().uploadImage(VariablesGlobales.dataBase, encodedImage, nombreImagen.getText().toString(), gEstadoAct, CatFragment.gIdCategoria);
            call.enqueue(new Callback<ResponsePOJO>() {
                @Override
                public void onResponse(@NonNull Call<ResponsePOJO> call, @NonNull Response<ResponsePOJO> response) {
                    FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                    fr.replace(R.id.fragment_layout, new CatFragment());
                    fr.commit();
                }

                @Override
                public void onFailure(@NonNull Call<ResponsePOJO> call, @NonNull Throwable t) {
                    Toast.makeText(getContext(), "No se pudo actualizar " + t, Toast.LENGTH_SHORT).show();
                }
            });
        }
        else {
            ejecutarServicio("http://" + VariablesGlobales.host
                    + "/android/kiosco/cliente/scripts/scripts_php/actualizarCategoria.php"
                    + "?base=" + VariablesGlobales.dataBase
                    + "&nombre_categoria=" + nombreImagen.getText().toString()
                    + "&estado_categoria=" + gEstadoAct
                    + "&id_categoria=" + CatFragment.gIdCategoria);
        }
    }

    public void ejecutarServicio (String URL){

        ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Custom);
        progressDialog.setMessage("Por favor, espera...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, URL,
                response -> {
                    progressDialog.dismiss();
                    FragmentTransaction fr = getFragmentManager().beginTransaction();
                    fr.replace(R.id.fragment_layout, new CatFragment());
                    fr.commit();
                },
                volleyError -> {
                    Toast.makeText(getContext(), "No se pudo actualizar " + volleyError, Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
        );
        RequestQueue requestQueue = Volley.newRequestQueue(requireActivity());
        requestQueue.add(stringRequest);
    }

}