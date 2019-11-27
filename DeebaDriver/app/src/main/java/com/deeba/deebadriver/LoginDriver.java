package com.deeba.deebadriver;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class LoginDriver extends AppCompatActivity {
    private EditText passwordfield,NIC;
    ConstraintLayout login_lay;
    private Button buttonLogin;
    private ProgressDialog progressDialog;
    private SharedPreferences mpreferences;
    private SharedPreferences.Editor mEditor;
    LocationListener locationListener1;
    LocationManager locationManager1;
    FirebaseDatabase database2;
    DatabaseReference LoginFire;
    String driverId,companyId,actualDriverFname;
    DateFormat timeFormat,dateFormat;
    Date currentdate;
    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager1.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener1);
            }
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_driver);
        login_lay=findViewById(R.id.login_layout);
        database2= FirebaseDatabase.getInstance();
        mpreferences= PreferenceManager.getDefaultSharedPreferences(this);
        mEditor=mpreferences.edit();
        companyId=mpreferences.getString(getString(R.string.companyId),"1");
        timeFormat= new SimpleDateFormat("HH:mm:ss");
        dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        currentdate = new Date();
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                login_lay.setVisibility(View.VISIBLE);
            }
        }, 3000);
        NIC=(EditText) findViewById(R.id.vehical_Licen_plate3);
        passwordfield =(EditText) findViewById(R.id.password);
        progressDialog = new ProgressDialog(this);
        locationManager1 = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener1=new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                Log.i("responrdetails1","working");
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
            locationManager1.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener1);
        }else{
            if (ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED){
                // ask for permission
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
            }else {
                //we have permission
                locationManager1.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener1);
            }
        }
    }
    public void Login(View view){
        locationManager1.removeUpdates(locationListener1);
        final String Nic= NIC.getText().toString();
        final String password= passwordfield.getText().toString();
        progressDialog.setMessage("Checking Details");
        progressDialog.show();
        StringRequest stringRequest=new StringRequest(Request.Method.POST,
                Constants.ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject=new JSONObject(response);
                            Log.i("",jsonObject.toString());
                            String status=jsonObject.getString("status").toString();
                            if(status.equals("true")){
                                actualDriverFname=mpreferences.getString(getString(R.string.currentdriverFname),"");
                                String messageId=getID();
                                LoginFire=database2.getReference("login/"+companyId+"/"+messageId+"/"+actualDriverFname);
                                LoginFire.child("Time").setValue( timeFormat.format(currentdate));
                                LoginFire.child("Date").setValue( dateFormat.format(currentdate));
                               // Toast.makeText(getApplicationContext(),"Success", Toast.LENGTH_LONG).show();
                                String result=jsonObject.getString("result");
                                //String DriverType=jsonObject.getString("driverType");
                                Log.i("piushapiusha","tst");
                                JSONObject resultobject=new JSONObject(result);
                                Log.i("piushapiusha",resultobject.toString());
                                // Object[] results=jsonObject.getJSONArray("result");
                                String Fname=resultobject.getString("driverFname");
                                String Lname=resultobject.getString("driverLname");
                                String DriverType=resultobject.getString("driverType");
                                driverId=resultobject.getString("driverId");
                                mEditor.putString(getString(R.string.currentdriverId),driverId);
                                mEditor.putString(getString(R.string.currentdriverFname),Fname);
                                mEditor.putString(getString(R.string.currentdriverLname),Lname);
                                mEditor.putString(getString(R.string.ending),"true");
                                mEditor.putString(getString(R.string.deleteonlineuser),"false");
                                mEditor.commit();
                                if (DriverType.equals("1")){
                                    Intent welcome = new Intent(getApplicationContext(), FreWelcome.class);
                                    welcome.putExtra("driverId", driverId);
                                    welcome.putExtra("taskPrevious", "-1");
                                    welcome.putExtra("Fname", Fname);
                                    welcome.putExtra("Lname", Lname);
                                    startActivity(welcome);
                                }else {
                                    Log.i("piushapiusha", "tst");
                                    Log.i("piushapiusha", "tst6");
                                    Log.i("piushapiusha", driverId);
                                    Intent welcome = new Intent(getApplicationContext(), Welcome.class);
                                    welcome.putExtra("driverId", driverId);
                                    welcome.putExtra("taskPrevious", "-1");
                                    welcome.putExtra("username", Fname);
                                    Log.i("testingStage", "2");
                                    startservice();
                                    startActivity(welcome);
                                    Log.i("piushapiusha", Fname);
                                    //Log.i("piushapiusha",result);
                                    Log.i("piushapiusha", resultobject.getString("driverFname"));
                                }
                            }else{
                              //  Toast.makeText(getApplicationContext(),"Fail", Toast.LENGTH_LONG).show();
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error){
                        progressDialog.dismiss();
                        Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<>();
                String query="select * from driver where driverNic='"+Nic + "' AND driverPassword='"+password+"'";
                Log.i("queryloging",query);
                params.put("query",query);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    public void updatedriver(final String taskNext){
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
                String query = "update driver set driverStatus='1' where driverId='"+driverId+"' ;";
                Log.i("queryloging1", query);
                Log.i("updatenexttask", query);
                params.put("query", query);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(updatetasknext);
    }
    private void startservice(){
        Intent serviceIntent = new Intent(this, ExampleService.class);
        serviceIntent.putExtra("companyId", companyId);
        ContextCompat.startForegroundService(this, serviceIntent);
    }
    public String getID(){
        Date dte=new Date();
        long milliSeconds = dte.getTime();
        String strLong = Long.toString(milliSeconds);
        return strLong;
    }
}
