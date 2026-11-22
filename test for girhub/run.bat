@echo off
title Employee Payroll Management System
echo ===================================================================
echo Starting Employee Payroll Management System (Spring Boot 3)
echo ===================================================================

REM Check for installed Java
if exist "%USERPROFILE%\.antigravity-ide\extensions\redhat.java-1.58.0-win32-x64\jre\25.0.4.1-win32-x86_64\bin\java.exe" (
    set "JAVA_HOME=%USERPROFILE%\.antigravity-ide\extensions\redhat.java-1.58.0-win32-x64\jre\25.0.4.1-win32-x86_64"
    set "PATH=%JAVA_HOME%\bin;%PATH%"
    echo Using detected OpenJDK at %JAVA_HOME%
)

call .\mvnw.cmd spring-boot:run
pause
