package com.richdroid.memorygame.rest;

/**
 * Created by richa.khanna on 5/11/16.
 */


import com.richdroid.memorygame.model.FlickrGetPhotoSizesResponse;
import com.richdroid.memorygame.model.FlickrGetPhotosResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;


public interface ApiInterface {
    @GET("services/rest")
    Call<FlickrGetPhotosResponse> getPhotoSet(@Query("method") String method, @Query("format") String format, @Query("api_key") String apiKey, @Query("photoset_id") String photosetId, @Query("nojsoncallback") String noJsonCallback, @Query("per_page") String perPage);
    // https://www.flickr.com/services/rest?method=flickr.photosets.getPhotos&format=json&api_key=a180dfbcc3cffb7c343fac401457e547&photoset_id=72157623480041879&nojsoncallback=1&per_page=9

    @GET("services/rest")
    Call<FlickrGetPhotoSizesResponse> getPhotoSizes(@Query("method") String method, @Query("format") String format, @Query("api_key") String apiKey, @Query("photo_id") String photoId, @Query("nojsoncallback") String noJsonCallback);
    // https://www.flickr.com/services/rest/?method=flickr.photos.getSizes&format=json&api_key=a180dfbcc3cffb7c343fac401457e547&photo_id=4426188531&nojsoncallback=1
}
