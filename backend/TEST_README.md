# 测试用例说明文档

## 概述

本项目包含了完整的测试用例，覆盖了用户管理系统的主要功能模块。测试用例分为单元测试和集成测试两大类。

## 测试结构

```
src/test/java/com/example/usermanagement/
├── controller/                 # 控制器层测试
│   ├── AuthControllerTest.java
│   └── UserControllerTest.java
├── service/                    # 服务层测试
│   ├── AuthServiceTest.java
│   └── UserServiceTest.java
├── security/                   # 安全组件测试
│   ├── JwtTokenProviderTest.java
│   └── UserDetailsServiceImplTest.java
├── dto/                        # DTO测试
│   └── ApiResponseTest.java
├── exception/                  # 异常处理测试
│   └── GlobalExceptionHandlerTest.java
├── integration/                # 集成测试
│   └── AuthIntegrationTest.java
├── TestRunner.java             # 测试套件运行器
└── UserManagementApplicationTests.java
```

## 测试覆盖范围

### 1. 服务层测试 (Service Tests)

#### UserServiceTest
- ✅ 获取用户个人信息
- ✅ 更新用户个人信息
- ✅ 修改密码
- ✅ 创建新用户（管理员功能）
- ✅ 更新用户状态
- ✅ 删除用户
- ✅ 批量更新用户状态
- ✅ 获取用户统计信息
- ✅ 用户名/邮箱可用性检查
- ✅ 重置用户密码
- ✅ 各种业务异常场景

#### AuthServiceTest
- ✅ 用户登录
- ✅ 用户注册
- ✅ 刷新令牌
- ✅ 验证用户凭据
- ✅ 检查用户状态
- ✅ 各种认证异常场景

### 2. 控制器层测试 (Controller Tests)

#### UserControllerTest
- ✅ 获取当前用户信息
- ✅ 更新当前用户信息
- ✅ 修改密码
- ✅ 获取用户列表（管理员）
- ✅ 创建用户（管理员）
- ✅ 获取用户详情（管理员）
- ✅ 更新用户状态（管理员）
- ✅ 重置用户密码（管理员）
- ✅ 删除用户（管理员）
- ✅ 批量更新用户状态（管理员）
- ✅ 获取用户统计信息（管理员）
- ✅ 权限控制测试

#### AuthControllerTest
- ✅ 用户登录
- ✅ 用户注册
- ✅ 刷新令牌
- ✅ 用户登出
- ✅ 验证令牌
- ✅ 检查用户名/邮箱可用性
- ✅ 参数验证测试

### 3. 安全组件测试 (Security Tests)

#### JwtTokenProviderTest
- ✅ 生成访问令牌
- ✅ 生成刷新令牌
- ✅ 从令牌中获取用户信息
- ✅ 验证令牌有效性
- ✅ 处理过期令牌
- ✅ 处理无效令牌

#### UserDetailsServiceImplTest
- ✅ 根据用户名加载用户详情
- ✅ 根据用户ID加载用户详情
- ✅ 处理用户不存在的情况
- ✅ 处理禁用用户
- ✅ 处理多角色用户

### 4. DTO和异常处理测试

#### ApiResponseTest
- ✅ 成功响应构建
- ✅ 错误响应构建
- ✅ 泛型类型支持

#### GlobalExceptionHandlerTest
- ✅ 业务异常处理
- ✅ 参数验证异常处理
- ✅ 访问拒绝异常处理
- ✅ 认证异常处理
- ✅ 通用异常处理

### 5. 集成测试 (Integration Tests)

#### AuthIntegrationTest
- ✅ 完整的注册登录流程
- ✅ 重复用户名/邮箱检查
- ✅ 无效凭据处理
- ✅ 参数验证
- ✅ 用户名/邮箱可用性检查

## 运行测试

### 运行所有测试
```bash
cd backend
mvn test
```

### 运行特定测试类
```bash
mvn test -Dtest=UserServiceTest
mvn test -Dtest=AuthControllerTest
```

### 运行特定测试方法
```bash
mvn test -Dtest=UserServiceTest#getUserProfile_Success
```

### 运行测试套件
```bash
mvn test -Dtest=TestRunner
```

### 生成测试报告
```bash
mvn test jacoco:report
```

## 测试配置

### 测试环境配置
- 使用H2内存数据库进行测试
- 配置文件：`src/test/resources/application-test.yml`
- 测试时自动创建和销毁数据库表

### Mock配置
- 使用Mockito进行单元测试
- 使用@MockBean进行Spring Boot集成测试
- 使用@WebMvcTest进行控制器测试

## 测试最佳实践

### 1. 测试命名规范
- 方法名格式：`methodName_scenario_expectedResult`
- 例如：`getUserProfile_Success`, `login_InvalidCredentials`

### 2. 测试结构
- **Given**: 准备测试数据和Mock行为
- **When**: 执行被测试的方法
- **Then**: 验证结果和Mock调用

### 3. 断言使用
- 使用JUnit 5的断言方法
- 优先使用具体的断言而不是通用断言
- 提供有意义的断言消息

### 4. Mock使用
- 只Mock必要的依赖
- 验证重要的Mock调用
- 避免过度Mock

## 测试数据

### 测试用户数据
```java
User testUser = new User();
testUser.setId(1L);
testUser.setUsername("testuser");
testUser.setEmail("test@example.com");
testUser.setStatus(1);
```

### 测试角色数据
```java
Role testRole = new Role();
testRole.setId(1L);
testRole.setName("USER");
testRole.setDescription("普通用户");
```

## 持续集成

测试用例可以集成到CI/CD流水线中：

```yaml
# GitHub Actions示例
- name: Run Tests
  run: mvn test
  
- name: Generate Test Report
  run: mvn jacoco:report
  
- name: Upload Coverage
  uses: codecov/codecov-action@v1
```

## 故障排除

### 常见问题

1. **测试数据库连接问题**
   - 确保H2依赖已添加
   - 检查test配置文件

2. **Mock不生效**
   - 确保使用了正确的注解
   - 检查Mock的配置

3. **权限测试失败**
   - 确保使用了@WithMockUser注解
   - 检查角色配置

### 调试技巧

1. 启用详细日志
2. 使用@Sql注解准备测试数据
3. 使用@DirtiesContext重置Spring上下文

## 测试覆盖率目标

- 代码覆盖率：> 80%
- 分支覆盖率：> 70%
- 核心业务逻辑：> 90%

## 扩展测试

如需添加新的测试用例：

1. 在相应的包下创建测试类
2. 遵循现有的命名和结构规范
3. 添加必要的Mock和断言
4. 更新本文档

---

**注意**: 运行测试前请确保已安装Java 8+和Maven 3.6+。