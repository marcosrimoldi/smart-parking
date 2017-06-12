package com.example.marcos.smartparking;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
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
import java.util.HashMap;
import java.util.Map;

import static com.google.android.gms.internal.zzt.TAG;

public class CustomFirebaseInstanceIdService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + refreshedToken);
        this.sendRegistrationToServer(refreshedToken);
    }


    private void sendRegistrationToServer(String token) {
        postToken(new TokenDTO(Constants.userIDNumber, token));
    }

    public void postToken(TokenDTO tokenDTO) {
        new CustomFirebaseInstanceIdService.HttpAsyncTask().execute(tokenDTO);
    }

    public static String POST(TokenDTO tokenDTO){
        InputStream inputStream = null;
        String result = "";
        try {

            HttpClient httpclient = new DefaultHttpClient();
            HttpEntityEnclosingRequestBase httpRequest;
            if (HttpPut.METHOD_NAME.equals(tokenDTO.getMethod())) {
                httpRequest = new HttpPut(Constants.SERVER_BASE + tokenDTO.getServiceURI());
            }
            else {
                httpRequest = new HttpPost(Constants.SERVER_BASE + tokenDTO.getServiceURI());
            }
            String json = "";

            JSONObject jsonObject = new JSONObject();
            HashMap<String, Object> params = tokenDTO.getPropertiesAsMap();
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

    private class HttpAsyncTask extends AsyncTask<TokenDTO, Void, String> {
        @Override
        protected String doInBackground(TokenDTO... tokenDTOs) {
            return POST(tokenDTOs[0]);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Sent!", Toast.LENGTH_LONG).show();
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }


}
