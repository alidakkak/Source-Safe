package com.network_applications.source_safe.controllers;

import com.network_applications.source_safe.Model.DTO.Response.UsageRecordResponseDto;
import com.network_applications.source_safe.Model.Entity.UsageRecord;
import com.network_applications.source_safe.Service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/report")
public class ReportController {
    @Autowired
    private ReportService reportService;

    @GetMapping("/file/{fileId}")
    public List<UsageRecordResponseDto> getFileOperationsReport(Authentication authentication, @PathVariable Long fileId) {
        return reportService.getFileOperationsReport(authentication, fileId);
    }

    @GetMapping("/user")
    public List<UsageRecordResponseDto> getUserOperationsReport(Authentication authentication, @RequestParam Long groupId, Long memberId) {
        return reportService.getUserOperationsReport(authentication, groupId, memberId);
    }
}
