#!/bin/sh
java -Djava.util.logging.config.file=console.cfg -cp ./../libs/*:l2universelogin.jar l2.universe.accountmanager.SQLAccountManager
