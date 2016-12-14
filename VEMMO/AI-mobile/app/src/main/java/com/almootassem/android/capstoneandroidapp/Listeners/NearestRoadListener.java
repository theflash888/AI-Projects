package com.almootassem.android.capstoneandroidapp.Listeners;

import com.almootassem.android.capstoneandroidapp.Models.Sign;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by 100520286 on 11/18/2016.
 */

public interface NearestRoadListener {
    void nearestRoadFound(JSONObject road, final Sign sign) throws JSONException;
}
