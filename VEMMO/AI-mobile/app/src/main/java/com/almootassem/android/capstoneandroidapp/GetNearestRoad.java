package com.almootassem.android.capstoneandroidapp;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.util.Log;

import com.almootassem.android.capstoneandroidapp.Listeners.NearestRoadListener;
import com.almootassem.android.capstoneandroidapp.Models.Sign;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by 100520286 on 11/18/2016.
 */

public class GetNearestRoad extends AsyncTask<LatLng, Void, JSONObject> {

    private final String TAG = "GetNearestRoad";
    private Context context;
    private NearestRoadListener listener = null;
    private LatLng latLng;
    private Sign sign;

    public GetNearestRoad(Context context, NearestRoadListener listener, Sign sign){
        this.context = context;
        this.listener = listener;
        this.sign = sign;
    }

    @Override
    protected JSONObject doInBackground(LatLng... latLngs) {
        HttpURLConnection urlConnection;
        String result = "";
        this.latLng = latLngs[0];
        try{
            URL url = new URL("https://roads.googleapis.com/v1/nearestRoads?points=" + latLng.latitude + "," + latLng.longitude + "&key=" +
            context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA).metaData.getString("com.google.android.geo.API_KEY"));
            urlConnection = (HttpURLConnection) url.openConnection();
            Log.v(TAG, url.toString());

            if (urlConnection.getResponseCode() == 200){
                InputStream in = new BufferedInputStream((urlConnection.getInputStream()));
                if (in != null){
                    String line;
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
                    while ((line = bufferedReader.readLine()) != null) result += "\n" + line;
                }
                in.close();
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        try {
            return new JSONObject(result);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onPostExecute(JSONObject result) {
        try {
            listener.nearestRoadFound(result, sign);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        Log.v(TAG, result.toString());
    }
}
