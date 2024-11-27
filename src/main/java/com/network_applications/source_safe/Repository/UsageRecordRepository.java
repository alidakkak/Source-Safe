package com.network_applications.source_safe.Repository;

import com.network_applications.source_safe.Model.Entity.UsageRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UsageRecordRepository extends JpaRepository<UsageRecord, Long> {
    boolean existsByFileIdAndUserId(Long fileId, Long userId);

    @Query("SELECT ur FROM UsageRecord ur WHERE ur.file.id = :fileId")
    List<UsageRecord> findByFileId(@Param("fileId") Long fileId);

    @Query("SELECT ur FROM UsageRecord ur WHERE ur.user.id = :userId")
    List<UsageRecord> findByUserId(@Param("userId") Long userId);
}
