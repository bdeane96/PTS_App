package com.example.brona.picasaconnect;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    static Context context;
    AuthenticationManager authenticationManager;
    DataManager dataManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();
        //permissions
        Permissions permissions = new Permissions();
        String[] permissionsRequired = permissions.getPermissions();
        requestPermissions(permissionsRequired);

        Log.d("Logging", "Initial log");
        //authenticationmanager
        authenticationManager = new AuthenticationManager(this);
        //login button
        Button loginButton = (Button)findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Toast toast = Toast.makeText(context, "works", Toast.LENGTH_SHORT);
                toast.show();
                //retrieve account names
                startService(new Intent(context,TheService.class));//use to start the services
                authenticationManager.showAccountPicker(MainActivity.this);

            }
        });




        //select account and get token
        //token acquired, access account

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /*
            gets auth token for selected account
         */
        authenticationManager.onAccountPickerResult(requestCode, resultCode, data);
    }


    private void requestPermissions(String[] permissions){
        List<String> permissionsToRequest = new ArrayList<>();
        for (String permission:permissions) {
            int result = ActivityCompat.checkSelfPermission(this, permission);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(permission);
            }
        }

        String[] pers = new String[permissionsToRequest.size()];
        for(int i=0; i<permissionsToRequest.size(); i++){
            pers[i]=permissionsToRequest.get(i);
        }

        //needs min sdk of 23
        if(pers.length > 0) {
            requestPermissions(pers, PackageManager.PERMISSION_GRANTED);
        }
    }
}
