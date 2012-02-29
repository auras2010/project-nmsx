@echo off
title Login Server Console
:start
echo Starting l2universe Login Server.
echo.

:: EXAMPLE PATH
:: set JAVA_PATH="C:\Program Files\Java\jdk1.6.0_20"
set JAVA_PATH=""

if NOT EXIST "%JAVA_HOME%"\lib\tools.jar GOTO check_path
goto je

:check_path
if NOT EXIST %JAVA_PATH%\lib\tools.jar GOTO jne
SET JAVA_HOME="%JAVA_PATH%"
goto je

:jne
ECHO.
echo     JAVA JDK not exists. 
echo     Set path to JAVA JDK into JAVA_PATH in this script file or 
echo     set JAVA_HOME variable into system variables.
goto end

:je
java -Xms128m -Xmx128m  -cp ./../libs/*;l2universelogin.jar l2.universe.loginserver.L2LoginServer
if ERRORLEVEL 2 goto restart
if ERRORLEVEL 1 goto error
goto end
:restart
echo.
echo Admin Restart ...
echo.
goto start
:error
echo.
echo Server terminated abnormaly
echo.
:end
echo.
echo server terminated
echo.
pause
