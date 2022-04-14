package com.pedidos.kiosco.fragments;

import static android.app.Activity.RESULT_OK;
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
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import com.pedidos.kiosco.imgtoserver.ResponsePOJO;
import com.pedidos.kiosco.imgtoserver.RetroClient;
import com.pedidos.kiosco.categorias.CatFragment;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AgregarCategorias extends Fragment {

    int IMG_REQUEST = 21;
    Bitmap bitmap;
    ImageView imageView;
    Button btnSelectImage, btnUploadImage, btnCancelar;
    private EditText nombreImagen;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View vista = inflater.inflate(R.layout.fragment_agregar_categorias, container, false);

        imageView = vista.findViewById(R.id.imageViewAddCat);
        btnSelectImage = vista.findViewById(R.id.btnSelectImage);
        btnUploadImage = vista.findViewById(R.id.btnUploadImage);

        nombreImagen = vista.findViewById(R.id.nombreImagen);

        btnCancelar = vista.findViewById(R.id.btncancelarAdd);
        btnCancelar.setOnClickListener(view -> {
            FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
            fr.replace(R.id.fragment_layout, new CatFragment());
            fr.commit();
        });

        btnSelectImage.setOnClickListener(v -> {

            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, IMG_REQUEST);

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
                bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),path);
                imageView.setImageBitmap(bitmap);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadImage() {

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,75, byteArrayOutputStream);
        byte[] imageInByte = byteArrayOutputStream.toByteArray();
        String encodedImage =  Base64.encodeToString(imageInByte,Base64.DEFAULT);

        Call<ResponsePOJO> call = RetroClient.getInstance().getApi().uploadImage(VariablesGlobales.dataBase, encodedImage, nombreImagen.getText().toString());
        call.enqueue(new Callback<ResponsePOJO>() {
            @Override
            public void onResponse(@NonNull Call<ResponsePOJO> call, @NonNull Response<ResponsePOJO> response) {
                Toast.makeText(getContext(), "Categoria agregada correctamente", Toast.LENGTH_SHORT).show();
                FragmentTransaction fr = requireActivity().getSupportFragmentManager().beginTransaction();
                fr.replace(R.id.fragment_layout, new CatFragment());
                fr.commit();
            }

            @Override
            public void onFailure(@NonNull Call<ResponsePOJO> call, @NonNull Throwable t) {
                Toast.makeText(getContext(), "No se pudo agregar " + t, Toast.LENGTH_SHORT).show();
            }
        });
    }

}