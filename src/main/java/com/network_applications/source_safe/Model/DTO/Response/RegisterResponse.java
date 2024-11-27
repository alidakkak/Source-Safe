package com.network_applications.source_safe.Model.DTO.Response;

import com.network_applications.source_safe.Model.Entity.User;

public class RegisterResponse {
    private String fullName;
    private String email;

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public static RegisterResponse fromUser(User user) {
        RegisterResponse response = new RegisterResponse();
        response.setFullName(user.getFullName());
        response.setEmail(user.getEmail());
        return response;
    }
}
