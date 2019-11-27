package com.deeba.deebadriver;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
public class Login extends AppCompatActivity {
    public static final String TAG="Login";
    private EditText vehical1,vehical2,vehical3;
    ConstraintLayout login_lay;
    private Button buttonLogin;
    private ProgressDialog progressDialog;
    private SharedPreferences mpreferences;
    private SharedPreferences.Editor mEditor;
    FirebaseDatabase database1;
    DatabaseReference onlineDriver,checking;
    String vehical1String,vehical2String,vehical3String,checked,plate1,plate2,plate3,vehicalId,companyId,vehicalType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.i("testinglogin","working1");
        database1=FirebaseDatabase.getInstance();

        mpreferences= PreferenceManager.getDefaultSharedPreferences(this);
        mEditor=mpreferences.edit();
        Log.i("test inglogin","working2");
        checkSharePreferences();
        Log.i("testinglogin","working6");
        login_lay=findViewById(R.id.login_layout);
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Do something after 5s = 5000ms
                login_lay.setVisibility(View.VISIBLE);
            }
        }, 3000);
        vehical1 =(EditText) findViewById(R.id.vehical_Licen_plate1);
        vehical2 =(EditText) findViewById(R.id.vehical_Licen_plate2);
        vehical3 =(EditText) findViewById(R.id.vehical_Licen_plate3);
        Log.i("testinglogin","working7");
        progressDialog = new ProgressDialog(this);
        mEditor.putString(getString(R.string.checked),"true");
    }
    public void Login(View view){
        vehical1String= vehical1.getText().toString();
        vehical2String= vehical2.getText().toString();
        vehical3String= vehical3.getText().toString();
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
                            Log.i(TAG+"responrdetails1",jsonObject.toString());
                            String status=jsonObject.getString("status").toString();
                            if(status.equals("true")){
                                // Toast.makeText(getApplicationContext(),"Success", Toast.LENGTH_LONG).show();
                                String result=jsonObject.getString("result");
                                final JSONObject resultobject=new JSONObject(result);
                                vehicalId=resultobject.getString("vehicalId");
                                companyId=resultobject.getString("companyId");
                                vehicalType=resultobject.getString("vehicalType");
                                Log.i(TAG+"vehicalType",vehicalType);
                                onlineDriver=database1.getReference("onGoingTask/"+companyId+"/tsk001");
                                checking=database1.getReference("onlineFleetDriver/"+companyId);
                                checking.orderByKey().equalTo(vehical1String+vehical2String+vehical3String).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        if(dataSnapshot.exists()) {

                                            //Key exists
                                            vehical1.setText("");
                                            vehical1.setHint("Already");
                                            vehical1.setHintTextColor(Color.parseColor("#ff0000"));
                                            vehical2.setText("");
                                            vehical2.setHint("Used");
                                            vehical2.setHintTextColor(Color.parseColor("#ff0000"));
                                            vehical3.setText("");
                                            // Object[] results=jsonObject.getJSONArray("result");
                                        } else {
                                            //Key does not exist

                                            onlineDriver.child("currentLag").setValue( "0");
                                            onlineDriver.child("currentLog").setValue( "0");
                                            onlineDriver.child("startLocName").setValue( "0");
                                            Log.i(TAG+"vehicalId",vehicalId);
                                            mEditor.putString(getString(R.string.vehicalId),vehicalId);
                                            mEditor.putString(getString(R.string.companyId),companyId);
                                            mEditor.putString(getString(R.string.plate1),vehical1String);
                                            mEditor.putString(getString(R.string.plate2),vehical2String);
                                            mEditor.putString(getString(R.string.plate3),vehical3String);
                                            mEditor.putString(getString(R.string.vehicalType),vehicalType);
                                            mEditor.putString(getString(R.string.checked),"true");
                                            mEditor.commit();
                                            Log.i(TAG+"piushapiusha1",mpreferences.getString(getString(R.string.vehicalId),""));
                                            Log.i(TAG+"piushapiusha",resultobject.toString());
                                            Intent LoginDriver = new Intent(getApplicationContext(), LoginDriver.class);
                                            startActivity(LoginDriver);
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });

                            }else{
                                // Toast.makeText(getApplicationContext(),"Fail", Toast.LENGTH_LONG).show();
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
                String query="select * from vehical where vehicalLicenPlate1='"+vehical1String + "' AND vehicalLicenPlate2='"+vehical2String+"' AND vehicalLicenPlate3='"+vehical3String+"'";
                Log.i("queryloging",query);
                params.put("query",query);
                return params;
            }
        };
        RequestQueue requestQueue=Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
    private void checkSharePreferences(){
        Log.i("testinglogin","working3");
        checked=mpreferences.getString(getString(R.string.checked),"false");
        Log.i("checked",checked);
        plate1=mpreferences.getString(getString(R.string.plate1),"");
        Log.i("testinglogin","working4");
        plate2=mpreferences.getString(getString(R.string.plate2),"");
        plate3=mpreferences.getString(getString(R.string.plate3),"");
        vehicalId=mpreferences.getString(getString(R.string.vehicalId),"");

        //vehicalId=mpreferences.getString(getString(R.string.vehicalId),"");
        Log.i("testinglogin",vehicalId);
        Log.i("testinglogin","working6");
        if (checked.equals("true")){
            Intent welcome = new Intent(getApplicationContext(), LoginDriver.class);
            startActivity(welcome);
        }
    }


}


