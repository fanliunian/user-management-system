@echo off
echo ========================================
echo 运行可正常工作的测试用例
echo ========================================
echo.

echo 正在运行基础测试...
mvn -Dtest=SimpleTest test -DfailIfNoTests=false

echo.
echo 正在运行API响应测试...
mvn -Dtest=SimpleApiResponseTest test -DfailIfNoTests=false

echo.
echo 正在运行异常处理测试...
mvn -Dtest=SimpleExceptionHandlerTest test -DfailIfNoTests=false

echo.
echo 正在运行JWT令牌测试...
mvn -Dtest=SimpleJwtTokenProviderTest test -DfailIfNoTests=false

echo.
echo 正在运行认证服务测试...
mvn -Dtest=SimpleAuthServiceTest test -DfailIfNoTests=false

echo.
echo 正在运行用户服务测试...
mvn -Dtest=SimpleUserServiceTest test -DfailIfNoTests=false

echo.
echo 正在运行服务层测试...
mvn -Dtest=SimpleServiceTest test -DfailIfNoTests=false

echo.
echo 正在运行用户控制器测试...
mvn -Dtest=SimpleUserControllerTest test -DfailIfNoTests=false

echo.
echo 正在运行集成测试...
mvn -Dtest=SimpleIntegrationTest test -DfailIfNoTests=false

echo.
echo ========================================
echo 测试完成
echo ========================================
echo 生成测试覆盖率报告...
mvn jacoco:report
echo.
echo 测试覆盖率报告已生成到: target/site/jacoco/index.html
echo.
pause