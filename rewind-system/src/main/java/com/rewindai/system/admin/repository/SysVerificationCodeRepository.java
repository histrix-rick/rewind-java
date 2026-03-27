package com.rewindai.system.admin.repository;

import com.rewindai.system.admin.entity.SysVerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

/**
 * 验证码 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface SysVerificationCodeRepository extends JpaRepository<SysVerificationCode, Long> {

    Optional<SysVerificationCode> findFirstByTargetAndSceneAndIsUsedFalseAndExpireAtAfterOrderByCreatedAtDesc(
            String target, String scene, OffsetDateTime now
    );

    List<SysVerificationCode> findByTargetAndSceneAndIsUsedFalseAndExpireAtAfter(
            String target, String scene, OffsetDateTime now
    );
}
