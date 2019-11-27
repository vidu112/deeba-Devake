package com.deeba.deebadriver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class FreWelcome extends AppCompatActivity {

    String driverId,taskPrevious,first,last,formattedDate;
    TextView firstName,timeset,lastName;
    private SharedPreferences mpreferences;
    private SharedPreferences.Editor mEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fre_welcome);
        mpreferences= PreferenceManager.getDefaultSharedPreferences(this);
        mEditor=mpreferences.edit();
        Calendar cal = Calendar.getInstance();
        Date date=cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        formattedDate=dateFormat.format(date);
        driverId = getIntent().getStringExtra("driverId");
        taskPrevious = getIntent().getStringExtra("taskPrevious");
        first = getIntent().getStringExtra("Fname");
        last = getIntent().getStringExtra("Lname");
        firstName=findViewById(R.id.first);
        lastName=findViewById(R.id.last);
        timeset=findViewById(R.id.timeset);
        firstName.setText(first);
        lastName.setText(last);
        boolean firstTime = checktimings( formattedDate, "12:00");
    }
    public void GoOnline(View view){
        Intent afterLogin = new Intent(getApplicationContext(), FreMapActivity.class);
        afterLogin.putExtra("driverId", driverId);
        afterLogin.putExtra("taskPrevious", "-1");
        afterLogin.putExtra("first",first);
        startActivity(afterLogin);
    }
    private boolean checktimings(String time, String endtime) {
        String pattern = "HH:mm";
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            Date date1 = sdf.parse(time);
            Date date2 = sdf.parse(endtime);

            if(date1.before(date2)) {
                Log.i("testingWelcome","morning");
                timeset.setText("Good Morning");
                return true;
            } else {
                Log.i("testingWelcome","afternoon");
                timeset.setText("Good Evening");
                return false;
            }
        } catch (ParseException e){
            e.printStackTrace();
        }
        return false;
    }
    public void Navigation(View view){
        Intent NavigationIntent = new Intent(getApplicationContext(), FreNav.class);
        NavigationIntent.putExtra("PreviousActivity", "FreMapActivity");
        startActivity(NavigationIntent);
    }
}

