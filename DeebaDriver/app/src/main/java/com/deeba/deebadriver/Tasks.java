package com.deeba.deebadriver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Tasks extends AppCompatActivity {

   // String driverId;
    TextView OriginName;
    TextView DesName,notasks;
    String taskPrevious,taskDesLon,taskDesLat,taskOriginLon,taskOriginLat,taskDesName,taskOriginName,nextTask,taskIds,PredriverId;
    String companyId,PredriverFname,PredriverLname,vehicalId,vehicalLicenPlate1,vehicalLicenPlate2,vehicalLicenPlate3,taskEndTime,taskStartTime;
    String stopLat,stopLon,stopName,gotresult,prestopLat,prestopLon,DifferentStop;
    int totalDistance,totalEta,distance,eta,allStops,activeStops;
    Button accept;
    private ProgressDialog progressDialog;
    LinearLayout startendview;
    ListView mylistview;
    ArrayList<String> stopArray;
    ArrayAdapter<String> arrayAdapter;
    private SharedPreferences mpreferences;
    private SharedPreferences.Editor mEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tasks);
        OriginName=(TextView)findViewById(R.id.originloc) ;
        DesName=(TextView)findViewById(R.id.destiname) ;
        accept=findViewById(R.id.accept);
        progressDialog = new ProgressDialog(this);
        companyId=getIntent().getStringExtra("companyId");
        notasks=findViewById(R.id.notasks);
        mpreferences= PreferenceManager.getDefaultSharedPreferences(this);
        mEditor=mpreferences.edit();
        vehicalId=mpreferences.getString(getString(R.string.vehicalId),"");
        startendview=findViewById(R.id.startendview);
        final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        gotresult="false";
        mylistview=(ListView)findViewById(R.id.currentTask);
        stopArray=new ArrayList<String>();
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,stopArray);
        StringRequest stringRequest1 = new StringRequest(Request.Method.POST,
                Constants.ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.i("responrdetails2", jsonObject.toString());
                            String status = jsonObject.getString("status").toString();
                            if (status=="true") {
                               // Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                                String result = jsonObject.getString("result");
                                Log.i("workingtest", result);
                                JSONObject resultobject = new JSONObject(result);
//                                Log.i("piushapiusha",resultobject.toString());
//                                // Object[] results=jsonObject.getJSONArray("result");
                                Log.i("AfterlogingTes   `t", "working 2");
                                taskOriginName = resultobject.getString("taskOriginName");
                                taskOriginLat = resultobject.getString("taskOriginLat");
                                taskOriginLon = resultobject.getString("taskOriginLon");
                                taskDesName = resultobject.getString("taskDesName");
                                taskDesLat = resultobject.getString("taskDesLat");
                                taskDesLon = resultobject.getString("taskDesLon");
                                String taskStatus=resultobject.getString("taskStatus");
                                taskIds=resultobject.getString("taskId");
                                nextTask=resultobject.getString("taskNext");
                                OriginName.setText(taskOriginName);
                                DesName.setText(taskDesName);
                                Log.i("AfterlogingTest", "working 3");
                                getDataForFirebase(taskIds);

                                progressDialog.setMessage("Checking Details");
                                progressDialog.show();
                                get();
                                Log.i("checkingStatus", taskStatus);
                                if (taskStatus.equals("2")) {
                                    compare();
                                }else{
                                    DifferentStop="false";
                                }
                            }else {
                               // Toast.makeText(getApplicationContext(), "No tasks", Toast.LENGTH_LONG).show();
                                accept.setVisibility(View.INVISIBLE);
                                startendview.setVisibility(View.INVISIBLE);
                                notasks.setVisibility(View.VISIBLE);
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
               // Log.i("driverId", driverId);
                String query = "select * from task where vehicalId='" + vehicalId + "' AND taskPrevious='-1' AND (taskStatus='0' OR taskStatus='2') AND taskDate='"+date+"'";
                Log.i("querylogingFORNEXT",query);
                params.put("query", query);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(stringRequest1);

    }
    public void  map(View view){
        ongoing();
        Intent afterLogin = new Intent(getApplicationContext(), MapActivity.class);
        //afterLogin.putExtra("driverId", driverId);
        afterLogin.putExtra("taskOriginName",taskOriginName);
        afterLogin.putExtra("taskOriginLat",taskOriginLat);
        afterLogin.putExtra("taskOriginLon",taskOriginLon);
        afterLogin.putExtra("taskDesName",taskDesName);
        afterLogin.putExtra("taskDesLat",taskDesLat);
        afterLogin.putExtra("taskDesLon",taskDesLon);
        afterLogin.putExtra("taskids",taskIds);
        afterLogin.putExtra("taskNext",nextTask);
        afterLogin.putExtra("PredriverFname",PredriverFname);
        afterLogin.putExtra("PredriverLname",PredriverLname);
        afterLogin.putExtra("PredriverId",PredriverId);
        afterLogin.putExtra("companyId",companyId);
        afterLogin.putExtra("vehicalId",vehicalId);
        afterLogin.putExtra("vehicalLicenPlate1",vehicalLicenPlate1);
        afterLogin.putExtra("vehicalLicenPlate2",vehicalLicenPlate2);
        afterLogin.putExtra("vehicalLicenPlate3",vehicalLicenPlate3);
        afterLogin.putExtra("taskStartTime",taskStartTime);
        afterLogin.putExtra("taskEndTime",taskEndTime);
        Log.i("testingmapfunc",vehicalId);
        Log.i("testingmapfuncING",companyId);
        Log.i("testingmapfuncIN",PredriverLname);
        afterLogin.putExtra("totaldistance",Integer.toString(totalDistance));
        afterLogin.putExtra("totalETA",Integer.toString(totalEta));
        afterLogin.putExtra("DifferentStop",DifferentStop);
        afterLogin.putExtra("stopLat",stopLat);
        afterLogin.putExtra("stopLon",stopLon);
        Log.i("taskOriginNameTask",taskOriginLat);
        // Log.i("taskids",taskIds);
        afterLogin.putExtra("taskPrevious",taskPrevious);
        Log.i("taskIds_tasks",taskIds);
        startActivity(afterLogin);
    }
    public void getDataForFirebase(final String taskId){
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST,
                Constants.ROOT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {


                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.i("responrdetails3", jsonObject.toString());
                            String status = jsonObject.getString("status").toString();
                            if (status=="true") {
                               // Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                                String result = jsonObject.getString("result");
                                Log.i("testinggetDataFor","working");
                                JSONObject resultobject = new JSONObject(result);
                                companyId = resultobject.getString("companyId");
                                PredriverFname = resultobject.getString("driverFname");
                                PredriverLname = resultobject.getString("driverLname");
                                PredriverId=resultobject.getString("driverId");
                                vehicalId= resultobject.getString("vehicalId");
                                vehicalLicenPlate1= resultobject.getString("vehicalLicenPlate1");
                                vehicalLicenPlate2= resultobject.getString("vehicalLicenPlate2");
                                vehicalLicenPlate3= resultobject.getString("vehicalLicenPlate3");
                                taskStartTime= resultobject.getString("taskStartTime");
                                taskEndTime= resultobject.getString("taskEndTime");
                                //addingDataToFirebase();
                            }else {

                             //   Toast.makeText(getApplicationContext(), "SOME thing is wrong", Toast.LENGTH_LONG).show();
                                accept.setVisibility(View.INVISIBLE);
                                //startendview.setVisibility(View.INVISIBLE);
                                notasks.setVisibility(View.VISIBLE);
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
                String query = "select * from task t  JOIN driver dr on dr.driverId=t.driverId join vehical v on v.vehicalId=t.vehicalId where t.taskId='"+taskId+"'";
                Log.i("queryloging",query);
                params.put("query", query);
                return params;
            }
        };
        RequestQueue requestQueue2 = Volley.newRequestQueue(this);
        requestQueue2.add(stringRequest2);
    }
    //public void addingDataToFirebase(){
//    onlineDriver=database.getReference("onGoingTask/"+companyId+"/"+taskIds);
//    onlineDriver.child("driverFname").setValue( driverFname);
//    onlineDriver.child("driverLname").setValue( driverLname);
//    onlineDriver.child("driverId").setValue( driverId);
//    onlineDriver.child("driverFname").setValue( driverFname);
//    onlineDriver.child("endLocName").setValue(taskDesName);
//    onlineDriver.child("endLat").setValue(taskDesLat);
//    onlineDriver.child("endLon").setValue(taskDesLon);
//    onlineDriver.child("startLocName").setValue(taskOriginName);
//    onlineDriver.child("startLat").setValue(taskOriginLat);
//    onlineDriver.child("startLon").setValue(taskOriginLon);
//    onlineDriver.child("vehicalId").setValue(vehicalId);
//    onlineDriver.child("companyId").setValue(companyId);
//    onlineDriver.child("taskIds").setValue(taskIds);
//
//
//
//}
    @Override
    public void onBackPressed() {
        // Do Here what ever you want do on back press;
    }
    public void get(){
        Log.i("responrdetails4","working");
        Log.i("responrdetails4",taskIds);
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST,
                Constants.OBJECT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            Log.i("responrdetails4", jsonObject.toString());
                            String status = jsonObject.getString("status").toString();
                            if(status.equals("true")) {
                               // Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                                JSONArray ar = jsonObject.getJSONArray("result");
                                Log.i("testingDistances",Integer.toString(ar.length()));
                                allStops=ar.length();
                                //working
                                for (int i = 0; i < ar.length(); i++) {
                                    JSONObject jo = (JSONObject) ar.get(i);
                                    Log.i("responrdetails4", jo.getString("stopName"));
                                    stopArray.add( jo.getString("stopName"));
                                }
                                mylistview.setAdapter(arrayAdapter);
                                //if there is noly one stop
                                if (ar.length()==1){
                                    JSONObject jo = (JSONObject) ar.get(0);
                                    stopLon=jo.getString("stopLon");
                                    stopLat=jo.getString("stopLat");
                                    Log.i("testingDistances",stopLat);
                                    Log.i("testingDistances",stopLon);
                                    Log.i("testingDistances",taskOriginLat);
                                    Log.i("testingDistances",taskOriginLon);
                                    gettingDistanceandeta(taskOriginLat,taskOriginLon,stopLat,stopLon);
                                    gettingDistanceandeta(stopLat,stopLon,taskDesLat,taskDesLon);
                                    Log.i("testingDistances",Integer.toString(totalDistance)+ " 1");
                                    progressDialog.dismiss();
                                    Log.i("responrdetails5","working");
                                }else {//if there is more than one stop
                                    JSONObject jo = (JSONObject) ar.get(0);
                                    prestopLon=jo.getString("stopLon");
                                    prestopLat=jo.getString("stopLat");
                                    gettingDistanceandeta(taskOriginLat,taskOriginLon,prestopLat,prestopLon);
                                    progressDialog.dismiss();
                                    for (int i = 1; i <= ar.length(); i++) {
                                        jo = (JSONObject) ar.get(i);
                                        Log.i("responrdetails4", jo.getString("stopLon"));
                                        stopLon=jo.getString("stopLon");
                                        stopLat=jo.getString("stopLat");
                                        gettingDistanceandeta(prestopLat,prestopLon,stopLat,stopLon);
                                        prestopLon=stopLon;
                                        prestopLat=stopLat;
                                    }
                                    gettingDistanceandeta(prestopLat,prestopLon,taskDesLat,taskDesLon);
                                    Log.i("testingDist1",Integer.toString(totalDistance)+ " 2");
                                    progressDialog.dismiss();
                                    Log.i("responrdetails5","working");
                                    compare();
                                }
                            }else{
                                //there there are no stops
                                gettingDistanceandeta(taskOriginLat,taskOriginLon,taskDesLat,taskDesLon);
                                Log.i("testingDistances",Integer.toString(totalDistance)+ " 0");
                                DifferentStop="false";
                                progressDialog.dismiss();
                            }
                            progressDialog.dismiss();
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
               // Log.i("driverId", driverId);
                String query = "select * from stop t   where t.taskId='"+taskIds+"'";
                Log.i("queryloging",query);
                params.put("query", query);
                return params;
            }
        };
        RequestQueue requestQueue2 = Volley.newRequestQueue(this);
        requestQueue2.add(stringRequest2);
    }
    public void navigation(View view){
        Intent Login = new Intent(getApplicationContext(), Navigation.class);
        startActivity(Login);
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
                                distance = Integer.parseInt(jsonObject.getString("distance"));
                                eta = Integer.parseInt(jsonObject.getString("time"));
                                Log.i("testinggetDataFordistan",Integer.toString(distance));
                                Log.i("testinggetDataFordistan",Integer.toString(eta));
                                gotresult="true";
                                totalDistance += distance;
                                totalEta += eta;
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
    public void compare(){
        progressDialog.setMessage("Checking Details");
        progressDialog.show();
        Log.i("responrdetails5","working");
        Log.i("responrdetails5",taskIds);
        StringRequest stringRequest2 = new StringRequest(Request.Method.POST,
                Constants.OBJECT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            progressDialog.dismiss();
                            JSONObject jsonObject = new JSONObject(response);
                            Log.i("responrdetails5", jsonObject.toString());
                            String status = jsonObject.getString("status").toString();
                            if (status.equals("true")) {
                                // Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
                                JSONArray active = jsonObject.getJSONArray("result");
                                Log.i("testingDistances", Integer.toString(active.length()));
                                activeStops=active.length();
                                Log.i("responrdetails5",Integer.toString(allStops));
                                Log.i("responrdetails5",Integer.toString(allStops));
                                if (activeStops!=allStops){
                                    JSONObject jo = (JSONObject) active.get(0);
                                    stopLon=jo.getString("stopLon");
                                    stopLat=jo.getString("stopLat");
                                    stopName=jo.getString("stopName");
                                    Log.i("stopName",stopName);
                                    DifferentStop="false";
                                }else{
                                    DifferentStop="false";
                                }
                                if (activeStops==0){
                                    DifferentStop="false";
                                }
                            }else{
                                DifferentStop="end";
                            }
                        }catch(JSONException e){
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
                // Log.i("driverId", driverId);
                String query = "select * from stop t   where t.taskId='"+taskIds+"' AND stopStatus='0'";
                Log.i("queryloging5",query);
                params.put("query", query);
                return params;

            }
        };

        RequestQueue requestQueue2 = Volley.newRequestQueue(this);
        requestQueue2.add(stringRequest2);
    }
    public void ongoing() {
        Log.i("MapsActivity", "working 10");
//        Log.i("testingStage","2");
//        Log.i("delete",taskids);
//        Log.i("delete",companyId);
        // ending="true";
        Log.i("testingpara", "update task set taskStatus='2' where taskId='" + taskIds + "';");
        StringRequest updatetaskprevious = new StringRequest(Request.Method.POST,
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
                String query = "update task set taskStatus='2' where taskId='" + taskIds + "'";
                Log.i("queryloging2", query);
                params.put("query", query);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        requestQueue.add(updatetaskprevious);
    }
}
