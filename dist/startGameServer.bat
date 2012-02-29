@echo off
title Game Server Console
:start
echo Starting l2universe Game Server.
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
REM -------------------------------------
REM Default parameters for a basic server.
java -Djava.util.logging.manager=l2.universe.util.L2LogManager -Xms1024m -Xmx1024m -cp ./../libs/*;l2universe.jar l2.universe.gameserver.GameServer
REM
REM If you have a big server and lots of memory, you could experiment for example with
REM java -server -Xmx1536m -Xms1024m -Xmn512m -XX:PermSize=256m -XX:SurvivorRatio=8 -Xnoclassgc -XX:+AggressiveOpts
REM If you are having troubles on server shutdown (saving data),
REM add this to startup paramethers: -Djava.util.logging.manager=l2.universe.L2LogManager. Example:
REM java -Djava.util.logging.manager=l2.universe.util.L2LogManager -Xmx1024m -cp ./../libs/*;l2universe.jar l2.universe.gameserver.GameServer
REM -------------------------------------
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
