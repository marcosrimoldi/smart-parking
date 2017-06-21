package com.example.marcos.smartparking;

import android.content.ComponentName;
import android.content.Context;
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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.marcos.smartparking.domain.ReloadStation;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class StoresActivity extends AppCompatActivity implements ServiceCallbacks {

    private StoresService storesService;
    boolean mBounded;
    private LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stores);
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (checkLocationPermission()) {
            locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                    10000, 1, locationListener);
        }

    }

    private LocationListener locationListener = new LocationListener(){
        public void onLocationChanged(Location location) {}
        public void onProviderDisabled(String provider) {}
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

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
        Intent mIntent = new Intent(this, StoresService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mBounded) {
            unbindService(mConnection);
            mBounded = false;
            if (locationManager != null && locationListener != null) {
                locationManager.removeUpdates(locationListener);
            }
        }
    }

    ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
            mBounded = false;
            storesService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded = true;
            StoresService.LocalBinder mLocalBinder = (StoresService.LocalBinder)service;
            storesService = mLocalBinder.getServerInstance();
            storesService.setCallbacks(StoresActivity.this);

            if (checkLocationPermission()) {
                storesService.searchStores(new SearchStoreDTO(Constants.userIDNumber, getLastKnownLocation()));
            }
        }
    };

    @Override
    public void callback(String result) {
        List<ReloadStation> stores = new ArrayList<ReloadStation>();
        try {
            JSONArray jsonArray = new JSONArray(result);
            for (int i = 0; i < jsonArray.length(); i++) {
                try {
                    stores.add(new ReloadStation(jsonArray.getJSONObject(i)));
                } catch (Exception e) {
                    //ignore
                }
            }
        } catch (JSONException e) {
            Toast.makeText(StoresActivity.this, "Servicio no disponible.", Toast.LENGTH_LONG).show();
        }

        ListView lv = (ListView) findViewById(R.id.storesList);
        ReloadStationAdapter arrayAdapter = new ReloadStationAdapter(StoresActivity.this, stores);
        lv.setAdapter(arrayAdapter);
    }

    public boolean checkLocationPermission() {
        String permission = "android.permission.ACCESS_FINE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    private Location getLastKnownLocation() throws SecurityException{
        locationManager = (LocationManager)getApplicationContext().getSystemService(LOCATION_SERVICE);
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

    public class ReloadStationAdapter extends ArrayAdapter<ReloadStation> {
        public ReloadStationAdapter(Context context, List<ReloadStation> stores) {
            super(context, 0, stores);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get the data item for this position
            ReloadStation store = getItem(position);
            // Check if an existing view is being reused, otherwise inflate the view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.store_item, parent, false);
            }
            TextView tvName = (TextView) convertView.findViewById(R.id.storeName);
            TextView tvHome = (TextView) convertView.findViewById(R.id.storeAddress);
            tvName.setText(store.getDisplayInfo());
            tvHome.setText(store.getAddress());
            // Return the completed view to render on screen
            return convertView;
        }
    }

}
