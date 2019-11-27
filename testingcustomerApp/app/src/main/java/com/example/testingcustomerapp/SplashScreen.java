package com.example.testingcustomerapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

public class SplashScreen extends AppCompatActivity {
//first page
    private SharedPreferences mpreferences;
    private SharedPreferences.Editor mEditor;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        mpreferences= PreferenceManager.getDefaultSharedPreferences(this);
        final String phoneNumber=mpreferences.getString(getString(R.string.phonenumber),"false");

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                if (phoneNumber!="false"){
                    Intent MapActivityListView = new Intent(getApplicationContext(), MapActivityListView.class);
                    startActivity(MapActivityListView);
                }else {
                    Intent UserLogin = new Intent(getApplicationContext(), UserLogin.class);
                    startActivity(UserLogin);
                }

            }
        }, 500);
    }
}
