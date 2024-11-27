package com.network_applications.source_safe.Model.DTO.Request;

import lombok.Data;

import java.util.List;
@Data
public class FileIdRequest {
    private List<Long> fileIds;
}
