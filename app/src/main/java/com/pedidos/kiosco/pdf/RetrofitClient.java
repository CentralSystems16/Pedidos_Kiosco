package com.pedidos.kiosco.pdf;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {

    private static final String BASE_URL="http://34.239.139.117/android/kiosco/cliente/scripts/scripts_php/";
    private static RetrofitClient myClient;
    private Retrofit retrofit;

    private RetrofitClient(){
        retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static synchronized RetrofitClient getInstance(){
        if (myClient == null){
            myClient = new RetrofitClient();
        }
        return myClient;
    }

    public Api getAPI(){
        return retrofit.create(Api.class);

    }
}
