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
public class Login extends AppCompatActivity {
EditText password;
String phoneNumber;
    private SharedPreferences mpreferences;
    private SharedPreferences.Editor mEditor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        password=(EditText)findViewById(R.id.password);
        phoneNumber=getIntent().getStringExtra("phoneNumber");
        mpreferences= PreferenceManager.getDefaultSharedPreferences(this);

    }
    public void login(View view){
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
                                mEditor=mpreferences.edit();
                                mEditor.putString(getString(R.string.password),password.getText().toString());
                                mEditor.commit();
                                Intent welcome = new Intent(getApplicationContext(), MapActivityListView.class);
                                welcome.putExtra("phoneNumber",password.getText().toString());
                                startActivity(welcome);
                            }else{
                                password.setText("");
                                password.setHintTextColor(Color.RED);
                                password.setHint("Password incorrect");
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
                String query="select * from customer where CustPhoneNumber='"+Integer.parseInt(phoneNumber )+ "' AND  CustPassword='"+password.getText()+"'";
                Log.i("queryloging",query);
                params.put("query",query);
                return params;
            }
        };
        RequestQueue requestQueue= Volley.newRequestQueue(this);
        requestQueue.add(stringRequest);
    }
}
