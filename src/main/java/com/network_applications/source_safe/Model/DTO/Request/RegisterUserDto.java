package com.network_applications.source_safe.Model.DTO.Request;


import jakarta.validation.constraints.*;

public class RegisterUserDto {
    @Email(message = "Email is not valid")
    @NotNull(message = "Email is required")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be longer than 8 characters")
    private String password;

    @NotBlank(message = "Full name is required")
    private String fullName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
