package com.rewindai.system.daydream.repository;

import com.rewindai.system.daydream.entity.DreamBranch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 梦境分支 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface DreamBranchRepository extends JpaRepository<DreamBranch, UUID> {

    List<DreamBranch> findByDreamId(UUID dreamId);
}
