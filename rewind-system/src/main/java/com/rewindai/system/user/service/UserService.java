package com.rewindai.system.user.service;

import com.rewindai.system.user.entity.User;
import com.rewindai.system.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 用户 Service
 *
 * @author Rewind.ai Team
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findById(UUID id) {
        return userRepository.findById(id);
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> findByPhoneNumber(String phoneNumber) {
        return userRepository.findByPhoneNumber(phoneNumber);
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    /**
     * 批量查询用户
     */
    public List<User> findAllByIds(Iterable<UUID> ids) {
        return userRepository.findAllByIdIn(ids);
    }

    /**
     * 根据账号查找用户（支持用户名/手机号/邮箱）
     */
    public Optional<User> findByAccount(String account) {
        Optional<User> user = findByUsername(account);
        if (user.isPresent()) {
            return user;
        }
        user = findByPhoneNumber(account);
        if (user.isPresent()) {
            return user;
        }
        return findByEmail(account);
    }

    public User save(User user) {
        return userRepository.save(user);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return userRepository.existsByPhoneNumber(phoneNumber);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * 更新登录信息
     */
    @Transactional
    public void updateLoginInfo(UUID userId, String ip, String deviceId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setLastLoginTime(OffsetDateTime.now());
            user.setLastLoginIp(ip);
            user.setLastLoginDevice(deviceId);
            userRepository.save(user);
        });
    }
}
