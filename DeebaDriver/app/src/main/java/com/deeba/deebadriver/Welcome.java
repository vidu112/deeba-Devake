package com.deeba.deebadriver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class Welcome extends AppCompatActivity {

    String driverId,taskPrevious,username,plate1,plate2,plate3,formattedDate;
    TextView usernametextfeild,timeset,vehiclaPlate;
    private SharedPreferences mpreferences;
    private SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        mpreferences= PreferenceManager.getDefaultSharedPreferences(this);
        mEditor=mpreferences.edit();
        Calendar cal = Calendar.getInstance();
        Date date=cal.getTime();
        DateFormat dateFormat = new SimpleDateFormat("HH:mm");
        formattedDate=dateFormat.format(date);
        driverId = getIntent().getStringExtra("driverId");
        taskPrevious = getIntent().getStringExtra("taskPrevious");
        username = getIntent().getStringExtra("username");
        usernametextfeild=findViewById(R.id.name);
        vehiclaPlate=findViewById(R.id.vehical_plate);
        plate1=mpreferences.getString(getString(R.string.plate1),"");
        Log.i("testinglogin","working4");
        plate2=mpreferences.getString(getString(R.string.plate2),"");
        plate3=mpreferences.getString(getString(R.string.plate3),"");
        vehiclaPlate.setText(plate1+" "+plate2+" "+plate3);
        timeset=findViewById(R.id.timeset);
        usernametextfeild.setText(username);
        boolean firstTime = checktimings( formattedDate, "12:00");


    }
    public void taskIntent(View view){
        Intent afterLogin = new Intent(getApplicationContext(), Tasks.class);
        afterLogin.putExtra("driverId", driverId);
        afterLogin.putExtra("taskPrevious", "-1");
        afterLogin.putExtra("username",username);
        startActivity(afterLogin);
    }
//    private boolean isTimeBetween(String timeToTest, String startTime, String endTime) {
//
//        LocalTime timeToTestDt = LocalTime.parse(timeToTest, DateTimeFormatter.ISO_LOCAL_TIME);
//        LocalTime startTimeDt = LocalTime.parse(startTime, DateTimeFormatter.ISO_LOCAL_TIME);
//        LocalTime endTimeDt = LocalTime.parse(endTime, DateTimeFormatter.ISO_LOCAL_TIME);
//
//        if(startTime.equals(endTime)) {
//            return false;
//        }
//        else if(startTimeDt.isBefore(endTimeDt)) {  // Period does not cross the day boundary
//            return (timeToTest.equals(startTime) || timeToTestDt.isAfter(startTimeDt))
//                    && timeToTestDt.isBefore(endTimeDt);
//        } else {  // Time period spans two days, e.g. 23:00 to 2:00
//            return (!((timeToTestDt.isAfter(endTimeDt) || timeToTest.equals(endTime))
//                    && timeToTestDt.isBefore(startTimeDt)));
//        }
//    }
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

}
