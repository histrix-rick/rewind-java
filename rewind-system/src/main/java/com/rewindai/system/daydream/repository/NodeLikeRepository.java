package com.rewindai.system.daydream.repository;

import com.rewindai.system.daydream.entity.NodeLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * 时间轴节点点赞 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface NodeLikeRepository extends JpaRepository<NodeLike, UUID> {

    Optional<NodeLike> findByNodeIdAndUserId(UUID nodeId, UUID userId);

    boolean existsByNodeIdAndUserId(UUID nodeId, UUID userId);

    void deleteByNodeIdAndUserId(UUID nodeId, UUID userId);

    long countByNodeId(UUID nodeId);
}
