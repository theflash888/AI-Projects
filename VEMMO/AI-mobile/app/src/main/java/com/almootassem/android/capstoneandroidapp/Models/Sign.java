package com.almootassem.android.capstoneandroidapp.Models;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 100520286 on 11/21/2016.
 */

public class Sign {
    private int type;
    private double distance;
    private boolean received;
    private int side;

    public static final int STOP_SIGN = 0;
    public static final int RED_LIGHT = 1;
    public static final int AMBER_LIGHT = 2;
    public static final int GREEN_LIGHT = 3;

    public Sign(){

    }

    public Sign(Object type, Object distance, Object side, Object received){
        this.type = Integer.parseInt(type.toString());
        this.distance = Double.parseDouble(distance.toString());
        this.side = Integer.parseInt(side.toString());
        this.received = Boolean.valueOf(received.toString());
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("type", type);
        result.put("distance", distance);
        result.put("side", side);
        result.put("received", received);

        return result;
    }

    public boolean isTrafficLight(){
        return type == RED_LIGHT || type == AMBER_LIGHT || type == GREEN_LIGHT;
    }

    public double getDistance() {
        return distance;
    }

    public int getType() {
        return type;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isReceived() {
        return received;
    }

    public void setReceived(boolean received) {
        this.received = received;
    }

    public int getSide() {
        return side;
    }

    public void setSide(int side) {
        this.side = side;
    }
}
