package com.example.marcos.smartparking;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class MainParkingActivity extends AppCompatActivity implements ParkingServiceCallbacks {

    private LocationManager locationManager;
    private LocationPostService locationPostService;
    boolean mBounded;
    ProgressDialog progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) throws SecurityException {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_parking);

        ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initProgressDialog();

        TextView statusField = (TextView) findViewById(R.id.parkingStatus);
        statusField.setText("");

        final Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setEnabled(false);
        startButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (checkLocationPermission()) {
                    final EditText input = (EditText) findViewById(R.id.domainText);
                    locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                            10000, 0, locationListener);

                    locationPostService.postStartParking(new ParkingDTO(input.getText().toString(),
                            getLastKnownLocation()));
                    progress.show();
                }
            }
        });

        final Button stopButton = (Button) findViewById(R.id.stopButton);
        stopButton.setEnabled(false);
        stopButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (locationManager != null) {
                    locationManager.removeUpdates(locationListener);
                }
                locationPostService.postStopParking();
                progress.show();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
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
            mBounded = false;
            locationPostService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded = true;
            LocationPostService.LocalBinder mLocalBinder = (LocationPostService.LocalBinder)service;
            locationPostService = mLocalBinder.getServerInstance();
            locationPostService.setCallbacks(MainParkingActivity.this);

            locationPostService.postGetCurrentParking();
            progress.show();
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

    @Override
    public void onStopParkingCallback(String result) {
        try {
            JSONObject jsonObj = new JSONObject(result);
            Long cost = Long.valueOf(jsonObj.get(Constants.STOP_PARKING_RESPONSE_PARAM).toString())/100;
            TextView costField = (TextView) findViewById(R.id.totalCost);
            costField.setText(getString(R.string.total_cost, cost));
            updateUIElements(Boolean.FALSE, Boolean.TRUE);
            progress.dismiss();

        } catch (JSONException e) {
            Toast.makeText(MainParkingActivity.this, "Servicio no disponible.", Toast.LENGTH_LONG).show();
        }
    }

    private void updateUIElements(Boolean isParkingRunning, Boolean showStopptedStatus) {
        final Button startButton = (Button) findViewById(R.id.startButton);
        final Button stopButton = (Button) findViewById(R.id.stopButton);

        startButton.setEnabled(!isParkingRunning);
        stopButton.setEnabled(isParkingRunning);
        TextView statusField = (TextView) findViewById(R.id.parkingStatus);

        String statusText = isParkingRunning ? getString(R.string.parking_active) :
                showStopptedStatus ? getString(R.string.parking_finished) : "";

        int statusColor = isParkingRunning ? getResources().getColor(R.color.success) : getResources().getColor(R.color.error);
        statusField.setTextColor(statusColor);
        statusField.setText(statusText);
    }

    @Override
    public void onGetParkingCallback(String result) {
        try {
            JSONObject jsonObj = new JSONObject(result);
            Boolean parkingIsRunning = Boolean.valueOf(jsonObj.get(Constants.GET_PARKING_RUNNING_PARAM).toString());
            final EditText input = (EditText) findViewById(R.id.domainText);

            if (parkingIsRunning) {
                String domain = jsonObj.get(Constants.GET_PARKING_DOMAIN_PARAM).toString();
                input.setText(domain);
            }
            else {
                input.setText("");
            }

            updateUIElements(parkingIsRunning, Boolean.FALSE);
            progress.dismiss();

        } catch (JSONException e) {
            Toast.makeText(MainParkingActivity.this, "Servicio no disponible.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onStartParkingCallback(String result) {
        updateUIElements(Boolean.TRUE, Boolean.TRUE);
        progress.dismiss();
    }

    private void initProgressDialog() {
        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.loading));
        progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progress.setCancelable(false);
    }
}
