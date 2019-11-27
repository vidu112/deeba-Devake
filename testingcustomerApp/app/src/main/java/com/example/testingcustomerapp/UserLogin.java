package com.example.testingcustomerapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class UserLogin extends AppCompatActivity {
EditText phoneNumber;
    private SharedPreferences mpreferences;
    private SharedPreferences.Editor mEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_login);
        phoneNumber=(EditText)findViewById(R.id.password);
        mpreferences= PreferenceManager.getDefaultSharedPreferences(this);
    }
    public void Login(View view){
        if ((phoneNumber.getText().toString().equals(""))||(phoneNumber.getText().toString().length()<10)||(phoneNumber.getText().toString().length()>10)) {
            phoneNumber.setText("");
            phoneNumber.setHintTextColor(Color.RED);
        }else {
            StringRequest stringRequest=new StringRequest(Request.Method.POST,
                    Constants.ROOT_URL,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject starting=new JSONObject(response);
                                Log.i("responrdetails1",starting.toString());
                                String status=starting.getString("status").toString();
                                if(status.equals("true")){
                                    String result = starting.getString("result");
                                    JSONObject resultob = new JSONObject(result);
                                    // Toast.makeText(getApplicationContext(), "Successes", Toast.LENGTH_LONG).show();
                                    Log.i("plsworkpls", "working");
                                    String CustName = resultob.getString("CustName");
                                    mEditor = mpreferences.edit();
                                    mEditor.putString(getString(R.string.phonenumber),phoneNumber.getText().toString());
                                    mEditor.commit();
                                    Intent welcome = new Intent(getApplicationContext(), Login.class);
                                    welcome.putExtra("phoneNumber",phoneNumber.getText().toString());
                                    welcome.putExtra("CustName",CustName);
                                    startActivity(welcome);
                                }else{
                                    Intent welcome = new Intent(getApplicationContext(), Register.class);
                                    welcome.putExtra("phoneNumber",phoneNumber.getText().toString());
                                    startActivity(welcome);
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

                            Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    String query="select * from customer where CustPhoneNumber='"+Integer.parseInt(phoneNumber.getText().toString()) + "' ";
                    Log.i("queryloging",query);
                    params.put("query",query);
                    return params;
                }
            };
            RequestQueue requestQueue= Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);

        }
    }
    public void register(View view){
        Log.i("phoneNumber",phoneNumber.getText().toString());
       if ((phoneNumber.getText().toString().equals(""))||(phoneNumber.getText().toString().length()<10)||(phoneNumber.getText().toString().length()>10)) {
           phoneNumber.setText("");
           phoneNumber.setHintTextColor(Color.RED);
       }else {
           StringRequest stringRequest=new StringRequest(Request.Method.POST,
                   Constants.ROOT_URL,
                   new Response.Listener<String>() {
                       @Override
                       public void onResponse(String response) {

                           try {
                               JSONObject jsonObject=new JSONObject(response);
                               Log.i("responrdetails1",jsonObject.toString());
                               String status=jsonObject.getString("status").toString();
                               if(status.equals("true")){
                                   phoneNumber.setText("");
                                   phoneNumber.setHintTextColor(Color.RED);
                                   phoneNumber.setHint("Number unavailable");
                               }else{
                                   Intent welcome = new Intent(getApplicationContext(), Register.class);
                                   welcome.putExtra("phoneNumber",phoneNumber.getText().toString());
                                   startActivity(welcome);

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

                           Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                       }
                   }){
               @Override
               protected Map<String, String> getParams() throws AuthFailureError {
                   Map<String,String> params = new HashMap<>();
                   String query="select * from customer where CustPhoneNumber='"+Integer.parseInt(phoneNumber.getText().toString()) + "' ";
                   Log.i("queryloging",query);
                   params.put("query",query);
                   return params;
               }
           };
           RequestQueue requestQueue= Volley.newRequestQueue(this);
           requestQueue.add(stringRequest);

       }
    }


}
