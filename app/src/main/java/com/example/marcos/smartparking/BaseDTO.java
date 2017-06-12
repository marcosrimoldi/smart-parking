package com.example.marcos.smartparking;

import org.apache.http.client.methods.HttpPost;

import java.util.HashMap;

public abstract class BaseDTO {

    public abstract HashMap<String, Object> getPropertiesAsMap();
    public abstract String getServiceURI();

    public String getMethod() { return HttpPost.METHOD_NAME; }
}
