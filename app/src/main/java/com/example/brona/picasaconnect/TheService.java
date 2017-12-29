package com.example.brona.picasaconnect;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.drew.imaging.ImageMetadataReader;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by brona on 11/10/2017.
 */

public class TheService extends Service{
    public static final long INTERVAL=10000;//variable to execute services every 10 second
    public Handler mHandler=new Handler(); // run on another Thread to avoid crash
    private Timer mTimer=null; // timer handling
    SharedPreferences sharedPref;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("unsupported Operation");
    }
    @Override
    public void onCreate() {
        /* cancel if service is  already existed */
        sharedPref = this.getSharedPreferences("uploadedfilepath", Context.MODE_PRIVATE);
        if(mTimer!=null)
            mTimer.cancel();
        else
            mTimer=new Timer(); // recreate new timer
        mTimer.scheduleAtFixedRate(new TimeDisplayTimerTask(),0,INTERVAL);// schedule task
    }
    @Override
    public void onDestroy() {
        Toast.makeText(this, "In Destroy", Toast.LENGTH_SHORT).show();//display toast when method called
        mTimer.cancel();//cancel the timer
    }
    //inner class of TimeDisplayTimerTask
    private class TimeDisplayTimerTask extends TimerTask {
        @Override
        public void run() {
            // run on another thread
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    // display toast at every 10 second
                    getImg();
                }
            });
        }
    }

    public void getImg(){

        Log.i("Process", "getting images from gallery");
        String[] projection = new String[]{
                MediaStore.Images.ImageColumns._ID,
                MediaStore.Images.ImageColumns.DATA,
                MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME,
                MediaStore.Images.ImageColumns.DATE_TAKEN,
                MediaStore.Images.ImageColumns.MIME_TYPE
        };
        final Cursor cursor = getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, projection, null,
                        null, MediaStore.Images.ImageColumns.DATE_TAKEN + " DESC");
        if (cursor.moveToFirst()) {
            Log.i("Process", "finding filepaths");
            String lastuploaded = getFilePathMostRecentUpload();
            if(!lastuploaded.isEmpty()) {
                Log.i("Process", "finding new filepaths");
                File imageFile = new File(lastuploaded);
                //check that file hasnt been deleted...if has been deleted need to connect to web album and get second last uploaded
                if(imageFile.exists()) {
                    Log.i("Last uploaded", lastuploaded);
                    while (!cursor.getString(1).equals(lastuploaded)) {
                        Log.i("File path", cursor.getString(1));
                        File img = new File(cursor.getString(1));
                        try{
                            Metadata metadata = ImageMetadataReader.readMetadata(img);
                            for (Directory directory : metadata.getDirectories()) {
                                for (Tag tag : directory.getTags()) {
                                    System.out.format("[%s] - %s = %s",
                                            directory.getName(), tag.getTagName(), tag.getDescription());
                                    Log.i("Metadata",directory.getName() +"," + tag.getTagName() +","+tag.getDescription()) ;
                                }
                                if (directory.hasErrors()) {
                                    for (String error : directory.getErrors()) {
                                        System.err.format("ERROR: %s", error);
                                        Log.i("Metadata", "error");
                                    }
                                }
                            }
                        } catch (Exception e){
                            e.printStackTrace();
                        }
                        cursor.moveToNext();
                    }
                }
            }


            cursor.moveToFirst();

            String imageLocation = cursor.getString(1);
            File imageFile = new File(imageLocation);
            if (imageFile.exists()) {
                Log.i("Process", "Last image uploaded " + imageLocation);
                setFilePathMostRecentUpload(imageLocation);
            }

            cursor.close();
        }
    }

    private String getFilePathMostRecentUpload(){
        return sharedPref.getString(getString(R.string.filepathuploaded), "");
    }

    private void setFilePathMostRecentUpload(String filepath){
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.filepathuploaded), filepath);
        editor.apply();
    }
}