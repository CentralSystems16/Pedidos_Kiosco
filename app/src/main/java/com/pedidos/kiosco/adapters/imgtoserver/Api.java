package com.pedidos.kiosco.adapters.imgtoserver;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {

    @FormUrlEncoded
    @POST("server_upload_image.php")
    Call<ResponsePOJO> uploadImage(
            @Field("EN_IMAGE") String encodedImage,
            @Field("nombre_categoria") String nombre_categoria
    );
}
