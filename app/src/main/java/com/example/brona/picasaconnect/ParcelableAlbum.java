package com.example.brona.picasaconnect;


import android.os.Parcel;
import android.os.Parcelable;

public class ParcelableAlbum implements Parcelable {

    private String mId = "";
    private String mThumbUrl = "";
    private String mTitle = "";

    public ParcelableAlbum(Album album) {
        mId = album.getId();
        mTitle = album.getTitle();
        mThumbUrl = album.getThumbUrl();
    }

    public String getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeString(mThumbUrl);
        dest.writeString(mTitle);
    }

    public static final Creator<ParcelableAlbum> CREATOR = new Creator<ParcelableAlbum>() {
        public ParcelableAlbum createFromParcel(Parcel in) {
            return new ParcelableAlbum(in);
        }

        @Override
        public ParcelableAlbum[] newArray(int size) {
            return new ParcelableAlbum[size];
        }
    };

    public ParcelableAlbum(Parcel in) {
        mId = in.readString();
        mThumbUrl = in.readString();
        mTitle = in.readString();
    }

    @Override
    public String toString() {
        return String.format("id: %s, title: %s, thumbUrl: %s",
                mId, mTitle, mThumbUrl);
    }
}


