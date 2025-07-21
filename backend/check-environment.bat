@echo off
echo ========================================
echo Java开发环境检测
echo ========================================
echo.

echo 1. 检查Java版本:
java -version
echo.

echo 2. 检查JAVA_HOME环境变量:
echo JAVA_HOME=%JAVA_HOME%
echo.

echo 3. 检查Maven版本:
mvn -version
echo.

echo 4. 检查Maven配置:
echo MAVEN_HOME=%MAVEN_HOME%
echo M2_HOME=%M2_HOME%
echo.

echo 5. 检查PATH中的Java和Maven:
echo PATH中的Java路径:
where java
echo.
echo PATH中的Maven路径:
where mvn
echo.

echo 6. 测试Maven编译:
echo 正在测试Maven编译...
cd /d "%~dp0"
mvn compile -q
if %errorlevel% equ 0 (
    echo ✓ Maven编译成功
) else (
    echo ✗ Maven编译失败
)
echo.

echo 7. 检查项目依赖:
echo 正在检查项目依赖...
mvn dependency:tree -q | findstr "ERROR\|WARN" > nul
if %errorlevel% equ 0 (
    echo ⚠ 发现依赖问题，请检查
) else (
    echo ✓ 项目依赖正常
)
echo.

echo ========================================
echo 环境检测完成
echo ========================================
pause