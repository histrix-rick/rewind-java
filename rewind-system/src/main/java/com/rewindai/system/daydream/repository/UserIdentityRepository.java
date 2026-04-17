package com.rewindai.system.daydream.repository;

import com.rewindai.system.daydream.entity.UserIdentity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 用户身份预设 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface UserIdentityRepository extends JpaRepository<UserIdentity, Long> {

    List<UserIdentity> findByIsActiveTrueOrderBySortOrderAsc();

    List<UserIdentity> findAllByOrderBySortOrderAsc();

    List<UserIdentity> findByMinAgeLessThanEqualAndMaxAgeGreaterThanEqualAndIsActiveTrueOrderBySortOrderAsc(
            Integer minAge, Integer maxAge);

    List<UserIdentity> findByUserIdIsNullAndIsActiveTrueOrderBySortOrderAsc();

    List<UserIdentity> findByUserIdAndIsActiveTrueOrderBySortOrderAsc(UUID userId);
}
