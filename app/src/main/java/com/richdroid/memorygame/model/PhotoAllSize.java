package com.richdroid.memorygame.model;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

/**
 * Created by richa.khanna on 26/02/17.
 */

public class PhotoAllSize {
    @SerializedName("size")
    private ArrayList<Size> sizes;

    public ArrayList<Size> getSizes() {
        return sizes;
    }
}
