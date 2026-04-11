package com.rewindai.system.daydream.repository;

import com.rewindai.system.daydream.entity.RelationshipType;
import com.rewindai.system.daydream.enums.RelationshipCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 关系类型 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface RelationshipTypeRepository extends JpaRepository<RelationshipType, Long> {

    List<RelationshipType> findByIsActiveTrueOrderBySortOrderAsc();

    List<RelationshipType> findByCategoryAndIsActiveTrueOrderBySortOrderAsc(RelationshipCategory category);
}
