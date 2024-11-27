package com.network_applications.source_safe.Model.DTO.Response;

import com.network_applications.source_safe.Model.Entity.File;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileResponseDto {
    private Long id;

    private String name;

    private String filePath;

    private File.FileStatus fileStatus;

    private File.RequestStatus requestStatus;

    public static FileResponseDto fromFile(File file) {
        return FileResponseDto.builder()
                .id(file.getId())
                .name(file.getName())
                .fileStatus(file.getFileStatus())
                .requestStatus(file.getRequestStatus())
                .filePath(file.getFilePath())
                .build();
    }
}
