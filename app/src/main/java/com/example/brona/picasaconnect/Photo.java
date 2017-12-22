package com.example.brona.picasaconnect;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Photo {
    public final static int HD_720_WIDTH = 1280;
    public final static int HD_720_HEIGHT = 720;

    @SerializedName(PicasaService.Api.TAG_GPHOTO_ID)
    @Expose
    private String id;

    @SerializedName(PicasaService.Api.TAG_TITLE)
    @Expose
    private String title;

    @SerializedName(PicasaService.Api.TAG_MEDIA_GROUP)
    private MediaContentGroup mediaContentGroup;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageUrl() {
        return mediaContentGroup.getImages().get(0).getUrl();
    }

    public String getThumbUrl() {
        return mediaContentGroup.getThumbnails().get(0).getUrl();
    }
}


