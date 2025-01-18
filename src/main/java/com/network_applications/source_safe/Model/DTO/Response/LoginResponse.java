package com.network_applications.source_safe.Model.DTO.Response;

import lombok.Data;

@Data
public class LoginResponse {
    private Long userId;

    private String token;

    private String name;

    private long expiresIn;

    public LoginResponse setUserId(Long userId) {
        this.userId = userId;
        return this;
    }

    public LoginResponse setName(String name) {
        this.name = name;
        return this;
    }

    public LoginResponse setToken(String token) {
        this.token = token;
        return this;
    }

    public LoginResponse setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
        return this;
    }
}
