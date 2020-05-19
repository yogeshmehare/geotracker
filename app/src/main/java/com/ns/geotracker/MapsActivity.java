package com.ns.geotracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import android.content.pm.PackageManager;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.GoogleMap;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import android.widget.Button;
import android.Manifest;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        ReverseGeo.OnTaskComplete{

    private GoogleMap mMap;
    private static final int MY_PERMISSIONS_REQUEST_LOCATION = 1;
    private Button button;
    private TextView textview;
    private boolean addressRequest;
    private DatabaseReference dbr;


//Create a member variable of the FusedLocationProviderClient type//

    private FusedLocationProviderClient mFusedLocationClient;
    private LocationCallback mLocationCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        button = findViewById(R.id.button);
        Button continuebutton = findViewById(R.id.button00);
        textview = findViewById(R.id.textview);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.map, mapFragment).commit();
        mapFragment.getMapAsync(this);

        dbr = FirebaseDatabase.getInstance().getReference();

        continuebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
                Intent launchactivity = new Intent(MapsActivity.this,Launch.class);
                startActivity(launchactivity);
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             if (!addressRequest) {
                    getAddress();

                }
            }
        });


        mLocationCallback = new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (addressRequest) {
                     new ReverseGeo(MapsActivity.this, MapsActivity.this)
                    .execute(locationResult.getLastLocation());
                }
            }
        };
    }

    private void sendMessage(){
        String loc = textview.getText().toString();
        InstantMessage chat = new InstantMessage(loc);
        dbr.child("messages").push().setValue(chat);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

//Inflate the maps_menu resource//

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.maps_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.normal:

//Use setMapType to change the map style based on the user’s selection//

                mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                return true;
            case R.id.hybrid:
                mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                return true;
            case R.id.terrain:
                mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                return true;
            case R.id.satellite:
                mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mMap.setMyLocationEnabled(true);
        }
    }

    public void onConnected(Bundle bundle) {
//To do//
    }

    @Override
    public void onConnectionSuspended(int i) {

    }


//Implement getAddress//

    private void getAddress() {
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]
                            {Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_LOCATION);
        } else {
            addressRequest = true;

//Request location updates//

            mFusedLocationClient.requestLocationUpdates
                    (getLocationRequest(),
                            mLocationCallback,
                            null);

//If the geocoder retrieves an address, then display this address in the TextView//

            textview.setText(getString(R.string.address_text));

        }
    }

//Specify the requirements for your application's location requests//

    private LocationRequest getLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();

//Specify how often the app should receive location updates, in milliseconds//

        locationRequest.setInterval(10000);
        return locationRequest;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//If the permission request has been granted, then call getAddress//

                    getAddress();
                } else {
                    Toast.makeText(this,
                            R.string.location_permission_denied,
                            Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }


    @Override
    public void onTaskComplete(String result) {
        if (addressRequest) {
            textview.setText(getString(R.string.address_text, result));
        }
    }
}

