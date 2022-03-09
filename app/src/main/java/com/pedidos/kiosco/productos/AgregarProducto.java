package com.pedidos.kiosco.productos;

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
import com.pedidos.kiosco.categorias.CatFragment;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Hashtable;
import java.util.Map;

public class AgregarProducto extends AppCompatActivity {

    Button guardarNewProd, btnBuscarProd;
    ImageView iv;
    EditText etAddProd, etAddPrec;
    Bitmap bitmap;
    int PICK_IMAGE_REQUEST = 1;
    String UPLOAD_URL = "http://"+ VariablesGlobales.host +"/android/kiosco/cliente/scripts/scripts_php/uploadProducto.php" + "?base=" + VariablesGlobales.dataBase;
    String KEY_IMAGE = "img_producto";
    String KEY_NOMBRE = "nombre_producto";
    String KEY_PRECIO = "precio_producto";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.agregar_producto);

        etAddProd = findViewById(R.id.etAddProd);
        etAddPrec = findViewById(R.id.etAddPrec);

        iv = findViewById(R.id.ivProducto);

        btnBuscarProd = findViewById(R.id.btnBuscarProducto);
        btnBuscarProd.setOnClickListener(v -> showFileChooser());

        guardarNewProd = findViewById(R.id.btnGuardarProd2);
        guardarNewProd.setOnClickListener(v -> {

            uploadImage();
            Intent i = new Intent(this, Principal.class);
            startActivity(i);

        });
    }

    public String getStringImagen(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }

    public void uploadImage() {
        final ProgressDialog loading = ProgressDialog.show(this, "Subiendo...", "Espere por favor");

        StringRequest stringRequest = new StringRequest(Request.Method.POST, UPLOAD_URL,
                response -> {
                    loading.dismiss();
                    Toast.makeText(this, response, Toast.LENGTH_LONG).show();
                }, error -> {
            loading.dismiss();
            Toast.makeText(this, error.getMessage(), Toast.LENGTH_LONG).show();
        }){
            @Override
            protected Map<String, String> getParams() {

                String imagen = getStringImagen(bitmap);
                String nombre = etAddProd.getText().toString().trim();
                String precio = etAddPrec.getText().toString().trim();
                int idCategoria = CatFragment.gIdCategoria;

                Map<String, String> params = new Hashtable<>();
                params.put(KEY_IMAGE, imagen);
                params.put(KEY_NOMBRE, nombre);
                params.put(KEY_PRECIO, precio);
                params.put("id_categoria", String.valueOf(idCategoria));

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


