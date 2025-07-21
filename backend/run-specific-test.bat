@echo off
echo ========================================
echo 运行特定测试类
echo ========================================
echo.

if "%1"=="" (
    echo 请提供测试类名称
    echo 用法: run-specific-test.bat TestClassName
    exit /b 1
)

echo 正在运行测试类: %1
echo.

mvn -Dtest=%1 test -DfailIfNoTests=false

echo.
echo ========================================
echo 测试完成
echo ========================================
pause