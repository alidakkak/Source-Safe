package com.network_applications.source_safe.Model.DTO.Request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddMemberRequestDto {
    @NotNull(message = "Group ID is required")
    private Long groupId;

    @NotNull(message = "Member ID is required")
    private Long memberId;
}
