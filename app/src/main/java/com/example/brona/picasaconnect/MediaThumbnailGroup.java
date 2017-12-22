package com.example.brona.picasaconnect;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class MediaThumbnailGroup {

    @SerializedName(PicasaService.Api.TAG_MEDIA_THUMBNAIL)
    @Expose
    public List<Thumbnail> thumbnails = new ArrayList<>();
    public List<Thumbnail> getThumbnails() {
        return thumbnails;
    }
}
