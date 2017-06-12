package com.example.marcos.smartparking;

import android.location.Location;

import java.text.MessageFormat;
import java.util.HashMap;

public class ParkingDTO extends BaseDTO {

    private Location location;
    private String domain;


    ParkingDTO(String domain, Location location) {
        this.location = location;
        this.domain = domain;
    }

    @Override
    public HashMap<String, Object> getPropertiesAsMap() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("domain", domain);
        map.put("lat", location.getLatitude());
        map.put("lng", location.getLongitude());
        return map;
    }

    @Override
    public String getServiceURI() {
        return MessageFormat.format(Constants.START_PARKING_URI, Constants.userIDNumber);
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }
}
