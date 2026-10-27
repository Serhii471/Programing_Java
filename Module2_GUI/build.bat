@echo off
setlocal

set "PROJECT_ROOT=%~dp0"
set "OUTPUT=%PROJECT_ROOT%out"
set "PACKAGE_ROOT=%PROJECT_ROOT%src\ua\edu\nubip\module2"

if not exist "%OUTPUT%" mkdir "%OUTPUT%"

javac -encoding UTF-8 -Xlint:all -d "%OUTPUT%" ^
    "%PACKAGE_ROOT%\Main.java" ^
    "%PACKAGE_ROOT%\IntegrationVerifier.java" ^
    "%PACKAGE_ROOT%\model\FinancialInstitution.java" ^
    "%PACKAGE_ROOT%\model\SampleData.java" ^
    "%PACKAGE_ROOT%\service\InstitutionArrayManager.java" ^
    "%PACKAGE_ROOT%\io\InstitutionFileService.java" ^
    "%PACKAGE_ROOT%\db\InstitutionRepository.java" ^
    "%PACKAGE_ROOT%\ui\MainFrame.java"
if errorlevel 1 exit /b 1

exit /b 0
