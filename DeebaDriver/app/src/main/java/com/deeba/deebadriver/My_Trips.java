package com.deeba.deebadriver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class My_Trips extends AppCompatActivity {
    private static final String TAG = "ViewAllTask";
    String vehicalId, returndate;
    ListView mListView;

    My_Trips_Adapter My_Trips_Adapter;
    ArrayList<My_Trip_class> Tasklist;
    private SharedPreferences mpreferences;
    private SharedPreferences.Editor mEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my__trips);

        mListView = (ListView) findViewById(R.id.my_trip_list);
        //driverId = getIntent().getStringExtra("driverId");
        mpreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mpreferences.edit();
        vehicalId = mpreferences.getString(getString(R.string.vehicalId), "");
        final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Tasklist = new ArrayList<>();

        Tasklist.add(new My_Trip_class("Matrix","Pizza hut","19/04/2019","10.22 am","11.15 km","350.00"));
        Tasklist.add(new My_Trip_class("Matrix","Pizza hut","19/04/2019","10.22 am","11.15 km","350.00"));
        Tasklist.add(new My_Trip_class("Matrix","Pizza hut","19/04/2019","10.22 am","11.15 km","350.00"));
        Tasklist.add(new My_Trip_class("Matrix","Pizza hut","19/04/2019","10.22 am","11.15 km","350.00"));
        Tasklist.add(new My_Trip_class("Matrix","Pizza hut","19/04/2019","10.22 am","11.15 km","350.00"));
//        StringRequest stringRequest2 = new StringRequest(Request.Method.POST,
//                Constants.OBJECT_URL,
//                new Response.Listener<String>() {
//                    @Override
//                    public void onResponse(String response) {
//
//
//                        try {
//                            JSONObject jsonObject = new JSONObject(response);
//                            Log.i("responrdetails4", jsonObject.toString());
//                            String status = jsonObject.getString("status").toString();
//                            if (status.equals("true")) {
//
//                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();
//
//                                JSONArray result = jsonObject.getJSONArray("result");
//                                for (int i = 0; i < result.length(); i++) {
//
//                                    JSONObject current = (JSONObject) result.get(i);
//                                    String strDate = current.getString("taskDate");
//
//                                    try {
//                                        Date date = new SimpleDateFormat("yyyy-MM-dd").parse(strDate);
//                                        returndate = new SimpleDateFormat("dd-MMM-yyyy").format(date);
//
//                                    } catch (ParseException e) {
//                                        // TODO Auto-generated catch block
//                                        e.printStackTrace();
//
//                                    }
//
//
//                                    Tasklist.add(new My_Trip_class(current.getString("taskOriginName"), current.getString("taskDesName"), returndate, current.getString("taskStatus"), current.getString("taskStartTime")));
//                                }
//
//                                updatelistview();
//
//                            }
//
//                        } catch (JSONException e) {
//                            e.printStackTrace();
//
//                        }
//                    }
//                },
//                new Response.ErrorListener() {
//                    @Override
//                    public void onErrorResponse(VolleyError error) {
//
//                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
//                    }
//                }) {
//            @Override
//            protected Map<String, String> getParams() throws AuthFailureError {
//                Map<String, String> params = new HashMap<>();
//
//                String query = "select * from task where vehicalId= '" + vehicalId + "'AND taskStatus='1' AND taskDate='" + date + "' ";
//                Log.i("queryloging", query);
//                params.put("query", query);
//
//
//                return params;
//
//            }
//        };
//
//        RequestQueue requestQueue2 = Volley.newRequestQueue(this);
//        requestQueue2.add(stringRequest2);

        updatelistview();
    }

    public void updatelistview() {
        My_Trips_Adapter = new My_Trips_Adapter(this, R.layout.my_trips_adapter, Tasklist);
        mListView.setAdapter(My_Trips_Adapter);
    }

    public void navigation(View view) {
        Intent Login = new Intent(getApplicationContext(), Navigation.class);
        startActivity(Login);
    }
}
