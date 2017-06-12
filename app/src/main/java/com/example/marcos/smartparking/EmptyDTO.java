package com.example.marcos.smartparking;

import java.util.HashMap;

public class EmptyDTO extends BaseDTO {

    private String serviceURI;
    private String method;

    EmptyDTO(String URI, String method) {
        this.serviceURI = URI;
        this.method = method;
    }

    @Override
    public HashMap<String, Object> getPropertiesAsMap() { throw new RuntimeException("EmptyDTO must be empty."); }

    @Override
    public String getServiceURI() {
        return this.serviceURI;
    }

    @Override
    public String getMethod() {
        return this.method;
    }
}
