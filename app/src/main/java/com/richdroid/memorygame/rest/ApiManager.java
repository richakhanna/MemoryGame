package com.richdroid.memorygame.rest;

import android.content.Context;
import android.util.Log;

import com.richdroid.memorygame.model.FlickrGetPhotoSizesResponse;
import com.richdroid.memorygame.model.FlickrGetPhotosResponse;

import java.io.IOException;
import java.lang.ref.WeakReference;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by richa.khanna on 5/11/16.
 */
public class ApiManager {

    private static final String TAG = ApiManager.class.getSimpleName();
    //Base Url for Flickr
    public static final String BASE_FLICKR_URL = "https://www.flickr.com/";

    public static final String GET_PHOTOS_METHOD = "flickr.photosets.getPhotos";
    public static final String GET_PHOTO_SIZES_METHOD = "flickr.photos.getSizes";
    public static final String FORMAT_TYPE = "json";
    public static final String NO_JSON_CALLBACK = "1";
    public static final String NO_OF_PHOTOS_PER_PAGE = "9";
    //Key to access Flickr
    public static final String API_KEY = "a180dfbcc3cffb7c343fac401457e547";
    public static final String PHOTOSET_ID = "72157623480041879";

    private static ApiManager mInstance;
    private Context mContext;
    private ApiInterface mApiService;

    private ApiManager(Context context) {
        mContext = context;
    }

    public static synchronized ApiManager getInstance(Context context) {
        if (mInstance == null) {
            Log.v(TAG, "Creating api manager instance");
            mInstance = new ApiManager(context.getApplicationContext());
        }
        return mInstance;
    }

    public void init() {
        mApiService = getApiService();
    }

    private ApiInterface getApiService() {
        if (mApiService == null) {
            mApiService = ApiClient.getClient().create(ApiInterface.class);
        }
        return mApiService;
    }

    /**
     * Get the list of photos from Flickr.
     *
     * @param wRequester
     */
    public void getPhotos(final WeakReference<ApiRequester> wRequester) {
        Log.v(TAG, "Api call : get Photos");

        final Callback<FlickrGetPhotosResponse> objectCallback = new Callback<FlickrGetPhotosResponse>() {
            @Override
            public void onResponse(Call<FlickrGetPhotosResponse> call, Response<FlickrGetPhotosResponse> response) {
                Log.v(TAG, "onResponse : get Photos returned a response");

                ApiRequester req = null;
                if (wRequester != null) {
                    req = wRequester.get();
                }

                if (req != null) {
                    // response.isSuccessful() is true if the response code is 2xx
                    if (response != null && response.isSuccessful()) {
                        req.onSuccess(response);
                    } else {
                        int statusCode = response.code();
                        // handle response errors yourself
                        ResponseBody errorBody = response.errorBody();
                        try {
                            Log.e(TAG, "onResponse status code : " + statusCode + " , error message : " + errorBody.string());
                        } catch (IOException e) {
                            Log.e(TAG, "onResponse exception message : " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<FlickrGetPhotosResponse> call, Throwable t) {
                // handle execution failures like no internet connectivity
                // timeout exception here IOException or SocketTOException
                Log.v(TAG, "onFailure : get Photos api failed");

                ApiRequester req = null;
                if (wRequester != null) {
                    req = wRequester.get();
                }
                if (req != null) {
                    req.onFailure(t);
                }
            }
        };

        Call<FlickrGetPhotosResponse> call;
        Log.v(TAG, "Calling : get Photos api");
        call = mApiService.getPhotoSet(GET_PHOTOS_METHOD, FORMAT_TYPE, API_KEY, PHOTOSET_ID, NO_JSON_CALLBACK, NO_OF_PHOTOS_PER_PAGE);
        call.enqueue(objectCallback);

    }

    /**
     * Get the list of photos from Flickr.
     *
     * @param wRequester
     */
    public void getPhotoSizesDescription(final WeakReference<ApiRequester> wRequester, String photoId) {
        Log.v(TAG, "Api call : get Photo Sizes Description");

        final Callback<FlickrGetPhotoSizesResponse> objectCallback = new Callback<FlickrGetPhotoSizesResponse>() {
            @Override
            public void onResponse(Call<FlickrGetPhotoSizesResponse> call, Response<FlickrGetPhotoSizesResponse> response) {
                Log.v(TAG, "onResponse : get Photo Sizes Description returned a response");

                ApiRequester req = null;
                if (wRequester != null) {
                    req = wRequester.get();
                }

                if (req != null) {
                    // response.isSuccessful() is true if the response code is 2xx
                    if (response != null && response.isSuccessful()) {
                        req.onSuccess(response);
                    } else {
                        int statusCode = response.code();
                        // handle response errors yourself
                        ResponseBody errorBody = response.errorBody();
                        try {
                            Log.e(TAG, "onResponse status code : " + statusCode + " , error message : " + errorBody.string());
                        } catch (IOException e) {
                            Log.e(TAG, "onResponse exception message : " + e.getMessage());
                        }
                    }
                }
            }

            @Override
            public void onFailure(Call<FlickrGetPhotoSizesResponse> call, Throwable t) {
                // handle execution failures like no internet connectivity
                // timeout exception here IOException or SocketTOException
                Log.v(TAG, "onFailure : get Photos api failed");

                ApiRequester req = null;
                if (wRequester != null) {
                    req = wRequester.get();
                }
                if (req != null) {
                    req.onFailure(t);
                }
            }
        };

        Call<FlickrGetPhotoSizesResponse> call;
        Log.v(TAG, "Calling : get Photo Sizes Description api");
        call = mApiService.getPhotoSizes(GET_PHOTO_SIZES_METHOD, FORMAT_TYPE, API_KEY, photoId, NO_JSON_CALLBACK);
        call.enqueue(objectCallback);

    }
}
