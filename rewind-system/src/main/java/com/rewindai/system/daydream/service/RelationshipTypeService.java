package com.rewindai.system.daydream.service;

import com.rewindai.system.daydream.entity.RelationshipType;
import com.rewindai.system.daydream.repository.RelationshipTypeRepository;
import com.rewindai.system.daydream.enums.RelationshipCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 关系类型 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class RelationshipTypeService {

    private final RelationshipTypeRepository relationshipTypeRepository;

    public Optional<RelationshipType> findById(Long id) {
        return relationshipTypeRepository.findById(id);
    }

    public List<RelationshipType> getActiveTypes() {
        List<RelationshipType> types = relationshipTypeRepository.findByIsActiveTrueOrderBySortOrderAsc();
        log.info("查询关系类型数据，数量: {}", types.size());
        return types;
    }

    public List<RelationshipType> getTypesByCategory(RelationshipCategory category) {
        return relationshipTypeRepository.findByCategoryAndIsActiveTrueOrderBySortOrderAsc(category);
    }
}
