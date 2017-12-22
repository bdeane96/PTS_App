package com.example.brona.picasaconnect;

import android.Manifest;

/**
 * Created by brona on 03/11/2017.
 */

public class Permissions {

    private static String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.GET_ACCOUNTS};

    public Permissions(){}

    public String[] getPermissions(){
        return permissions;
    }

}
