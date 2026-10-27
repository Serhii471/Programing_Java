@echo off
setlocal

chcp 65001 >nul

set "PROJECT_ROOT=%~dp0"
set "DRIVER=%PROJECT_ROOT%lib\sqlite-jdbc-3.53.4.0.jar"
set "SOURCE=%PROJECT_ROOT%src\Main.java"
set "OUTPUT=%PROJECT_ROOT%out"

if not exist "%DRIVER%" (
    echo Помилка: не знайдено JDBC-драйвер:
    echo %DRIVER%
    exit /b 1
)

if not exist "%OUTPUT%" mkdir "%OUTPUT%"

javac -encoding UTF-8 -d "%OUTPUT%" "%SOURCE%"
if errorlevel 1 (
    echo Помилка компіляції. Перевірте, чи встановлено JDK 17 або новішу версію.
    exit /b 1
)

pushd "%PROJECT_ROOT%"
java -Dfile.encoding=UTF-8 -Dsun.stdout.encoding=UTF-8 -Dsun.stderr.encoding=UTF-8 -cp "%OUTPUT%;%DRIVER%" Main
set "RESULT=%ERRORLEVEL%"
popd

exit /b %RESULT%
