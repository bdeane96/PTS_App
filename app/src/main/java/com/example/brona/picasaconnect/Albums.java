package com.example.brona.picasaconnect;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by brona on 18/12/2017.
 */

public class Albums {

    @SerializedName(PicasaService.Api.TAG_ENTRY)
    @Expose
    private List<Album> albumList = new ArrayList<>();
    public List<Album> getAlbumList() {
        return albumList;
    }

}


