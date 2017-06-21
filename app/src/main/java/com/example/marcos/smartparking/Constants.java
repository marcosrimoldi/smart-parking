package com.example.marcos.smartparking;

public class Constants {

    public static final String SERVER_BASE = "http://192.168.0.11:8080/parking";
    public static final String userID = "marcos.rimoldi";
    public static final String POSITION_URI = "/user/{0}/position";
    public static final String DEVICE_TOKEN_URI = "/user/{0}/device";
    public static final String START_PARKING_URI = "/user/{0}/parking";
    public static final String STOP_PARKING_URI = "/user/{0}/parking/{1}";
    public static final String AVAILABILITY_URI = "/user/{0}/parking/availability";
    public static final String STORE_SEARCH_URI = "/store/search";

    public static final Long userIDNumber = 1l;
    public static final String RESPONSE_AVAILABILITY = "availability";
    public static final String AVAILABILITY_HIGH = "HIGH";
    public static final String AVAILABILITY_MEDIUM = "MEDIUM";
    public static final String AVAILABILITY_LOW = "LOW";


}

