package com.rewindai.system.report.repository;

import com.rewindai.system.report.entity.ContentReportAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 内容举报操作记录Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface ContentReportActionRepository extends JpaRepository<ContentReportAction, Long> {

    /**
     * 按举报ID查询操作记录
     */
    List<ContentReportAction> findByReportIdOrderByCreatedAtDesc(Long reportId);

    /**
     * 按操作人ID查询
     */
    List<ContentReportAction> findByOperatorIdOrderByCreatedAtDesc(Long operatorId);

    /**
     * 删除举报的所有操作记录
     */
    void deleteByReportId(Long reportId);
}
