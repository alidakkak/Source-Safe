package com.network_applications.source_safe.Model.DTO.Response;

import com.network_applications.source_safe.Model.Entity.Group;
import com.network_applications.source_safe.Model.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GroupResponseDto {
    private Long id;
    private String groupName;

    public static GroupResponseDto fromGroup(Group group) {
        return GroupResponseDto.builder()
                .id(group.getId())
                .groupName(group.getGroupName())
                .build();
    }
}
