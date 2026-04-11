package com.rewindai.system.user.repository;

import com.rewindai.system.user.entity.User;
import com.rewindai.system.user.enums.UserStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * 用户 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByPhoneNumber(String phoneNumber);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByPhoneNumber(String phoneNumber);

    boolean existsByEmail(String email);

    /**
     * 批量查询用户
     */
    List<User> findAllByIdIn(Iterable<UUID> ids);

    /**
     * 后台管理：分页查询所有用户
     */
    @Override
    Page<User> findAll(Pageable pageable);

    /**
     * 后台管理：按状态分页查询用户
     */
    Page<User> findByStatus(UserStatus status, Pageable pageable);

    /**
     * 后台管理：搜索用户（用户名/昵称/手机号/邮箱）
     */
    @Query("SELECT u FROM User u WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "u.username LIKE %:keyword% OR " +
           "u.nickname LIKE %:keyword% OR " +
           "u.phoneNumber LIKE %:keyword% OR " +
           "u.email LIKE %:keyword%)")
    Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);

    /**
     * 统计：按状态统计用户数
     */
    long countByStatus(com.rewindai.system.user.enums.UserStatus status);

    /**
     * 统计：指定时间之后注册的用户数
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :after")
    long countByCreatedAtAfter(@Param("after") java.time.OffsetDateTime after);

    /**
     * 统计：指定时间范围内注册的用户数
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :start AND u.createdAt < :end")
    long countByCreatedAtBetween(@Param("start") java.time.OffsetDateTime start, @Param("end") java.time.OffsetDateTime end);

    /**
     * 导出：查询指定时间范围内的用户
     */
    @Query("SELECT u FROM User u WHERE u.createdAt >= :start AND u.createdAt < :end ORDER BY u.createdAt DESC")
    List<User> findByDateRangeForExport(@Param("start") java.time.OffsetDateTime start, @Param("end") java.time.OffsetDateTime end);
}
