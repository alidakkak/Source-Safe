package com.network_applications.source_safe.Model.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Entity
@Table(name = "user_files")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UsageRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "userId", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "fileId", nullable = false)
    private File file;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ActionType actionType;

    @Column(name = "backup_file_path", nullable = true)
    private String backupFilePath;

    @CreationTimestamp
    @Column(updatable = false, name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date updatedAt;

    public enum ActionType {
        RESERVE,
        RELEASE
    }

    public static UsageRecord createRecord(User user, File file, ActionType actionType, String backupFilePath) {
        return UsageRecord.builder()
                .user(user)
                .file(file)
                .actionType(actionType)
                .backupFilePath(backupFilePath)
                .build();
    }

}
