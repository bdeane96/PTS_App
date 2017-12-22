package com.example.brona.picasaconnect;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by brona on 18/12/2017.
 */

public class Album {





    public Album() {
    }

    @SerializedName(PicasaService.Api.TAG_GPHOTO_ID)
    @Expose
    public String id;

    @SerializedName(PicasaService.Api.TAG_TITLE)
    @Expose
    public String title;

    @SerializedName(PicasaService.Api.TAG_MEDIA_GROUP)
    private MediaThumbnailGroup mediaThumbnailGroup;

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

    public String getThumbUrl() {
        return mediaThumbnailGroup.getThumbnails().get(0).getUrl();
    }
}


