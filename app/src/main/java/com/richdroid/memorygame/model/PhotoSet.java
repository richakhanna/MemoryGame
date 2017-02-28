package com.richdroid.memorygame.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by richa.khanna on 26/02/17.
 */

public class PhotoSet {
    @SerializedName("photo")
    private ArrayList<Photo> photos;

    public ArrayList<Photo> getPhotos() {
        return photos;
    }
}
