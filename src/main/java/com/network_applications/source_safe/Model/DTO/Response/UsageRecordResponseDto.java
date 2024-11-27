package com.network_applications.source_safe.Model.DTO.Response;

import com.network_applications.source_safe.Model.Entity.UsageRecord;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UsageRecordResponseDto {
    private String actionType;
    private String fileName;
    private String userName;
    private String createdAt;

    public static UsageRecordResponseDto fromEntity(UsageRecord usageRecord) {
        return UsageRecordResponseDto.builder()
                .actionType(usageRecord.getActionType().toString())
                .fileName(usageRecord.getFile().getName())
                .userName(usageRecord.getUser().getFullName())
                .createdAt(usageRecord.getCreatedAt().toString())
                .build();
    }
}
