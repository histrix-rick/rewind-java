package com.rewindai.app.controller;

import com.rewindai.app.dto.*;
import com.rewindai.common.core.result.Result;
import com.rewindai.system.daydream.entity.UserIdentity;
import com.rewindai.system.daydream.entity.RelationshipType;
import com.rewindai.system.daydream.entity.EducationLevel;
import com.rewindai.system.daydream.entity.KnowledgeQuestion;
import com.rewindai.system.daydream.service.UserIdentityService;
import com.rewindai.system.daydream.service.RelationshipTypeService;
import com.rewindai.system.daydream.service.EducationLevelService;
import com.rewindai.system.daydream.service.KnowledgeQuestionService;
import com.rewindai.system.daydream.repository.EducationLevelRepository;
import com.rewindai.system.daydream.repository.UserIdentityRepository;
import com.rewindai.system.daydream.repository.RelationshipTypeRepository;
import com.rewindai.system.daydream.enums.QuestionSubject;
import com.rewindai.system.daydream.enums.RelationshipCategory;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * 梦境元数据 Controller
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Tag(name = "梦境元数据", description = "身份、关系类型、学历、题库等元数据接口")
@RestController
@RequestMapping("/api/dream-meta")
@RequiredArgsConstructor
public class DreamMetaController {

    private final UserIdentityService userIdentityService;
    private final RelationshipTypeService relationshipTypeService;
    private final EducationLevelService educationLevelService;
    private final KnowledgeQuestionService knowledgeQuestionService;
    private final EducationLevelRepository educationLevelRepository;
    private final UserIdentityRepository userIdentityRepository;
    private final RelationshipTypeRepository relationshipTypeRepository;

    // ========== 用户身份 ==========

    @Operation(summary = "获取所有激活的用户身份（系统身份 + 当前用户自定义身份）")
    @GetMapping("/identities")
    public Result<List<UserIdentityResponse>> getActiveIdentities(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<UserIdentity> identities = userIdentityService.getAllIdentitiesForUser(userId);
        log.info("查询用户身份，数量: {}", identities.size());
        return Result.success(identities.stream().map(UserIdentityResponse::from).toList());
    }

    @Operation(summary = "获取系统身份")
    @GetMapping("/identities/system")
    public Result<List<UserIdentityResponse>> getSystemIdentities() {
        List<UserIdentity> identities = userIdentityService.getSystemIdentities();
        return Result.success(identities.stream().map(UserIdentityResponse::from).toList());
    }

    @Operation(summary = "获取用户自定义身份")
    @GetMapping("/identities/custom")
    public Result<List<UserIdentityResponse>> getUserIdentities(Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        List<UserIdentity> identities = userIdentityService.getUserIdentities(userId);
        return Result.success(identities.stream().map(UserIdentityResponse::from).toList());
    }

    @Operation(summary = "创建用户自定义身份")
    @PostMapping("/identities/custom")
    public Result<UserIdentityResponse> createUserIdentity(
            @Valid @RequestBody CreateUserIdentityRequest request,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        UserIdentity identity = UserIdentity.builder()
                .name(request.getName())
                .icon(request.getIcon())
                .minAge(request.getMinAge())
                .maxAge(request.getMaxAge())
                .description(request.getDescription())
                .build();
        UserIdentity saved = userIdentityService.createUserIdentity(userId, identity);
        return Result.success(UserIdentityResponse.from(saved));
    }

    @Operation(summary = "删除用户自定义身份")
    @DeleteMapping("/identities/custom/{identityId}")
    public Result<Void> deleteUserIdentity(
            @PathVariable Long identityId,
            Authentication authentication) {
        UUID userId = UUID.fromString(authentication.getName());
        userIdentityService.deleteUserIdentity(userId, identityId);
        return Result.success();
    }

    @Operation(summary = "根据年龄获取适用的用户身份")
    @GetMapping("/identities/by-age")
    public Result<List<UserIdentityResponse>> getIdentitiesByAge(@RequestParam Integer age) {
        List<UserIdentity> identities = userIdentityService.getIdentitiesByAge(age);
        return Result.success(identities.stream().map(UserIdentityResponse::from).toList());
    }

    // ========== 关系类型 ==========

    @Operation(summary = "获取所有激活的关系类型")
    @GetMapping("/relationship-types")
    public Result<List<RelationshipTypeResponse>> getActiveRelationshipTypes() {
        List<RelationshipType> all = relationshipTypeRepository.findAll();
        log.info("查询所有关系类型，数量: {}", all.size());
        List<RelationshipType> active = all.stream().filter(RelationshipType::getIsActive).toList();
        log.info("过滤后激活的关系类型，数量: {}", active.size());
        return Result.success(active.stream().map(RelationshipTypeResponse::from).toList());
    }

    @Operation(summary = "根据分类获取关系类型")
    @GetMapping("/relationship-types/by-category")
    public Result<List<RelationshipTypeResponse>> getRelationshipTypesByCategory(
            @RequestParam RelationshipCategory category) {
        List<RelationshipType> types = relationshipTypeService.getTypesByCategory(category);
        return Result.success(types.stream().map(RelationshipTypeResponse::from).toList());
    }

    // ========== 学历水平 ==========

    @Operation(summary = "获取所有激活的学历水平")
    @GetMapping("/education-levels")
    public Result<List<EducationLevelResponse>> getActiveEducationLevels() {
        List<EducationLevel> allLevels = educationLevelRepository.findAll();
        log.info("查询所有学历水平，数量: {}", allLevels.size());
        allLevels.forEach(l -> log.info("学历: id={}, name={}, isActive={}", l.getId(), l.getName(), l.getIsActive()));
        List<EducationLevel> activeLevels = allLevels.stream()
                .filter(EducationLevel::getIsActive)
                .toList();
        log.info("过滤后激活的学历水平，数量: {}", activeLevels.size());
        return Result.success(activeLevels.stream().map(EducationLevelResponse::from).toList());
    }

    // ========== 知识题库 ==========

    @Operation(summary = "随机获取指定学历水平的题目")
    @GetMapping("/questions/random")
    public Result<List<KnowledgeQuestionResponse>> getRandomQuestions(
            @RequestParam Long levelId,
            @RequestParam(defaultValue = "3") int limit) {
        List<KnowledgeQuestion> questions = knowledgeQuestionService.getRandomQuestionsByLevel(levelId, limit);
        return Result.success(questions.stream().map(KnowledgeQuestionResponse::from).toList());
    }

    @Operation(summary = "随机获取指定学历水平和科目的一道题目")
    @GetMapping("/questions/random-by-subject")
    public Result<KnowledgeQuestionResponse> getRandomQuestionBySubject(
            @RequestParam Long levelId,
            @RequestParam QuestionSubject subject) {
        KnowledgeQuestion question = knowledgeQuestionService.getRandomQuestionByLevelAndSubject(levelId, subject);
        if (question == null) {
            return Result.notFound("没有找到题目");
        }
        return Result.success(KnowledgeQuestionResponse.from(question));
    }

    @Operation(summary = "获取指定学历水平和科目的所有题目")
    @GetMapping("/questions/by-subject")
    public Result<List<KnowledgeQuestionResponse>> getQuestionsBySubject(
            @RequestParam Long levelId,
            @RequestParam QuestionSubject subject) {
        List<KnowledgeQuestion> questions = knowledgeQuestionService.getQuestionsByLevelAndSubject(levelId, subject);
        return Result.success(questions.stream().map(KnowledgeQuestionResponse::from).toList());
    }

    // ========== 临时调试接口 ==========
    @Operation(summary = "调试：获取所有学历水平（包括未激活）")
    @GetMapping("/debug/education-levels-all")
    public Result<List<EducationLevel>> debugGetAllEducationLevels() {
        return Result.success(educationLevelRepository.findAll());
    }

    @Operation(summary = "调试：获取所有用户身份（包括未激活）")
    @GetMapping("/debug/identities-all")
    public Result<List<UserIdentity>> debugGetAllIdentities() {
        return Result.success(userIdentityRepository.findAll());
    }

    @Operation(summary = "调试：获取所有关系类型（包括未激活）")
    @GetMapping("/debug/relationship-types-all")
    public Result<List<RelationshipType>> debugGetAllRelationshipTypes() {
        return Result.success(relationshipTypeRepository.findAll());
    }
}
