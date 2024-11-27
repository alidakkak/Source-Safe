package com.network_applications.source_safe.Model.DTO.Request;

import com.network_applications.source_safe.Model.Entity.File;
import com.network_applications.source_safe.Model.Entity.Group;
import com.network_applications.source_safe.Model.Entity.User;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileRequestDto {
    @NotNull(message = "Name File is required")
    private String name;

    @NotNull(message = "File is required")
    private MultipartFile filePath;

    @NotNull(message = "Group Id is required")
    private Long groupId;

    public File toEntity(User user, Group group, String filePath, File.RequestStatus requestStatus) {
        try {
            return File.builder()
                    .name(this.name)
                    .filePath(filePath)
                    .user(user)
                    .group(group)
                    .requestStatus(requestStatus)
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Error while converting file data", e);
        }
    }
}
