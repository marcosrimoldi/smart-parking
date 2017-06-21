package com.example.marcos.smartparking;

import java.text.MessageFormat;
import java.util.HashMap;

public class AvailabilityDTO extends BaseDTO {

    private String street;
    private Integer number;


    AvailabilityDTO(String street, Integer number) {
        this.street = street;
        this.number = number;
    }

    @Override
    public HashMap<String, Object> getPropertiesAsMap() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("street", this.street);
        map.put("number", this.number);
        return map;
    }

    @Override
    public String getServiceURI() {
        return MessageFormat.format(Constants.AVAILABILITY_URI, Constants.userIDNumber);
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }
}
