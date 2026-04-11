package com.rewindai.system.security.service;

import com.rewindai.system.security.entity.UserSensitiveLog;
import com.rewindai.system.security.enums.RiskLevel;
import com.rewindai.system.security.repository.UserSensitiveLogRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

/**
 * 用户敏感操作日志 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserSensitiveLogService {

    private final UserSensitiveLogRepository userSensitiveLogRepository;

    public Page<UserSensitiveLog> findAll(Pageable pageable) {
        return userSensitiveLogRepository.findAll(pageable);
    }

    public Page<UserSensitiveLog> findByUserId(UUID userId, Pageable pageable) {
        return userSensitiveLogRepository.findByUserId(userId, pageable);
    }

    public Page<UserSensitiveLog> findByOperationType(String operationType, Pageable pageable) {
        return userSensitiveLogRepository.findByOperationType(operationType, pageable);
    }

    public Page<UserSensitiveLog> findByRiskLevel(RiskLevel riskLevel, Pageable pageable) {
        return userSensitiveLogRepository.findByRiskLevel(riskLevel, pageable);
    }

    public Page<UserSensitiveLog> findByDateRange(OffsetDateTime start, OffsetDateTime end, Pageable pageable) {
        return userSensitiveLogRepository.findByDateRange(start, end, pageable);
    }

    public Page<UserSensitiveLog> searchByKeyword(String keyword, Pageable pageable) {
        return userSensitiveLogRepository.searchByKeyword(keyword, pageable);
    }

    public List<UserSensitiveLog> findByDateRangeForExport(OffsetDateTime start, OffsetDateTime end) {
        return userSensitiveLogRepository.findByDateRangeForExport(start, end);
    }

    @Transactional
    public UserSensitiveLog logOperation(UserSensitiveLog logEntry) {
        return userSensitiveLogRepository.save(logEntry);
    }
}
