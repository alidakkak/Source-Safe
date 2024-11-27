package com.network_applications.source_safe.Service;

import com.network_applications.source_safe.Model.DTO.Response.UsageRecordResponseDto;
import com.network_applications.source_safe.Model.Entity.UsageRecord;
import com.network_applications.source_safe.Model.Entity.User;
import com.network_applications.source_safe.Model.Entity.UserGroup;
import com.network_applications.source_safe.Repository.UsageRecordRepository;
import com.network_applications.source_safe.Repository.UserGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReportService {
    @Autowired
    private UsageRecordRepository usageRecordRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    public boolean isUserGroupAdmin(Long userId, Long groupId) {
        return userGroupRepository.findByUserIdAndGroupIdAndRoleType(userId, groupId, UserGroup.RoleType.ADMIN).isPresent();
    }

    public List<UsageRecordResponseDto> getFileOperationsReport(Authentication authentication, Long fileId) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

//        boolean isMember = userGroupRepository.existsByUserIdAndFileGroupId(fileId, userId);
//
//        if (!isMember) {
//            throw new AccessDeniedException("Access denied: You are not a member of the group associated with this file.");
//        }
        List<UsageRecord> usageRecords = usageRecordRepository.findByFileId(fileId);

        return usageRecords.stream()
                .map(UsageRecordResponseDto::fromEntity)
                .collect(Collectors.toList());
    }

    public List<UsageRecordResponseDto> getUserOperationsReport(Authentication authentication, Long groupId, Long memberId) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        if (!isUserGroupAdmin(userId, groupId)) {
            throw new AccessDeniedException("Access denied: You do not have the required permissions.");
        }

        List<UsageRecord> usageRecords = usageRecordRepository.findByUserId(memberId);

        return usageRecords.stream()
                .map(UsageRecordResponseDto::fromEntity)
                .collect(Collectors.toList());
    }
}
