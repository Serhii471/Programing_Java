@echo off
setlocal

chcp 65001 >nul
set "PROJECT_ROOT=%~dp0"

pushd "%PROJECT_ROOT%"
if not exist "docs" mkdir "docs"
if exist "docs\api" rmdir /s /q "docs\api"

javadoc -encoding UTF-8 -charset UTF-8 -docencoding UTF-8 -private -d "docs\api" -sourcepath "src" -subpackages ua.edu.nubip.module2
if errorlevel 1 (
    popd
    exit /b 1
)

echo API documentation generated: docs\api\index.html
popd
exit /b 0
