package com.example.marcos.smartparking;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {

    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        Button parkingButton = (Button)findViewById(R.id.parkingButton);
        parkingButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, MainParkingActivity.class);
                startActivity(intent);
        }
        });

        Button storesButton = (Button)findViewById(R.id.storesButton);
        storesButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, StoresActivity.class);
                startActivity(intent);
            }
        });

        Button availabilityButton = (Button)findViewById(R.id.availabilityButton);
        availabilityButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, AvailabilityActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStop() {
        super.onStop();
        stopRequestingLocationUpdates();
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestLocationUpdates();
    }

    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private void requestLocationUpdates() {
        stopRequestingLocationUpdates();
        if (checkLocationPermission()) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    10000, 0, locationListener);
        }
    }

    private void stopRequestingLocationUpdates() {
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }
    }

    private LocationListener locationListener = new LocationListener(){
        public void onLocationChanged(Location location) {
            Log.d("LocationListener", "Lat " +   location.getLatitude() + " Long " + location.getLongitude());
        }
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

}