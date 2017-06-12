package com.example.marcos.smartparking;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
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
    private Long currentParkingId = 0l;

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
        if (this.currentParkingId != 0l) {
            new StopParkingAsyncTask().execute(new EmptyDTO(
                    MessageFormat.format(Constants.STOP_PARKING_URI, Constants.userIDNumber, this.currentParkingId),
                    HttpDelete.METHOD_NAME));
        }
    }

    public void postStartParking(ParkingDTO parkingDTO) {
        if (this.currentParkingId == 0l) {
            new StartParkingAsyncTask().execute(parkingDTO);
        }
    }

    public void postLocation(LocationDTO locationDTO) {
        new HttpAsyncTask().execute(locationDTO);
    }

    public static String POST(BaseDTO baseDTO){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // 2. make POST request to the given URL
            HttpEntityEnclosingRequestBase httpRequest;
            if (HttpPut.METHOD_NAME.equals(baseDTO.getMethod())) {
                httpRequest = new HttpPut(Constants.SERVER_BASE + baseDTO.getServiceURI());
            }
            else {
                httpRequest = new HttpPost(Constants.SERVER_BASE + baseDTO.getServiceURI());
            }

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();
            HashMap<String, Object> params = baseDTO.getPropertiesAsMap();

            for (Map.Entry<String, Object> entry : params.entrySet()) {
                jsonObject.accumulate(entry.getKey(), entry.getValue());
            }

            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpRequest.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpRequest.setHeader("Accept", "application/json");
            httpRequest.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpRequest);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
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

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();
            // 2. make POST request to the given URL
            HttpDelete httpDelete = new HttpDelete(Constants.SERVER_BASE + baseDTO.getServiceURI());

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpDelete);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
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

    private class StartParkingAsyncTask extends HttpAsyncTask {
        @Override
        protected void onPostExecute(String result) {
            LocationPostService.this.currentParkingId = Long.valueOf(result);
        }
    }

    private class StopParkingAsyncTask extends HttpAsyncDelete {
        @Override
        protected void onPostExecute(String result) {
            LocationPostService.this.currentParkingId = 0l;
            Toast.makeText(getBaseContext(), "Costo total: " + result, Toast.LENGTH_LONG).show();
        }
    }

    private class HttpAsyncTask extends AsyncTask<BaseDTO, Void, String> {
        @Override
        protected String doInBackground(BaseDTO... baseDTOs) {
            return POST(baseDTOs[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }
    }

    private class HttpAsyncDelete extends AsyncTask<BaseDTO, Void, String> {
        @Override
        protected String doInBackground(BaseDTO... baseDTOs) {
            return DELETE(baseDTOs[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }
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

}
