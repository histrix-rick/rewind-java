package com.rewindai.auth.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rewindai.auth.config.IdCardCheckProperties;
import com.rewindai.common.core.exception.BusinessException;
import com.rewindai.common.core.result.ErrorCode;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * 身份证二要素认证服务
 * 阿里云市场 API: https://market.aliyun.com/detail/cmapi00066570
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class IdCardCheckService {

    private final IdCardCheckProperties properties;
    private final RestTemplate restTemplate;

    /**
     * 验证身份证号和姓名是否一致
     *
     * @param idCardNo 身份证号
     * @param name     姓名
     * @return 验证结果
     */
    public IdCardCheckResult verify(String idCardNo, String name) {
        if (!properties.isEnabled()) {
            log.warn("身份证二要素认证已禁用，跳过验证");
            return IdCardCheckResult.pass();
        }

        try {
            // 构建请求URL
            URI uri = UriComponentsBuilder.fromHttpUrl(properties.getApiUrl())
                    .build()
                    .toUri();

            // 设置请求头
            HttpHeaders headers = new HttpHeaders();
            headers.set("Authorization", "APPCODE " + properties.getAppCode());
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            // 构建表单参数
            org.springframework.util.MultiValueMap<String, String> body = new org.springframework.util.LinkedMultiValueMap<>();
            body.add("name", name);
            body.add("idcard", idCardNo);

            HttpEntity<org.springframework.util.MultiValueMap<String, String>> entity = new HttpEntity<>(body, headers);

            log.info("调用身份证二要素认证API: name={}, idCard={}", name, maskIdCard(idCardNo));

            // 先作为String获取响应，查看实际格式
            ResponseEntity<String> stringResponse = restTemplate.exchange(
                    uri,
                    HttpMethod.POST,
                    entity,
                    String.class
            );

            String responseBody = stringResponse.getBody();
            log.info("身份证二要素认证原始响应: {}", responseBody);

            // 使用ObjectMapper从String解析为对象，避免重复调用API
            IdCardCheckApiResponse apiResponse;
            try {
                apiResponse = new com.fasterxml.jackson.databind.ObjectMapper().readValue(responseBody, IdCardCheckApiResponse.class);
            } catch (Exception e) {
                log.error("解析身份证认证响应失败", e);
                throw new BusinessException(ErrorCode.BAD_REQUEST, "身份证认证服务响应解析失败");
            }

            if (apiResponse == null) {
                log.error("身份证二要素认证API返回空响应");
                throw new BusinessException(ErrorCode.BAD_REQUEST, "身份证认证服务暂时不可用");
            }

            log.info("身份证二要素认证结果: code={}, message={}", apiResponse.getCode(), apiResponse.getMessage());

            // 解析响应 - code=200表示API调用成功
            if ("200".equals(apiResponse.getCode()) || "0000".equals(apiResponse.getCode())) {
                // 成功
                IdCardCheckApiResponse.Result result = apiResponse.getResult();
                if (result != null) {
                    // data.result=0 表示一致（通过），1 表示不一致（失败）
                    if (Integer.valueOf(0).equals(result.getResult())) {
                        // 实名认证通过
                        return IdCardCheckResult.pass();
                    } else {
                        // 实名认证不通过
                        String desc = result.getDesc() != null ? result.getDesc() :
                                       result.getDescription() != null ? result.getDescription() :
                                       "姓名和身份证号不匹配";
                        return IdCardCheckResult.fail(desc);
                    }
                } else {
                    // 没有result，返回失败
                    log.error("响应中没有result字段");
                    return IdCardCheckResult.fail("身份证认证失败，请重试");
                }
            } else {
                // API调用失败
                log.error("身份证二要素认证API调用失败: code={}, message={}", apiResponse.getCode(), apiResponse.getMessage());
                throw new BusinessException(ErrorCode.BAD_REQUEST, "身份证认证服务暂时不可用: " + apiResponse.getMessage());
            }

        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("身份证二要素认证异常", e);
            throw new BusinessException(ErrorCode.BAD_REQUEST, "身份证认证服务暂时不可用");
        }
    }

    /**
     * 脱敏身份证号
     */
    private String maskIdCard(String idCardNo) {
        if (idCardNo == null || idCardNo.length() < 8) {
            return "****";
        }
        return idCardNo.substring(0, 4) + "****" + idCardNo.substring(idCardNo.length() - 4);
    }

    /**
     * 身份证认证结果
     */
    @Data
    public static class IdCardCheckResult {
        private final boolean passed;
        private final String message;

        private IdCardCheckResult(boolean passed, String message) {
            this.passed = passed;
            this.message = message;
        }

        public static IdCardCheckResult pass() {
            return new IdCardCheckResult(true, "实名认证通过");
        }

        public static IdCardCheckResult fail(String message) {
            return new IdCardCheckResult(false, message);
        }
    }

    /**
     * API响应结构
     */
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class IdCardCheckApiResponse {
        @JsonProperty("code")
        private String code;

        @JsonProperty("msg")
        private String message;

        @JsonProperty("data")
        private Result result;

        @JsonProperty("seqid")
        private String seqId;

        @JsonProperty("res")
        private Object res;

        @JsonProperty("desc")
        private String desc;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        private static class Result {
            @JsonProperty("status")
            private String status;

            @JsonProperty("description")
            private String description;

            @JsonProperty("desc")
            private String desc;

            @JsonProperty("idCard")
            private String idCard;

            @JsonProperty("name")
            private String name;

            @JsonProperty("sex")
            private String sex;

            @JsonProperty("birthday")
            private String birthday;

            @JsonProperty("address")
            private String address;

            @JsonProperty("res")
            private Object res;

            @JsonProperty("result")
            private Integer result;
        }
    }
}
