package com.example.marcos.smartparking;

public interface ParkingServiceCallbacks {

    void onStopParkingCallback(String result);
    void onStartParkingCallback(String result);
    void onGetParkingCallback(String result);
}
