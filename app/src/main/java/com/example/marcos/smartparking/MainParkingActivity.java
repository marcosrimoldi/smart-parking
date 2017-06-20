package com.example.marcos.smartparking;

import android.Manifest;
import android.app.ActionBar;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.List;

public class MainParkingActivity extends AppCompatActivity {

    private LocationManager locationManager;
    private LocationPostService locationPostService;
    boolean mBounded;

    @Override
    protected void onCreate(Bundle savedInstanceState) throws SecurityException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_parking);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Button button = (Button) findViewById(R.id.startButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checkLocationPermission()) {
                    final EditText input = (EditText) findViewById(R.id.domainText);
                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            10000, 0, locationListener);

                    locationPostService.postStartParking(new ParkingDTO(input.getText().toString(),
                            getLastKnownLocation()));
                }
            }
        });

        final Button stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (locationManager != null) {
                    locationManager.removeUpdates(locationListener);
                }
                locationPostService.postStopParking();
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                //Intent myIntent = new Intent(getApplicationContext(), HomeActivity.class);
                //startActivityForResult(myIntent, 0);
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        Intent mIntent = new Intent(this, LocationPostService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    }

    ServiceConnection mConnection = new ServiceConnection() {

        public void onServiceDisconnected(ComponentName name) {
            //Toast.makeText(MainParkingActivity.this, "Service is disconnected", Toast.LENGTH_LONG).show();
            mBounded = false;
            locationPostService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            //Toast.makeText(MainParkingActivity.this, "Service is connected", Toast.LENGTH_LONG).show();
            mBounded = true;
            LocationPostService.LocalBinder mLocalBinder = (LocationPostService.LocalBinder)service;
            locationPostService = mLocalBinder.getServerInstance();
        }
    };

    @Override
    protected void onStop() {
        super.onStop();
        if(mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    }

    private LocationListener locationListener = new LocationListener(){
        public void onLocationChanged(Location location) {
            Log.d("LocationListener", "Lat " +   location.getLatitude() + " Long " + location.getLongitude());
            locationPostService.postLocation(new LocationDTO(Constants.userIDNumber, location));
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private Location getLastKnownLocation() throws SecurityException{
        List<String> providers = locationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            Location l = locationManager.getLastKnownLocation(provider);
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                // Found best last known location: %s", l);
                bestLocation = l;
            }
        }
        return bestLocation;
    }

}
