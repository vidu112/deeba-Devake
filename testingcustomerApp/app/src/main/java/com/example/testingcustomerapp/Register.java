package com.example.testingcustomerapp;


import android.content.Intent;
import android.graphics.Color;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText name, email, password, comfirmPassword;
    String correct,phoneNumber;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        name = (EditText) findViewById(R.id.NameReg);
        email = (EditText) findViewById(R.id.EmailReg);
        password = (EditText) findViewById(R.id.PaswordReg);
        comfirmPassword = (EditText) findViewById(R.id.CPasswordReg);
        phoneNumber=getIntent().getStringExtra("phoneNumber");
    }
    public void Register(View view) {
        correct="true";
        Log.i("testingRegister",name.getText().toString());

        if (name.getText().toString().equals("")) {
            name.setText("");
            name.setHintTextColor(Color.RED);
            name.setHint("Please enter your name");
            correct="false";
            Log.i("testingRegister","working1");
        } if (email.getText().toString().equals("")){
            email.setText("");
            email.setHintTextColor(Color.RED);
            email.setHint("Please enter your email");
            correct="false";
            Log.i("testingRegister","working2");
        } if (password.getText().toString().equals("")) {
            password.setText("");
            password.setHintTextColor(Color.RED);
            password.setHint("Please enter your password");
            correct="false";
            Log.i("testingRegister","working3");
        }
        if (comfirmPassword.getText().toString().equals("")) {
            comfirmPassword.setText("");
            comfirmPassword.setHintTextColor(Color.RED);
            comfirmPassword.setHint("Please enter your password");
            correct="false";
            Log.i("testingRegister","working4");
        }
        if (password.getText().toString().equals(comfirmPassword.getText().toString())) {
        }else{
            comfirmPassword.setText("");
            comfirmPassword.setHintTextColor(Color.RED);
            comfirmPassword.setHint("Passwords don't match");
            correct = "false";
            Log.i("testingRegister","working5");
        }
        if (correct.equals("true")){
            StringRequest stringRequest=new StringRequest(Request.Method.POST,
                    Constants.update,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject jsonObject=new JSONObject(response);
                                Log.i("responrdetails1",jsonObject.toString());
                                String status=jsonObject.getString("status").toString();
                                if(status.equals("true")){
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
                            Toast.makeText(getApplicationContext(),error.getMessage(),Toast.LENGTH_LONG).show();
                        }
                    }){
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String,String> params = new HashMap<>();
                    String query="INSERT INTO customer(customerID,CustName,CustEmail,CustPhoneNumber,CustPassword,CustStatus) " +
                            "VALUES ('"+getID()+"','"+name.getText()+"','"+email.getText()+"','"+phoneNumber+"','"+password.getText()+"','0')";
                    Log.i("queryloging",query);
                    params.put("query",query);
                    return params;
                }
            };
            RequestQueue requestQueue= Volley.newRequestQueue(this);
            requestQueue.add(stringRequest);
        }
        Intent welcome = new Intent(getApplicationContext(), MapActivityListView.class);
        welcome.putExtra("phoneNumber",password.getText().toString());
        startActivity(welcome);
    }
    public String getID(){
        Date dte=new Date();
        long milliSeconds = dte.getTime();
        String strLong = Long.toString(milliSeconds);
        return strLong;
    }
}
