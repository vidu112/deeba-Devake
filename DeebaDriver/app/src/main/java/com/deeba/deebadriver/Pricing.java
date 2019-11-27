package com.deeba.deebadriver;

import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class Pricing extends AppCompatActivity {
    TextView TotalDistanceText,TotalTimeText,TotalPriceText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pricing);
        TotalDistanceText=(TextView)findViewById(R.id.total_distance);
        TotalTimeText=(TextView)findViewById(R.id.total_waiting);
        TotalPriceText=(TextView)findViewById(R.id.total_price);

        String Total_Distance=getIntent().getStringExtra("TotalDistance")+"Km";
        String Total_Time=getIntent().getStringExtra("TotalTime");
        Log.d("Total_Time",Total_Time);
        String Total_Price=getIntent().getStringExtra("TotalPrice")+" LKR";
        TotalDistanceText.setText(Total_Distance);
        TotalTimeText.setText(timeConversion((int) Double.parseDouble(Total_Time)));
        TotalPriceText.setText(Total_Price);
    }
    private static String timeConversion(int totalSeconds) {

        final int MINUTES_IN_AN_HOUR = 60;
        final int SECONDS_IN_A_MINUTE = 60;

        int seconds = totalSeconds % SECONDS_IN_A_MINUTE;
        int totalMinutes = totalSeconds / SECONDS_IN_A_MINUTE;
        int minutes = totalMinutes % MINUTES_IN_AN_HOUR;
        int hours = totalMinutes / MINUTES_IN_AN_HOUR;

        return hours + "." + minutes + "." + seconds ;
    }
    public void Confirm_Payment(View view){
        Intent FreMapActivity = new Intent(getApplicationContext(), FreMapActivity.class);
        startActivity(FreMapActivity);
    }
}
