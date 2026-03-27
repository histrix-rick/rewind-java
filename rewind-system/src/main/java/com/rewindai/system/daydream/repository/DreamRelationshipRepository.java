package com.rewindai.system.daydream.repository;

import com.rewindai.system.daydream.entity.DreamRelationship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 梦境人物关系 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface DreamRelationshipRepository extends JpaRepository<DreamRelationship, UUID> {

    List<DreamRelationship> findByDreamIdAndNodeIdOrderByCreatedAtAsc(UUID dreamId, UUID nodeId);

    List<DreamRelationship> findByDreamIdOrderByCreatedAtAsc(UUID dreamId);

    void deleteByDreamIdAndNodeId(UUID dreamId, UUID nodeId);
}
