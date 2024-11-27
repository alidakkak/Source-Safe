package com.network_applications.source_safe.Model.DTO.Response;

import com.network_applications.source_safe.Model.Entity.User;
import com.network_applications.source_safe.Model.Entity.UserGroup;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserResponseDto {
    private Long id;
    private String fullName;
    private String email;
    private Date createdAt;
    private Date updatedAt;
    private UserGroup.RoleType roleType;

    public static UserResponseDto fromEntityToDto(User user, UserGroup.RoleType roleType) {
            return UserResponseDto.builder()
                    .id(user.getId())
                    .fullName(user.getFullName())
                    .email(user.getEmail())
                    .roleType(roleType)
                    .createdAt(user.getCreatedAt())
                    .updatedAt(user.getUpdatedAt())
                    .build();
    }

    public static UserResponseDto fromEntityToDtoWithoutRole(User user) {
        return UserResponseDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }
}
