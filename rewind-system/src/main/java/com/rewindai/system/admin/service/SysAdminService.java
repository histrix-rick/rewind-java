package com.rewindai.system.admin.service;

import com.rewindai.system.admin.entity.SysAdmin;
import com.rewindai.system.admin.entity.SysAdminRole;
import com.rewindai.system.admin.repository.SysAdminRepository;
import com.rewindai.system.admin.repository.SysAdminRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.List;
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
    private final SysAdminRoleRepository sysAdminRoleRepository;

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

    public Page<SysAdmin> findAll(Pageable pageable) {
        return sysAdminRepository.findAll(pageable);
    }

    public Page<SysAdmin> searchAdmins(String keyword, Pageable pageable) {
        return sysAdminRepository.searchAdmins(keyword, pageable);
    }

    public SysAdmin save(SysAdmin admin) {
        return sysAdminRepository.save(admin);
    }

    @Transactional
    public void delete(Integer adminId) {
        sysAdminRoleRepository.deleteByAdminId(adminId);
        sysAdminRepository.deleteById(adminId);
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

    @Transactional
    public void assignRoles(Integer adminId, List<Long> roleIds) {
        sysAdminRoleRepository.deleteByAdminId(adminId);
        if (roleIds != null && !roleIds.isEmpty()) {
            for (Long roleId : roleIds) {
                SysAdminRole ar = SysAdminRole.builder()
                        .adminId(adminId)
                        .roleId(roleId)
                        .build();
                sysAdminRoleRepository.save(ar);
            }
        }
    }

    public List<Long> getRoleIdsByAdminId(Integer adminId) {
        return sysAdminRoleRepository.findRoleIdsByAdminId(adminId);
    }
}
