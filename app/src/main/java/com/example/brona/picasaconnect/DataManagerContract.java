package com.example.brona.picasaconnect;

import android.content.Context;
/*
import com.pixby.gphotos.model.Album;
import com.pixby.gphotos.model.AlbumResponse;
import com.pixby.gphotos.model.Photo;
import com.pixby.gphotos.model.PhotoResponse;

import java.util.List;

import io.reactivex.Observable;
import retrofit2.Response;*/

public interface DataManagerContract {

    void setToken(String token);
    String getToken();

    void setAccountName(Context context, String accountName);
    String getAccountName(Context context);
    void clearAccount(Context context);
/*
    Album getAlbum();
    void setAlbum(Album album);
    void clearCache(int flag);

    Photo getPhoto(int index);

    Observable<Response<AlbumResponse>> getAlbums();
    Observable<List<Album>> getCachedAlbumListObservable();
    void setCachedAlbumList(List<Album> albumList);
    boolean haveAlbumCache();

    Observable<Response<PhotoResponse>> getPhotos(String albumId);
    Observable<List<Photo>> getCachedPhotoListObservable();
    void setCachedPhotoList(List<Photo> photoList);
    List<Photo> getCachedPhotoList();
    boolean havePhotoCache();


    void clearNetworkCache();*/
}
