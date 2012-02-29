@echo off
color 17
cls
java -Djava.util.logging.config.file=console.cfg -cp ./../libs/*;l2universelogin.jar l2.universe.gsregistering.BaseGameServerRegister -c
exit