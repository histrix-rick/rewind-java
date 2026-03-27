package com.rewindai.system.aijudge.service;

import com.rewindai.system.aijudge.client.DoubaoApiClient;
import com.rewindai.system.aijudge.entity.AiJudgmentRule;
import com.rewindai.system.aijudge.enums.RuleType;
import com.rewindai.system.aijudge.repository.AiJudgmentRuleRepository;
import com.rewindai.system.daydream.entity.DreamAsset;
import com.rewindai.system.daydream.entity.DreamContext;
import com.rewindai.system.daydream.enums.AssetType;
import com.rewindai.system.daydream.repository.DreamContextRepository;
import com.rewindai.system.daydream.service.DreamAssetService;
import com.rewindai.system.user.entity.UserAttribute;
import com.rewindai.system.user.repository.UserAttributeRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * AI 现实判官 Service（增强版 - 支持资金验证和资产管理）
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AiJudgeService {

    private final AiJudgmentRuleRepository aiJudgmentRuleRepository;
    private final DoubaoApiClient doubaoApiClient;
    private final DreamContextRepository dreamContextRepository;
    private final DreamAssetService dreamAssetService;
    private final UserAttributeRepository userAttributeRepository;

    private static final List<String> REASONING_STEPS = Arrays.asList(
            "正在检索历史档案...",
            "正在分析历史背景...",
            "正在验证逻辑合理性...",
            "正在进行概率计算...",
            "正在生成判定结果..."
    );

    /**
     * AI 判定增强版 - 支持资金验证和资产上下文
     */
    public AiJudgmentResultEnhanced judgeEnhanced(
            String userDecision,
            LocalDate contextDate,
            UUID dreamId,
            UUID userId) {

        log.info("AI 判官开始判定(增强版): decision={}, date={}, dreamId={}",
                userDecision, contextDate, dreamId);

        List<String> reasoningTrace = new ArrayList<>(REASONING_STEPS);

        // 1. 获取当前资金状况
        DreamContext context = dreamContextRepository.findFirstByDreamIdOrderByCreatedAtDesc(dreamId)
                .orElse(null);
        BigDecimal currentCash = context != null ? context.getFinancialAmount() : BigDecimal.ZERO;

        // 2. 获取当前资产列表
        List<DreamAsset> assets = dreamAssetService.getActiveAssetsByDream(dreamId);

        // 3. 获取用户属性
        UserAttribute userAttr = userAttributeRepository.findByUserId(userId).orElse(null);

        // 4. 构建增强的AI请求
        String systemPrompt = buildEnhancedSystemPrompt(contextDate, currentCash, userAttr, assets);
        String userMessage = buildEnhancedUserMessage(userDecision, contextDate, currentCash, assets);

        // 5. 调用豆包API
        String response = doubaoApiClient.chat(systemPrompt, userMessage);

        // 6. 解析增强响应
        AiJudgmentResultEnhanced result = parseEnhancedResponse(response, reasoningTrace, currentCash);

        log.info("AI 判定结果(增强版): approved={}, summary={}", result.approved, result.summary);
        return result;
    }

    /**
     * 构建增强版系统提示词
     */
    private String buildEnhancedSystemPrompt(LocalDate contextDate, BigDecimal currentCash,
                                               UserAttribute userAttr, List<DreamAsset> assets) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
            你是"AI现实判官"，负责判断用户在梦境中的决策是否现实可行。

            【核心规则】
            1. 历史守恒定律：自然事件（如地震、疫情等）无法阻止，但允许改变个人命运
            2. 个人命运改变：允许找工作、买房、投资、学习、考试、帮助家人朋友等个人行为
            3. 重大历史事件：911事件、汶川地震、新冠疫情等不可改变
            4. 资金能力验证：必须考虑用户的实际资金能力！比如只有2000元不能买2000万的房子

            【当前上下文】
            """);
        sb.append("- 当前时间：").append(contextDate).append("\n");
        sb.append("- 当前现金余额：¥").append(currentCash).append("\n");

        if (userAttr != null) {
            sb.append("- 用户属性：\n");
            sb.append("  - 财力：").append(userAttr.getFinancialPower()).append("\n");
            sb.append("  - 智力：").append(userAttr.getIntelligence()).append("\n");
            sb.append("  - 体力：").append(userAttr.getPhysicalPower()).append("\n");
            sb.append("  - 魅力：").append(userAttr.getCharisma()).append("\n");
            sb.append("  - 运气：").append(userAttr.getLuck()).append("\n");
        }

        if (assets != null && !assets.isEmpty()) {
            sb.append("- 已有资产：\n");
            for (DreamAsset asset : assets) {
                sb.append("  - ").append(asset.getAssetName());
                sb.append(" (").append(asset.getAssetType().getName()).append(")");
                sb.append("：¥").append(asset.getAssetValue());
                if (asset.getLocationCity() != null) {
                    sb.append("，位置：").append(asset.getLocationCity());
                }
                sb.append("\n");
            }
        }

        sb.append("""

            【你的任务】
            1. 判断决策是否通过（approved: true/false）
               - 特别注意资金能力：如果决策需要的资金超过当前现金余额，判定为不通过
            2. 生成反馈信息（feedback）
            3. 生成决策摘要（summary）- 用简洁的话总结用户的决策，格式如"你决定在YYYY年MM月DD日..."
            4. 计算资金影响（financialImpact）- 如果通过，计算对现金余额的影响
            5. 根据决策内容判断应该增减哪些属性（attributeChanges）
            6. 如果决策涉及购买资产，返回建议资产信息（suggestedAssets）
            7. 返回你的真实推理过程（aiReasoningTrace）- 详细描述你的分析和思考过程

            【属性说明】
            - financialPower: 财力（投资、赚钱相关）
            - intelligence: 智力（学习、考试、思考相关）
            - physicalPower: 体力（运动、劳动相关）
            - charisma: 魅力（社交、演讲、谈判相关）
            - luck: 运气（随机事件相关）

            【属性变化规则】
            - 决策成功时：相关属性+1~+3
            - 决策失败时：运气-1~-2

            【资产类型】
            - REALTY: 房产（需要位置信息）
            - STOCK: 股票
            - CASH: 现金
            - VEHICLE: 车辆
            - GOLD: 黄金
            - BITCOIN: 比特币
            - OTHER: 其他

            请以JSON格式返回结果，格式如下：
            {
              "approved": true,
              "feedback": "AI判官判定：...",
              "summary": "你决定在...",
              "aiReasoningTrace": "你的真实思考过程...",
              "financialImpact": {
                "amountChange": -2000000,
                "newBalance": 18000000,
                "description": "购买房产支出200万元"
              },
              "attributeChanges": {
                "financialPower": 2,
                "charisma": 1
              },
              "suggestedAssets": [
                {
                  "assetType": "REALTY",
                  "assetName": "房产",
                  "estimatedValue": 2000000,
                  "needsLocation": true
                }
              ]
            }

            【注意事项】
            - 对于房产购买，必须标记needsLocation为true，后续需要用户补充位置信息
            - 资金影响计算要准确，确保不会出现负数余额（除非是借款等特殊场景）
            - 属性变化要合理，不能太夸张，单次变化建议在-5到+5之间
            - aiReasoningTrace应该详细描述你的推理过程，包括如何判断资金能力、如何分析历史背景等
            """);

        return sb.toString();
    }

    /**
     * 构建增强版用户消息
     */
    private String buildEnhancedUserMessage(String userDecision, LocalDate contextDate,
                                             BigDecimal currentCash, List<DreamAsset> assets) {
        StringBuilder sb = new StringBuilder();
        sb.append("用户决策：").append(userDecision).append("\n");
        sb.append("决策日期：").append(contextDate).append("\n");
        sb.append("当前现金余额：¥").append(currentCash).append("\n");

        if (assets != null && !assets.isEmpty()) {
            sb.append("已有资产：\n");
            for (DreamAsset asset : assets) {
                sb.append("- ").append(asset.getAssetName());
                sb.append(" (").append(asset.getAssetType().getName()).append(")");
                sb.append("：¥").append(asset.getAssetValue()).append("\n");
            }
        }

        sb.append("\n请判断此决策是否现实可行，并返回JSON格式结果。");
        return sb.toString();
    }

    /**
     * 解析增强版响应
     */
    @SuppressWarnings("unchecked")
    private AiJudgmentResultEnhanced parseEnhancedResponse(String response,
                                                              List<String> reasoningTrace,
                                                              BigDecimal currentCash) {
        Map<String, Object> result = doubaoApiClient.parseJsonResponse(response);

        boolean approved = Optional.ofNullable(result.get("approved"))
                .map(Object::toString)
                .map(Boolean::parseBoolean)
                .orElse(true);

        String feedback = Optional.ofNullable(result.get("feedback"))
                .map(Object::toString)
                .orElse(approved ? "AI判官判定：行动成功！" : "AI判官判定：行动失败。");

        String summary = Optional.ofNullable(result.get("summary"))
                .map(Object::toString)
                .orElse(null);

        // 获取AI真实推理过程
        String aiReasoningTrace = Optional.ofNullable(result.get("aiReasoningTrace"))
                .map(Object::toString)
                .orElse(null);

        // 解析属性变化
        Map<String, Integer> attributeChanges = null;
        Object attrChangesObj = result.get("attributeChanges");
        if (attrChangesObj instanceof Map) {
            attributeChanges = (Map<String, Integer>) attrChangesObj;
        }

        // 解析资金影响
        FinancialImpact financialImpact = null;
        Object finImpactObj = result.get("financialImpact");
        if (finImpactObj instanceof Map) {
            Map<String, Object> finMap = (Map<String, Object>) finImpactObj;
            financialImpact = FinancialImpact.builder()
                    .amountChange(Optional.ofNullable(finMap.get("amountChange"))
                            .map(o -> new BigDecimal(o.toString()))
                            .orElse(BigDecimal.ZERO))
                    .newBalance(Optional.ofNullable(finMap.get("newBalance"))
                            .map(o -> new BigDecimal(o.toString()))
                            .orElse(currentCash))
                    .description(Optional.ofNullable(finMap.get("description"))
                            .map(Object::toString)
                            .orElse(null))
                    .build();
        }

        // 解析建议资产
        List<SuggestedAsset> suggestedAssets = null;
        Object suggestedObj = result.get("suggestedAssets");
        if (suggestedObj instanceof List) {
            List<?> suggestedList = (List<?>) suggestedObj;
            suggestedAssets = suggestedList.stream()
                    .filter(obj -> obj instanceof Map)
                    .map(obj -> {
                        Map<String, Object> assetMap = (Map<String, Object>) obj;
                        return SuggestedAsset.builder()
                                .assetType(Optional.ofNullable(assetMap.get("assetType"))
                                        .map(Object::toString)
                                        .map(AssetType::fromCode)
                                        .orElse(AssetType.OTHER))
                                .assetName(Optional.ofNullable(assetMap.get("assetName"))
                                        .map(Object::toString)
                                        .orElse("资产"))
                                .estimatedValue(Optional.ofNullable(assetMap.get("estimatedValue"))
                                        .map(o -> new BigDecimal(o.toString()))
                                        .orElse(BigDecimal.ZERO))
                                .needsLocation(Optional.ofNullable(assetMap.get("needsLocation"))
                                        .map(Object::toString)
                                        .map(Boolean::parseBoolean)
                                        .orElse(false))
                                .build();
                    })
                    .collect(Collectors.toList());
        }

        reasoningTrace.add(feedback);
        return AiJudgmentResultEnhanced.builder()
                .approved(approved)
                .feedback(feedback)
                .reasoningTrace(reasoningTrace)
                .attributeChanges(attributeChanges)
                .summary(summary)
                .aiReasoningTrace(aiReasoningTrace)
                .financialImpact(financialImpact)
                .suggestedAssets(suggestedAssets)
                .build();
    }

    /**
     * 保持向后兼容的原始判定方法
     */
    public AiJudgmentResult judge(String userDecision, LocalDate contextDate) {
        AiJudgmentResultEnhanced enhanced = judgeEnhanced(userDecision, contextDate, null, null);
        return new AiJudgmentResult(
                enhanced.approved,
                enhanced.feedback,
                enhanced.reasoningTrace,
                enhanced.attributeChanges,
                enhanced.summary
        );
    }

    /**
     * 增强版判定结果
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AiJudgmentResultEnhanced {
        public boolean approved;
        public String feedback;
        public List<String> reasoningTrace;
        public Map<String, Integer> attributeChanges;
        public String summary;
        public String aiReasoningTrace;
        public FinancialImpact financialImpact;
        public List<SuggestedAsset> suggestedAssets;
    }

    /**
     * 资金影响
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class FinancialImpact {
        public BigDecimal amountChange;
        public BigDecimal newBalance;
        public String description;
    }

    /**
     * 建议资产
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SuggestedAsset {
        public AssetType assetType;
        public String assetName;
        public BigDecimal estimatedValue;
        public boolean needsLocation;
    }

    /**
     * 向后兼容的原始判定结果类
     */
    public static class AiJudgmentResult {
        public final boolean approved;
        public final String feedback;
        public final List<String> reasoningTrace;
        public final Map<String, Integer> attributeChanges;
        public final String summary;

        public AiJudgmentResult(boolean approved, String feedback, List<String> reasoningTrace,
                                  Map<String, Integer> attributeChanges, String summary) {
            this.approved = approved;
            this.feedback = feedback;
            this.reasoningTrace = reasoningTrace;
            this.attributeChanges = attributeChanges;
            this.summary = summary;
        }
    }
}
