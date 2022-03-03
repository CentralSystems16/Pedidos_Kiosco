package com.pedidos.kiosco.adapters.imgtoserver;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetroClient2 {
    private static final String BASE_URL="http://34.239.139.117/android/kiosco/cliente/scripts/scripts_php/";
    private static RetroClient2 myClient;
    private Retrofit retrofit;

    private RetroClient2(){
        retrofit=new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
    }

    public static synchronized RetroClient2 getInstance(){
        if (myClient==null){
            myClient=new RetroClient2();
        }
        return myClient;
    }

    public Api2 getApi2(){
        return retrofit.create(Api2.class);

    }
}
