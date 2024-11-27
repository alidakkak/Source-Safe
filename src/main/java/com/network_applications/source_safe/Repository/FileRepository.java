package com.network_applications.source_safe.Repository;

import com.network_applications.source_safe.Model.Entity.File;
import com.network_applications.source_safe.Model.Entity.Group;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface FileRepository extends JpaRepository<File, Long> {
    List<File> findByGroupIdAndRequestStatus(Long groupId, File.RequestStatus requestStatus);

    List<File> findByGroupId(Long groupId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT f FROM File f WHERE f.id IN :fileIds")
    List<File> findAllByIdWithLock(@Param("fileIds") List<Long> fileIds);
}
