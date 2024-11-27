package com.network_applications.source_safe.controllers;

import com.network_applications.source_safe.ApiResponse.SuccessResponse;
import com.network_applications.source_safe.Model.DTO.Request.AddMemberRequestDto;
import com.network_applications.source_safe.Model.DTO.Request.GroupRequestDto;
import com.network_applications.source_safe.Model.DTO.Response.GroupResponseDto;
import com.network_applications.source_safe.Model.DTO.Response.UserResponseDto;
import com.network_applications.source_safe.Model.DTO.Response.UserWithFilesDto;
import com.network_applications.source_safe.Model.Entity.Group;
import com.network_applications.source_safe.Model.Entity.User;
import com.network_applications.source_safe.Model.Entity.UserGroup;
import com.network_applications.source_safe.Service.GroupService;
import com.network_applications.source_safe.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;


@RestController
@RequestMapping("/groups")
public class GroupController {
    @Autowired
    private GroupService groupService;

    @Autowired
    private UserService userService;

    @GetMapping("/getUsersByGroupId/{groupId}")
    public List<UserResponseDto> getUsersByGroupId(@PathVariable Long groupId) {
        return groupService.getUsersByGroupId(groupId);
    }

    @GetMapping("/myGroups")
    public List<GroupResponseDto> getUserGroups(Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();
        return groupService.getUserGroupsByUserId(userId);
    }

    @GetMapping("/usersWithFiles/{groupId}")
    public ResponseEntity<List<UserWithFilesDto>> getUsersWithFiles(@PathVariable Long groupId) {
        List<UserWithFilesDto> usersWithFiles = userService.getUsersWithFilesByGroupId(groupId);
        return ResponseEntity.ok(usersWithFiles);
    }


    @PostMapping("/create")
    public ResponseEntity<GroupResponseDto> createGroup(@RequestBody @Valid GroupRequestDto groupRequestDto, Authentication authentication) {
        Group group = groupService.createGroup(groupRequestDto, authentication);

        GroupResponseDto responseDto = GroupResponseDto.fromGroup(group);

        return ResponseEntity.ok(responseDto);
    }

    @PostMapping("/addMember")
    public ResponseEntity<SuccessResponse> addMemberToGroup(@RequestBody @Valid AddMemberRequestDto addMemberRequestDto, Authentication authentication) {
        groupService.addMemberToGroup(addMemberRequestDto, authentication);
        return ResponseEntity.ok(new SuccessResponse("Member added to group successfully.", 200));
    }

}
