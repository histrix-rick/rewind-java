package com.rewindai.system.admin.service;

import com.rewindai.system.admin.entity.SysVerificationCode;
import com.rewindai.system.admin.repository.SysVerificationCodeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Random;

/**
 * 验证码 Service
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SysVerificationCodeService {

    private final SysVerificationCodeRepository verificationCodeRepository;

    private static final int CODE_EXPIRE_MINUTES = 10;
    private static final String SCENE_ADMIN_LOGIN = "ADMIN_LOGIN";
    private static final String SCENE_ADMIN_PWD_RESET = "ADMIN_PWD_RESET";

    private final Random random = new Random();

    /**
     * 发送验证码
     */
    public String sendCode(String receiver, String type) {
        String code = generateCode();

        SysVerificationCode verificationCode = SysVerificationCode.builder()
                .receiver(receiver)
                .code(code)
                .type(type)
                .isUsed(false)
                .expireAt(OffsetDateTime.now().plusMinutes(CODE_EXPIRE_MINUTES))
                .build();

        verificationCodeRepository.save(verificationCode);

        // 演示环境：直接输出验证码到日志
        log.info("========================================");
        log.info("验证码已发送:");
        log.info("  接收者: {}", receiver);
        log.info("  类型: {}", type);
        log.info("  验证码: {}", code);
        log.info("  有效期: {} 分钟", CODE_EXPIRE_MINUTES);
        log.info("========================================");

        return code;
    }

    /**
     * 验证验证码
     */
    public boolean verifyCode(String receiver, String type, String code) {
        Optional<SysVerificationCode> opt = verificationCodeRepository
                .findFirstByReceiverAndTypeAndIsUsedFalseAndExpireAtAfterOrderByCreatedAtDesc(
                        receiver, type, OffsetDateTime.now()
                );

        if (opt.isEmpty()) {
            return false;
        }

        SysVerificationCode verificationCode = opt.get();
        if (!verificationCode.getCode().equals(code)) {
            return false;
        }

        verificationCode.setIsUsed(true);
        verificationCodeRepository.save(verificationCode);
        return true;
    }

    /**
     * 生成6位数字验证码
     */
    private String generateCode() {
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }
}
