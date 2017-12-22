package com.example.brona.picasaconnect;

import com.google.gson.annotations.SerializedName;

public class PhotoResponse {

    @SerializedName(PicasaService.Api.TAG_FEED)
    private PhotoFeed photoFeed;

    public PhotoFeed getPhotoFeed() {
        return photoFeed;
    }
}

