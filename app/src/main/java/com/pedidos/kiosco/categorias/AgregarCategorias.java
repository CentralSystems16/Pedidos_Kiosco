package com.pedidos.kiosco.categorias;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.pedidos.kiosco.Principal;
import com.pedidos.kiosco.R;
import com.pedidos.kiosco.VariablesGlobales;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class AgregarCategorias extends AppCompatActivity {

    Button btnBuscar, btnSubir;
    ImageView iv;
    EditText et;
    TextView imagen, nueva;
    Bitmap bitmap;
    int PICK_IMAGE_REQUEST = 1;
    String UPLOAD_URL = "http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/upload.php", KEY_IMAGE = "img_categoria", KEY_NOMBRE = "nombre_categoria";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agregar_categorias);

        btnBuscar = findViewById(R.id.btnBuscar);
        btnSubir = findViewById(R.id.btnSubir);

        et = findViewById(R.id.editText);
        iv = findViewById(R.id.imageView);
        imagen = findViewById(R.id.imagenNueva);
        nueva = findViewById(R.id.nuevaCategoria);

        btnBuscar.setOnClickListener(v -> showFileChooser());

        btnSubir.setOnClickListener(v -> uploadImage());
    }

    public String getStringImagen(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    public void uploadImage() {
        final ProgressDialog loading = ProgressDialog.show(this, "Subiendo...", "Espere por favor");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                response -> {
                    loading.dismiss();
                    Toast.makeText(this, response, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(), Principal.class));

                }, error -> {
                    loading.dismiss();
                    Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
                }){
            @Override
            protected Map<String, String> getParams() {
                String nombre = et.getText().toString().trim();
                String imagen = getStringImagen(bitmap);

                Map<String, String> params = new Hashtable<>();
                params.put(KEY_NOMBRE, nombre);
                params.put(KEY_IMAGE, imagen);

                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Seleciona imagen"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), filePath);
                iv.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}