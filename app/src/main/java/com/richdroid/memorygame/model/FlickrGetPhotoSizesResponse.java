package com.richdroid.memorygame.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by richa.khanna on 26/02/17.
 */

public class FlickrGetPhotoSizesResponse {

    @SerializedName("sizes")
    private PhotoAllSize allSize;

    public PhotoAllSize getAllSize() {
        return allSize;
    }
}
