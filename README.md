# Rewind Java

Rewind.ai 白日梦想家 - Java后端模块

## 核心功能

### 白日梦系统
- 开启白日梦，设定起始时间点
- 时间轴进度展示（从起始时间到当前时间）
- 垂直时间轴节点记录
- 时间轴回溯（分支功能）
- AI现实判官（50%真实判定模拟）
- 用户属性系统（财力、智力、体力、魅力、运气）
- 活跃梦境限额（最多3个）
- 梦境上下文配置（身份、学历、出生地、社会关系）
- 知识验证挑战（开启梦境前需通过知识问答）
- 梦境列表（支持状态筛选：进行中/已完成/已删除）
- 梦境结束/删除操作

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
- rewind-auth (认证服务 - 端口 8080)
  - 用户注册/登录
  - 实名认证
- rewind-admin (后台管理API - 端口 8081)
  - 用户管理
  - 梦境管理
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

## 数据库初始化

执行 `schema.sql` 初始化数据库表，执行 `init_data.sql` 初始化预置数据：

```bash
# 初始化表结构
psql -U postgres -d rewind_db -f schema.sql

# 初始化预置数据
psql -U postgres -d rewind_db -f init_data.sql
```

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
**最后更新**: 2026-03-24
