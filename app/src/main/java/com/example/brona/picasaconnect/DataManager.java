package com.example.brona.picasaconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import io.reactivex.Observable;
import retrofit2.Response;
/*
import com.pixby.gphotos.Constants;
import com.pixby.gphotos.model.Album;
import com.pixby.gphotos.model.AlbumResponse;
import com.pixby.gphotos.model.Photo;
import com.pixby.gphotos.model.PhotoResponse;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

*/

//@Singleton
public class DataManager implements DataManagerContract {

    private static final String TAG = DataManager.class.getSimpleName();
    private static final String USER_ACCOUNT_KEY = "gdata_account";
    private String mAccountName = "";
    private String mToken = "";
    /*
    private List<Album> mCachedAlbumList = new ArrayList<>();
    private List<Photo> mCachedPhotoList = new ArrayList<>();
    private Album mCurrentAlbum = new Album();
    */

    private PicasaService mPicasaService;

    public DataManager(PicasaService picasaService) {
        mPicasaService = picasaService;
    }

    @Override
    public void setAccountName(Context context, String accountName) {
        mAccountName = accountName;
        storeAccountName(context, mAccountName);
    }

    @Override
    public String getAccountName(Context context) {
        if (mAccountName.isEmpty()) {
            mAccountName = getStoredAccountName(context);
        }
        return mAccountName;
    }

    @Override
    public void clearAccount(Context context) {
        setAccountName(context,  "");
        setToken("");
    }

    private String getStoredAccountName(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(USER_ACCOUNT_KEY, "");
    }

    private void storeAccountName(Context context, String name) {
        Log.d(TAG, "storeAccountName in preferences");
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(USER_ACCOUNT_KEY, name);
        editor.apply();
    }

    @Override
    public void setToken(String token) {
        mToken = token;
    }

    @Override
    public String getToken() {
        return mToken;
    }

    public Observable<Response<Albums>> getAlbums() {
        return mPicasaService.getAlbums(mAccountName, mToken);
    }

    public Observable<retrofit2.Response<PhotoResponse>> getPhotos(String albumId) {
        return mPicasaService.getPhotos("107263820117403503338", albumId);
    }

    /*
    @Override
    public Observable<retrofit2.Response<PhotoResponse>> getPhotos(String albumId) {

        return mPicasaService.getPhotos(mAccountName, albumId);
    }

    @Override
    public Observable<List<Album>> getCachedAlbumListObservable() {
        return Observable.just(mCachedAlbumList);
    }

    @Override
    public void setCachedAlbumList(List<Album> albumList) {
        mCachedAlbumList.clear();
        mCachedAlbumList.addAll(albumList);
    }

    @Override
    public boolean haveAlbumCache() {
        return !mCachedAlbumList.isEmpty();
    }

    @Override
    public void setAlbum(Album album) {
        if (mCurrentAlbum.id != null
                && !mCurrentAlbum.getId().equals(album.getId())) {
            clearPhotosCache();
        }
        mCurrentAlbum = album;
    }

    @Override
    public Album getAlbum() {
        return mCurrentAlbum;
    }

    @Override
    public Observable<List<Photo>> getCachedPhotoListObservable() {
        return Observable.just(getCachedPhotoList());
    }

    @Override
    public List<Photo> getCachedPhotoList() {
        return mCachedPhotoList;
    }

    @Override
    public void setCachedPhotoList(List<Photo> photoList) {
        mCachedPhotoList.clear();
        mCachedPhotoList.addAll(photoList);
    }

    @Override
    public boolean havePhotoCache() {
        return !mCachedPhotoList.isEmpty();
    }

    @Override
    public Photo getPhoto(int position) {
        return mCachedPhotoList.isEmpty() ? null : mCachedPhotoList.get(position);
    }

    @Override
    public void clearNetworkCache() {
        mPicasaService.clearNetworkCache();
    }

    @Override
    public void clearCache(int flag) {
        switch (flag) {
            case Constants.ALBUM_CACHE:
                clearAlbumsCache();
                break;
            case Constants.PHOTOS_CACHE:
                clearPhotosCache();
                break;
            case Constants.ALL_CACHES:
                clearPhotosCache();
                clearAlbumsCache();
                break;
            default:
                break;
        }
    }

    private void clearPhotosCache() {
        if (mCachedPhotoList != null) {
            mCachedPhotoList.clear();
        }
    }

    private void clearAlbumsCache() {
        if (mCachedAlbumList != null) {
            mCachedAlbumList.clear();
        }
    }*/
}
