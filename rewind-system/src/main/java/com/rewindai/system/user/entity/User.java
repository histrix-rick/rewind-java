package com.rewindai.system.user.entity;

import com.rewindai.system.user.enums.Gender;
import com.rewindai.system.user.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

/**
 * 用户主体表实体
 *
 * @author Rewind.ai Team
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users", indexes = {
        @Index(name = "idx_users_username", columnList = "username"),
        @Index(name = "idx_users_phone", columnList = "phoneNumber"),
        @Index(name = "idx_users_email", columnList = "email"),
        @Index(name = "idx_users_status", columnList = "status")
})
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "username", unique = true, nullable = false, length = 50)
    private String username;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Column(name = "nickname", length = 50)
    private String nickname;

    @Column(name = "avatar_url", columnDefinition = "TEXT")
    private String avatarUrl;

    @Column(name = "gender", nullable = false)
    @Convert(converter = GenderConverter.class)
    private Gender gender;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(name = "phone_number", unique = true, length = 20)
    private String phoneNumber;

    @Column(name = "email", unique = true, length = 100)
    private String email;

    @Column(name = "real_name", length = 50)
    private String realName;

    @Column(name = "id_card_no", length = 18)
    private String idCardNo;

    @Column(name = "status")
    @Convert(converter = UserStatusConverter.class)
    private UserStatus status;

    @Column(name = "register_ip", length = 45)
    private String registerIp;

    @Column(name = "register_device_id", length = 100)
    private String registerDeviceId;

    @Column(name = "last_login_time")
    private OffsetDateTime lastLoginTime;

    @Column(name = "last_login_ip", length = 45)
    private String lastLoginIp;

    @Column(name = "last_login_device", length = 100)
    private String lastLoginDevice;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.gender == null) {
            this.gender = Gender.SECRET;
        }
        if (this.status == null) {
            this.status = UserStatus.NORMAL;
        }
    }

    @Converter
    public static class GenderConverter implements AttributeConverter<Gender, Integer> {
        @Override
        public Integer convertToDatabaseColumn(Gender gender) {
            return gender != null ? gender.getCode() : Gender.SECRET.getCode();
        }

        @Override
        public Gender convertToEntityAttribute(Integer code) {
            return code != null ? Gender.fromCode(code) : Gender.SECRET;
        }
    }

    @Converter
    public static class UserStatusConverter implements AttributeConverter<UserStatus, Integer> {
        @Override
        public Integer convertToDatabaseColumn(UserStatus status) {
            return status != null ? status.getCode() : UserStatus.NORMAL.getCode();
        }

        @Override
        public UserStatus convertToEntityAttribute(Integer code) {
            return code != null ? UserStatus.fromCode(code) : UserStatus.NORMAL;
        }
    }
}
