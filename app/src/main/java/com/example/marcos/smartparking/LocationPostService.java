package com.example.marcos.smartparking;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

public class LocationPostService extends Service {

    IBinder mBinder = new LocalBinder();
    private ParkingServiceCallbacks serviceCallbacks;

    public LocationPostService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public LocationPostService getServerInstance() {
            return LocationPostService.this;
        }
    }

    public void postStopParking() {
        new StopParkingAsyncTask().execute(new EmptyDTO(
                MessageFormat.format(Constants.STOP_PARKING_URI, Constants.userIDNumber),
                HttpDelete.METHOD_NAME));
    }

    public void postStartParking(ParkingDTO parkingDTO) {
        new StartParkingAsyncTask().execute(parkingDTO);
    }

    public void postGetCurrentParking() {
        new CurrentParkingAsyncTask().execute(new EmptyDTO(
                MessageFormat.format(Constants.GET_CURRENT_PARKING_URI, Constants.userIDNumber),
                HttpGet.METHOD_NAME));
    }

    public void postLocation(LocationDTO locationDTO) {
        new HttpAsyncTask().execute(locationDTO);
    }

    public static String POST(BaseDTO baseDTO){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpEntityEnclosingRequestBase httpRequest;
            if (HttpPut.METHOD_NAME.equals(baseDTO.getMethod())) {
                httpRequest = new HttpPut(Constants.SERVER_BASE + baseDTO.getServiceURI());
            }
            else {
                httpRequest = new HttpPost(Constants.SERVER_BASE + baseDTO.getServiceURI());
            }

            String json = "";

            JSONObject jsonObject = new JSONObject();
            HashMap<String, Object> params = baseDTO.getPropertiesAsMap();

            for (Map.Entry<String, Object> entry : params.entrySet()) {
                jsonObject.accumulate(entry.getKey(), entry.getValue());
            }

            json = jsonObject.toString();
            StringEntity se = new StringEntity(json);
            httpRequest.setEntity(se);

            httpRequest.setHeader("Accept", "application/json");
            httpRequest.setHeader("Content-type", "application/json");

            HttpResponse httpResponse = httpclient.execute(httpRequest);
            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    public static String DELETE(BaseDTO baseDTO){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpDelete httpDelete = new HttpDelete(Constants.SERVER_BASE + baseDTO.getServiceURI());

            HttpResponse httpResponse = httpclient.execute(httpDelete);
            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    public static String GET(BaseDTO baseDTO){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpGet httpGet = new HttpGet(Constants.SERVER_BASE + baseDTO.getServiceURI());

            HttpResponse httpResponse = httpclient.execute(httpGet);
            inputStream = httpResponse.getEntity().getContent();

            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        return result;
    }

    private class StartParkingAsyncTask extends HttpAsyncTask {
        @Override
        protected void onPostExecute(String result) {
            if (serviceCallbacks != null) {
                serviceCallbacks.onStartParkingCallback(result);
            }
        }
    }

    private class StopParkingAsyncTask extends HttpAsyncDelete {
        @Override
        protected void onPostExecute(String result) {
            if (serviceCallbacks != null) {
                serviceCallbacks.onStopParkingCallback(result);
            }
        }
    }

    private class CurrentParkingAsyncTask extends HttpAsyncGet {
        @Override
        protected void onPostExecute(String result) {
            if (serviceCallbacks != null) {
                serviceCallbacks.onGetParkingCallback(result);
            }
        }
    }

    private class HttpAsyncTask extends AsyncTask<BaseDTO, Void, String> {
        @Override
        protected String doInBackground(BaseDTO... baseDTOs) {
            return POST(baseDTOs[0]);
        }
        @Override
        protected void onPostExecute(String result) {}
    }

    private class HttpAsyncDelete extends AsyncTask<BaseDTO, Void, String> {
        @Override
        protected String doInBackground(BaseDTO... baseDTOs) {
            return DELETE(baseDTOs[0]);
        }
        @Override
        protected void onPostExecute(String result) {}
    }

    private class HttpAsyncGet extends AsyncTask<BaseDTO, Void, String> {
        @Override
        protected String doInBackground(BaseDTO... baseDTOs) {
            return GET(baseDTOs[0]);
        }
        @Override
        protected void onPostExecute(String result) {}
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public void setCallbacks(ParkingServiceCallbacks callbacks) {
        serviceCallbacks = callbacks;
    }

}
