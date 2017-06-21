package com.example.marcos.smartparking;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.ImageView;

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

public class AvailabilityService extends Service {

    IBinder mBinder = new LocalBinder();
    private ServiceCallbacks serviceCallbacks;

    public AvailabilityService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    public class LocalBinder extends Binder {
        public AvailabilityService getServerInstance() {
            return AvailabilityService.this;
        }
    }


    public void checkAvailability(AvailabilityDTO availabilityDTO) {
        new CheckAvailabilityAsyncTask ().execute(availabilityDTO);
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
        return result;
    }

    private class CheckAvailabilityAsyncTask extends HttpAsyncTask {
        @Override
        protected void onPostExecute(String result) {
           try {
               if (serviceCallbacks != null) {
                   serviceCallbacks.callback(result);
               }
           } catch(NumberFormatException e) {

           }
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

    public void setCallbacks(ServiceCallbacks callbacks) {
        serviceCallbacks = callbacks;
    }

}
