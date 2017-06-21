package com.example.marcos.smartparking;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class AvailabilityActivity extends AppCompatActivity implements ServiceCallbacks {

    private AvailabilityService availabilityService;
    boolean mBounded;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_availability);
        ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final Button button = (Button) findViewById(R.id.searchButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final EditText streetInput = (EditText) findViewById(R.id.streetText);
                final EditText numberInput = (EditText) findViewById(R.id.numberText);
                boolean isValid = true;

                if( streetInput.getText().toString().trim().equals("")){
                    streetInput.setError( "Ingrese la calle." );
                    isValid = false;
                }
                if( numberInput.getText().toString().trim().equals("")){
                    numberInput.setError( "Ingrese la altura." );
                    isValid = false;
                }
                if (isValid) {
                    availabilityService.checkAvailability(new AvailabilityDTO(streetInput.getText().toString(),
                            Integer.valueOf(numberInput.getText().toString())));
                }
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
        Intent mIntent = new Intent(this, AvailabilityService.class);
        bindService(mIntent, mConnection, BIND_AUTO_CREATE);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(mBounded) {
            unbindService(mConnection);
            mBounded = false;
        }
    }

    ServiceConnection mConnection = new ServiceConnection() {
        public void onServiceDisconnected(ComponentName name) {
            mBounded = false;
            availabilityService = null;
        }

        public void onServiceConnected(ComponentName name, IBinder service) {
            mBounded = true;
            AvailabilityService.LocalBinder mLocalBinder = (AvailabilityService.LocalBinder)service;
            availabilityService = mLocalBinder.getServerInstance();
            availabilityService.setCallbacks(AvailabilityActivity.this);
        }
    };

    @Override
    public void callback(String result) {
        try {
            Map<String, Object> map = Utils.jsonToMap(new JSONObject(result));
            String availability = String.valueOf(map.get(Constants.RESPONSE_AVAILABILITY));
            final ImageView image = (ImageView) findViewById(R.id.imageView);

            switch (availability) {
                case Constants.AVAILABILITY_HIGH:
                    image.setImageResource(R.drawable.traffic_light_green);
                    break;
                case Constants.AVAILABILITY_MEDIUM:
                    image.setImageResource(R.drawable.traffic_light_yellow);
                    break;
                case Constants.AVAILABILITY_LOW:
                    image.setImageResource(R.drawable.traffic_light_red);
                    break;
            }
        } catch (JSONException e) {
            Toast.makeText(AvailabilityActivity.this, "Servicio no disponible.", Toast.LENGTH_LONG).show();
        }
    }
}
