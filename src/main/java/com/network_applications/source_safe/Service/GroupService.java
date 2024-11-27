package com.network_applications.source_safe.Service;

import com.network_applications.source_safe.Model.DTO.Request.AddMemberRequestDto;
import com.network_applications.source_safe.Model.DTO.Request.GroupRequestDto;
import com.network_applications.source_safe.Model.DTO.Response.GroupResponseDto;
import com.network_applications.source_safe.Model.DTO.Response.UserResponseDto;
import com.network_applications.source_safe.Model.Entity.Group;
import com.network_applications.source_safe.Model.Entity.User;
import com.network_applications.source_safe.Model.Entity.UserGroup;
import com.network_applications.source_safe.Repository.GroupRepository;
import com.network_applications.source_safe.Repository.UserGroupRepository;
import com.network_applications.source_safe.Repository.UserRepository;
import com.network_applications.source_safe.exception.ErrorResponse;
import com.network_applications.source_safe.exception.ErrorResponseException;
import com.network_applications.source_safe.exception.NotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GroupService {
    @Autowired
    private GroupRepository groupRepository;
    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserRepository userRepository;

    public List<UserResponseDto> getUsersByGroupId(Long groupId) {
        return userGroupRepository.findByGroupId(groupId)
                .stream()
                .map(userGroup -> UserResponseDto.fromEntityToDto(
                        userGroup.getUser(),
                        userGroup.getRoleType()
                ))
                .collect(Collectors.toList());
    }

    public List<GroupResponseDto> getUserGroupsByUserId(Long userId) {
        return userGroupRepository.findByUserId(userId)
                .stream()
                .map(userGroup -> GroupResponseDto.fromGroup(userGroup.getGroup()))
                .collect(Collectors.toList());
    }

    @Transactional
    public Group createGroup(GroupRequestDto groupRequestDto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Group group = new Group();
        group.setGroupName(groupRequestDto.getGroupName());
        group = groupRepository.save(group);

        UserGroup userGroup = new UserGroup();
        userGroup.setUser(user);
        userGroup.setGroup(group);
        userGroup.setRoleType(UserGroup.RoleType.ADMIN);
        userGroupRepository.save(userGroup);

        return group;
    }

    @Transactional
    public void addMemberToGroup(AddMemberRequestDto addMemberRequestDto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        User member = userRepository.findById(addMemberRequestDto.getMemberId())
                .orElseThrow(() -> new NotFoundException("Member not found"));

        Group group = groupRepository.findById(addMemberRequestDto.getGroupId())
                .orElseThrow(() -> new NotFoundException("Group not found"));

        UserGroup existingMembers = userGroupRepository.findByUserAndGroup(member, group);
        if (existingMembers != null) {
            throw  new ErrorResponseException("Member is already exceeded");
        }

        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Creator not found"));

        UserGroup creatorRelation = userGroupRepository.findByUserAndGroupAndRoleType(
                creator, group, UserGroup.RoleType.ADMIN);

        if (creatorRelation == null) {
            throw new ErrorResponseException("Only the group creator can add members.");
        }

        UserGroup userGroup = new UserGroup();
        userGroup.setUser(member);
        userGroup.setGroup(group);
        userGroup.setRoleType(UserGroup.RoleType.MEMBER);
        userGroupRepository.save(userGroup);
    }


}
