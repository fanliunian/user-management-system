# 用户管理系统

基于Spring Boot 2.7.x + Vue 2的用户管理系统，提供完整的用户生命周期管理功能。

## 项目概述

这是一个前后端分离的用户管理系统，包含用户注册、登录、信息管理、状态控制和基于角色的权限管理等功能。

### 技术栈

**后端：**
- Java 8
- Spring Boot 2.7.18
- Spring Security
- MyBatis-Plus 3.5.3
- PostgreSQL
- JWT
- Maven

**前端：**
- Vue 2.7.x
- Vue Router
- Vuex
- Element UI
- Axios
- Webpack

## 功能特性

### 用户管理
- ✅ 用户注册（用户名、邮箱唯一性验证）
- ✅ 用户登录（JWT认证）
- ✅ 个人信息管理（查看、编辑）
- ✅ 密码修改（当前密码验证）
- ✅ 用户列表管理（分页、搜索、筛选）
- ✅ 用户状态管理（启用/禁用）
- ✅ 批量操作支持

### 角色权限
- ✅ 角色管理（CRUD操作）
- ✅ 用户角色分配
- ✅ 基于角色的访问控制(RBAC)
- ✅ 权限验证和路由守卫

### 系统功能
- ✅ JWT令牌管理和自动刷新
- ✅ 全局异常处理
- ✅ 响应式设计
- ✅ 数据验证和安全控制
- ✅ 统计信息和仪表盘

## 项目结构

```
user-management-system/
├── backend/                 # 后端项目
│   ├── src/main/java/
│   │   └── com/example/usermanagement/
│   │       ├── config/      # 配置类
│   │       ├── controller/  # 控制器层
│   │       ├── service/     # 服务层
│   │       ├── mapper/      # 数据访问层
│   │       ├── entity/      # 实体类
│   │       ├── dto/         # 数据传输对象
│   │       ├── security/    # 安全相关
│   │       └── exception/   # 异常处理
│   ├── src/main/resources/
│   │   ├── application.yml  # 配置文件
│   │   └── db/migration/    # 数据库脚本
│   └── pom.xml             # Maven配置
├── frontend/               # 前端项目
│   ├── src/
│   │   ├── components/     # 通用组件
│   │   ├── views/          # 页面组件
│   │   ├── router/         # 路由配置
│   │   ├── store/          # 状态管理
│   │   ├── api/            # API接口
│   │   ├── utils/          # 工具函数
│   │   └── styles/         # 样式文件
│   ├── public/
│   ├── package.json        # 依赖配置
│   └── vue.config.js       # Vue配置
└── README.md              # 项目说明
```

## 快速开始

### 环境要求

- Java 8+
- Node.js 14+
- PostgreSQL 14+
- Maven 3.6+

### 后端启动

1. 创建PostgreSQL数据库：
```sql
CREATE DATABASE user_management;
```

2. 修改配置文件：
```yaml
# backend/src/main/resources/application.yml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/user_management
    username: your_username
    password: your_password
```

3. 启动后端服务：
```bash
cd backend
mvn spring-boot:run
```

后端服务将在 http://localhost:8080/api 启动。

### 前端启动

1. 安装依赖：
```bash
cd frontend
npm install
```

2. 启动开发服务器：
```bash
npm run serve
```

前端应用将在 http://localhost:8081 启动。

### 默认账户

系统会自动创建默认角色：
- ADMIN（管理员）
- USER（普通用户）

您可以通过注册功能创建新用户，第一个注册的用户建议手动分配管理员角色。

## API文档

### 认证接口

| 方法 | 路径 | 描述 |
|------|------|------|
| POST | /api/auth/login | 用户登录 |
| POST | /api/auth/register | 用户注册 |
| POST | /api/auth/logout | 用户登出 |
| POST | /api/auth/refresh | 刷新令牌 |

### 用户管理接口

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/users | 获取用户列表 | ADMIN |
| GET | /api/users/{id} | 获取用户详情 | ADMIN |
| PUT | /api/users/{id}/status | 更新用户状态 | ADMIN |
| DELETE | /api/users/{id} | 删除用户 | ADMIN |
| GET | /api/users/profile | 获取个人信息 | 认证用户 |
| PUT | /api/users/profile | 更新个人信息 | 认证用户 |
| PUT | /api/users/password | 修改密码 | 认证用户 |

### 角色管理接口

| 方法 | 路径 | 描述 | 权限 |
|------|------|------|------|
| GET | /api/roles | 获取角色列表 | ADMIN |
| POST | /api/roles | 创建角色 | ADMIN |
| PUT | /api/roles/{id} | 更新角色 | ADMIN |
| DELETE | /api/roles/{id} | 删除角色 | ADMIN |
| PUT | /api/roles/assign/{userId} | 分配用户角色 | ADMIN |

## 开发指南

### 后端开发

1. **添加新的API接口：**
   - 在对应的Controller中添加方法
   - 在Service层实现业务逻辑
   - 在Mapper层添加数据访问方法

2. **数据库变更：**
   - 在`src/main/resources/db/migration/`目录下添加SQL脚本
   - 更新对应的实体类和DTO

3. **权限控制：**
   - 使用`@PreAuthorize`注解控制方法级权限
   - 在SecurityConfig中配置URL级权限

### 前端开发

1. **添加新页面：**
   - 在`src/views/`目录下创建Vue组件
   - 在`src/router/index.js`中添加路由配置
   - 根据需要添加权限控制

2. **API调用：**
   - 在`src/api/`目录下添加API方法
   - 使用统一的request工具进行HTTP请求

3. **状态管理：**
   - 在`src/store/modules/`中添加Vuex模块
   - 管理页面状态和数据

## 部署说明

### 生产环境配置

1. **后端配置：**
```yaml
spring:
  profiles:
    active: prod
  datasource:
    url: ${DATABASE_URL}
    username: ${DATABASE_USERNAME}
    password: ${DATABASE_PASSWORD}

jwt:
  secret: ${JWT_SECRET}
```

2. **前端构建：**
```bash
npm run build
```

3. **Docker部署（可选）：**
```dockerfile
# 后端Dockerfile示例
FROM openjdk:8-jre-slim
COPY target/user-management-system-1.0.0.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 打开 Pull Request

## 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 联系方式

如有问题或建议，请通过以下方式联系：

- 提交 Issue
- 发送邮件至：[your-email@example.com]

## 更新日志

### v1.0.0 (2024-01-20)
- ✅ 完成用户管理基础功能
- ✅ 完成角色权限管理
- ✅ 完成前后端分离架构
- ✅ 完成响应式设计
- ✅ 完成JWT认证机制