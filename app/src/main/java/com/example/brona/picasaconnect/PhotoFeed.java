package com.example.brona.picasaconnect;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class PhotoFeed {

    @SerializedName(PicasaService.Api.TAG_ENTRY)
    @Expose
    private List<Photo> photoList = new ArrayList<>();

    public List<Photo> getPhotoList() {
        return photoList;
    }
}