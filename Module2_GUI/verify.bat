@echo off
setlocal

chcp 65001 >nul
set "PROJECT_ROOT=%~dp0"
set "DRIVER=%PROJECT_ROOT%lib\sqlite-jdbc-3.53.4.0.jar"

call "%PROJECT_ROOT%build.bat"
if errorlevel 1 exit /b 1

pushd "%PROJECT_ROOT%"
java -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -cp "%PROJECT_ROOT%out;%DRIVER%" ua.edu.nubip.module2.Main --verify
set "RESULT=%ERRORLEVEL%"
popd

exit /b %RESULT%
