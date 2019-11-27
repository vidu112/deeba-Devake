package com.deeba.deebadriver;

import android.Manifest;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.provider.Settings;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.deeba.deebadriver.App.CHANNEL_ID;

public class ExampleService extends Service {
    private LocationListener listener;
    private LocationManager locationManager;
    FirebaseDatabase database2,date;
    DatabaseReference onlineDriver,dateRef;
    FirebaseDatabase onlinefleet;
    DatabaseReference onlinefleetref,messages;
    String taskids,companyId,plate1,plate2,plate3;
    double lati,longi;
    int totalIdleTime,alertIdle;
    String ending,delete;
    String actualDriverFname;
    boolean starttime;
    private SharedPreferences mpreferences;
    private SharedPreferences.Editor mEditor;
    @Override
    public void onCreate() {
        super.onCreate();
        Log.i("tseting","testingworkingsdfghj1");
        mpreferences= PreferenceManager.getDefaultSharedPreferences(this);
        mEditor=mpreferences.edit();
        starttime=true;
        database2= FirebaseDatabase.getInstance();
        date= FirebaseDatabase.getInstance();

        listener = new LocationListener() {
            @Override
            public void onLocationChanged( final Location location) {
                Log.i("tseting","testingworkingsdfghj2");
                companyId=mpreferences.getString(getString(R.string.companyId),"1");
                Log.i("companyId",companyId);
                taskids=mpreferences.getString(getString(R.string.taskids),"1");
                plate1=mpreferences.getString(getString(R.string.plate1),"1").toUpperCase();
                plate2=mpreferences.getString(getString(R.string.plate2),"1").toUpperCase();
                plate3=mpreferences.getString(getString(R.string.plate3),"1").toUpperCase();
                delete=mpreferences.getString(getString(R.string.deleteonlineuser),"");
                DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
                DateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                Date currentdate = new Date();
                actualDriverFname=mpreferences.getString(getString(R.string.currentdriverFname),"");
                Log.i("testingmpre","onlineFleetDriver/"+companyId+"/"+plate1+"-"+plate2+"-"+plate3);
                dateRef=date.getReference("onlineFleetDriver/"+companyId+"/"+plate1+"-"+plate2+"-"+plate3);
                onlineDriver=database2.getReference("onGoingTask/"+companyId+"/"+taskids);
                messages=database2.getReference("notifications/"+companyId);
                Log.i("testing", Double.toString(location.getLatitude()));
                ending=mpreferences.getString(getString(R.string.ending),"");
                dateRef.child("currentLag").setValue(Double.toString(location.getLatitude()));
                dateRef.child("currentLog").setValue(Double.toString(location.getLongitude()));
                dateRef.child("uploadDate").setValue( dateFormat.format(currentdate));
                dateRef.child("uploadtime").setValue( timeFormat.format(currentdate));
                if (ending.equals("false")) {
                    lati = location.getLatitude();
                    longi = location.getLongitude();
                    if (starttime) {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                double distanceCal = distance(location.getLatitude(), location.getLongitude(), lati, longi);
                                Log.i("distanceCal", Double.toString(distanceCal));
                                starttime = true;
                                if (distanceCal < (0.0024)) {
                                    Log.i("testingTotalidle", Double.toString(distance(location.getLatitude(), location.getLongitude(), lati, longi)));
                                    totalIdleTime = totalIdleTime + 5;
                                    alertIdle = alertIdle + 5;
                                    Log.i("testingTotalidle", Integer.toString(totalIdleTime));
                                    Log.i("testingTotalidle", Integer.toString(alertIdle));
                                    onlineDriver.child("totalIdle").setValue(Integer.toString(totalIdleTime));
                                    onlineDriver.child("alert").setValue(Integer.toString(alertIdle));
                                    if (alertIdle==900){
                                        String messageId=getID();
                                        messages.child(messageId).child("msg").setValue( actualDriverFname+"is idle for more than 15 minutes");
                                        messages.child(messageId).child("status").setValue( "0");

                                    }
                                } else {
                                    alertIdle = 0;
                                    Log.i("testingTotalidle", Integer.toString(alertIdle));
                                    onlineDriver.child("Alert").setValue(Integer.toString(alertIdle));
                                }
                                lati = location.getLatitude();
                                longi = location.getLongitude();
                            }
                        }, 5000);
                    }
                    mEditor = mpreferences.edit();
                    mEditor.putString(getString(R.string.totalIdle), Integer.toString(totalIdleTime));//
                    mEditor.commit();
                    starttime = false;
                }else {
                    onlineDriver.removeValue();
                }
                if (delete.equals("true")){
                    dateRef.removeValue();
                }
                dateRef.child("currentLag").setValue(Double.toString(location.getLatitude()));
                dateRef.child("currentLog").setValue(Double.toString(location.getLongitude()));
            }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {
            }
            @Override
            public void onProviderEnabled(String s) {
            }
            @Override
            public void onProviderDisabled(String s) {
                Intent i = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(i);
            }
        };
        locationManager = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
        //noinspection MissingPermission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, listener);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //String taskids = intent.getStringExtra("taskids");
        Intent notificationIntent = new Intent(this, MapActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this,
                0, notificationIntent, 0);
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("Example Service")
                .setContentText(taskids)
                .setContentIntent(pendingIntent)
                .build();
        startForeground(1, notification);
        //do heavy work on a background thread
        //stopSelf();
        return START_NOT_STICKY;
}
    @Override
    public void onDestroy() {
        super.onDestroy();
            //noinspection MissingPermission
            locationManager.removeUpdates(listener);
            onlineDriver.removeValue();
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private double distance(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1))
                * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1))
                * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;
        return (dist);
    }
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }
    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }

    public String getID(){
        Date dte=new Date();
        long milliSeconds = dte.getTime();
        String strLong = Long.toString(milliSeconds);
        return strLong;
    }
}