# 用户管理系统 - 后端服务

基于Spring Boot 2.7.x + MyBatis-Plus的用户管理系统后端服务。

## 技术栈

- Java 8
- Spring Boot 2.7.18
- Spring Security
- MyBatis-Plus 3.5.3
- PostgreSQL
- JWT
- Maven

## 项目结构

```
src/main/java/com/example/usermanagement/
├── config/          # 配置类
├── controller/      # 控制器层
├── service/         # 服务层
├── mapper/          # 数据访问层
├── entity/          # 实体类
├── dto/             # 数据传输对象
├── security/        # 安全相关
└── exception/       # 异常处理
```

## 运行要求

1. Java 8+
2. Maven 3.6+
3. PostgreSQL 14+

## 配置说明

在运行前，请确保：
1. PostgreSQL数据库已启动
2. 创建数据库：`user_management`
3. 修改 `application.yml` 中的数据库连接配置

## 运行方式

```bash
mvn spring-boot:run
```

服务将在 http://localhost:8080/api 启动。