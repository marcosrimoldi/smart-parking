package com.example.marcos.smartparking;

import com.example.marcos.smartparking.domain.User;

public class TokenDTO {
    String token;
    User user;

    TokenDTO(Long userId, String token){
        this.token = token;
        this.user = new User(userId, "");
    }

}
