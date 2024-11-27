package com.network_applications.source_safe.Service;

import com.network_applications.source_safe.Model.DTO.Request.FileRequestDto;
import com.network_applications.source_safe.Model.DTO.Response.FileResponseDto;
import com.network_applications.source_safe.Model.DTO.Update.FileUpdateDto;
import com.network_applications.source_safe.Model.Entity.*;
import com.network_applications.source_safe.Repository.*;
import com.network_applications.source_safe.exception.ErrorResponseException;
import com.network_applications.source_safe.exception.NotFoundException;
import org.springframework.core.io.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FileService {
    private final String UPLOAD_DIR = "C://Users/HP/IdeaProjects/source-safe/src/main/Files";
    private final String BACKUP_FILE = "C://Users/HP/IdeaProjects/source-safe/src/main/Backup";

    @Autowired
    private FileRepository fileRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GroupRepository groupRepository;

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UsageRecordRepository usageRecordRepository;

    @Autowired
    private NotificationService notificationService;

    public boolean isUserGroupAdmin(Long userId, Long groupId) {
        return userGroupRepository.findByUserIdAndGroupIdAndRoleType(userId, groupId, UserGroup.RoleType.ADMIN).isPresent();
    }

    private boolean hasPermissionToGetFile(Long fileId, Long userId) {
        return usageRecordRepository.existsByFileIdAndUserId(fileId, userId);
    }

    public String addFile(FileRequestDto fileRequestDto, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found"));

        Group group = groupRepository.findById(fileRequestDto.getGroupId())
                .orElseThrow(() -> new NotFoundException("Group not found"));

        UserGroup userGroup = userGroupRepository.findByUserAndGroupAndRoleType(user, group, UserGroup.RoleType.ADMIN);


        File.RequestStatus requestStatus;
        String responseMessage ;
        if (userGroup != null) {
            requestStatus = File.RequestStatus.APPROVED;
            responseMessage  = "The file has been added successfully and is now approved .";
        } else {
            requestStatus = File.RequestStatus.PENDING;
            responseMessage  = "The file has been added and will be published after the administrator's approval .";
        }
        String filePath = saveFileToSystem(fileRequestDto.getFilePath());

        File newFile = fileRequestDto.toEntity(user, group, filePath, requestStatus);

        fileRepository.save(newFile);

        return responseMessage;
    }

    public List<FileResponseDto> getAllFile() {
        List<File> files = fileRepository.findAll();
        return files.stream()
                .map(FileResponseDto::fromFile)
                .collect(Collectors.toList());
    }

    public List<FileResponseDto> getAccessibleFiles(Authentication authentication, Long groupId) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        boolean isMember = userGroupRepository.existsByUserIdAndGroupId(userId, groupId);
        if (!isMember) {
            throw new AccessDeniedException("Access denied: You do not have access to this group.");
        }

        List<File> files = fileRepository.findByGroupId(groupId);
        return files.stream()
                .map(FileResponseDto::fromFile)
                .collect(Collectors.toList());
    }

    public List<FileResponseDto> getFilePending(Authentication authentication, Long groupId) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        if (!isUserGroupAdmin(userId, groupId)) {
            throw new AccessDeniedException("Access denied: You do not have the required permissions.");
        }

        List<File> files = fileRepository.findByGroupIdAndRequestStatus(groupId, File.RequestStatus.PENDING);
        return files.stream()
                .map(FileResponseDto::fromFile)
                .collect(Collectors.toList());
    }

    public String delete(Authentication authentication, Long fileId, Long groupId) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        if (!isUserGroupAdmin(userId, groupId)) {
            throw new AccessDeniedException("Access denied: You do not have the required permissions.");
        }

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("File not found: " + fileId));

        deleteFileFromSystem(file.getFilePath());
        fileRepository.delete(file);
        return "File deleted successfully.";
    }

    public String updateFile(Authentication authentication, FileUpdateDto fileUpdateDto) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        if (!isUserGroupAdmin(userId, fileUpdateDto.getGroupId())) {
            throw new AccessDeniedException("Access denied: You do not have the required permissions.");
        }

        File file = fileRepository.findById(fileUpdateDto.getFileId())
                .orElseThrow(() -> new NotFoundException("File not found: " + fileUpdateDto.getFileId()));

        if (fileUpdateDto.getName() != null) {
            file.setName(fileUpdateDto.getName());
        }

        if (fileUpdateDto.getRequestStatus() != null) {
            file.setRequestStatus(fileUpdateDto.getRequestStatus());
        }

        if (fileUpdateDto.getFilePath() != null) {
            deleteFileFromSystem(file.getFilePath());

            String newFilePath = saveFileToSystem(fileUpdateDto.getFilePath());
            file.setFilePath(newFilePath);
        }

        fileRepository.save(file);

        return "File updated successfully.";
    }

    @Transactional
    public String inCheckFiles(Authentication authentication, List<Long> fileIds) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

//        List<File> files = fileRepository.findAllById(fileIds);
        List<File> files = fileRepository.findAllByIdWithLock(fileIds);
        boolean allFilesFree = files.stream().allMatch(
                file -> file.getFileStatus() == File.FileStatus.FREE
        );
        if (!allFilesFree) {
            throw  new ErrorResponseException("All files must be in FREE status to reserve them.");
        }
        files.forEach(file -> {
            if (!hasPermissionToGetFile(file.getId(), userId)) {
                throw new AccessDeniedException("You do not have permission to reserve this file: " + file.getId());
            }
            file.setFileStatus(File.FileStatus.RESERVED);
            String backupFilePath = backupFile(file.getFilePath(), BACKUP_FILE);
            UsageRecord usageRecord = UsageRecord.createRecord(user, file, UsageRecord.ActionType.RESERVE, backupFilePath);
            usageRecordRepository.save(usageRecord);
        });

        fileRepository.saveAll(files);

        return "Files reserved successfully.";
    }

    public ResponseEntity<Resource> downloadFile(Long fileId, Authentication authentication) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("File not found: " + fileId));

        if (file.getFileStatus() != File.FileStatus.RESERVED) {
            throw new ErrorResponseException("File must be reserved before downloading.");
        }

        if (!hasPermissionToGetFile(fileId,userId)) {
            throw new AccessDeniedException("Access denied: You do not have permission to download this file.");
        }

        try {
            Path filePath = Paths.get(file.getFilePath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
            file.setFileStatus(File.FileStatus.IN_USE);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                        .body(resource);
            } else {
                throw new NotFoundException("File not found or is not readable");
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while loading the file", e);
        }
    }
    @Transactional
    public String outCheckFile(Authentication authentication, Long fileId, MultipartFile newFile) {
        User user = (User) authentication.getPrincipal();
        Long userId = user.getId();

        File file = fileRepository.findById(fileId)
                .orElseThrow(() -> new NotFoundException("File not found: " + fileId));

        if (file.getFileStatus() != File.FileStatus.IN_USE) {
            throw new ErrorResponseException("File must be in 'IN_USE' status before uploading a new version.");
        }

        if (!hasPermissionToGetFile(fileId,userId)) {
            throw new AccessDeniedException("Access denied: You do not have the required permissions.");
        }

        String originalFileName = Paths.get(file.getFilePath()).getFileName().toString();
        if (!newFile.getOriginalFilename().equals(originalFileName)) {
            throw new IllegalArgumentException("File name and extension must match the reserved file.");
        }

        deleteFileFromSystem(file.getFilePath());

        String newFilePath = saveFileToSystem(newFile);
        file.setFilePath(newFilePath);

        file.setFileStatus(File.FileStatus.FREE);
        String backupFilePath = backupFile(file.getFilePath(), BACKUP_FILE);
        UsageRecord usageRecord = UsageRecord.createRecord(user, file, UsageRecord.ActionType.RELEASE, backupFilePath);
        usageRecordRepository.save(usageRecord);

        fileRepository.save(file);

        String actionMessage = UsageRecord.ActionType.RELEASE.toString();
        notificationService.notifyUsersAboutFileUpdate(file, actionMessage);

        return "File updated and freed successfully.";
    }

    private String saveFileToSystem(MultipartFile file) {
        try {
            Path uploadPath = Paths.get(UPLOAD_DIR);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String FileName = file.getOriginalFilename();
            Path filePath = uploadPath.resolve(FileName);

            // Save File In The System
            file.transferTo(filePath.toFile());

            return filePath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Error while saving file to system", e);
        }
    }

    private void deleteFileFromSystem(String filePath) {
        try {
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
            }
        } catch (IOException e) {
            throw new RuntimeException("Error while deleting old file from system", e);
        }
    }

    private String backupFile(String originalFilePath, String backupDirectory) {
        try {
            Path directory = Paths.get(backupDirectory);
            if (!Files.exists(directory)) {
                Files.createDirectories(directory);
            }

            // استخراج اسم الملف الأصلي
            Path originalPath = Path.of(originalFilePath);
            String originalFileName = originalPath.getFileName().toString();
            String fileBaseName = originalFileName.contains(".")
                    ? originalFileName.substring(0, originalFileName.lastIndexOf('.')) // الجزء بدون الامتداد
                    : originalFileName;
            String fileExtension = originalFileName.contains(".")
                    ? originalFileName.substring(originalFileName.lastIndexOf('.')) // الامتداد (مثل .txt)
                    : "";

            // تحديد النسخة الجديدة بناءً على الملفات الموجودة
            int version = 1;
            String backupFileName;
            Path backupPath;

            do {
                backupFileName = fileBaseName + "_v" + version + fileExtension; // اسم النسخة الجديدة
                backupPath = Path.of(backupDirectory, backupFileName);
                version++;
            } while (Files.exists(backupPath)); // تكرار حتى العثور على نسخة غير موجودة

            // نسخ الملف إلى النسخة الاحتياطية
            Files.copy(originalPath, backupPath, StandardCopyOption.REPLACE_EXISTING);

            // إعادة مسار النسخة الجديدة
            return backupPath.toString();
        } catch (IOException e) {
            throw new RuntimeException("Failed to backup file: " + originalFilePath, e);
        }
    }


}