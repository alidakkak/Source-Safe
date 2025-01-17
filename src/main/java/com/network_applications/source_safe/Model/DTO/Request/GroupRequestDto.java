package com.network_applications.source_safe.Model.DTO.Request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class GroupRequestDto {
    @NotNull(message = "Group name is required")
    private String groupName;
}
