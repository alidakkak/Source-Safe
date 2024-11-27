package com.network_applications.source_safe.Model.DTO.Response;

import com.network_applications.source_safe.Model.Entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserWithFilesDto {
    private Long userId;
    private String userName;
    private List<FileResponseDto> files;

    public static UserWithFilesDto fromUser(User user) {
        List<FileResponseDto> files = user.getFiles().stream()
                .map(FileResponseDto::fromFile)
                .collect(Collectors.toList());

        return UserWithFilesDto.builder()
                .userId(user.getId())
                .userName(user.getFullName())
                .files(files)
                .build();
    }
}
