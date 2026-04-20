# 📚 MyLesson 智学云课堂

> 基于微服务架构的在线教育云平台，面向校园场景提供一站式教学管理解决方案

---

## 📖 项目简介

MyLesson 是一套面向教育场景的**微服务架构云课堂系统**，采用面向对象与接口化设计，实现了从用户管理、课程服务到数据报表的全链路业务覆盖，支持高并发、高可用部署，可作为校园教学管理、在线课程平台的基础项目参考。

---

## 🛠️ 技术栈

| 分类 | 技术选型 |
| :--- | :--- |
| 后端框架 | Spring Boot 2.7.x、Spring Cloud Alibaba |
| 持久层 | MyBatis-Flex、MySQL 8.x |
| 微服务组件 | Nacos（注册/配置中心）、Sentinel（限流容错）、Seata（分布式事务） |
| 中间件 | Redis（缓存）、RocketMQ（消息队列）、XXL-Job（定时任务） |
| 工具类 | Knife4j（API文档）、JUnit（单元测试）、MinIO（对象存储）、EasyExcel（报表） |
| 部署 | Docker、Docker Compose（可选） |

---

## ✨ 核心功能模块

| 模块 | 功能描述 |
| :--- | :--- |
| 用户服务（ml-user） | 角色权限管理、用户认证、菜单管理 |
| 课程服务 | 课程发布、课时管理、选课与学习进度追踪 |
| 订单/支付服务（oms） | 课程订单、支付流程、订单状态管理 |
| 消息服务（sms） | 通知推送、消息队列解耦 |
| 内容管理（cms） | 课程内容管理、素材资源管理 |
| 数据报表 | 学习数据统计、课程报表导出 |

---

## 🚀 快速启动

### 1. 环境要求

- JDK 11+
- MySQL 8.x
- Redis 6.x
- Nacos 2.x
- Maven 3.6+

### 2. 克隆项目

```bash
git clone https://github.com/idontcarethishahaha/wwj-lesson.git
cd wwj-lesson
