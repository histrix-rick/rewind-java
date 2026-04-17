package com.rewindai.system.daydream.repository;

import com.rewindai.system.daydream.entity.TimelineNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 时间轴节点 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface TimelineNodeRepository extends JpaRepository<TimelineNode, UUID> {

    List<TimelineNode> findByDreamIdAndBranchId(UUID dreamId, UUID branchId, Sort sort);

    Page<TimelineNode> findByDreamIdAndBranchId(UUID dreamId, UUID branchId, Pageable pageable);

    Optional<TimelineNode> findFirstByDreamIdAndBranchIdOrderBySequenceNumDesc(UUID dreamId, UUID branchId);

    List<TimelineNode> findByDreamIdAndBranchIdAndSequenceNumLessThanEqualOrderBySequenceNumAsc(
            UUID dreamId, UUID branchId, Integer sequenceNum);

    // 获取梦境所有节点（不区分分支）
    List<TimelineNode> findByDreamId(UUID dreamId, Sort sort);

    // 用于获取最后一个节点（不区分分支）
    Optional<TimelineNode> findFirstByDreamIdOrderBySequenceNumDesc(UUID dreamId);

    // 获取指定分支中序号大于指定值的所有节点（用于回滚删除）
    List<TimelineNode> findByDreamIdAndBranchIdAndSequenceNumGreaterThanOrderBySequenceNumDesc(
            UUID dreamId, UUID branchId, Integer sequenceNum);

    // 删除梦境的所有时间轴节点
    void deleteByDreamId(UUID dreamId);
}
