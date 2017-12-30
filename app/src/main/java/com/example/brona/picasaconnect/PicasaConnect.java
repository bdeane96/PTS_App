package com.example.brona.picasaconnect;

import android.util.Log;

import com.google.gdata.client.photos.PicasawebService;
import com.google.gdata.data.photos.AlbumEntry;
import com.google.gdata.data.photos.UserFeed;

import java.net.URL;

/**
 * Created by brona on 29/12/2017.
 * Attempting picasa connect using gdata import
 */


public class PicasaConnect {
    PicasawebService pws = new PicasawebService("PicasaConnect");

    public void retrieveAlbums(String token, String username){
        pws.setAuthSubToken(token, null);
        URL feedUrl = null;
        try {
            feedUrl = new URL("https://picasaweb.google.com/data/feed/api/user/" + username +"?kind=album");
            UserFeed myUserFeed = pws.getFeed(feedUrl, UserFeed.class);
            for (AlbumEntry myAlbum : myUserFeed.getAlbumEntries()) {
                Log.d("Gdata", (myAlbum.getTitle().getPlainText()));
            }
        }
        catch (Exception e){
            Log.d("Gdata", e.toString());
        }


    }
}
