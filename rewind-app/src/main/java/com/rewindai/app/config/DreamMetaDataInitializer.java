package com.rewindai.app.config;

import com.rewindai.system.daydream.entity.EducationLevel;
import com.rewindai.system.daydream.entity.KnowledgeQuestion;
import com.rewindai.system.daydream.entity.RelationshipType;
import com.rewindai.system.daydream.entity.UserIdentity;
import com.rewindai.system.daydream.repository.EducationLevelRepository;
import com.rewindai.system.daydream.repository.KnowledgeQuestionRepository;
import com.rewindai.system.daydream.repository.RelationshipTypeRepository;
import com.rewindai.system.daydream.repository.UserIdentityRepository;
import com.rewindai.system.dream.enums.QuestionSubject;
import com.rewindai.system.dream.enums.RelationshipCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 梦境元数据初始化器
 * 服务启动时自动初始化身份、关系类型、学历水平等基础数据
 *
 * @author Rewind.ai Team
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DreamMetaDataInitializer implements CommandLineRunner {

    private final UserIdentityRepository userIdentityRepository;
    private final RelationshipTypeRepository relationshipTypeRepository;
    private final EducationLevelRepository educationLevelRepository;
    private final KnowledgeQuestionRepository knowledgeQuestionRepository;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("========================================");
        log.info("开始初始化梦境元数据...");
        log.info("========================================");

        log.info("当前用户身份表记录数: {}", userIdentityRepository.count());
        log.info("当前关系类型表记录数: {}", relationshipTypeRepository.count());
        log.info("当前学历水平表记录数: {}", educationLevelRepository.count());
        log.info("当前知识题库记录数: {}", knowledgeQuestionRepository.count());

        initUserIdentities();
        initRelationshipTypes();
        initEducationLevels();
        initKnowledgeQuestions();

        log.info("========================================");
        log.info("梦境元数据初始化完成！");
        log.info("初始化后 - 用户身份: {} 条", userIdentityRepository.count());
        log.info("初始化后 - 关系类型: {} 条", relationshipTypeRepository.count());
        log.info("初始化后 - 学历水平: {} 条", educationLevelRepository.count());
        log.info("初始化后 - 知识题库: {} 条", knowledgeQuestionRepository.count());
        log.info("========================================");
    }

    private void initUserIdentities() {
        long count = userIdentityRepository.count();
        if (count > 0) {
            log.info("用户身份数据已存在 ({} 条)，跳过初始化", count);
            return;
        }

        List<UserIdentity> identities = List.of(
                createIdentity("儿童", "学龄前儿童", 0, 6, 1),
                createIdentity("小学生", "小学阶段学生", 6, 12, 2),
                createIdentity("中学生", "初中/高中阶段学生", 12, 18, 3),
                createIdentity("大学生", "大学阶段学生", 18, 25, 4),
                createIdentity("刚入职的新手小白", "刚进入职场的新人", 22, 30, 5),
                createIdentity("某公司员工", "职场工作人士", 22, 65, 6)
        );

        userIdentityRepository.saveAll(identities);
        log.info("用户身份数据初始化完成，共 {} 条", identities.size());
    }

    private void initRelationshipTypes() {
        long count = relationshipTypeRepository.count();
        if (count > 0) {
            log.info("关系类型数据已存在 ({} 条)，跳过初始化", count);
            return;
        }

        List<RelationshipType> types = List.of(
                createRelationshipType("父亲", RelationshipCategory.FAMILY, 1),
                createRelationshipType("母亲", RelationshipCategory.FAMILY, 2),
                createRelationshipType("哥哥", RelationshipCategory.FAMILY, 3),
                createRelationshipType("姐姐", RelationshipCategory.FAMILY, 4),
                createRelationshipType("弟弟", RelationshipCategory.FAMILY, 5),
                createRelationshipType("妹妹", RelationshipCategory.FAMILY, 6),
                createRelationshipType("爱人", RelationshipCategory.LOVER, 7),
                createRelationshipType("好朋友", RelationshipCategory.FRIEND, 8),
                createRelationshipType("同学", RelationshipCategory.FRIEND, 9),
                createRelationshipType("同事", RelationshipCategory.COLLEAGUE, 10),
                createRelationshipType("老师", RelationshipCategory.TEACHER, 11),
                createRelationshipType("其他", RelationshipCategory.OTHER, 99)
        );

        relationshipTypeRepository.saveAll(types);
        log.info("关系类型数据初始化完成，共 {} 条", types.size());
    }

    private void initEducationLevels() {
        long count = educationLevelRepository.count();
        if (count > 0) {
            log.info("学历水平数据已存在 ({} 条)，跳过初始化", count);
            return;
        }

        List<EducationLevel> levels = List.of(
                createEducationLevel("小学", 1, 3, 100, 1),
                createEducationLevel("初中", 2, 3, 100, 2),
                createEducationLevel("高中", 3, 3, 100, 3),
                createEducationLevel("大专", 4, 3, 100, 4),
                createEducationLevel("本科", 5, 3, 100, 5),
                createEducationLevel("硕士", 6, 3, 100, 6),
                createEducationLevel("博士", 7, 3, 100, 7)
        );

        educationLevelRepository.saveAll(levels);
        log.info("学历水平数据初始化完成，共 {} 条", levels.size());
    }

    private UserIdentity createIdentity(String name, String desc, Integer minAge, Integer maxAge, Integer sortOrder) {
        return UserIdentity.builder()
                .name(name)
                .description(desc)
                .minAge(minAge)
                .maxAge(maxAge)
                .sortOrder(sortOrder)
                .isActive(true)
                .build();
    }

    private RelationshipType createRelationshipType(String name, RelationshipCategory category, Integer sortOrder) {
        return RelationshipType.builder()
                .name(name)
                .category(category)
                .sortOrder(sortOrder)
                .isActive(true)
                .build();
    }

    private EducationLevel createEducationLevel(String name, Integer level, Integer questionCount, Integer passingScore, Integer sortOrder) {
        return EducationLevel.builder()
                .name(name)
                .level(level)
                .questionCount(questionCount)
                .passingScore(passingScore)
                .sortOrder(sortOrder)
                .isActive(true)
                .build();
    }

    private void initKnowledgeQuestions() {
        long count = knowledgeQuestionRepository.count();
        if (count > 0) {
            log.info("知识题库数据已存在 ({} 条)，跳过初始化", count);
            return;
        }

        List<KnowledgeQuestion> questions = List.of(
                // 小学题目 (level 1)
                createQuestion(1L, QuestionSubject.CHINESE, "中国的首都是哪里？", "上海", "北京", "广州", "深圳", "B", 1),
                createQuestion(1L, QuestionSubject.MATH, "1 + 1 = ?", "1", "2", "3", "4", "B", 1),
                createQuestion(1L, QuestionSubject.CHINESE, "太阳从哪个方向升起？", "东边", "西边", "南边", "北边", "A", 1),
                // 初中题目 (level 2)
                createQuestion(2L, QuestionSubject.CHINESE, "《红楼梦》的作者是谁？", "罗贯中", "曹雪芹", "施耐庵", "吴承恩", "B", 2),
                createQuestion(2L, QuestionSubject.MATH, "勾股定理中，a² + b² = ?", "a² - b²", "c²", "a² + c²", "b² + c²", "B", 2),
                createQuestion(2L, QuestionSubject.ENGLISH, "What is the past tense of \"go\"?", "goed", "went", "gone", "going", "B", 2),
                // 高中题目 (level 3)
                createQuestion(3L, QuestionSubject.CHINESE, "牛顿第一定律又被称为？", "加速度定律", "惯性定律", "作用力定律", "万有引力定律", "B", 3),
                createQuestion(3L, QuestionSubject.MATH, "水的化学式是？", "H2O", "CO2", "O2", "H2", "A", 3),
                createQuestion(3L, QuestionSubject.CHINESE, "中国第一个统一的封建王朝是？", "商朝", "周朝", "秦朝", "汉朝", "C", 3),
                // 本科题目 (level 5)
                createQuestion(5L, QuestionSubject.CHINESE, "人工智能的英文缩写是？", "IT", "AI", "IQ", "EQ", "B", 3),
                createQuestion(5L, QuestionSubject.MATH, "圆周率π大约等于多少？", "3.14", "3.16", "3.12", "3.18", "A", 3),
                createQuestion(5L, QuestionSubject.CHINESE, "世界上最高的山峰是？", "喜马拉雅山", "珠穆朗玛峰", "乞力马扎罗山", "阿尔卑斯山", "B", 3)
        );

        knowledgeQuestionRepository.saveAll(questions);
        log.info("知识题库数据初始化完成，共 {} 条", questions.size());
    }

    private KnowledgeQuestion createQuestion(Long levelId, QuestionSubject subject, String questionText,
                                             String optionA, String optionB, String optionC, String optionD,
                                             String correctAnswer, Integer difficulty) {
        return KnowledgeQuestion.builder()
                .educationLevelId(levelId)
                .subject(subject)
                .questionText(questionText)
                .optionA(optionA)
                .optionB(optionB)
                .optionC(optionC)
                .optionD(optionD)
                .correctAnswer(correctAnswer)
                .difficulty(difficulty)
                .isActive(true)
                .build();
    }
}
