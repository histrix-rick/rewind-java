package com.rewindai.auth.service;

import com.rewindai.auth.dto.*;
import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import com.rewindai.common.core.util.IdCardUtil;
import com.rewindai.common.security.util.JwtUtil;
import com.rewindai.system.admin.entity.SysAdmin;
import com.rewindai.system.admin.enums.AdminStatus;
import com.rewindai.system.admin.service.SysAdminService;
import com.rewindai.system.admin.service.SysVerificationCodeService;
import com.rewindai.system.user.entity.User;
import com.rewindai.system.user.enums.Gender;
import com.rewindai.system.user.enums.UserStatus;
import com.rewindai.system.user.service.AttributeService;
import com.rewindai.system.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * 认证服务
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserService userService;
    private final AttributeService attributeService;
    private final SysAdminService sysAdminService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final SysVerificationCodeService verificationCodeService;
    private final IdCardCheckService idCardCheckService;

    private static final String SCENE_ADMIN_LOGIN = "ADMIN_LOGIN";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /**
     * 用户注册 - App端
     */
    @Transactional
    public LoginResponse register(RegisterRequest request, HttpServletRequest httpRequest) {
        // 校验两次密码是否一致
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "两次密码输入不一致");
        }

        // 检查用户名是否已存在
        if (userService.existsByUsername(request.getUsername())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "用户名已存在");
        }

        // 检查手机号是否已存在
        if (request.getPhone() != null && userService.existsByPhoneNumber(request.getPhone())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "手机号已存在");
        }

        // 检查邮箱是否已存在
        if (request.getEmail() != null && userService.existsByEmail(request.getEmail())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "邮箱已存在");
        }

        // 验证身份证号格式
        IdCardUtil.validate(request.getIdCardNo());
        LocalDate birthDateFromIdCard = IdCardUtil.extractBirthDate(request.getIdCardNo());
        Integer genderFromIdCard = IdCardUtil.extractGender(request.getIdCardNo());

        // 身份证二要素认证
        log.info("开始身份证二要素认证: name={}", request.getRealName());
        IdCardCheckService.IdCardCheckResult checkResult = idCardCheckService.verify(request.getIdCardNo(), request.getRealName());
        if (!checkResult.isPassed()) {
            log.warn("身份证二要素认证失败: name={}, message={}", request.getRealName(), checkResult.getMessage());
            throw new BusinessException(ErrorCode.BAD_REQUEST, checkResult.getMessage());
        }
        log.info("身份证二要素认证成功: name={}", request.getRealName());

        // 创建用户
        String ip = getClientIp(httpRequest);
        String deviceId = httpRequest.getHeader("X-Device-Id");

        User user = User.builder()
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .nickname(request.getUsername())
                .realName(request.getRealName())
                .idCardNo(request.getIdCardNo())
                .gender(Gender.fromCode(genderFromIdCard))
                .birthDate(birthDateFromIdCard)
                .phoneNumber(request.getPhone())
                .email(request.getEmail())
                .status(UserStatus.NORMAL)
                .registerIp(ip)
                .registerDeviceId(deviceId)
                .build();

        user = userService.save(user);
        log.info("用户注册成功: username={}, userId={}", user.getUsername(), user.getId());

        // 初始化用户属性
        attributeService.createAttribute(user.getId());
        log.info("用户属性初始化成功: userId={}", user.getId());

        // 更新登录信息
        userService.updateLoginInfo(user.getId(), ip, deviceId);

        // 生成Token并返回
        return generateLoginResponse(user);
    }

    /**
     * 用户登录 - App端
     */
    public LoginResponse login(LoginRequest request, HttpServletRequest httpRequest) {
        String account = request.getAccount();

        // 查找用户
        User user = userService.findByAccount(account)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));

        // 校验密码
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }

        // 检查账号状态
        if (user.getStatus() == UserStatus.BANNED) {
            throw new BusinessException(ErrorCode.USER_BANNED, "账号已被封禁");
        }
        if (user.getStatus() == UserStatus.MUTED) {
            log.warn("用户 {} 登录，账号处于禁言状态", user.getUsername());
        }

        // 更新登录信息
        String ip = getClientIp(httpRequest);
        String deviceId = httpRequest.getHeader("X-Device-Id");
        userService.updateLoginInfo(user.getId(), ip, deviceId);

        log.info("用户 {} (APP) 登录成功", user.getUsername());

        // 生成Token并返回
        return generateLoginResponse(user);
    }

    /**
     * 发送验证码
     */
    public void sendVerificationCode(SendCodeRequest request) {
        String email = request.getEmail();
        // 检查邮箱是否存在（管理员）
        if (sysAdminService.findByEmail(email).isEmpty()) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "该邮箱未注册");
        }
        String code = verificationCodeService.sendCode(email, SCENE_ADMIN_LOGIN);
        log.info("验证码已发送到: {}, 验证码: {}", email, code);
    }

    /**
     * 管理员登录 - Admin端（需要验证码）
     */
    public AdminLoginResponse adminLogin(AdminLoginRequest request, HttpServletRequest httpRequest) {
        String account = request.getAccount();

        // 查找管理员
        SysAdmin admin = sysAdminService.findByUsernameOrEmail(account, account)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        // 验证邮箱验证码
        if (!verificationCodeService.verifyCode(admin.getEmail(), SCENE_ADMIN_LOGIN, request.getVerificationCode())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "验证码错误或已过期");
        }

        // 校验密码
        if (!passwordEncoder.matches(request.getPassword(), admin.getPasswordHash())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR);
        }

        // 检查账号状态
        if (admin.getStatus() == AdminStatus.DISABLED) {
            throw new BusinessException(ErrorCode.ADMIN_DISABLED);
        }

        // 更新登录信息
        String ip = getClientIp(httpRequest);
        sysAdminService.updateLoginInfo(admin.getId(), ip);

        log.info("管理员 {} (ADMIN) 登录成功", admin.getUsername());

        // 生成Token并返回
        return generateAdminLoginResponse(admin);
    }

    /**
     * 修改密码
     */
    @Transactional
    public void changePassword(Long adminId, ChangePasswordRequest request) {
        // 校验两次新密码是否一致
        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            throw new BusinessException(ErrorCode.BAD_REQUEST, "两次新密码输入不一致");
        }

        // 查找管理员
        SysAdmin admin = sysAdminService.findById(adminId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ADMIN_NOT_FOUND));

        // 校验旧密码
        if (!passwordEncoder.matches(request.getOldPassword(), admin.getPasswordHash())) {
            throw new BusinessException(ErrorCode.PASSWORD_ERROR, "旧密码错误");
        }

        // 更新密码
        sysAdminService.updatePassword(adminId, passwordEncoder.encode(request.getNewPassword()));
        log.info("管理员 {} 修改密码成功", admin.getUsername());
    }

    private AdminLoginResponse generateAdminLoginResponse(SysAdmin admin) {
        String token = jwtUtil.generateAdminToken(admin.getId().longValue(), admin.getUsername());

        AdminLoginResponse.AdminInfo adminInfo = AdminLoginResponse.AdminInfo.builder()
                .adminId(admin.getId().longValue())
                .username(admin.getUsername())
                .email(admin.getEmail())
                .realName(admin.getNickname())
                .needChangePassword(admin.getIsDefaultPassword())
                .build();

        return AdminLoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationInSeconds())
                .adminInfo(adminInfo)
                .build();
    }

    private LoginResponse generateLoginResponse(User user) {
        String token = jwtUtil.generateAppToken(user.getId().toString(), user.getUsername());

        LoginResponse.UserInfo userInfo = LoginResponse.UserInfo.builder()
                .userId(user.getId().toString())
                .username(user.getUsername())
                .phone(user.getPhoneNumber())
                .email(user.getEmail())
                .gender(user.getGender().getCode())
                .birthDate(user.getBirthDate() != null ? user.getBirthDate().format(DATE_FORMATTER) : null)
                .nickname(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .build();

        return LoginResponse.builder()
                .accessToken(token)
                .tokenType("Bearer")
                .expiresIn(jwtUtil.getExpirationInSeconds())
                .userInfo(userInfo)
                .build();
    }

    private String getClientIp(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 对于多个代理的情况，取第一个IP
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
    }
}
