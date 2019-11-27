package com.example.testingcustomerapp;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
public class MainActivity extends AppCompatActivity {
    LocationListener locationListener;
    LocationManager locationManager;
    Query query;
    DatabaseReference reference;
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
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        reference = FirebaseDatabase.getInstance().getReference();
        gettingdata();
//        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
//        locationListener=new LocationListener() {
//            @Override
//            public void onLocationChanged(final Location location) {
//                Log.i("Location_started","Yes working");
//                // currentLOc = new MarkerOptions().position(new LatLng(6.8974681,79.8586554)).title("Mardiwela");//
//                gettingdata(location);
//            }
//            @Override
//            public void onStatusChanged(String provider, int status, Bundle extras) {
//            }
//            @Override
//            public void onProviderEnabled(String provider) {
//            }
//            @Override
//            public void onProviderDisabled(String provider) {
//            }
//        };

}
public void gettingdata(){
    query = reference.child("onlineFreeLancer").child("VHT001").orderByChild("lat").startAt(Double.toString(6.87293-0.02)).endAt(Double.toString(6.87293+0.02));
    query.addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            if (dataSnapshot.exists()) {
                // dataSnapshot is the "issue" node with all children with id 0
                for (DataSnapshot issue : dataSnapshot.getChildren()) {
                    Log.i("testingvalue",issue.child("lat").getValue().toString());
                }
                // do something with the individual "issues"
            }
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
    });
}
}