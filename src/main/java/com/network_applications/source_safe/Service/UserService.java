package com.network_applications.source_safe.Service;

import com.network_applications.source_safe.Model.DTO.Response.FileResponseDto;
import com.network_applications.source_safe.Model.DTO.Response.UserResponseDto;
import com.network_applications.source_safe.Model.DTO.Response.UserWithFilesDto;
import com.network_applications.source_safe.Model.Entity.User;
import com.network_applications.source_safe.Model.Entity.UserGroup;
import com.network_applications.source_safe.Repository.UserGroupRepository;
import com.network_applications.source_safe.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    public List<UserResponseDto> getUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(UserResponseDto::fromEntityToDtoWithoutRole)
                .collect(Collectors.toList());
    }

    public List<UserResponseDto> searchUsersByFullName(String fullName) {
        return userRepository.findByFullNameContainingIgnoreCase(fullName)
                .stream()
                .map(UserResponseDto::fromEntityToDtoWithoutRole)
                .collect(Collectors.toList());
    }

    public List<UserWithFilesDto> getUsersWithFilesByGroupId(Long groupId) {
        List<UserGroup> userGroups = userGroupRepository.findByGroupId(groupId);

        return userGroups.stream()
                .map(userGroup -> UserWithFilesDto.fromUser(userGroup.getUser()))
                .collect(Collectors.toList());
    }

}
