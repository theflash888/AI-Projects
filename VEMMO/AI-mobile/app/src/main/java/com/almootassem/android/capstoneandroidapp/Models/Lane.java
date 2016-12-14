package com.almootassem.android.capstoneandroidapp.Models;

/**
 * Created by 100520286 on 12/1/2016.
 */

public class Lane {
    public static final String RIGHT = "right";
    public static final String LEFT = "left";

    public static final String SAFE = "safe";
    public static final String UNSAFE = "unsafe";

    private String status, direction;

    public Lane(String status, String direction){
        this.status = status;
        this.direction = direction;
    }

    public Lane(){

    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }
}
