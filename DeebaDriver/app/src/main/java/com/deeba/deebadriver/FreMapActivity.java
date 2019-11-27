package com.deeba.deebadriver;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FreMapActivity extends AppCompatActivity implements OnMapReadyCallback, TaskLoadedCallback {
    public static final String TAG="FreMApActivity ";
    private GoogleMap mMap;
    int buttoncount=0;
    Double TotalWaitingTime=0.0,TotalPrice=0.0;
    private Polyline currentPolyline;
    int statusChange=0;
    Double PreviousLat,PreviousLon,DistanceTravelled=0.0;
    boolean HasNotAccepted=true,CustomerPickup=false,ClearMap=true,TripStarted=false;
    String vehicalType,vehicalId,driverId,CustomerName,CustomerId,CustomerLat,CustomerLon,CustomerEndLat,CustomerEndLon,StopLocationUpdates;
    String showMapLat,showMapLon,showMapName;
    LocationListener locationListener;
    LocationManager locationManager;
    MarkerOptions place1,place2,place3;
    LinearLayout AcceptEnd,After_Accept,Price;
    TextView AcceptEndText,CustomerNameText,DistanceText,DistancePriceText,WaitingText,WaitingPrizeText,TotalPriceText,Timer;
    Button GoOffline,GoOnline,AcceptEndButton,StartTrip;
    MapFragment mapFragment;
    ConstraintLayout filter;
    FirebaseDatabase database2;
    DatabaseReference onlineFreeLancer,listening;
    private SharedPreferences mpreferences;
    private SharedPreferences.Editor mEditor;
    DateFormat timeFormat,dateFormat;
    Date currentdate;
    CameraUpdate cu;
    ImageView navigation;
    LatLngBounds.Builder builder,builder2;
    private CountDownTimer countDownTimer;
    private long timeLeftMili = 15000;
    private boolean timer_running;
    MediaPlayer bellsound;
    Vibrator vibe;
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
    public void onBackPressed() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fre_map);
        vibe= (Vibrator) FreMapActivity.this.getSystemService(Context.VIBRATOR_SERVICE);
        bellsound = MediaPlayer.create(this,R.raw.bell);
        vibe.vibrate(1000);
        buttoncount=0;
        timeFormat= new SimpleDateFormat("HH:mm:ss");
        dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        currentdate = new Date();
        database2= FirebaseDatabase.getInstance();
        mpreferences= PreferenceManager.getDefaultSharedPreferences(this);
        vehicalType=mpreferences.getString(getString(R.string.vehicalType),"1");
        mEditor=mpreferences.edit();
        mEditor.putString(getString(R.string.stop_location_updates),"false");
        mEditor.apply();
        mEditor.commit();
        vehicalId=mpreferences.getString(getString(R.string.vehicalId),"1");
        driverId=mpreferences.getString(getString(R.string.currentdriverId),"1");
        onlineFreeLancer=database2.getReference("onlineFreeLancer/"+vehicalType+"/"+vehicalId);
        listening=database2.getReference("onlineFreeLancer/"+vehicalType);
        onlineFreeLancer.child("Time").setValue( timeFormat.format(currentdate));
        onlineFreeLancer.child("Date").setValue( dateFormat.format(currentdate));
        onlineFreeLancer.child("status").setValue("0");
        onlineFreeLancer.child("customerId").setValue("0");
        onlineFreeLancer.child("customerLat").setValue("6.8729287");
        onlineFreeLancer.child("customerLon").setValue("79.8867418");
        onlineFreeLancer.child("customerEndLat").setValue("6.8929287");
        onlineFreeLancer.child("customerEndLon").setValue("79.8867418");
        onlineFreeLancer.child("customerName").setValue("Customer name");
        onlineFreeLancer.child("driverId").setValue(driverId);
        navigation=findViewById(R.id.imageView13);
        navigation.setVisibility(View.GONE);
        GoOffline=(Button)findViewById(R.id.go_offline);
        GoOnline=(Button)findViewById(R.id.go_online);
        StartTrip=findViewById(R.id.start_trip);
        filter=(ConstraintLayout)findViewById(R.id.filter);
        filter.setVisibility(View.INVISIBLE);
        DistanceText=(TextView)findViewById(R.id.distance);
        DistancePriceText=(TextView)findViewById(R.id.distance_price);
        WaitingText=(TextView)findViewById(R.id.waiting);
        WaitingPrizeText=(TextView)findViewById(R.id.wait_price);
        TotalPriceText=(TextView)findViewById(R.id.total);
        AcceptEndText=(TextView)findViewById(R.id.accept_end_title) ;
        Timer=findViewById(R.id.timer);
        AcceptEndButton=(Button)findViewById(R.id.accept_end_button);
        AcceptEnd=(LinearLayout)findViewById(R.id.Accept_End);
        After_Accept=(LinearLayout)findViewById(R.id.After_Accept_6);
        Price=(LinearLayout)findViewById(R.id.pricing9) ;
        CustomerNameText=(TextView)findViewById(R.id.customer_name);
        //place1 = new MarkerOptions().position(new LatLng(6.88714,79.85829666666667)).title("Location 1");
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapNearBy);
        mapFragment.getMapAsync(this);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                StopLocationUpdates = mpreferences.getString(getString(R.string.stop_location_updates), "false");
                if (StopLocationUpdates.equals("false")){
                    onlineFreeLancer.child("lon").setValue(Double.toString(location.getLongitude()));
                    onlineFreeLancer.child("lat").setValue(Double.toString(location.getLatitude()));
                    onlineFreeLancer.child("driverId").setValue(driverId);
//                location.getLatitude(), location.getLongitude()
                    place1 = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Driver");
                    if (ClearMap) {
                     mMap.clear();
                        mMap.addMarker(place1);
                    }
                    if (HasNotAccepted) {
                      mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
                      HasNotAccepted = false;
                    }
                    if (CustomerPickup && (buttoncount == 1)) {
                        if ((distance(location.getLatitude(), location.getLongitude(), Double.parseDouble(CustomerLat), Double.parseDouble(CustomerLon)) < 0.050)) {
                            CustomerPickup = false;
                            Log.d(TAG, "working");
                            PreviousLat = location.getLatitude();
                            PreviousLon = location.getLongitude();
                            Arrival();
                            vibe.vibrate(300);

                            //StartTrip.setVisibility(View.VISIBLE);

                        }
                    }
                    if (TripStarted) {
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                double distanceCal = (distance(location.getLatitude(), location.getLongitude(), PreviousLat, PreviousLon));
                                Log.i("DistanceTravelled", Double.toString(distanceCal));
                                if (distanceCal < (0.0048)) {
                                    TotalWaitingTime = TotalWaitingTime + 0.5;

                                    WaitingText.setText(Double.toString(TotalWaitingTime) + " s");
                                    WaitingPrizeText.setText("Rs " + Double.toString(TotalWaitingTime * 0.25));
                                    TotalPrice = TotalWaitingTime * 0.25 + DistanceTravelled * 40;
                                    TotalPriceText.setText(Double.toString(TotalPrice));
                                } else {

                                    DistanceTravelled = DistanceTravelled + distanceCal;
                                    PreviousLat = location.getLatitude();
                                    PreviousLon = location.getLongitude();
                                    DistanceText.setText(String.format("%.2f", DistanceTravelled * 1000));
                                    DistancePriceText.setText(String.format("%.2f", DistanceTravelled * 40) + "m");
                                    TotalPrice = TotalWaitingTime * 0.25 + DistanceTravelled * 40;
                                    TotalPriceText.setText(Double.toString(TotalPrice));

                                }
                            }
                        }, 10000);
                    }
                    }
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
        onlineFreeLancer.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                onlineFreelanceClass test=dataSnapshot.getValue(onlineFreelanceClass.class);
                Log.d(TAG,dataSnapshot.toString());
                try {
                    onlineFreelanceClass Test= new onlineFreelanceClass();
                    Test.setDriverId(dataSnapshot.getValue(onlineFreelanceClass.class).getDriverId());
                    Test.setCustomerId(dataSnapshot.getValue(onlineFreelanceClass.class).getCustomerId());
                    String status=dataSnapshot.child("status").getValue().toString();
                    Log.i("FreMApActivity",status);

                    if ((status.equals("1"))&& (statusChange==0)){
                        statusChange=1;
                        bellsound.start();
                        GoOffline.setVisibility(View.INVISIBLE);
                        AcceptEnd.setVisibility(View.VISIBLE);
                        filter.setVisibility(View.VISIBLE);
                        Timer.setVisibility(View.VISIBLE);
                        CustomerId=dataSnapshot.child("customerId").getValue().toString();
                        CustomerName=dataSnapshot.child("customerName").getValue().toString();
                        CustomerLat=dataSnapshot.child("customerLat").getValue().toString();
                        CustomerLon=dataSnapshot.child("customerLon").getValue().toString();
                        CustomerEndLat=dataSnapshot.child("customerEndLat").getValue().toString();
                        CustomerEndLon=dataSnapshot.child("customerEndLon").getValue().toString();

                        //place1 = new MarkerOptions().position(new LatLng(Double.parseDouble(CustomerLat), Double.parseDouble(CustomerLon))).title("Customer");
                        CustomerPickup=true;
                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(buttoncount==0){
                                    onlineFreeLancer.child("status").setValue("0");
                                    GoOffline.setVisibility(View.VISIBLE);
                                    AcceptEnd.setVisibility(View.INVISIBLE);
                                    filter.setVisibility(View.INVISIBLE);
                                    statusChange=0;
                                    countDownTimer.cancel();
                                    Timer.setVisibility(View.GONE);
                                    timeLeftMili = 15000;
                                    vibe.vibrate(1000);
                                    bellsound.start();
                                }
                            }
                        }, 15000);

                        countDownTimer=new CountDownTimer(timeLeftMili,1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {
                                timeLeftMili=millisUntilFinished;
                                int minutes= (int)timeLeftMili/60000;
                                int seconds=(int)timeLeftMili %60000 / 1000;
                                String TimeLeftString="";
                                if (seconds<10)TimeLeftString="0";
                                TimeLeftString +=seconds;
                                Log.d(TAG,TimeLeftString);
                                Timer.setText(TimeLeftString);
                            }

                            @Override
                            public void onFinish() {

                            }
                        }.start();



                    }

                }catch (Exception e){
                    e.printStackTrace();
                }


            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }

    public void AcceptEndbuttonpress(View view){
        if (buttoncount==0){
            onlineFreeLancer.child("status").setValue("2");

                    Log.d(TAG, "onDataChange: "+CustomerName);
                    vibe.vibrate(80);
                    countDownTimer.cancel();
                    Timer.setVisibility(View.GONE);
                    navigation.setVisibility(View.VISIBLE);
                    timeLeftMili = 15000;
                    showMapLat=CustomerLat;
                    showMapLon=CustomerLon;
                    showMapName="Customer";
                    AcceptEnd.setVisibility(View.INVISIBLE);
                    filter.setVisibility(View.INVISIBLE);
                    After_Accept.setVisibility(View.VISIBLE);
                    CustomerNameText.setText(CustomerName);
                    ClearMap=false;
                    place2 = new MarkerOptions().position(new LatLng(Double.parseDouble(CustomerLat), Double.parseDouble(CustomerLon))).title("Customer");
                    mMap.addMarker(place2);
                    new FetchURL(FreMapActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
                    builder = new LatLngBounds.Builder();
                    builder.include(place1.getPosition());
                    builder.include(place2.getPosition());
                    LatLngBounds bounds = builder.build();
                    int width = getResources().getDisplayMetrics().widthPixels;
                    int height = getResources().getDisplayMetrics().heightPixels;
                    int padding = (int) (width * 0.10);
                    cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
                    mMap.animateCamera(cu);
                    buttoncount=1;


        }else if (buttoncount==1){

            vibe.vibrate(80);
            vibe.vibrate(80);
            Timer.setVisibility(View.GONE);
            timeLeftMili = 15000;
            ClearMap=false;
            showMapLat=CustomerEndLat;
            showMapLon=CustomerEndLon;
            showMapName="Customer End Location";
            showMapName="Customer";
            AcceptEnd.setVisibility(View.INVISIBLE);
            filter.setVisibility(View.INVISIBLE);
            StartTrip.setVisibility(View.VISIBLE);
            buttoncount=2;
            mMap.clear();
            Log.d(TAG,CustomerEndLat+" "+CustomerEndLon);
            place1 = new MarkerOptions().position(new LatLng(Double.parseDouble(CustomerLat), Double.parseDouble(CustomerLon))).title("Customer New Location");
            place2 = new MarkerOptions().position(new LatLng(Double.parseDouble(CustomerEndLat), Double.parseDouble(CustomerEndLon))).title("Drop Location");
            mMap.addMarker(place2);
            mMap.addMarker(place1);
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(Double.parseDouble(CustomerEndLat), Double.parseDouble(CustomerEndLat)))
                    .title("Drop Location"));
            Log.d(TAG,(place2.getPosition().latitude)+" "+place2.getPosition().longitude);
            new FetchURL(FreMapActivity.this).execute(getUrl(place1.getPosition(), place2.getPosition(), "driving"), "driving");
            builder2 = new LatLngBounds.Builder();
            builder2.include(place1.getPosition());
            builder2.include(place2.getPosition());
            LatLngBounds bounds = builder2.build();
            int width = getResources().getDisplayMetrics().widthPixels;
            int height = getResources().getDisplayMetrics().heightPixels;
            int padding = (int) (width * 0.10);
            cu = CameraUpdateFactory.newLatLngBounds(bounds, width, height, padding);
            mMap.animateCamera(cu);
        }else {

            vibe.vibrate(80);
            vibe.vibrate(80);
            vibe.vibrate(80);
            TripStarted=false;
            String TotalWaitingTimeText=String.format("%.2f",TotalWaitingTime);
            String TotalPriceText=String.format("%.2f",TotalPrice);
            String DistanceTravelledText=String.format("%.2f",DistanceTravelled);
            Intent Pricing = new Intent(getApplicationContext(), Pricing.class);
            Pricing.putExtra("TotalPrice",TotalPriceText);
            Pricing.putExtra("TotalDistance",DistanceTravelledText);
            Pricing.putExtra("TotalTime",TotalWaitingTimeText);
            startActivity(Pricing);
        }
    }
    public void End(View view){
        AcceptEnd.setVisibility(View.VISIBLE);
        filter.setVisibility(View.VISIBLE);
        AcceptEndText.setText("End Trip");
        AcceptEndButton.setText("End>>>");
        After_Accept.setVisibility(View.GONE);
    }
    public void Arrival(){
        After_Accept.setVisibility(View.GONE);
        AcceptEnd.setVisibility(View.VISIBLE);
        filter.setVisibility(View.VISIBLE);
        AcceptEndText.setText("Confirm Arrival");
        AcceptEndButton.setText("Comfirm>>>");
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
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
    public void open_google_maps(View view){
        showMap(showMapLat,showMapLon,showMapName);
    }

    public void showMap(String destinationLatitude,String destinationLongitude,String stopName) {
        String uri = "http://maps.google.com/maps?daddr=" + destinationLatitude + "," + destinationLongitude + " (" + stopName+ ")";
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
        intent.setPackage("com.google.android.apps.maps");
        startActivity(intent);
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
    @Override
    public void onTaskDone(Object... values) {
        if (currentPolyline != null)
            currentPolyline.remove();
        currentPolyline = mMap.addPolyline((PolylineOptions) values[0]);
    }
    public void Navigation(View view){
        Intent NavigationIntent = new Intent(getApplicationContext(), FreNav.class);
        startActivity(NavigationIntent);
    }

    public void GoOffline(View view){
        mEditor=mpreferences.edit();
        mEditor.putString(getString(R.string.stop_location_updates),"true");
        mEditor.apply();
        mEditor.commit();
        onlineFreeLancer.removeValue();
        GoOffline.setVisibility(View.GONE);
        GoOnline.setVisibility(View.VISIBLE);
    }
    public void GoOnline(View view){
        mEditor=mpreferences.edit();
        mEditor.putString(getString(R.string.stop_location_updates),"false");
        mEditor.apply();
        mEditor.commit();
        onlineFreeLancer.child("Time").setValue( timeFormat.format(currentdate));
        onlineFreeLancer.child("Date").setValue( dateFormat.format(currentdate));
        onlineFreeLancer.child("status").setValue("0");
        onlineFreeLancer.child("customerId").setValue("0");
        onlineFreeLancer.child("customerLat").setValue("6.8729287");
        onlineFreeLancer.child("customerLon").setValue("79.8867418");
        onlineFreeLancer.child("customerEndLat").setValue("6.8929287");
        onlineFreeLancer.child("customerEndLon").setValue("79.8867418");
        onlineFreeLancer.child("customerName").setValue("Vidu Senanayake");
        onlineFreeLancer.child("driverId").setValue(driverId);
        GoOffline.setVisibility(View.VISIBLE);
        GoOnline.setVisibility(View.GONE);

    }
    public  void StartTrip(View view){
        vibe.vibrate(80);
        TripStarted = true;
        StartTrip.setVisibility(View.GONE);
        Price.setVisibility(View.VISIBLE);
        Timer.setVisibility(View.GONE);
        timeLeftMili = 15000;
    }
}
