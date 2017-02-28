package com.richdroid.memorygame.rest;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

class ResponseInterceptor implements Interceptor {
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        Response response = chain.proceed(request);

        boolean unAuthorized = (response.code() == 401);
        if (unAuthorized) {
            throw new IOException("Unexpected response : " + response.body().string());
        }

        return response;
    }
}

