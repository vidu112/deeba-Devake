package com.example.testingcustomerapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.android.libraries.places.api.net.PlacesClient;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
public class MapActivityListView extends AppCompatActivity implements OnMapReadyCallback {
    private static final String TAG = "MapActivity";
    LocationListener locationListener;
    LocationManager locationManager;
    Query query;
    DatabaseReference reference,onlineFreeLancer;
    private GoogleMap mMap;
    ListView listview1,listView2,listView3;
    LinearLayout slider;
    Location currentLocation;
    ImageView Arrow,drop_pin,CurrentLoc;
    EditText start,end;
    Button BookNow;
    String ArrowAction,currentPlaceSelection;
    FetchPlaceRequest request;
    MarkerOptions startLoc,endLoc;
    int AUTOCOMPLETE_REQUEST_CODE = 1;
    boolean DisableLocation=false;
    private ProgressDialog progressDialog;
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG);

    VehicalSelectionAdapter adapter1,adapter2,adapter3;
//    private ArrayList<String> mNames= new ArrayList<>();
//    private ArrayList<String>mImageUrls=new ArrayList<>();
     ArrayList<VehicalSelection> sectionList1,sectionList2,sectionList3;
    VehicalSelection output;
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
    public void onMapReady (GoogleMap googleMap){
        mMap = googleMap;
        mMap.setOnCameraMoveListener(new GoogleMap.OnCameraMoveListener() {
            @Override
            public void onCameraMove() {
                drop_pin.setImageResource(R.drawable.drop_pin);
                DisableLocation=true;
            }
        });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {
                Log.i(TAG,mMap.getCameraPosition().target.toString());
                drop_pin.setImageResource(R.drawable.drop_pin_stationary);
                try {
                    String cityName=getCityName(mMap.getCameraPosition().target);
                    start.setText(cityName);
                    LatLng startLoccoordinates=mMap.getCameraPosition().target;
                    startLoc =new MarkerOptions().position(startLoccoordinates).title("placeOne");
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        });

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reference = FirebaseDatabase.getInstance().getReference();
        setContentView(R.layout.activity_map);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Checking Details");
        progressDialog.show();

        BookNow=(Button)findViewById(R.id.book_now);
        BookNow.setBackgroundColor(Color.parseColor("#808080"));
        BookNow.setEnabled(false);
        start=(EditText)findViewById(R.id.start_loc);
        end=(EditText)findViewById(R.id.end_loc);
        drop_pin=(ImageView)findViewById(R.id.drop_pin);
        CurrentLoc=findViewById(R.id.currentLocation);
            Places.initialize(getApplicationContext(), "AIzaSyAGFIvdRAqQs_gmmlAuMOdXGVAka_O5DOc");
            PlacesClient placesClient = Places.createClient(this);

//        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
//            Place place = response.getPlace();
//            Log.i(TAG, "Place found: " + place.getName());
//        }).addOnFailureListener((exception) -> {
//            if (exception instanceof ApiException) {
//                ApiException apiException = (ApiException) exception;
//                int statusCode = apiException.getStatusCode();
//                // Handle error with given status code.
//                Log.e(TAG, "Place not found: " + exception.getMessage());
//            }
//        });
// Start the autocomplete intent.
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutocompleteRoadNames();
                currentPlaceSelection="startText";
            }
        });
        end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AutocompleteRoadNames();
                currentPlaceSelection="endText";
            }
        });
//        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);
//
//
//        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME));
//
//
//        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(Place place) {
//                // TODO: Get info about the selected place.
//                Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
//            }
//
//            @Override
//            public void onError(Status status) {
//                // TODO: Handle the error.
//                Log.i(TAG, "An error occurred: " + status);
//            }
//        });
        sectionList1 = new ArrayList<>();
        sectionList2 = new ArrayList<>();
        sectionList3 = new ArrayList<>();
        listview1=(ListView)findViewById(R.id.listview1);
        listView2=(ListView)findViewById(R.id.listview2);
        listView3=(ListView)findViewById(R.id.listview3);
        adapter1 = new VehicalSelectionAdapter(this, R.layout.layout_listitem, sectionList1);
        adapter2 = new VehicalSelectionAdapter(this, R.layout.layout_listitem, sectionList2);
        adapter3 = new VehicalSelectionAdapter(this, R.layout.layout_listitem, sectionList3);
        slider=(LinearLayout)findViewById(R.id.slide_bar);
        listView3.setVisibility(View.GONE);
        listview1.setVisibility(View.GONE);
        listView2.setVisibility(View.GONE);
        Arrow=(ImageView)findViewById(R.id.arrow);
        ArrowAction="Category";
        Arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if (ArrowAction.equals("Map")) {
                   listView3.setVisibility(View.GONE);
                   listview1.setVisibility(View.GONE);
                   listView2.setVisibility(View.GONE);
                   ArrowAction = "Category";
               }else if (ArrowAction.equals("Category")) {
                   VehicalSelectionGrid("0");
                    ArrowAction="Map";
            }
               if (ArrowAction.equals("CategoryPre")){
                   VehicalSelectionGrid("0");
                   ArrowAction="Map";
               }
            }
        });
        MapFragment mapFragment = (MapFragment) getFragmentManager()
                .findFragmentById(R.id.mapNearBy);
        mapFragment.getMapAsync(this);
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                progressDialog.dismiss();
                currentLocation=location;
                //gettingdata(location);
                Log.i("testinglocation", location.toString());
                if(!DisableLocation){
                    try {
                        mMap.clear();
                        LatLng myCurrentcoordinates = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myCurrentcoordinates, 19));
                        startLoc =new MarkerOptions().position(myCurrentcoordinates).title("placeOne");
                    } catch(Exception e){
                        e.printStackTrace();
                    }
                }
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
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // ask for permission
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            } else {
                //we have permission
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
            }
        }
        listview1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    output=(VehicalSelection)parent.getItemAtPosition(position);
                if (output.getTruckChild().equals("0")) {
                    AfterSelection(output);
                }else {
                    Toast.makeText(getApplicationContext(), output.getPrice(), Toast.LENGTH_LONG).show();
                    VehicalSelectionGrid(output.getTruckChild());
                    ArrowAction = "CategoryPre";
                }
            }
        });
        listView2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                output=(VehicalSelection)parent.getItemAtPosition(position);
                if (output.getTruckChild().equals("0")) {
                    AfterSelection(output);
                }else {
                    Toast.makeText(getApplicationContext(),  output.getPrice(), Toast.LENGTH_LONG).show();
                    VehicalSelectionGrid(output.getTruckChild());
                    ArrowAction = "CategoryPre";
                }
            }
        });
        listView3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                output=(VehicalSelection)parent.getItemAtPosition(position);
                if (output.getTruckChild().equals("0")) {
                    AfterSelection(output);
                }else {
                    Toast.makeText(getApplicationContext(),  output.getPrice(), Toast.LENGTH_LONG).show();
                    VehicalSelectionGrid(output.getTruckChild());
                    ArrowAction = "CategoryPre";
                }
            }
        });
    }
    public void VehicalSelectionGrid(final String ChildNumber){
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
                                sectionList1.clear();
                                sectionList2.clear();
                                sectionList3.clear();
                                listView3.setVisibility(View.VISIBLE);
                                listView2.setVisibility(View.VISIBLE);
                                listview1.setVisibility(View.VISIBLE);
                                if (ar.length()==1){
                                    listView2.setVisibility(View.GONE);
                                    listView3.setVisibility(View.GONE);
                                }else if (ar.length()==2){
                                    listView3.setVisibility(View.GONE);
                                }
                                //calculating how long each list is from 3 lists sectionList1,sectionList2,sectionList3
                                int remainder=ar.length() % 3;
                                int rep=(ar.length()-remainder)/3;
                                int c=0;
                                //Adding the data to lists
                                for (int i = 0; i < rep; i++) {
                                    JSONObject jo = (JSONObject) ar.get(i);
                                    sectionList1.add(new VehicalSelection(jo.getString("truckSelectionName"),jo.getString("imageUrl"),jo.getString("truckChild"),jo.getString("truckPrice")));
                                    VehicalSelection test=sectionList1.get(c);
                                    Log.i("responrdetails1", test.getName());
                                    c++;
                                }
                                c=0;
                                for (int i = rep; i < rep*2; i++) {
                                    JSONObject jo = (JSONObject) ar.get(i);
                                    Log.i("responrdetails2", jo.getString("truckSelectionName"));
                                    sectionList2.add(new VehicalSelection(jo.getString("truckSelectionName"),jo.getString("imageUrl"),jo.getString("truckChild"),jo.getString("truckPrice")));
                                    VehicalSelection test=sectionList2.get(c);
                                    Log.i("responrdetails2", test.getName());
                                    c++;
                                }
                                c=0;
                                for (int i = rep*2; i < rep*3; i++) {
                                    JSONObject jo = (JSONObject) ar.get(i);
                                    Log.i("responrdetails3", jo.getString("truckSelectionName"));
                                    sectionList3.add(new VehicalSelection(jo.getString("truckSelectionName"),jo.getString("imageUrl"),jo.getString("truckChild"),jo.getString("truckPrice")));
                                    VehicalSelection test=sectionList3.get(c);
                                    Log.i("responrdetails3", test.getName());
                                    c++;
                                }
                                // adding the data into if there is a remainder
                                if ((remainder==1)&&(ar.length()==1)){
                                    listview1.setVisibility(View.VISIBLE);
                                    JSONObject jo = (JSONObject) ar.get(ar.length()-1);
                                    sectionList1.add(new VehicalSelection(jo.getString("truckSelectionName"),jo.getString("imageUrl"),jo.getString("truckChild"),jo.getString("truckPrice")));
                                }else if (remainder==1){
                                    JSONObject jo = (JSONObject) ar.get(ar.length()-1);
                                    sectionList2.add(new VehicalSelection(jo.getString("truckSelectionName"),jo.getString("imageUrl"),jo.getString("truckChild"),jo.getString("truckPrice")));
                                }
                                if (remainder==2){
                                    JSONObject jo = (JSONObject) ar.get(ar.length()-1);
                                    Log.i("testing_remainder",jo.getString("truckSelectionName"));
                                    sectionList2.add(new VehicalSelection(jo.getString("truckSelectionName"),jo.getString("imageUrl"),jo.getString("truckChild"),jo.getString("truckPrice")));

                                    jo = (JSONObject) ar.get(ar.length()-2);
                                    Log.i("testing_remainder",jo.getString("truckSelectionName"));
                                    sectionList1.add(new VehicalSelection(jo.getString("truckSelectionName"),jo.getString("imageUrl"),jo.getString("truckChild"),jo.getString("truckPrice")));
                                }
                                listview1.setAdapter(adapter1);
                                listView2.setAdapter(adapter2);
                                listView3.setAdapter(adapter3);
                            }else{
                                // there are no stops
                                Toast.makeText(getApplicationContext(), "Sorry will be coming soon", Toast.LENGTH_LONG).show();
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
                String query = "select * from truck_selction where truckParent='"+ChildNumber+"'";
                Log.i("queryloging",query);
                params.put("query", query);
                return params;
            }
        };
        RequestQueue requestQueue2 = Volley.newRequestQueue(this);
        requestQueue2.add(stringRequest2);
    }
    public void AfterSelection(VehicalSelection selection){
        sectionList1.clear();
        sectionList2.clear();
        sectionList3.clear();
        sectionList1.add(selection);
        listview1.setAdapter(adapter1);
        listView2.setVisibility(View.GONE);
        listView3.setVisibility(View.GONE);
        BookNow.setBackgroundColor(Color.parseColor("#007fa3"));
        BookNow.setEnabled(true);
    }
    private String getCityName(LatLng coordinates)  {
        String cityName="";
        Geocoder geocoder =new Geocoder(MapActivityListView.this, Locale.getDefault());
        try {
            List<Address> addresses=geocoder.getFromLocation(coordinates.latitude,coordinates.longitude,1);
            Log.i("addresses",addresses.toString());
            cityName=addresses.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return cityName;
    }
    /**
     * Override the activity's onActivityResult(), check the request code, and
     * do something with the returned place data (in this example it's place name and place ID).
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = Autocomplete.getPlaceFromIntent(data);
                Log.i(TAG, "Place: " + place.toString());
                Log.i(TAG, "Place: " + place.getName() + ", " + place.getLatLng());
                request = FetchPlaceRequest.newInstance(place.getId(), fields);
                if (currentPlaceSelection.equals("startText")) {
                    start.setText(place.getName());
                }else{
                    end.setText(place.getName());
                }
                try {
                    mMap.clear();
                    LatLng myCurrentcoordinates = place.getLatLng();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myCurrentcoordinates, 19));
                    startLoc =new MarkerOptions().position(myCurrentcoordinates).title("placeOne");
                } catch(Exception e){
                    e.printStackTrace();
                }
            } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                // TODO: Handle the error.
                Status status = Autocomplete.getStatusFromIntent(data);
                Log.i("STAUS_message", status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }
    public void AutocompleteRoadNames(){
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.OVERLAY, fields)
                .build(this);
        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
    }
    public void onCameraIdle() {
        LatLng midLatLng = mMap.getCameraPosition().target;
        String cityName=getCityName(midLatLng);
        start.setText(cityName);
    }
    public void BookNowButton(View view){
        if (start.getText().toString().isEmpty()){
            start.setError("pls fill this");
        }else if (end.getText().toString().isEmpty()){
            end.setError("pls fill this");
        }else{
            gettingdata(currentLocation);
        }

    }
    public void GetCurrentLocation(View view){
        DisableLocation=false;
    }
    public void gettingdata(final Location location){
        query = reference.child("onlineFreeLancer").child("VHT001").orderByChild("lat").startAt(Double.toString(location.getLatitude()-0.02)).endAt(Double.toString(location.getLatitude()+0.02));
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot issue : dataSnapshot.getChildren()) {
                        if (issue.child("status").getValue().toString().equals("0")) {
                            Log.i("testingvalue", issue.child("lon").getValue().toString());
                            String Lon = issue.child("lon").getValue().toString();
                            if ((Double.parseDouble(Lon) > location.getLongitude() - 0.2) && (Double.parseDouble(Lon) < location.getLongitude() + 0.2)) {
                                onlineFreeLancer=issue.getRef();
                                onlineFreeLancer.child("status").setValue("1");
                                Log.i("testingvalue", "Working status change 1");
                                break;
                            }
                        }
                    }
                    // do something with the individual "issues"
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}