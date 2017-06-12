package com.example.marcos.smartparking;

import com.example.marcos.smartparking.domain.User;

import org.apache.http.client.methods.HttpPut;

import java.text.MessageFormat;
import java.util.HashMap;

public class TokenDTO extends BaseDTO {

    private String token;
    private User user;

    TokenDTO(Long userId, String token){
        this.token = token;
        this.user = new User(userId, "");
    }

    @Override
    public HashMap<String, Object> getPropertiesAsMap() {
        HashMap<String, Object> map = new HashMap<String, Object>();
        map.put("device", token);
        return map;
    }

    @Override
    public String getServiceURI() {
        return MessageFormat.format(Constants.DEVICE_TOKEN_URI, Constants.userIDNumber);
    }

    @Override
    public String getMethod() {
        return HttpPut.METHOD_NAME;
    }
}
