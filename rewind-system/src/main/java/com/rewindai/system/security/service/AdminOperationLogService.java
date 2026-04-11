package com.rewindai.system.security.service;

import com.rewindai.system.security.entity.AdminOperationLog;
import com.rewindai.system.security.repository.AdminOperationLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;

/**
 * 管理员操作日志 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AdminOperationLogService {

    private final AdminOperationLogRepository adminOperationLogRepository;

    public Page<AdminOperationLog> findAll(Pageable pageable) {
        return adminOperationLogRepository.findAll(pageable);
    }

    public Page<AdminOperationLog> findByAdminId(Integer adminId, Pageable pageable) {
        return adminOperationLogRepository.findByAdminId(adminId, pageable);
    }

    public Page<AdminOperationLog> findByOperationType(String operationType, Pageable pageable) {
        return adminOperationLogRepository.findByOperationType(operationType, pageable);
    }

    public Page<AdminOperationLog> findByModule(String module, Pageable pageable) {
        return adminOperationLogRepository.findByModule(module, pageable);
    }

    public Page<AdminOperationLog> findByDateRange(OffsetDateTime start, OffsetDateTime end, Pageable pageable) {
        return adminOperationLogRepository.findByDateRange(start, end, pageable);
    }

    public Page<AdminOperationLog> searchByKeyword(String keyword, Pageable pageable) {
        return adminOperationLogRepository.searchByKeyword(keyword, pageable);
    }

    public List<AdminOperationLog> findByDateRangeForExport(OffsetDateTime start, OffsetDateTime end) {
        return adminOperationLogRepository.findByDateRangeForExport(start, end);
    }

    @Transactional
    public AdminOperationLog logOperation(AdminOperationLog logEntry) {
        return adminOperationLogRepository.save(logEntry);
    }
}
