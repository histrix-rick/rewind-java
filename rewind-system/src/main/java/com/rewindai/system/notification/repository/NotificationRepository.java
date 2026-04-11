package com.rewindai.system.notification.repository;

import com.rewindai.system.notification.entity.Notification;
import com.rewindai.system.notification.enums.NotificationType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * 通知 Repository
 *
 * @author Rewind.ai Team
 */
@Repository
public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    /**
     * 查询用户的通知列表（分页）
     */
    Page<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    /**
     * 查询用户的未读通知列表
     */
    Page<Notification> findByUserIdAndIsReadFalseOrderByCreatedAtDesc(UUID userId, Pageable pageable);

    /**
     * 统计用户未读通知数量
     */
    long countByUserIdAndIsReadFalse(UUID userId);

    /**
     * 标记单条通知为已读
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.id = :id AND n.userId = :userId")
    int markAsRead(@Param("id") UUID id, @Param("userId") UUID userId);

    /**
     * 标记用户所有通知为已读
     */
    @Modifying
    @Query("UPDATE Notification n SET n.isRead = true WHERE n.userId = :userId")
    int markAllAsRead(@Param("userId") UUID userId);

    /**
     * 删除用户的所有通知
     */
    @Modifying
    @Query("DELETE FROM Notification n WHERE n.userId = :userId")
    int deleteAllByUserId(@Param("userId") UUID userId);

    // ========== 后台管理查询方法 ==========

    /**
     * 后台管理：按用户ID查询
     */
    Page<Notification> findByUserId(UUID userId, Pageable pageable);

    /**
     * 后台管理：按类型查询
     */
    Page<Notification> findByType(NotificationType type, Pageable pageable);

    /**
     * 后台管理：按已读状态查询
     */
    Page<Notification> findByIsRead(Boolean isRead, Pageable pageable);

    /**
     * 后台管理：按用户和类型查询
     */
    Page<Notification> findByUserIdAndType(UUID userId, NotificationType type, Pageable pageable);

    /**
     * 后台管理：查询所有（用于统计）
     */
    @Override
    List<Notification> findAll(Sort sort);

    /**
     * 后台管理：统计总数
     */
    @Query("SELECT COUNT(n) FROM Notification n")
    long countAll();

    /**
     * 后台管理：统计未读总数
     */
    @Query("SELECT COUNT(n) FROM Notification n WHERE n.isRead = false")
    long countUnread();
}
