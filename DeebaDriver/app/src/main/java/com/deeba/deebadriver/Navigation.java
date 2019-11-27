package com.deeba.deebadriver;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Navigation extends AppCompatActivity {
    FirebaseDatabase onlinefleet;
    DatabaseReference onlinefleetref;
    private SharedPreferences mpreferences;
    private SharedPreferences.Editor mEditor;
    String plate1,plate2,plate3,companyId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        mpreferences= PreferenceManager.getDefaultSharedPreferences(this);
        mEditor=mpreferences.edit();
        onlinefleet=FirebaseDatabase.getInstance();
        plate1=mpreferences.getString(getString(R.string.plate1),"");
        plate2=mpreferences.getString(getString(R.string.plate2),"");
        plate3=mpreferences.getString(getString(R.string.plate3),"");
        companyId=mpreferences.getString(getString(R.string.companyId),"");
        mEditor.commit();
        onlinefleetref=onlinefleet.getReference("onlineFleetDriver/"+companyId+"/"+plate1+"-"+plate2+"-"+plate3);

    }
    public void signout(View view){
        Intent serviceIntent = new Intent(this, ExampleService.class);
        stopService(serviceIntent);
        onlinefleetref.removeValue();
        mEditor=mpreferences.edit();
        mEditor.putString(getString(R.string.deleteonlineuser),"true");
        mEditor.commit();
        Intent Login = new Intent(getApplicationContext(), Login.class);
        startActivity(Login);
    }
    public void viewAll(View view){
        Intent viewTask = new Intent(getApplicationContext(), AllAvailableTasks.class);

        startActivity(viewTask);
    }
    public void history(View view){
        Intent history = new Intent(getApplicationContext(), History.class);

        startActivity(history);
    }
    public void Task(View view) {
        Intent afterLogin = new Intent(getApplicationContext(), Tasks.class);


        startActivity(afterLogin);
    }
}
