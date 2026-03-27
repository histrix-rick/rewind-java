package com.rewindai.system.daydream.service;

import com.rewindai.system.daydream.entity.UserIdentity;
import com.rewindai.system.daydream.repository.UserIdentityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * 用户身份预设 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserIdentityService {

    private final UserIdentityRepository userIdentityRepository;

    public Optional<UserIdentity> findById(Long id) {
        return userIdentityRepository.findById(id);
    }

    public List<UserIdentity> getActiveIdentities() {
        List<UserIdentity> identities = userIdentityRepository.findByIsActiveTrueOrderBySortOrderAsc();
        log.info("查询用户身份数据，数量: {}", identities.size());
        return identities;
    }

    public List<UserIdentity> getIdentitiesByAge(Integer age) {
        return userIdentityRepository.findByMinAgeLessThanEqualAndMaxAgeGreaterThanEqualAndIsActiveTrueOrderBySortOrderAsc(
                age, age);
    }
}
