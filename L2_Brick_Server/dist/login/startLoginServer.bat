@echo off
title Login Server Console
color 0b
:start
echo Starting L2 Brick Login Server.
echo.
java -Xms128m -Xmx256m  -cp ./../libs/*;l2brick_login.jar l2.brick.loginserver.L2LoginServer
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
