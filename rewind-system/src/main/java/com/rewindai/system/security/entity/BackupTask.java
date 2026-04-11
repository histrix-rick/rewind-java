package com.rewindai.system.security.entity;

import com.rewindai.system.security.enums.RuleStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;

/**
 * 备份任务实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "backup_tasks")
public class BackupTask {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "task_name", nullable = false, length = 100)
    private String taskName;

    @Column(name = "task_type", nullable = false, length = 20)
    private String taskType;

    @Column(name = "backup_type", nullable = false, length = 20)
    private String backupType;

    @Column(name = "cron_expression", length = 50)
    private String cronExpression;

    @Column(name = "storage_path", length = 500)
    private String storagePath;

    @Column(name = "retention_days")
    private Integer retentionDays;

    @Column(name = "compress")
    private Boolean compress;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private RuleStatus status;

    @Column(name = "last_executed_at")
    private OffsetDateTime lastExecutedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.retentionDays == null) {
            this.retentionDays = 30;
        }
        if (this.compress == null) {
            this.compress = true;
        }
        if (this.status == null) {
            this.status = RuleStatus.ACTIVE;
        }
    }
}
