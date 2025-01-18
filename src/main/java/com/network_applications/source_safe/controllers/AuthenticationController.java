package com.network_applications.source_safe.controllers;

import com.network_applications.source_safe.Model.DTO.Request.LoginUserDto;
import com.network_applications.source_safe.Model.DTO.Request.RegisterUserDto;
import com.network_applications.source_safe.Model.DTO.Response.LoginResponse;
import com.network_applications.source_safe.Model.DTO.Response.RegisterResponse;
import com.network_applications.source_safe.Model.Entity.User;
import com.network_applications.source_safe.Service.AuthenticationService;
import com.network_applications.source_safe.Service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;


@RequestMapping("/auth")
@RestController
public class AuthenticationController {
    @Autowired
    private  JwtService jwtService;
    @Autowired
    private  AuthenticationService authenticationService;


    @PostMapping("/signup")
    public ResponseEntity<RegisterResponse> register(@RequestBody @Valid RegisterUserDto registerUserDto) {
        User registeredUser = authenticationService.signup(registerUserDto);

        RegisterResponse responseDto = RegisterResponse.fromUser(registeredUser);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> authenticate(@RequestBody @Valid LoginUserDto loginUserDto) {
        User authenticatedUser = authenticationService.authenticate(loginUserDto);

        String jwtToken = jwtService.generateToken(authenticatedUser);

        LoginResponse loginResponse = new LoginResponse()
                .setToken(jwtToken)
                .setName(authenticatedUser.getFullName())
                .setExpiresIn(jwtService.getExpirationTime())
                .setUserId(authenticatedUser.getId());

        return ResponseEntity.ok(loginResponse);
    }
}
