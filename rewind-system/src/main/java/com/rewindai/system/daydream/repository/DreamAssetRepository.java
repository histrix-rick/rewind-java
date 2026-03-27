package com.rewindai.system.daydream.repository;

import com.rewindai.system.daydream.entity.DreamAsset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 梦境资产 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface DreamAssetRepository extends JpaRepository<DreamAsset, UUID> {

    List<DreamAsset> findByDreamIdAndIsActiveOrderByCreatedAtAsc(UUID dreamId, Boolean isActive);

    List<DreamAsset> findByDreamIdAndNodeIdOrderByCreatedAtAsc(UUID dreamId, UUID nodeId);

    List<DreamAsset> findByDreamIdOrderByCreatedAtAsc(UUID dreamId);

    void deleteByDreamIdAndNodeId(UUID dreamId, UUID nodeId);
}
