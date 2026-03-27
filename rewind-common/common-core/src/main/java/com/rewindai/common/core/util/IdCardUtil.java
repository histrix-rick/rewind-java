package com.rewindai.common.core.util;

import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

/**
 * 身份证号工具类
 *
 * @author Rewind.ai Team
 */
@Slf4j
public class IdCardUtil {

    private static final Pattern ID_CARD_PATTERN = Pattern.compile("^[1-9]\\d{5}(18|19|20)\\d{2}((0[1-9])|(1[0-2]))(([0-2][1-9])|10|20|30|31)\\d{3}[\\dXx]$");

    private static final int[] WEIGHTS = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    private static final char[] CHECK_CODES = {'1', '0', 'X', '9', '8', '7', '6', '5', '4', '3', '2'};

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private IdCardUtil() {
    }

    /**
     * 验证身份证号是否有效
     *
     * @param idCard 身份证号
     * @return 是否有效
     */
    public static boolean isValid(String idCard) {
        if (idCard == null || idCard.length() != 18) {
            return false;
        }

        if (!ID_CARD_PATTERN.matcher(idCard).matches()) {
            return false;
        }

        return verifyCheckCode(idCard);
    }

    /**
     * 验证身份证号并抛出异常
     *
     * @param idCard 身份证号
     */
    public static void validate(String idCard) {
        if (!isValid(idCard)) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "身份证号格式不正确");
        }
    }

    /**
     * 从身份证号提取出生日期
     *
     * @param idCard 身份证号
     * @return 出生日期
     */
    public static LocalDate extractBirthDate(String idCard) {
        validate(idCard);
        String dateStr = idCard.substring(6, 14);
        return LocalDate.parse(dateStr, DATE_FORMATTER);
    }

    /**
     * 从身份证号提取性别
     *
     * @param idCard 身份证号
     * @return 性别：1-男，2-女
     */
    public static Integer extractGender(String idCard) {
        validate(idCard);
        char genderChar = idCard.charAt(16);
        int genderNum = Integer.parseInt(String.valueOf(genderChar));
        return genderNum % 2 == 1 ? 1 : 2;
    }

    /**
     * 验证校验码
     */
    private static boolean verifyCheckCode(String idCard) {
        int sum = 0;
        for (int i = 0; i < 17; i++) {
            sum += (idCard.charAt(i) - '0') * WEIGHTS[i];
        }
        int mod = sum % 11;
        char expectedCheckCode = CHECK_CODES[mod];
        char actualCheckCode = Character.toUpperCase(idCard.charAt(17));
        return expectedCheckCode == actualCheckCode;
    }
}
