package com.deeba.deebadriver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
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

public class AllAvailableTasks extends AppCompatActivity {
    private static final String TAG = "ViewAllTask";
    String vehicalId,returndate;
    ListView mListView;
    TaskListAdapter tasklistadapter;
    ArrayList<TaskClass> Tasklist;
    private SharedPreferences mpreferences;
    private SharedPreferences.Editor mEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_available_tasks);
        Log.d(TAG,"onCreate: Started0");
        mListView=(ListView)findViewById(R.id.listview);
        //driverId = getIntent().getStringExtra("driverId");
        mpreferences= PreferenceManager.getDefaultSharedPreferences(this);
        mEditor=mpreferences.edit();
        vehicalId=mpreferences.getString(getString(R.string.vehicalId),"");
        final String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        Tasklist=new ArrayList<>();





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

                                Toast.makeText(getApplicationContext(), "Success", Toast.LENGTH_LONG).show();

                                JSONArray result = jsonObject.getJSONArray("result");
                                for (int i = 0; i < result.length(); i++) {

                                    JSONObject current = (JSONObject) result.get(i);
                                    String strDate  =current.getString("taskDate");




                                    Tasklist.add(new TaskClass(current.getString("taskOriginName"),current.getString("taskDesName"),returndate,current.getString("taskStatus"),current.getString("taskStartTime")));
                                }

                                updatelistview();

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

                String query = "select * from task where vehicalId= '"+vehicalId+"'AND taskStatus='0' AND taskDate='"+date+"' ";
                Log.i("queryloging",query);
                params.put("query", query);


                return params;

            }
        };

        RequestQueue requestQueue2 = Volley.newRequestQueue(this);
        requestQueue2.add(stringRequest2);



    }
    public void updatelistview(){
        tasklistadapter=new TaskListAdapter(this,R.layout.task_adapter_view,Tasklist);
        mListView.setAdapter(tasklistadapter);
    }
    public void navigation(View view){
        Intent Login = new Intent(getApplicationContext(), Navigation.class);
        startActivity(Login);
    }
}
