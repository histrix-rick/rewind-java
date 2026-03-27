package com.rewindai.system.daydream.repository;

import com.rewindai.system.daydream.entity.CashFlowRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 现金变动记录 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface CashFlowRecordRepository extends JpaRepository<CashFlowRecord, UUID> {

    List<CashFlowRecord> findByDreamIdOrderByCreatedAtAsc(UUID dreamId);

    List<CashFlowRecord> findByDreamIdAndNodeIdOrderByCreatedAtAsc(UUID dreamId, UUID nodeId);

    void deleteByDreamIdAndNodeId(UUID dreamId, UUID nodeId);
}
