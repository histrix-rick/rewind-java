# Rewind Java

Rewind.ai 白日梦想家 - Java后端模块

## 重要说明

**关于 Dream vs Daydream 系统**:
- **Daydream (白日梦系统)**: 这是当前正在使用的系统，对应数据库表 `dream_worlds`
- **Dream (简单梦境系统)**: 早期版本的简单梦境系统，已废弃，相关代码已删除

所有新功能和后台管理均使用 **Daydream 系统**。

## 核心功能

### 白日梦系统 (Daydream)
- 开启白日梦，设定起始时间点
- 时间轴进度展示（从起始时间到当前时间）
- 垂直时间轴节点记录
- 时间轴回溯（从任意节点创建新分支）
- 多分支支持（切换不同的时间线分支）
- AI现实判官（50%真实判定模拟）
- 用户属性系统（财力、智力、体力、魅力、运气）
- 活跃梦境限额（最多3个）
- 梦境上下文配置（身份、学历、出生地、社会关系）
- 知识验证挑战（开启梦境前需通过知识问答）
- 梦境列表（支持状态筛选：进行中/已完成/已删除）
- 梦境结束/删除操作
- 梦境点赞/取消点赞
- 时间轴节点点赞/取消点赞
- 梦境评论/回复/删除（软删除）
- 通知消息系统（点赞通知、评论通知、评论回复通知）
- 梦想档案库（归档/恢复/永久删除）
- 梦境打赏功能
- 用户关注/粉丝系统
- 梦境关注系统

### 后台管理系统
- 用户管理（状态管理、数据导出）
- 梦境管理（状态管理、审核、精选、置顶、数据导出）
- 评论管理（查看、删除、恢复、数据导出）
- 内容举报管理（举报处理、操作记录）
- 工单与客服（工单管理、用户反馈、知识库）
- 数据统计（各模块数据统计）
- 数据导出（CSV格式导出用户、梦境、评论数据）

### 用户系统
- 实名认证（身份证号验证）
- 自动从身份证提取出生日期和性别
- 用户属性管理

### 元数据系统
- 用户身份预设（儿童、小学生、中学生、大学生、职场新人等）
- 学历水平（小学、初中、高中、大专、本科、硕士、博士）
- 社会关系类型（家人、朋友、同事、爱人等）
- 知识题库（各学科、各学历水平题目，共12+道）

### 文件存储系统
- 存储配置管理（支持多种存储策略）
- 文件上传/下载/预览
- 图片URL永久化处理

## 技术栈

- Java: 17
- Spring Boot: 3.2.4
- Spring Data JPA
- Spring Data Redis
- Spring Security + JWT
- JJWT: 0.12.3
- Hutool: 5.8.26
- MapStruct: 1.5.5.Final
- SpringDoc: 2.3.0
- PostgreSQL: 数据库
- Lombok

## 模块结构

- rewind-common (公共模块)
  - common-core (核心公共类)
  - common-security (安全认证)
- rewind-system (系统模块)
  - 白日梦实体、Service、Repository
  - 用户属性实体、Service、Repository
  - 梦境上下文、社会关系、知识题库
  - AI判官 Service
  - 文件存储实体、Service、Repository
  - 存储配置实体、Service、Repository
  - 内容举报实体、Service、Repository
  - 工单与客服实体、Service、Repository
- rewind-auth (认证服务 - 端口 8080)
  - 用户注册/登录
  - 实名认证
- rewind-admin (后台管理API - 端口 8081)
  - 用户管理（状态管理、数据导出）
  - 梦境管理（审核、精选、置顶、数据导出）
  - 评论管理（查看、删除、恢复、数据导出）
  - 内容举报管理（举报处理、操作记录）
  - 工单与客服管理（工单管理、用户反馈、知识库）
  - 数据统计（各模块数据统计）
  - 存储配置管理
  - 文件管理
- rewind-app (App端API - 端口 8082)
  - 白日梦管理 API
  - 时间轴 API
  - 梦境上下文 API
  - 社会关系 API
  - 知识验证挑战 API
  - 用户属性 API
  - 文件上传 API
  - 梦境互动 API（点赞、评论）
  - 通知消息 API

## 数据库初始化

执行 `schema.sql` 初始化数据库表，执行 `init_data.sql` 初始化预置数据：

```bash
# 初始化表结构
psql -U postgres -d rewind_db -f schema.sql

# 初始化预置数据
psql -U postgres -d rewind_db -f init_data.sql

# 如需添加like_count字段（如果schema.sql已更新可跳过）
psql -U postgres -d rewind_db -f migration_add_like_count.sql
```

### 数据库表说明
- `users` - 用户表
- `user_attributes` - 用户属性表
- `user_follows` - 用户关注表
- `dream_worlds` - 白日梦世界表（核心表）
- `dream_branches` - 梦境分支表
- `dream_timeline_nodes` - 时间轴节点表
- `dream_contexts` - 梦境上下文表
- `dream_relationships` - 梦境人物关系表
- `dream_likes` - 梦境点赞表
- `dream_follows` - 梦境关注表
- `dream_comments` - 梦境评论表
- `dream_rewards` - 梦境打赏表
- `node_likes` - 节点点赞表
- `notifications` - 通知消息表
- `user_identities` - 用户身份预设表
- `education_levels` - 学历水平表
- `relationship_types` - 社会关系类型表
- `knowledge_questions` - 知识题库表
- `user_wallets` - 用户钱包表
- `cash_transactions` - 现金交易表
- `storage_configs` - 存储配置表
- `files` - 文件表

## 项目结构

```
rewind-java/
  rewind-common/
    common-core/
    common-security/
  rewind-system/
  rewind-auth/
  rewind-admin/
  rewind-app/
  schema.sql
  init_data.sql
  pom.xml
  README.md
```

## 快速开始

```bash
# 编译项目
mvn clean install

# 运行认证服务
cd rewind-auth && mvn spring-boot:run

# 运行后台管理服务
cd rewind-admin && mvn spring-boot:run

# 运行App端服务
cd rewind-app && mvn spring-boot:run
```

## API 文档

启动服务后访问 Swagger UI：
- 认证服务: http://localhost:8080/swagger-ui.html
- 后台管理: http://localhost:8081/swagger-ui.html
- App端: http://localhost:8082/swagger-ui.html

---
**最后更新**: 2026-04-11
