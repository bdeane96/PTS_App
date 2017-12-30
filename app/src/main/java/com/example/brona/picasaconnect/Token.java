package com.example.brona.picasaconnect;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by brona on 29/12/2017.
 */

public class Token extends Object implements Parcelable {
    private String token;
    private String accountName;

    public Token(String token, String accountName){
        this.token = token;
        this.accountName = accountName;
    }

    public String getAccountName() {return accountName;}

    public void setAccountName(String accountName){ this.accountName = accountName; }

    public String getToken(){return token;}

    public void setToken(String token){this.token = token;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }
}
