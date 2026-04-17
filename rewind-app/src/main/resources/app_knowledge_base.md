# Rewind.ai 白日梦想家 - APP 知识库

## 项目概述

**项目名称**：Rewind.ai（白日梦想家）  
**项目类型**：AI 驱动的梦境模拟平台  
**技术栈**：
- 后端：Java 17 + Spring Boot 3.2.4 + Spring Data JPA + PostgreSQL + Redis
- 前端：uni-app 3.0 + Vue 3.4 + TypeScript + Pinia
- AI 模型：豆包（字节跳动大模型）

---

## 一、核心功能列表

### 1. 用户系统
- **用户注册/登录**
  - 账号密码登录
  - 短信验证码登录
  - 实名认证（注册时必需）
- **用户信息管理**
  - 个人资料编辑（昵称、头像、生日、性别）
  - 账号安全（修改密码、重置密码）
  - 实名认证信息

### 2. 用户属性系统
- **五大核心属性**：
  - 财力（financialPower）
  - 智力（intelligence）
  - 体力（physicalPower）
  - 魅力（charisma）
  - 运气（luck）

### 3. 白日梦系统（核心功能）
- **创建白日梦**
  - 设定标题、描述、封面
  - 选择起始时间
  - 配置梦境上下文：
    - 身份预设（身份、学历、出生地）
    - 社会关系（人物姓名、关系类型、亲密程度）
  - 支持草稿保存
- **梦境列表**
  - 我的梦境列表（支持状态筛选）
  - 公开梦境广场
  - 搜索公开梦境
  - 精选梦境（按点赞数排序）
  - 我的归档梦境
- **梦境详情**
  - 查看梦境基本信息
  - 时间轴展示
  - 点赞/取消点赞
  - 评论/回复评论
  - 打赏功能
  - 分享功能
- **梦境操作**
  - 结束梦境
  - 归档/恢复梦境
  - 永久删除梦境
  - 公开/取消公开
  - 编辑梦境信息

### 4. 时间轴系统
- **时间轴节点**
  - 节点日期
  - 用户决策内容
  - AI 现实判官判定
  - 属性变化快照
- **做决策**
  - 选择决策模板或自定义输入
  - 设定决策时间
  - 触发 AI 现实判官判定
  - 流式输出判定结果
- **时间轴操作**
  - 撤销上一个决策
  - 创建分支（时间轴回溯）
  - 回滚到指定节点
  - 重新判定某个节点
  - 查询节点判定状态

### 5. AI 现实判官
- **判定流程**：
  1. 检索历史档案
  2. 分析历史背景
  3. 验证逻辑合理性
  4. 进行概率计算
  5. 生成判定结果
- **判定结果**：
  - 通过/失败
  - AI 反馈意见
  - 属性变化
  - 财务影响
  - 建议资产

### 6. 知识验证挑战
- **答题系统**
  - 选择学历水平
  - 回答相关题目
  - 验证通过才能开启高学历梦境

### 7. 社交系统
- **关注/粉丝**
  - 关注用户
  - 取消关注
  - 查看粉丝列表
  - 查看关注列表
- **梦境互动**
  - 点赞梦境
  - 评论梦境
  - 回复评论
  - 打赏梦境（使用梦想币）
- **用户主页**
  - 查看用户信息
  - 查看用户梦境列表
  - 查看用户统计数据

### 8. 钱包系统
- **钱包功能**
  - 查看余额
  - 交易记录
- **交易类型**：
  - REWARD (打赏收入)
  - SHARE (分享收入)
  - CONSUME (消费)
  - TRANSFER_IN (转入)
  - TRANSFER_OUT (转出)
  - ADMIN_GRANT (管理员发放)
  - ADMIN_DEDUCT (管理员扣除)

### 9. 通知系统
- **通知类型**：
  - 点赞通知
  - 评论通知
  - 打赏通知
  - 关注通知
- **通知功能**
  - 查看通知列表
  - 标记已读
  - 未读数统计

### 10. 客服中心
- **AI 智能客服**
  - 在线对话
  - 流式回复
- **工单系统**
  - 提交工单
  - 查看我的工单列表
  - 查看工单详情
  - 查看工单回复
- **意见反馈**
  - 提交反馈
  - 选择反馈分类
  - 查看我的反馈
- **知识库**
  - 热门帮助文章
  - 查看文章详情

### 11. 图片上传
- 支持图片上传到云存储
- 支持腾讯云 COS
- 支持本地上传

---

## 二、API 接口用途

### 基础信息
- **API 基础路径**：
  - 认证服务：`http://localhost:8080`
  - 后台管理：`http://localhost:8081`
  - App 端：`http://localhost:8082`
- **认证方式**：JWT Bearer Token
- **响应格式**：统一 `Result<T>` 包装

### 认证相关 API (`/api/auth`)
| 接口 | 方法 | 用途 |
|------|------|------|
| `/api/auth/login` | POST | 账号密码登录 |
| `/api/auth/sms-login` | POST | 短信验证码登录 |
| `/api/auth/register` | POST | 注册（实名认证） |
| `/api/auth/send-code` | POST | 发送短信验证码 |
| `/api/auth/change-password` | POST | 修改密码 |
| `/api/auth/reset-password` | POST | 重置密码 |
| `/api/auth/forgot-password` | POST | 忘记密码 |

### 用户相关 API (`/api/user`)
| 接口 | 方法 | 用途 |
|------|------|------|
| `/api/user/info` | GET | 获取当前用户信息 |
| `/api/user/profile` | PUT | 更新用户资料 |
| `/api/user/avatar` | POST | 上传头像 |
| `/api/user/attribute` | GET | 获取用户属性 |
| `/api/user/stats` | GET | 获取用户统计数据 |
| `/api/user/{userId}` | GET | 获取指定用户信息 |

### 白日梦相关 API (`/api/daydreams`)
| 接口 | 方法 | 用途 |
|------|------|------|
| `/api/daydreams` | POST | 创建白日梦 |
| `/api/daydreams/full` | POST | 完整创建（含上下文和关系） |
| `/api/daydreams/draft` | POST | 保存草稿 |
| `/api/daydreams/my` | GET | 获取我的梦境列表 |
| `/api/daydreams/my/active` | GET | 获取我的活跃梦境 |
| `/api/daydreams/my/archived` | GET | 获取我的归档梦境 |
| `/api/daydreams/public` | GET | 获取公开梦境列表 |
| `/api/daydreams/search` | GET | 搜索公开梦境 |
| `/api/daydreams/featured` | GET | 获取精选梦境 |
| `/api/daydreams/{id}` | GET | 获取梦境详情 |
| `/api/daydreams/{id}/detail` | GET | 获取完整详情（含上下文和关系） |
| `/api/daydreams/{id}` | PUT | 更新梦境信息 |
| `/api/daydreams/{id}/finish` | POST | 结束梦境 |
| `/api/daydreams/{id}/publish` | POST | 公开分享 |
| `/api/daydreams/{id}/unpublish` | POST | 取消公开 |
| `/api/daydreams/{id}/like` | POST | 点赞梦境 |
| `/api/daydreams/{id}/unlike` | POST | 取消点赞 |
| `/api/daydreams/{id}/toggle-like` | POST | 切换点赞状态 |
| `/api/daydreams/{id}/share` | POST | 分享梦境 |
| `/api/daydreams/{id}/progress` | GET | 获取梦境进度 |
| `/api/daydreams/{id}` | DELETE | 归档梦境 |
| `/api/daydreams/{id}/restore` | POST | 恢复归档梦境 |
| `/api/daydreams/{id}/permanent` | DELETE | 永久删除梦境 |

### 时间轴相关 API (`/api/daydreams/{dreamId}/timeline`)
| 接口 | 方法 | 用途 |
|------|------|------|
| `/api/daydreams/{dreamId}/timeline` | GET | 获取时间轴 |
| `/api/daydreams/{dreamId}/timeline` | POST | 添加时间轴节点（触发 AI 判定） |
| `/api/daydreams/{dreamId}/timeline/undo` | DELETE | 撤销上一个决策 |
| `/api/daydreams/{dreamId}/timeline/{nodeId}/branch` | POST | 创建分支 |
| `/api/daydreams/{dreamId}/timeline/{nodeId}/rollback` | POST | 回滚到指定节点 |
| `/api/daydreams/{dreamId}/timeline/{nodeId}/rejudge` | POST | 重新判定节点 |
| `/api/daydreams/{dreamId}/timeline/node/{nodeId}/status` | GET | 查询节点判定状态 |

### 客服中心 API (`/api/customer-service`)
| 接口 | 方法 | 用途 |
|------|------|------|
| `/api/customer-service/agent/chat` | POST | 发送消息给 AI 智能客服 |
| `/api/customer-service/ai-chat` | POST | 发送消息给 AI 客服（流式） |
| `/api/customer-service/tickets` | POST | 提交工单 |
| `/api/customer-service/tickets` | GET | 获取我的工单列表 |
| `/api/customer-service/tickets/{id}` | GET | 获取工单详情 |
| `/api/customer-service/tickets/{id}/replies` | GET | 获取工单回复 |
| `/api/customer-service/feedback` | POST | 提交意见反馈 |
| `/api/customer-service/feedback/categories` | GET | 获取反馈分类 |
| `/api/customer-service/feedback` | GET | 获取我的反馈列表 |
| `/api/customer-service/knowledge` | GET | 获取知识库列表 |
| `/api/customer-service/knowledge/{id}` | GET | 获取知识库详情 |

---

## 三、用户交互逻辑

### 3.1 新用户注册流程
```
1. 用户进入注册页面
2. 输入用户名、密码、确认密码
3. 输入真实姓名、身份证号（实名认证）
4. 可选：输入手机号、邮箱
5. 点击注册
6. 系统验证身份证信息
7. 注册成功，自动登录
8. 跳转到首页
```

### 3.2 开启白日梦流程
```
1. 用户点击"开启白日梦"按钮
2. 进入梦境编辑页面
3. 填写基本信息：
   - 标题（必填）
   - 描述（可选）
   - 封面图片（可选）
   - 起始时间（必填）
4. 配置梦境上下文：
   - 选择身份预设
   - 设定初始财力
   - 选择学历水平
   - 设定出生地
5. 添加社会关系（可选）：
   - 输入人物姓名
   - 选择关系类型
   - 设定亲密程度
6. 可以保存为草稿，或直接创建
7. 如果选择了高学历，需要先通过知识验证挑战
8. 创建成功，进入梦境详情页
9. 开始做决策
```

### 3.3 做决策流程
```
1. 用户在梦境详情页点击"做决策"
2. 弹出决策输入框
3. 可以选择预设模板或自定义输入
4. 填写决策内容
5. 选择决策时间（在梦境时间范围内）
6. 点击提交
7. 显示 AI 判定动画
8. 流式输出判定结果
9. 判定通过：
   - 显示 AI 反馈
   - 显示属性变化
   - 可能触发资产信息填写
10. 判定失败：
    - 显示失败原因
    - 可以重新决策
11. 时间轴添加新节点
```

### 3.4 查看梦境详情流程
```
1. 用户从列表点击某个梦境
2. 进入梦境详情页
3. 查看梦境基本信息：
   - 标题、状态
   - 创建/更新时间
   - 进度条
   - 作者信息
4. 查看统计数据：
   - 浏览量
   - 点赞数
   - 分享数
   - 评论数
   - 打赏金额
5. 切换标签页：
   - 时间轴
   - 梦境设定
   - 社会关系
   - 评论
6. 可以进行操作：
   - 点赞/取消点赞
   - 发表评论
   - 打赏（如果不是自己的梦境）
   - 分享
   - 如果是作者：做决策、结束梦境、编辑、归档等
```

### 3.5 工单提交与查询流程
```
1. 用户进入客服中心
2. 点击"提交工单"
3. 填写工单信息：
   - 标题
   - 问题描述
   - 选择分类
   - 选择优先级
4. 提交工单
5. 返回客服中心或工单列表
6. 可以在"我的工单"查看所有工单
7. 点击某个工单查看详情
8. 查看工单状态和回复记录
9. 可以查看管理员回复
```

### 3.6 AI 客服对话流程
```
1. 用户进入客服中心
2. 点击"AI客服"
3. 进入聊天页面
4. 输入问题
5. 发送消息
6. AI 客服回复
7. 多轮对话
8. 如果无法解决，可以引导提交工单
```

---

## 七、常用语料和话术

### 7.1 欢迎语
- "欢迎来到白日梦想家，开启你的另一段人生！"
- "你好，{nickname}！今天想做什么样的梦？"

### 7.2 梦境创建引导
- "请给你的梦境起个名字"
- "想从什么时候开始你的梦境？"
- "设定一下你的初始身份吧"

### 7.3 AI 判定相关
- "AI 现实判官正在分析你的决策..."
- "正在检索历史档案..."
- "正在验证逻辑合理性..."

### 7.4 客服常见问题回答
- "如何创建白日梦？在首页点击'开启白日梦'按钮即可。"
- "如何创建梦境？在首页点击'开启白日梦'按钮即可。"
- "梦境可以删除吗？可以，先归档后可永久删除。"
- "梦想币怎么获得？通过被打赏、分享等方式获得。"
- "如何联系人工客服？工作时间：周一至周五 9:00-18:00，可以拨打客服热线。"
- "如何修改密码？在个人中心-设置-账号安全中可以修改密码。"
- "如何编辑个人资料？在个人中心-设置-个人资料中可以编辑。"
- "实名认证后可以修改吗？实名认证信息提交后不可修改，请谨慎填写。"

---

**文档版本**：v1.0  
**最后更新**：2026-04-16  
**维护者**：AI 智能客服团队
