package com.rewindai.system.user.repository;

import com.rewindai.system.user.entity.UserAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * 用户属性 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface UserAttributeRepository extends JpaRepository<UserAttribute, UUID> {

    Optional<UserAttribute> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);
}
