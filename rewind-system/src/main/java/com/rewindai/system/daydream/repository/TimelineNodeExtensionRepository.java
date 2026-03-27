package com.rewindai.system.daydream.repository;

import com.rewindai.system.daydream.entity.TimelineNodeExtension;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * 时间轴节点扩展 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface TimelineNodeExtensionRepository extends JpaRepository<TimelineNodeExtension, UUID> {

    Optional<TimelineNodeExtension> findByNodeId(UUID nodeId);
}
