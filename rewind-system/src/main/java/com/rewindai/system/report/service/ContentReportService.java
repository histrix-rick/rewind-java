package com.rewindai.system.report.service;

import com.rewindai.system.report.entity.ContentReport;
import com.rewindai.system.report.entity.ContentReportAction;
import com.rewindai.system.report.enums.ReportActionType;
import com.rewindai.system.report.enums.ReportStatus;
import com.rewindai.system.report.enums.ReportTargetType;
import com.rewindai.system.report.repository.ContentReportActionRepository;
import com.rewindai.system.report.repository.ContentReportRepository;
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
 * 内容举报服务
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ContentReportService {

    private final ContentReportRepository contentReportRepository;
    private final ContentReportActionRepository contentReportActionRepository;

    /**
     * 分页获取举报列表
     */
    public Page<ContentReport> getReportList(ReportStatus status, ReportTargetType targetType, Pageable pageable) {
        if (status != null && targetType != null) {
            return contentReportRepository.findByStatusAndTargetType(status, targetType, pageable);
        } else if (status != null) {
            return contentReportRepository.findByStatus(status, pageable);
        } else if (targetType != null) {
            return contentReportRepository.findByTargetType(targetType, pageable);
        }
        return contentReportRepository.findAll(pageable);
    }

    /**
     * 获取举报详情
     */
    public ContentReport getReportById(Long id) {
        return contentReportRepository.findById(id).orElse(null);
    }

    /**
     * 获取举报操作记录
     */
    public List<ContentReportAction> getReportActions(Long reportId) {
        return contentReportActionRepository.findByReportIdOrderByCreatedAtDesc(reportId);
    }

    /**
     * 处理举报
     */
    @Transactional
    public ContentReport handleReport(Long reportId, ReportStatus status, String handleResult,
                                         String handleRemark, Long adminId, String adminName) {
        ContentReport report = contentReportRepository.findById(reportId).orElse(null);
        if (report == null) {
            return null;
        }

        // 更新举报状态
        report.setStatus(status);
        report.setHandledBy(adminId);
        report.setHandledAt(OffsetDateTime.now());
        report.setHandleResult(handleResult);
        report.setHandleRemark(handleRemark);

        ContentReport saved = contentReportRepository.save(report);

        // 记录操作
        addAction(saved.getId(), ReportActionType.RESOLVE,
                "处理结果: " + handleResult, adminId, adminName);

        log.info("管理员 {} 处理了举报 {}, 状态: {}", adminName, reportId, status);
        return saved;
    }

    /**
     * 将举报标记为处理中
     */
    @Transactional
    public ContentReport startProcessing(Long reportId, Long adminId, String adminName) {
        ContentReport report = contentReportRepository.findById(reportId).orElse(null);
        if (report == null) {
            return null;
        }

        report.setStatus(ReportStatus.PROCESSING);
        report.setHandledBy(adminId);

        ContentReport saved = contentReportRepository.save(report);

        addAction(saved.getId(), ReportActionType.PROCESS, "开始处理", adminId, adminName);

        log.info("管理员 {} 开始处理举报 {}", adminName, reportId);
        return saved;
    }

    /**
     * 驳回举报
     */
    @Transactional
    public ContentReport dismissReport(Long reportId, String reason, Long adminId, String adminName) {
        ContentReport report = contentReportRepository.findById(reportId).orElse(null);
        if (report == null) {
            return null;
        }

        report.setStatus(ReportStatus.DISMISSED);
        report.setHandledBy(adminId);
        report.setHandledAt(OffsetDateTime.now());
        report.setHandleRemark(reason);

        ContentReport saved = contentReportRepository.save(report);

        addAction(saved.getId(), ReportActionType.DISMISS, "驳回原因: " + reason, adminId, adminName);

        log.info("管理员 {} 驳回了举报 {}, 原因: {}", adminName, reportId, reason);
        return saved;
    }

    /**
     * 获取统计数据
     */
    public Object[] getStatusStats() {
        return contentReportRepository.countByStatus();
    }

    /**
     * 获取时间段内的举报数量
     */
    public Long getCountByDateRange(OffsetDateTime startDate, OffsetDateTime endDate) {
        return contentReportRepository.countByDateRange(startDate, endDate);
    }

    /**
     * 批量删除举报
     */
    @Transactional
    public void deleteReports(List<Long> ids, Long adminId, String adminName) {
        for (Long id : ids) {
            ContentReport report = contentReportRepository.findById(id).orElse(null);
            if (report != null) {
                report.setDeleted(true);
                contentReportRepository.save(report);
                log.info("管理员 {} 删除了举报 {}", adminName, id);
            }
        }
    }

    /**
     * 添加操作记录
     */
    private void addAction(Long reportId, ReportActionType actionType,
                            String details, Long operatorId, String operatorName) {
        ContentReportAction action = new ContentReportAction();
        action.setReportId(reportId);
        action.setActionType(actionType);
        action.setActionDetails(details);
        action.setOperatorId(operatorId);
        action.setOperatorName(operatorName);
        contentReportActionRepository.save(action);
    }

    /**
     * 检查目标是否被举报过
     */
    public boolean isTargetReported(ReportTargetType targetType, UUID targetId) {
        return contentReportRepository.existsByTargetTypeAndTargetIdAndStatusNot(targetType, targetId, ReportStatus.DISMISSED);
    }
}
