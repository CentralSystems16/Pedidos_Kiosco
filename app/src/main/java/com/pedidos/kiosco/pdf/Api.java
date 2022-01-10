package com.pedidos.kiosco.pdf;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface Api {

    @FormUrlEncoded
    @POST("server_upload_pdf.php")

    Call<ResponsePOJO> uploadDocument(
            @Field("PDF") String encodedPDF,
            @Field("id_prefactura") int id_prefactura,
            @Field("id_cliente") int id_cliente
    );

}
