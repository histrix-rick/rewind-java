package com.rewindai.system.storage.repository;

import com.rewindai.system.storage.entity.FileRecord;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 文件记录 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface FileRecordRepository extends JpaRepository<FileRecord, UUID> {

    Page<FileRecord> findByIsDeletedFalseOrderByCreatedAtDesc(Pageable pageable);

    Page<FileRecord> findByFileNameContainingIgnoreCaseAndIsDeletedFalseOrderByCreatedAtDesc(
            String fileName, Pageable pageable);

    Page<FileRecord> findByOriginalNameContainingIgnoreCaseAndIsDeletedFalseOrderByCreatedAtDesc(
            String originalName, Pageable pageable);

    Page<FileRecord> findByFileExtIgnoreCaseAndIsDeletedFalseOrderByCreatedAtDesc(
            String fileExt, Pageable pageable);

    Page<FileRecord> findByStorageProviderIgnoreCaseAndIsDeletedFalseOrderByCreatedAtDesc(
            String storageProvider, Pageable pageable);

    @Query("SELECT f FROM FileRecord f WHERE f.isDeleted = false " +
           "AND (:fileName IS NULL OR :fileName = '' OR f.fileName LIKE %:fileName%) " +
           "AND (:originalName IS NULL OR :originalName = '' OR f.originalName LIKE %:originalName%) " +
           "AND (:fileExt IS NULL OR :fileExt = '' OR f.fileExt = :fileExt) " +
           "AND (:storageProvider IS NULL OR :storageProvider = '' OR f.storageProvider = :storageProvider) " +
           "AND (:startDate IS NULL OR f.createdAt >= :startDate) " +
           "AND (:endDate IS NULL OR f.createdAt <= :endDate) " +
           "ORDER BY f.createdAt DESC")
    Page<FileRecord> searchFiles(
            @Param("fileName") String fileName,
            @Param("originalName") String originalName,
            @Param("fileExt") String fileExt,
            @Param("storageProvider") String storageProvider,
            @Param("startDate") OffsetDateTime startDate,
            @Param("endDate") OffsetDateTime endDate,
            Pageable pageable);
}
