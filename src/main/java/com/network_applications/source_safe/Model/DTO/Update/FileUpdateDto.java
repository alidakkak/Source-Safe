package com.network_applications.source_safe.Model.DTO.Update;

import com.network_applications.source_safe.Model.Entity.File;
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
public class FileUpdateDto {
    @NotNull(message = "File Id is required")
    private Long fileId;

    @NotNull(message = "Group Id is required")
    private Long groupId;

    private String name;

    private MultipartFile filePath;

    private File.RequestStatus requestStatus;
}
