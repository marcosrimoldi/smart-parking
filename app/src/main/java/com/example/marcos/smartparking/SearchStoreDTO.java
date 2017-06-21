package com.example.marcos.smartparking;

import android.location.Location;

import com.example.marcos.smartparking.domain.User;

import org.apache.http.client.methods.HttpPut;

import java.text.MessageFormat;
import java.util.HashMap;

public class SearchStoreDTO extends BaseDTO {

    private Location location;
    private User user;

    SearchStoreDTO(Long userId, Location location) {
        this.location = location;
        this.user = new User(userId, "");
    }

    @Override
    public HashMap<String, Object> getPropertiesAsMap() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("lat", location.getLatitude());
        map.put("lng", location.getLongitude());
        return map;
    }

    @Override
    public String getServiceURI() {
        return Constants.STORE_SEARCH_URI;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
