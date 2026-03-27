package com.rewindai.system.admin.service;

import com.rewindai.system.admin.entity.SysAdmin;
import com.rewindai.system.admin.repository.SysAdminRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * 管理员 Service
 *
 * @author Rewind.ai Team
 */
@Service
@RequiredArgsConstructor
public class SysAdminService {

    private final SysAdminRepository sysAdminRepository;

    public Optional<SysAdmin> findById(Integer id) {
        return sysAdminRepository.findById(id);
    }

    public Optional<SysAdmin> findById(Long id) {
        return sysAdminRepository.findById(id.intValue());
    }

    public Optional<SysAdmin> findByUsername(String username) {
        return sysAdminRepository.findByUsername(username);
    }

    public Optional<SysAdmin> findByPhoneNumber(String phoneNumber) {
        return sysAdminRepository.findByPhoneNumber(phoneNumber);
    }

    public Optional<SysAdmin> findByEmail(String email) {
        return sysAdminRepository.findByEmail(email);
    }

    public Optional<SysAdmin> findByUsernameOrEmail(String username, String email) {
        return sysAdminRepository.findByUsernameOrEmail(username, email);
    }

    public SysAdmin save(SysAdmin admin) {
        return sysAdminRepository.save(admin);
    }

    public void updateLoginInfo(Integer adminId, String ip) {
        findById(adminId).ifPresent(admin -> {
            admin.setLastLoginAt(OffsetDateTime.now());
            save(admin);
        });
    }

    public void updateLoginInfo(Long adminId, String ip) {
        updateLoginInfo(adminId.intValue(), ip);
    }

    public void updatePassword(Integer adminId, String newPasswordHash) {
        findById(adminId).ifPresent(admin -> {
            admin.setPasswordHash(newPasswordHash);
            admin.setIsDefaultPassword(false);
            admin.setLastPwdChangeAt(OffsetDateTime.now());
            save(admin);
        });
    }

    public void updatePassword(Long adminId, String newPasswordHash) {
        updatePassword(adminId.intValue(), newPasswordHash);
    }

    public boolean existsByUsername(String username) {
        return sysAdminRepository.existsByUsername(username);
    }

    public boolean existsByPhoneNumber(String phoneNumber) {
        return sysAdminRepository.existsByPhoneNumber(phoneNumber);
    }

    public boolean existsByEmail(String email) {
        return sysAdminRepository.existsByEmail(email);
    }
}
