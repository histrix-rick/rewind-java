package com.rewindai.system.daydream.repository;

import com.rewindai.system.daydream.entity.DreamContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 梦境上下文 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface DreamContextRepository extends JpaRepository<DreamContext, UUID> {

    Optional<DreamContext> findByDreamIdAndNodeId(UUID dreamId, UUID nodeId);

    List<DreamContext> findByDreamIdOrderByCreatedAtDesc(UUID dreamId);

    default Optional<DreamContext> findFirstByDreamIdOrderByCreatedAtDesc(UUID dreamId) {
        List<DreamContext> list = findByDreamIdOrderByCreatedAtDesc(dreamId);
        return list.isEmpty() ? Optional.empty() : Optional.of(list.get(0));
    }

    void deleteByDreamId(UUID dreamId);
}
