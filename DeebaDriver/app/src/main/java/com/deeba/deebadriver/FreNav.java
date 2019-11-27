package com.deeba.deebadriver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FreNav extends AppCompatActivity {
    FirebaseDatabase database2;
    DatabaseReference onlineFreeLancer,listening;
    private SharedPreferences mpreferences;
    private SharedPreferences.Editor mEditor;
    String vehicalType,vehicalId,driverId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fre_nav);
        mpreferences= PreferenceManager.getDefaultSharedPreferences(this);
        vehicalType=mpreferences.getString(getString(R.string.vehicalType),"1");
        vehicalId=mpreferences.getString(getString(R.string.vehicalId),"1");
        driverId=mpreferences.getString(getString(R.string.currentdriverId),"1");
        database2= FirebaseDatabase.getInstance();
        onlineFreeLancer=database2.getReference("onlineFreeLancer/"+vehicalType+"/"+vehicalId);
    }
    public  void Profile(View view){
        Intent NavigationIntent = new Intent(getApplicationContext(), Profile.class);
        startActivity(NavigationIntent);
    }
    public  void My_trips(View view){
        Intent NavigationIntent = new Intent(getApplicationContext(), My_Trips.class);
        startActivity(NavigationIntent);
    }
    public  void Suport(View view){
        Intent NavigationIntent = new Intent(getApplicationContext(), support.class);
        startActivity(NavigationIntent);
    }
    public  void Legal(View view){
        Intent NavigationIntent = new Intent(getApplicationContext(), Legal.class);
        startActivity(NavigationIntent);
    }
    public void SignOut(View view){
        mEditor=mpreferences.edit();
        mEditor.putString(getString(R.string.stop_location_updates),"true");
        mEditor.apply();
        mEditor.commit();
        onlineFreeLancer.removeValue();
        Intent NavigationIntent = new Intent(getApplicationContext(), LoginDriver.class);
        startActivity(NavigationIntent);
    }

    public void Close(View view){
        super.onBackPressed();
    }
}
