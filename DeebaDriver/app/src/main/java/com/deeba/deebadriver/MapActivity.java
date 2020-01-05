package com.deeba.deebadriver;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.deeba.deebadriver.directionhelpers.FetchURL;
import com.deeba.deebadriver.directionhelpers.TaskLoadedCallback;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    private GoogleMap mMap;
    private MarkerOptions place1, place2,currentLOc;
    TextView start,end,timetext,distancetext;
    LocationListener locationListener;
    LocationManager locationManager;
    ImageView clockimg,distanceimg;
    Button startbutton,nextbutton,endbutton;
    Double longi,lati;
    ImageButton map_view;
    private Polyline currentPolyline;
    LatLng sri_lanka;
    int curETA,curDistance,totalDistance,totalEta,totalIdleTime,alertIdle;
    boolean starttime;
    String CurrentDriverId,taskPrevious,taskOriginName,taskOriginLat,taskOriginLon,taskDesName,taskDesLat,taskDesLon,stopPrevious,stopLon,stopLat,startLat,startLon,startName,stopName,taskids,started,finalstop,stopNext;
    String PredriverId,PredriverFname,PredriverLname,companyId,vehicalId,firsttime,ending,nextStarted,taskNextid,vehicalLicenPlate2,vehicalLicenPlate3,vehicalLicenPlate1,taskEndTime,taskStartTime,enableStart,Starttime,setEndVisible;
    String preStopLat,preStopLon,DifferentStop,actualDriverFname,actualDriverLname,messageId;
    CameraUpdate cu;
    FirebaseDatabase database;
    FirebaseDatabase database2;
    DatabaseReference onlineDriver,onlineFletDriver,messages;
    DatabaseReference delete;
    LatLngBounds.Builder builder;
    Vibrator vibe;
    private SharedPreferences mpreferences;
    private SharedPreferences.Editor mEditor;
    private LatLngBounds currentlocationzoombounds;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        DifferentStop=getIntent().getStringExtra("DifferentStop");
        Log.i("totaldistance",DifferentStop);
        vibe= (Vibrator) MapActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        database= FirebaseDatabase.getInstance();
        mpreferences= PreferenceManager.getDefaultSharedPreferences(this);
        mEditor=mpreferences.edit();
        database2=FirebaseDatabase.getInstance();
        timetext=findViewById(R.id.timetext);
        distancetext=findViewById(R.id.distancetext);
        clockimg=findViewById(R.id.clockimg);
        distanceimg=findViewById(R.id.distanceimg);
        totalDistance= Integer.parseInt(getIntent().getStringExtra("totaldistance"));
        totalEta=Integer.parseInt(getIntent().getStringExtra("totalETA"));
        Log.i("totaldistance",Integer.toString(totalDistance));
        if (totalDistance>=1000){
            distancetext.setText(Integer.toString(totalDistance/1000)+"Km");
        }else {
            distancetext.setText(Integer.toString(totalDistance) + "m");
        }
        timetext.setText(Integer.toString(totalEta/60)+"min");
        startbutton=findViewById(R.id.start);
        endbutton=findViewById(R.id.end);
        nextbutton=findViewById(R.id.next);
        map_view=findViewById(R.id.map_view);
        starttime=false;
        totalIdleTime=0; alertIdle=0;
        // startbutton.setVisibility(View.INVISIBLE);
        //map_view.setVisibility(View.INVISIBLE);
        start=(findViewById(R.id.starttext));
        end=(findViewById(R.id.endtext));
        finalstop="false";
        setEndVisible="false";
        ending="false";
        mEditor = mpreferences.edit();
        mEditor.putString(getString(R.string.ending),ending);
        mEditor.apply();
        mEditor.commit();
        vehicalId=mpreferences.getString(getString(R.string.vehicalId),"");
        CurrentDriverId=mpreferences.getString(getString(R.string.currentdriverId),"");
        actualDriverFname=mpreferences.getString(getString(R.string.currentdriverFname),"");
        actualDriverLname=mpreferences.getString(getString(R.string.currentdriverLname),"");
        PredriverId = getIntent().getStringExtra("PredriverId");
        PredriverFname=getIntent().getStringExtra("PredriverFname");
        PredriverLname=getIntent().getStringExtra("PredriverLname");
        taskOriginName = getIntent().getStringExtra("taskOriginName");
        taskOriginLat = getIntent().getStringExtra("taskOriginLat");
        taskOriginLon = getIntent().getStringExtra("taskOriginLon");
        taskDesName = getIntent().getStringExtra("taskDesName");
        taskDesLat = getIntent().getStringExtra("taskDesLat");
        taskDesLon = getIntent().getStringExtra("taskDesLon");
        taskPrevious=getIntent().getStringExtra("taskPrevious");
        taskids=getIntent().getStringExtra("taskids");
        companyId=getIntent().getStringExtra("companyId");
        mEditor = mpreferences.edit();
        mEditor.putString(getString(R.string.companyId),companyId);
        mEditor.putString(getString(R.string.taskids),taskids);
        mEditor.commit();
        messages=database.getReference("notifications/"+companyId);
        messageId=getID();
        messages.child(messageId).child("msg").setValue( actualDriverFname+" has accepted the task");
        messages.child(messageId).child("status").setValue( "0");
        Intent serviceIntent = new Intent(this, ExampleService.class);
        serviceIntent.putExtra("taskids", taskids);
        serviceIntent.putExtra("companyId", companyId);
        ContextCompat.startForegroundService(this, serviceIntent);
        taskNextid=getIntent().getStringExtra("taskNext");
        delete=database2.getReference("onGoingTask");
        started="true";
        nextStarted="true";
        vehicalId=getIntent().getStringExtra("vehicalId");
        vehicalLicenPlate1=getIntent().getStringExtra("vehicalLicenPlate1");
        vehicalLicenPlate2=getIntent().getStringExtra("vehicalLicenPlate2");
        vehicalLicenPlate3=getIntent().getStringExtra("vehicalLicenPlate3");
        taskStartTime=getIntent().getStringExtra("taskStartTime");
        taskEndTime=getIntent().getStringExtra("taskEndTime");
        DifferentStop=getIntent().getStringExtra("DifferentStop");
        enableStart="false";
        if (DifferentStop.equals("false")){
            started = "false";
            startLat=taskOriginLat;
            startLon=taskOriginLon;
            startName=taskOriginName;
            Log.i("testingMapIntend",companyId);
            Log.i("taskids",taskids);
//        Log.i("taskOriginName",taskOriginName);
            start.setText(taskOriginName);
            end.setText(taskDesName);
            place1 = new MarkerOptions().position(new LatLng(Double.parseDouble(taskOriginLat), Double.parseDouble(taskOriginLon))).title("Location 1");
            Log.i("marker_testing","working_place1");
            place2 = new MarkerOptions().position(new LatLng(Double.parseDouble(taskDesLat), Double.parseDouble(taskDesLon))).title("Location 2");
            Log.i("marker_testing","working_place2");
            sri_lanka = new LatLng(Double.parseDouble(taskOriginLat), Double.parseDouble(taskOriginLon));
        }else if (DifferentStop.equals("true")){
            stopLon = getIntent().getStringExtra("stopLon");
            stopLat = getIntent().getStringExtra("stopLat");
            starttime=true;
            nextStarted = "false";
            startName = stopName;
            startLat = stopLat;
            startLon = stopLon;
        }else{
            startbutton.setVisibility(View.INVISIBLE);
            setEndVisible="true";
            startLat=taskDesLat;
            startLon=taskDesLon;
            startName=taskDesName;
            Log.i("testingMapIntend",companyId);
            Log.i("taskids",taskids);
//        Log.i("taskOriginName",taskOriginName);
            start.setText(taskOriginName);
            end.setText(taskDesName);
            place1 = new MarkerOptions().position(new LatLng(Double.parseDouble(taskOriginLat), Double.parseDouble(taskOriginLon))).title(taskOriginName);
            Log.i("marker_testing","working_place1");
            place2 = new MarkerOptions().position(new LatLng(Double.parseDouble(taskDesLat), Double.parseDouble(taskDesLon))).title(taskDesName);
            sri_lanka = new LatLng(Double.parseDouble(taskOriginLat), Double.parseDouble(taskOriginLon));
        }
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapNearBy);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                Log.i("testinglocation",location.toString());
                //currentLOc = new MarkerOptions().position(new LatLng(location.getLatitude(),location.getLongitude())).title(taskDesName);
                if (started.equals("false")){
                    if ((distance(location.getLatitude(),location.getLongitude(),Double.parseDouble(taskOriginLat),Double.parseDouble(taskOriginLon))<0.050)&&(started.equals("false"))){
                        //startbutton.setBackgroundColor(Android.Graphics.Color.parseColor("#007FA3"));
                        startbutton.setBackgroundColor(getResources().getColor(R.color.deeba));
                        vibe.vibrate(1000);
                        enableStart="true";
                        // Toast.makeText(getApplicationContext(), "Start button active", Toast.LENGTH_LONG).show();
                        started="true";
                        startbutton.setVisibility(View.VISIBLE);
                        //start();
                    }}
                if (nextStarted.equals("false")) {
                    Log.i("stopLat",startLat);
                    Log.i("stopLat",stopLon);
                    if ((distance(location.getLatitude(), location.getLongitude(), Double.parseDouble(stopLat), Double.parseDouble(stopLon)) < 0.050) && (nextStarted.equals("false"))) {
                        nextbutton.setVisibility(View.VISIBLE);
                        vibe.vibrate(1000);
                        nextStarted = "true";
                        next();
                    }
                }
                Log.i("speed",Float.toString(location.getSpeed()));
//                if ((!firsttime.equals("true"))&&()){
//                    addingDataToFirebase(location);
//                }
                if (ending.equals("false")) {
                    addingDataToFirebase(location);
                }
                if (setEndVisible.equals("true")){
                    if ((distance(location.getLatitude(), location.getLongitude(), Double.parseDouble(taskDesLat), Double.parseDouble(taskDesLon)) < 0.050)) {
                        endbutton.setVisibility(View.VISIBLE);
                        vibe.vibrate(1000);
                        setEndVisible="false";
                        endbutton.setVisibility(View.VISIBLE);
                        //ending();
                    }
                }
                // currentLOc = new MarkerOptions().position(new LatLng(6.8974681,79.8586554)).title("Mardiwela");//
            }
            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {
            }
            @Override
            public void onProviderEnabled(String provider) {
            }
            @Override
            public void onProviderDisabled(String provider) {
            }
        };
        if (Build.VERSION.SDK_INT < 23) {
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        }else{
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                // ask for permission
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }else {
                //we have permission
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        if ((DifferentStop.equals("false"))||(DifferentStop.equals("end"))) {
            Log.d("mylog", "Added Markers");
            mMap.addMarker(place1);
            mMap.addMarker(place2);
            builder = new LatLngBounds.Builder();
            //currentlocationzoombounds= new LatLngBounds( new LatLng(Double.parseDouble(taskDesLat),Double.parseDouble(taskDesLon)),new LatLng(Double.parseDouble(taskOriginLat),Double.parseDouble(taskOriginLon)));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sri_lanka));
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(sri_lanka, 17));
            new FetchURL(MapActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
            builder.include(place1.getPosition());
            builder.include(place2.getPosition());
            LatLngBounds bounds = builder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.10);
            cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mMap.animateCamera(cu);
        }
    }
    private String getUrl(LatLng origin, LatLng dest, String directionMode) {
        // Origin of route
        String str_origin = "origin=" + origin.latitude + "," + origin.longitude;
        // Destination of route
        String str_dest = "destination=" + dest.latitude + "," + dest.longitude;
        // Mode
        String mode = "mode=" + directionMode;
        // Building the parameters to the web service
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        // Output format
        String output = "json";
        // Building the url to the web service
        String url = "https://maps.googleapis.com/maps/api/directions/" + output + "?" + parameters + "&key=" + getString(R.string.google_maps_key1);
        return url;
    }
//    @Override
//    public void onBackPressed() {
//        // Do Here what ever you want do on back press;
//    }
    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
    public void ending(View view){
        messageId=getID();
        messages.child(messageId).child("msg").setValue( actualDriverFname+" has reached his final stop");
        messages.child(messageId).child("status").setValue( "0");

        vibe.vibrate(1000);
//        Intent serviceIntent = new Intent(this, ExampleService.class);
//        stopService(serviceIntent);
        Log.i("MapsActivity", "working 10");
//        Log.i("testingStage","2");
//        Log.i("delete",taskids);
//        Log.i("delete",companyId);
        mEditor = mpreferences.edit();
        ending="true";
        mEditor.putString(getString(R.string.ending),ending);
        mEditor.commit();
        Log.i("testingpara","update task set taskStatus='1' where taskId='"+taskids+"';");
        StringRequest updatetaskprevious = new StringRequest(Request.Method.POST,
                Constants.update,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.i("taskNextid",taskNextid);
                        updatenexttask(taskNextid);
                        delete.child(companyId).child(taskids).removeValue();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String query = "update task set taskStatus='1' where taskId='"+taskids+"'";
                Log.i("queryloging2", query);
                params.put("query", query);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(updatetaskprevious);
        locationManager.removeUpdates(locationListener);
        ending="true";
        updatenexttask(taskNextid);
        delete.child(companyId).child(taskids).removeValue();
        Intent afterLogin = new Intent(getApplicationContext(), Tasks.class);
        afterLogin.putExtra("driverId", PredriverId);
        afterLogin.putExtra("taskPrevious", taskids);
        AddingToCompletedTasks();
        startActivity(afterLogin);
    }
    public void start(View view){
        vibe.vibrate(1000);
        starttime=true;
        if (enableStart.equals("true")) {
            startbutton.setVisibility(View.INVISIBLE);
            messageId=getID();
            messages.child(messageId).child("msg").setValue( actualDriverFname+" has started the task");
            messages.child(messageId).child("status").setValue( "0");

            Starttime=getTime();
            mEditor=mpreferences.edit();
            mEditor.putString(getString(R.string.startTime),Starttime);

            Log.i("MapsActivity", "working 11");
//            distanceimg.setVisibility(View.INVISIBLE);
//            clockimg.setVisibility(View.INVISIBLE);
//            distancetext.setVisibility(View.INVISIBLE);
//            timetext.setVisibility(View.INVISIBLE);
            StringRequest stringRequest2 = new StringRequest(Request.Method.POST,
                    Constants.ROOT_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject starting = new JSONObject(response);
                                Log.i("start_objects", starting.toString());
                                String status = starting.getString("status").toString();
                                if (status == "true") {
                                    // Toast.makeText(getApplicationContext(), "testing", Toast.LENGTH_LONG).show();
                                    String result = starting.getString("result");
                                    JSONObject resultob = new JSONObject(result);
                                    // Toast.makeText(getApplicationContext(), "Successes", Toast.LENGTH_LONG).show();
                                    Log.i("plsworkpls", "working");
                                    stopLon = resultob.getString("stopLon");
                                    Log.i("plsworkpls", "working");
                                    stopLat = resultob.getString("stopLat");
                                    Log.i("plsworkpls", "working");
                                    stopName = resultob.getString("stopName");
                                    Log.i("plsworkpls", stopName);
                                    map_view.setVisibility(View.VISIBLE);
                                    mMap.clear();
                                    stopPrevious = resultob.getString("stopId");
                                    stopNext = resultob.getString("stopNext");
                                    updatestopsafter(stopNext);
                                    updatestopspre(stopPrevious);
                                    if (resultob.getString("stopNext").equals("-1")) {
                                        start.setText(taskOriginName);
                                        end.setText(stopName);
                                        place2 = new MarkerOptions().position(new LatLng(Double.parseDouble(stopLat), Double.parseDouble(stopLon))).title(stopName);
                                        finalstop = "true";
                                    }
                                    gettingDistanceandeta(taskOriginLat,taskOriginLon,stopLat,stopLon);
                                    updatestopsafter(stopNext);                  //this is to set the next stop's stopPrevious to -1
                                    updatestopspre(stopPrevious);                //stopPrevious is stop Id //this is to set stopStatus to 1
                                    //Toast.makeText(getApplicationContext(),stopName , Toast.LENGTH_LONG).show();
                                    place2 = new MarkerOptions().position(new LatLng(Double.parseDouble(stopLat), Double.parseDouble(stopLon))).title(stopName);
                                    mMap.addMarker(place1);
                                    mMap.addMarker(place2);
                                    builder = new LatLngBounds.Builder();
                                    builder.include(place1.getPosition());
                                    builder.include(place2.getPosition());
                                    LatLngBounds bounds = builder.build();
                                    int width = getResources().getDisplayMetrics().widthPixels;
                                    int height = getResources().getDisplayMetrics().heightPixels;
                                    int padding = (int) (width * 0.10);
                                    cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                    mMap.animateCamera(cu);
                                    end.setText(stopName);
                                    start.setText(taskOriginName);
                                    startName = stopName;
                                    startLat = stopLat;
                                    startLon = stopLon;
                                    new FetchURL(MapActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
                                    preStopLat=stopLat;
                                    preStopLon=stopLon;
                                    place1 = new MarkerOptions().position(new LatLng(Double.parseDouble(stopLat), Double.parseDouble(stopLon))).title(stopName);
                                    nextStarted = "false";
                                    nextbutton.setVisibility(View.INVISIBLE);

//                                place2 = new MarkerOptions().position(new LatLng(Double.parseDouble(stopLat), Double.parseDouble(stopLon))).title(stopName);
//                                mMap.addMarker(place1);
//                                mMap.addMarker(place2);
                                } else {
                                    setEndVisible="true";
                                    gettingDistanceandeta(taskOriginLat,taskOriginLon,taskDesLat,taskDesLon);
                                    startName = taskDesName;
                                    startLat = taskDesLat;
                                    startLon = taskDesLon;
                                    start.setText(taskOriginName);
                                    end.setText(taskDesName);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    Log.i("taskids", taskids);
                    String query = "select * from stop where taskId='" + taskids + "' AND stopPrevious='" + "-1' AND stopStatus='0'";
                    Log.i("queryloging_task", query);
                    params.put("query", query);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(stringRequest2);
            Log.i("MapsActivity", "working 12");
            startbutton.setVisibility(View.INVISIBLE);
//                                                         nextbutton.setVisibility(View.VISIBLE);
//Log.i("testing_map",stopLat);
            //    Log.i("testing_map",stopLon);
            // place2.position((new LatLng(Double.parseDouble(stopLat), Double.parseDouble(stopLon))));
//        Log.i("place1Marker","working");
//        mMap.addMarker(place1);
//        mMap.addMarker(place2);
            //      Log.i("place1Marker","working2");
            mEditor.apply();
            mEditor.commit();
        }else {
            Toast.makeText(getApplicationContext(), "Go to the start location", Toast.LENGTH_LONG).show();
        }
    }
    public void next(){
        vibe.vibrate(80);
        nextbutton.setVisibility(View.INVISIBLE);
        if (finalstop.equals("true")){
            messageId=getID();
            messages.child(messageId).child("msg").setValue(actualDriverFname+"has reached his stop");
            messages.child(messageId).child("status").setValue( "0");

            nextbutton.setVisibility(View.INVISIBLE);
            setEndVisible="true";
            gettingDistanceandeta(preStopLat,preStopLon,taskDesLat,taskDesLon);
            start.setText(startName);
            end.setText(taskDesName);
            stopLat=taskDesLat;
            stopLon=taskDesLon;
            startName = taskDesName;
            startLat = taskDesLat;
            startLon = taskDesLon;
            place2=new MarkerOptions().position(new LatLng(Double.parseDouble(taskDesLat), Double.parseDouble(taskDesLon))).title(taskDesName);
            mMap.clear();
            mMap.addMarker(place1);
            mMap.addMarker(place2);
            builder = new LatLngBounds.Builder();
            builder.include(place1.getPosition());
            builder.include(place2.getPosition());
            LatLngBounds bounds = builder.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.10);
            cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mMap.animateCamera(cu);
            new FetchURL(MapActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
        }else{
            StringRequest nextRequest = new StringRequest(Request.Method.POST,
                    Constants.ROOT_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject next = new JSONObject(response);
                                Log.i("start_objects", next.toString());
                                String status = next.getString("status").toString();
                                if (status == "true") {
                                    //Toast.makeText(getApplicationContext(), "testing", Toast.LENGTH_LONG).show();
                                    String result = next.getString("result");
                                    JSONObject resultob = new JSONObject(result);
                                    stopNext= resultob.getString("stopNext");
                                    Log.i("stopNext", stopNext);
                                    // Toast.makeText(getApplicationContext(), "Successes", Toast.LENGTH_LONG).show();
                                    Log.i("plsworkpls", "working");
                                    stopLon = resultob.getString("stopLon");
                                    Log.i("plsworkpls", "working");
                                    stopLat = resultob.getString("stopLat");
                                    gettingDistanceandeta(preStopLat,preStopLon,stopLat,stopLon);
                                    preStopLon=stopLon;
                                    preStopLat=stopLat;
                                    Log.i("plsworkpls", "working");
                                    stopPrevious = resultob.getString("stopId");
                                    stopName = resultob.getString("stopName");
                                    updatestopsafter(stopNext);
                                    updatestopspre(stopPrevious);
                                    place2 = new MarkerOptions().position(new LatLng(Double.parseDouble(stopLat), Double.parseDouble(stopLon))).title(stopName);
                                    mMap.clear();
                                    mMap.addMarker(place1);
                                    mMap.addMarker(place2);
                                    builder = new LatLngBounds.Builder();
                                    builder.include(place1.getPosition());
                                    builder.include(place2.getPosition());
                                    LatLngBounds bounds = builder.build();
                                    int width = getResources().getDisplayMetrics().widthPixels;
                                    int height = getResources().getDisplayMetrics().heightPixels;
                                    int padding = (int) (width * 0.10);
                                    cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                                    mMap.animateCamera(cu);
                                    start.setText(startName);
                                    end.setText(stopName);
                                    //Toast.makeText(getApplicationContext(),stopName , Toast.LENGTH_LONG).show();end.setText(stopName);
                                    new FetchURL(MapActivity.this). execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
                                    startName = stopName;
                                    startLat = stopLat;
                                    startLon = stopLon;
                                    place1 = new MarkerOptions().position(new LatLng(Double.parseDouble(stopLat), Double.parseDouble(stopLon))).title(stopName);
                                    if (stopNext.equals("-1")) {
                                        place2 = new MarkerOptions().position(new LatLng(Double.parseDouble(taskDesLat), Double.parseDouble(taskDesLon))).title(taskDesName);
                                        finalstop="true";
                                    }
                                    nextStarted = "false";
//                                place2 = new MarkerOptions().position(new LatLng(Double.parseDouble(stopLat), Double.parseDouble(stopLon))).title(stopName);
//                                mMap.addMarker(place1);
//                                mMap.addMarker(place2);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    String query = "select * from stop where taskId='" + taskids + "' AND stopPrevious='-1' AND stopStatus='0'";
                    Log.i("queryloging", query);
                    params.put("query", query);
                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(this);
            requestQueue.add(nextRequest);
        }
    }
    public void showMap(String destinationLatitude,String destinationLongitude,String stopName) {
        String uri = "http://maps.google.com/maps?daddr=" + destinationLatitude + "," + destinationLongitude + " (" + stopName+ ")";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
    }
    //for calculating the distance
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
    public void open_google_maps(View view){
        showMap(startLat,startLon,stopName);
    }
    public void addingDataToFirebase(Location location){
        Log.i("testingfirebase","working1");
        Log.i("testingfirebase",companyId);
        Log.i("testingfirebase",taskids);
        onlineDriver=database.getReference("onGoingTask/"+companyId+"/"+taskids);
        Log.i("testingfirebase","working2");
        onlineDriver.child("PredriverFname").setValue( PredriverFname);
        onlineDriver.child("PredriverLname").setValue( PredriverLname);
        onlineDriver.child("PredriverId").setValue( PredriverId);
        onlineDriver.child("actualDriver").setValue(CurrentDriverId);
        onlineDriver.child("actualDriverFname").setValue( actualDriverFname);
        onlineDriver.child("actualDriverLname").setValue( actualDriverLname);
        Log.i("testingfirebase","working3");
        onlineDriver.child("endLocName").setValue(taskDesName);
        onlineDriver.child("endLat").setValue(taskDesLat);
        onlineDriver.child("endLon").setValue(taskDesLon);
        Log.i("testingfirebase","working4");
        onlineDriver.child("startLocName").setValue(taskOriginName);
        onlineDriver.child("startLat").setValue(taskOriginLat);
        onlineDriver.child("startLon").setValue(taskOriginLon);
        onlineDriver.child("vehicalId").setValue(vehicalId);
        Log.i("testingfirebase","working5");
        onlineDriver.child("currentLag").setValue(location.getLatitude());
        onlineDriver.child("currentLog").setValue(location.getLongitude());
        onlineDriver.child("companyId").setValue(companyId);
        onlineDriver.child("taskids").setValue(taskids);
        onlineDriver.child("vehicalLicenPlate1").setValue(vehicalLicenPlate1);
        onlineDriver.child("vehicalLicenPlate2").setValue(vehicalLicenPlate2);
        onlineDriver.child("vehicalLicenPlate3").setValue(vehicalLicenPlate3);
        onlineDriver.child("taskStartTime").setValue(taskStartTime);
        onlineDriver.child("taskEndTime").setValue(taskEndTime);
        onlineDriver.child("eta").setValue(Integer.toString(totalEta));
        onlineDriver.child("totalDistance").setValue(Integer.toString(totalDistance));
        onlineDriver.child("totalIdle").setValue(mpreferences.getString(getString(R.string.totalIdle),""));
        onlineDriver.child("alert").setValue(Integer.toString(alertIdle));
    }
    public void updatenexttask(final String taskNext){
        Log.i("updatenexttask","working");

        StringRequest updatetasknext = new StringRequest(Request.Method.POST,
                Constants.ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String query = "update task set taskPrevious='-1' where taskId='"+taskNext+"' ;";

                Log.i("queryloging1", query);
                Log.i("updatenexttask", query);
                params.put("query", query);


                return params;


            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(updatetasknext);
        delete.child(companyId).child(taskids).removeValue();
    }
    public void AddingToCompletedTasks(){
        StringRequest updatetaskcomplete = new StringRequest(Request.Method.POST,
                Constants.ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String query = "INSERT INTO completedtask(taskId, vehicalId, companyId, taskActualStartTime, taskActualEndTime, taskTotalDistance, taskEndDate, taskETA, taskIdleTime, completedtaskStatus, preferredDriverId, currentDriverId)" +
                        " VALUES ('"+taskids+"','"+vehicalId+"','"+companyId+"','"+mpreferences.getString(getString(R.string.startTime),"0")+"','"+getTime()+"','"+Integer.toString(totalDistance)+"','"+getDate()+"','"+Integer.toString(totalEta)+"','"+mpreferences.getString(getString(R.string.totalIdle),"9")+"','1','"+PredriverId+"','"+CurrentDriverId+"');";
                Log.i("queryloging1", query);
                Log.i("updatenexttask", query);
                params.put("query", query);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(updatetaskcomplete);
    }

    private String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
    private String getDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date date = new Date();
        return dateFormat.format(date);
    }
    public void gettingDistanceandeta(final String originLatitude,final String originLongitude,final  String desLatitude,final String desLongitude){
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST,
                Constants.distance,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.i("responrdetails8", jsonObject.toString());
                            String status = jsonObject.getString("status").toString();
                            if (status=="true") {
                                //  Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                                curDistance = Integer.parseInt(jsonObject.getString("distance"));
                                curETA = Integer.parseInt(jsonObject.getString("time"));
                                Log.i("testinggetDataFordistan",Integer.toString(curDistance));
                                Log.i("testinggetDataFordistan",Integer.toString(curETA));
                                timetext.setText(Integer.toString(curETA/60)+"min");
                                distancetext.setText(Integer.toString(curDistance/1000)+"KM");

                            }else {
                                Toast.makeText(getApplicationContext(), "SOME thing is wrong", Toast.LENGTH_LONG).show();
                            }
                        } catch(JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                //Log.i("driverId", driverId);
                params.put("originLatitude", originLatitude);
                params.put("originLongitude",originLongitude);
                params.put("desLatitude", desLatitude);
                params.put("desLongitude", desLongitude);
                return params;
            }
        };
        RequestQueue requestQueue2 = Volley.newRequestQueue(this);
        requestQueue2.add(stringRequest2);
    }
    public void updatestopspre (final String stopId){
        Log.i("updatestopspre","working");
        StringRequest updatetasknext = new StringRequest(Request.Method.POST,
                Constants.update,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String query = "update stop set stopStatus='1' where stopId='"+stopId+"' ;";

                Log.i("queryloging1", query);
                Log.i("updatestop", query);
                params.put("query", query);
                return params;


            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(updatetasknext);

    }
    public void updatestopsafter (final String stoppre){
        Log.i("updatestopsafter","working");

        StringRequest updatetasknext = new StringRequest(Request.Method.POST,
                Constants.update,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                String query = "update stop set stopPrevious='-1' where stopId='"+stoppre+"' ;";

                Log.i("queryloging1", query);
                Log.i("updatestop", query);
                params.put("query", query);
                return params;

//                6.87055 79.8862
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(updatetasknext);

    }
    public String getID(){
        Date dte=new Date();
        long milliSeconds = dte.getTime();
        String strLong = Long.toString(milliSeconds);
        return strLong;

    }


}
