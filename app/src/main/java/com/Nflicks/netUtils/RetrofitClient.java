package com.Nflicks.netUtils;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by CRAFT BOX on 5/1/2017.
 */

public class RetrofitClient {

    private static Retrofit retrofit = null;
    public static String service_url = "";
   
    //public static String message_pack_name = "ROYALA";
    public static String message_pack_name = "NFLICK";
    public static Retrofit getClient() {
        if (retrofit==null) {
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .readTimeout(36000, TimeUnit.SECONDS)
                    .connectTimeout(36000, TimeUnit.SECONDS)
                    .build();
            retrofit = new Retrofit.Builder()
                    .baseUrl(service_url)
                    .client(okHttpClient)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
