package com.rewindai.system.daydream.service;

import com.rewindai.system.daydream.entity.UserIdentity;
import com.rewindai.system.daydream.repository.UserIdentityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

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

    /**
     * 获取所有激活的系统身份和用户自定义身份
     */
    public List<UserIdentity> getActiveIdentities() {
        List<UserIdentity> identities = userIdentityRepository.findByIsActiveTrueOrderBySortOrderAsc();
        log.info("查询用户身份数据，数量: {}", identities.size());
        return identities;
    }

    /**
     * 获取系统身份（userId 为 null 的）
     */
    public List<UserIdentity> getSystemIdentities() {
        return userIdentityRepository.findByUserIdIsNullAndIsActiveTrueOrderBySortOrderAsc();
    }

    /**
     * 获取用户的自定义身份
     */
    public List<UserIdentity> getUserIdentities(UUID userId) {
        return userIdentityRepository.findByUserIdAndIsActiveTrueOrderBySortOrderAsc(userId);
    }

    /**
     * 获取系统身份 + 用户自定义身份
     */
    public List<UserIdentity> getAllIdentitiesForUser(UUID userId) {
        List<UserIdentity> systemIdentities = getSystemIdentities();
        List<UserIdentity> userIdentities = getUserIdentities(userId);
        // 用户自定义身份在前，系统身份在后
        userIdentities.addAll(systemIdentities);
        return userIdentities;
    }

    public List<UserIdentity> getIdentitiesByAge(Integer age) {
        return userIdentityRepository.findByMinAgeLessThanEqualAndMaxAgeGreaterThanEqualAndIsActiveTrueOrderBySortOrderAsc(
                age, age);
    }

    /**
     * 创建用户自定义身份
     */
    public UserIdentity createUserIdentity(UUID userId, UserIdentity identity) {
        identity.setUserId(userId);
        identity.setIsActive(true);
        // 用户自定义身份排序在前
        identity.setSortOrder(-1);
        return userIdentityRepository.save(identity);
    }

    /**
     * 删除用户自定义身份
     */
    public void deleteUserIdentity(UUID userId, Long identityId) {
        Optional<UserIdentity> identityOpt = userIdentityRepository.findById(identityId);
        if (identityOpt.isPresent()) {
            UserIdentity identity = identityOpt.get();
            // 只能删除自己的身份
            if (userId.equals(identity.getUserId())) {
                identity.setIsActive(false);
                userIdentityRepository.save(identity);
            }
        }
    }
}
