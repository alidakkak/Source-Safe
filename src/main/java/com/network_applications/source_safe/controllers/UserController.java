package com.network_applications.source_safe.controllers;

import com.network_applications.source_safe.Model.DTO.Response.UserResponseDto;
import com.network_applications.source_safe.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/users")
public class UserController {
    @Autowired
    private UserService userService;

    @GetMapping("/getUsers")
    public List<UserResponseDto> getUsers() {
        return userService.getUsers();
    }

    @GetMapping("/searchUsersByFullName")
    public List<UserResponseDto> searchUsersByFullName(@RequestParam String fullName) {
        return userService.searchUsersByFullName(fullName);
    }
}
