package com.pedidos.kiosco.adapters.imgtoserver;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api2 {

    @FormUrlEncoded
    @POST("server_update_image.php")
    Call<ResponsePOJO> uploadImage(
            @Field("EN_IMAGE") String encodedImage,
            @Field("nombre_categoria") String nombre_categoria,
            @Field("estado_categoria") int estado_categoria,
            @Field("id_categoria") int id_categoria


    );
}
