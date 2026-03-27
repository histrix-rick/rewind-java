package com.rewindai.system.user.service;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.system.user.entity.UserAttribute;
import com.rewindai.system.user.repository.UserAttributeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * 用户属性 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AttributeService {

    private final UserAttributeRepository userAttributeRepository;

    public Optional<UserAttribute> findByUserId(UUID userId) {
        return userAttributeRepository.findByUserId(userId);
    }

    public UserAttribute getOrCreateAttribute(UUID userId) {
        return userAttributeRepository.findByUserId(userId)
                .orElseGet(() -> createAttribute(userId));
    }

    @Transactional
    public UserAttribute createAttribute(UUID userId) {
        UserAttribute attribute = UserAttribute.builder()
                .userId(userId)
                .financialPower(50)
                .intelligence(50)
                .physicalPower(50)
                .charisma(50)
                .luck(50)
                .build();
        return userAttributeRepository.save(attribute);
    }

    @Transactional
    public UserAttribute updateAttribute(UUID userId, Integer financialPower, Integer intelligence,
                                          Integer physicalPower, Integer charisma, Integer luck) {
        UserAttribute attribute = getOrCreateAttribute(userId);

        if (financialPower != null) {
            attribute.setFinancialPower(clamp(financialPower, 0, 100));
        }
        if (intelligence != null) {
            attribute.setIntelligence(clamp(intelligence, 0, 100));
        }
        if (physicalPower != null) {
            attribute.setPhysicalPower(clamp(physicalPower, 0, 100));
        }
        if (charisma != null) {
            attribute.setCharisma(clamp(charisma, 0, 100));
        }
        if (luck != null) {
            attribute.setLuck(clamp(luck, 0, 100));
        }

        return userAttributeRepository.save(attribute);
    }

    public Map<String, Object> getAttributeSnapshot(UserAttribute attribute) {
        Map<String, Object> snapshot = new HashMap<>();
        snapshot.put("financialPower", attribute.getFinancialPower());
        snapshot.put("intelligence", attribute.getIntelligence());
        snapshot.put("physicalPower", attribute.getPhysicalPower());
        snapshot.put("charisma", attribute.getCharisma());
        snapshot.put("luck", attribute.getLuck());
        return snapshot;
    }

    private int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
