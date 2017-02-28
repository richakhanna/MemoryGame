package com.richdroid.memorygame.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by richa.khanna on 26/02/17.
 */

public class FlickrGetPhotosResponse {

    @SerializedName("photoset")
    private PhotoSet photoSet;

    public PhotoSet getPhotoSet() {
        return photoSet;
    }
}
