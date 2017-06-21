package com.example.marcos.smartparking.domain;

import org.json.JSONObject;

public class ReloadStation {

    private Long id;
    private Double latitude;
    private Double longitude;
    private String displayInfo;
    private String address;

    public ReloadStation() {
    }

    public ReloadStation(JSONObject jsonObject) throws Exception {
        this.id = Long.valueOf(jsonObject.get("id").toString());
        this.latitude = Double.valueOf(jsonObject.get("latitude").toString());
        this.longitude = Double.valueOf(jsonObject.get("longitude").toString());
        this.displayInfo = jsonObject.get("displayInfo").toString();
        this.address = jsonObject.get("address").toString();
    }



    public ReloadStation(Double latitude, Double longitude, String displayInfo, String address) {
        super();
        this.longitude = longitude;
        this.latitude = latitude;
        this.displayInfo = displayInfo;
        this.address = address;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getDisplayInfo() {
        return displayInfo;
    }

    public void setDisplayInfo(String displayInfo) {
        this.displayInfo = displayInfo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

}
