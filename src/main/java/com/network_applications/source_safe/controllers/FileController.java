package com.network_applications.source_safe.controllers;

import com.network_applications.source_safe.ApiResponse.SuccessResponse;
import com.network_applications.source_safe.Model.DTO.Request.FileIdRequest;
import com.network_applications.source_safe.Model.DTO.Request.FileRequestDto;
import com.network_applications.source_safe.Model.DTO.Response.FileResponseDto;
import com.network_applications.source_safe.Model.DTO.Update.FileUpdateDto;
import com.network_applications.source_safe.Service.FileService;
import jakarta.validation.Valid;
import org.apache.coyote.Request;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileService fileService;

    @PostMapping(value = "/addFile", consumes = "multipart/form-data")
    public ResponseEntity<SuccessResponse> addFile(@ModelAttribute @Valid FileRequestDto fileRequestDto, Authentication authentication) {
        String message = fileService.addFile(fileRequestDto, authentication);
        return ResponseEntity.ok(new SuccessResponse(message, 200));
    }

    @GetMapping("/getAllFiles")
    public List<FileResponseDto> getAllFiles() {
        return fileService.getAllFile();
    }

    @GetMapping("/getAccessibleFiles")
    public ResponseEntity<List<FileResponseDto>> getAccessibleFiles(@RequestParam Long groupId, Authentication authentication) {
        List<FileResponseDto> accessibleFiles = fileService.getAccessibleFiles(authentication, groupId);
        return ResponseEntity.ok(accessibleFiles);
    }

    @GetMapping("/getFilePending/{groupId}")
    public ResponseEntity<List<FileResponseDto>> getFilePending(@PathVariable Long groupId, Authentication authentication) {
        List<FileResponseDto> pendingFiles = fileService.getFilePending(authentication, groupId);
        return ResponseEntity.ok(pendingFiles);
    }

    @DeleteMapping("/deleteFile")
    public ResponseEntity<SuccessResponse> deleteFile(@RequestParam Long fileId, @RequestParam Long groupId, Authentication authentication) {
        String message = fileService.delete(authentication, fileId, groupId);
        return ResponseEntity.ok(new SuccessResponse(message, HttpStatus.OK.value()));
    }

    @PatchMapping("/updateFile")
    public ResponseEntity<SuccessResponse> updateFile(@ModelAttribute @Valid FileUpdateDto fileUpdateDto,
                                                      Authentication authentication) {
        String message = fileService.updateFile(authentication, fileUpdateDto);
        return ResponseEntity.ok(new SuccessResponse(message, HttpStatus.OK.value()));
    }

    @PostMapping("/inCheckFiles")
    public ResponseEntity<SuccessResponse> inCheckFiles(@RequestBody FileIdRequest fileIdRequest, Authentication authentication) {
        List<Long> fileIds = fileIdRequest.getFileIds();
        String message = fileService.inCheckFiles(authentication, fileIds);
        return ResponseEntity.ok(new SuccessResponse(message, HttpStatus.OK.value()));
    }

    @GetMapping("/download/{fileId}")
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId, Authentication authentication) {
        return fileService.downloadFile(fileId, authentication);
    }

    @PostMapping("/outCheckFile")
    public ResponseEntity<SuccessResponse> outCheckFile(@RequestParam Long fileId,
                                                        @RequestParam MultipartFile newFile,
                                                        Authentication authentication) {
        String message = fileService.outCheckFile(authentication, fileId, newFile);
        return ResponseEntity.ok(new SuccessResponse(message, HttpStatus.OK.value()));
    }
}
//    @PreAuthorize("hasRole('ADMIN')")