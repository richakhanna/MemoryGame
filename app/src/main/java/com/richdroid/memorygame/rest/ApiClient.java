package com.richdroid.memorygame.rest;

/**
 * Created by richa.khanna on 5/11/16.
 */

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class ApiClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit == null) {
            ResponseInterceptor responseInterceptor = new ResponseInterceptor();

            HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
            httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
            okHttpClient.addInterceptor(responseInterceptor);
            okHttpClient.addInterceptor(httpLoggingInterceptor);  // <-- add logging interceptor at last

            retrofit = new Retrofit.Builder()
                    .baseUrl(ApiManager.BASE_FLICKR_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(okHttpClient.build())
                    .build();
        }
        return retrofit;
    }
}
