-- ----------------------------
-- Procedure `accessGetList`(accessLevelState)
-- AccessLevelState:
--  1) accessLevels < 0
--  2) accessLevels > 0
--  3) accessLevels = 0
-- NULL) all accessLevels
-- 
--   Procedure Show list of Access Levels.
-- ----------------------------
DROP PROCEDURE IF EXISTS `accessGetList`;
DELIMITER ;;
CREATE PROCEDURE `accessGetList`(
  `accLvl` TINYINT(1)
  )
    READS SQL DATA
BEGIN

	CASE accLvl
		/* Only deleted accounts */
		WHEN 1 THEN
			SELECT 
				login, accessLevel 
			FROM 
				accounts 
			WHERE 
				accessLevel < 0
			ORDER BY 
				login ASC;

		/* 'not normal' players */
		WHEN 2 THEN
			SELECT 
				login, accessLevel 
			FROM 
				accounts 
			WHERE 
				accessLevel > 0
			ORDER BY 
				login ASC;
	 
		/* Players */ 
		WHEN 3 THEN
			SELECT 
				login, accessLevel 
			FROM 
				accounts 
			WHERE 
				accessLevel = 0
			ORDER BY 
				login ASC;

		WHEN NULL THEN
			SELECT 
				login, accessLevel 
			FROM 
				accounts 
			ORDER BY 
				login ASC;

		/* Other not used cases */
		ELSE 
			BEGIN
      END;
	END CASE;
END
;;
DELIMITER ;

-- ----------------------------
-- Procedure `createUpdateAccount`(login, password, accessLvl)
-- Creating or updating account.
-- ----------------------------
DROP PROCEDURE IF EXISTS `createUpdateAccount`;
DELIMITER ;;
CREATE PROCEDURE `createUpdateAccount`(
  tLogin VARCHAR(45), 
  tPass VARCHAR(45), 
  tAcc TINYINT(3)
  )
    MODIFIES SQL DATA
BEGIN
	/* checking MAX and MIN lvl declare from calling procedure */
	SET @lAccessLevel = getRightAccessLvl(tAcc);

	/* account exist */
	IF checkingAccountExist(tLogin) THEN
		UPDATE accounts 
		SET 
			`password` = tPass,
			`accessLevel` = @lAccessLevel
		WHERE login = tLogin;
	ELSE /* account not exist */
		INSERT INTO accounts(login, password, accessLevel)
		VALUES ( tLogin, tPass, @lAccessLevel );
	END IF;
END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for `deleteAccount`(nameOfAccount)
--   Deleting account.
-- ----------------------------
DROP PROCEDURE IF EXISTS `deleteAccount`;
DELIMITER ;;
CREATE PROCEDURE `deleteAccount`(
  `accName` VARCHAR(45)
  )
BEGIN
	/* value for cursor done query */
	DECLARE done BOOLEAN DEFAULT 0;
	/* temp charId and clanId for the cursor */
	DECLARE lCharId, lClanId INT;

	/* cursor declaration */
	DECLARE charAccount CURSOR FOR
		SELECT charId, clanid
				FROM characters 
					WHERE account_name=accName;

	/* when cursor will be have 0 rows or end rows then done=1 */
	DECLARE CONTINUE HANDLER FOR NOT FOUND SET done=1;

	/* opening cursor */
	OPEN charAccount;

	REPEAT
		/* give values from cursor to temp values */
		FETCH charAccount INTO lCharId, lClanId;

		/* if character is CL then remove clan name and wars */
		IF charIsLeader(lCharId) > 0 THEN
			CALL deleteClan(lClanId);
			CALL deleteClanWar(lClanId);
		END IF;

		/* delete owner for CH if this character is CH Owner */
		CALL deleteOwnerCH(lCharId);
		
		/* delete character with settings, items, ... for this character */
		CALL deleteCharacter(lCharId);

	/* if is done don't repeat anumore and close cursor */ 
	UNTIL done END REPEAT;
	CLOSE charAccount;

	/* Delete Account */
	DELETE FROM accounts WHERE login=accName;

END
;;
DELIMITER ;

-- ----------------------------
-- Procedure `deleteCharacter`(characterId)
--   Deleting character and all his stuff.
-- ----------------------------
DROP PROCEDURE IF EXISTS `deleteCharacter`;
DELIMITER ;;
CREATE PROCEDURE `deleteCharacter`(
  `tCharId` INT(10)
  )
BEGIN
	/* friends */
	DELETE FROM character_friends 
	WHERE charId=tCharId;

	/* hennas */
	DELETE FROM character_hennas 
	WHERE charId=tCharId;

	/* merchant_lease */
	DELETE FROM merchant_lease 
	WHERE player_id=tCharId;

	/* macroses */
	DELETE FROM character_macroses 
	WHERE charId=tCharId;

	/* quests */
	DELETE FROM character_quests 
	WHERE charId=tCharId;

	/* recipebook */
	DELETE FROM character_recipebook 
	WHERE charId=tCharId;
				
	/* recommends */
	DELETE FROM character_recommends 
	WHERE charId=tCharId;

	/* skills */
	DELETE FROM character_skills 
	WHERE charId=tCharId;
				
	/* skills save */
	DELETE FROM character_skills_save 
	WHERE charId=tCharId;

	/* subclasses */
	DELETE FROM character_subclasses 
	WHERE charId=tCharId;

	/* shortcuts */
	DELETE FROM character_shortcuts 
	WHERE charId=tCharId;

	/* ui categories */
	DELETE FROM character_ui_categories 
	WHERE charId=tCharId;

	/* ui keys */
	/*DELETE FROM character_ui_keys 
	WHERE charId=tCharId;*/

	/* characters */
	DELETE FROM characters 
	WHERE charId=tCharId;

	/* items */
	DELETE FROM items 
	WHERE owner_id=tCharId;

	/* boxaccess ?? This not exist */
	/*DELETE FROM boxaccess 
	WHERE charname= '';*/

	/* TODO: delete pets, olympiad/noble/hero stuff */
END
;;
DELIMITER ;

-- ----------------------------
-- Procedure `deleteClan`(ClanLeaderId)
--   Deleting clan.
-- ----------------------------
DROP PROCEDURE IF EXISTS `deleteClan`;
DELIMITER ;;
CREATE PROCEDURE `deleteClan`(
  `tClanId` INT(10)
  )
BEGIN
	/* Deleting clan */
	DELETE FROM clan_data 
	WHERE clan_id=tClanId;

	/* Clan privileges */
	DELETE FROM clan_privs 
	WHERE clan_id=tClanId;

	/* Clan subpledges */
	DELETE FROM clan_subpledges 
	WHERE clan_id=tClanId;

	/* Clan skills */
	DELETE FROM clan_skills 
	WHERE clan_id=tClanId;

	/* and resering clan for all clan members */
	UPDATE characters SET clanid=0 
	WHERE clan_id=tClanId;

END
;;
DELIMITER ;

-- ----------------------------
-- Procedure `deleteClanWar`(ClanId)
--    Deleting all ClanWars
-- ----------------------------
DROP PROCEDURE IF EXISTS `deleteClanWar`;
DELIMITER ;;
CREATE PROCEDURE `deleteClanWar`(
  `tClanId` INT(10)
  )
BEGIN
	/* TODO: change to delete */
	/*DELETE FROM clan_wars */
	SELECT * FROM clan_wars
	WHERE 
		`clan1` = tClanId 
		OR 
		`clan2` = tClanId;
END
;;
DELIMITER ;

-- ----------------------------
-- Procedure `deleteOwnerCH`(OwnerId)
--   getting Clan Hall from user if is owner.
-- ----------------------------
DROP PROCEDURE IF EXISTS `deleteOwnerCH`;
DELIMITER ;;
CREATE PROCEDURE `deleteOwnerCH`(
  `tOwnerId` INT(10)
  )
BEGIN
	UPDATE clanhall SET ownerId=0, paidUntil=0, paid=0 WHERE ownerId=tOwnerId;
END
;;
DELIMITER ;

-- ----------------------------
-- Procedure `setAccountAccessLvl`(login, accessLevel)
-- ----------------------------
DROP PROCEDURE IF EXISTS `setAccountAccessLvl`;
DELIMITER ;;
CREATE PROCEDURE `setAccountAccessLvl`(
  `tLogin` VARCHAR(45), 
  `acc` TINYINT(3)
  )
BEGIN
	/* if account exist */
	IF checkingAccountExist(tLogin) THEN
		/* then set access level to account */
		UPDATE 
			accounts 
		SET 
			accessLevel=getRightAccessLvl(acc) 
		WHERE 
			login=tLogin;
	END IF;
END
;;
DELIMITER ;

-- ----------------------------
-- Procedure structure for `setRestart`(daysToNextRestart, timeRestartWhenShoulBe, contingToRestart)
--   procedure to set Restart time for Server.
-- ----------------------------
DROP PROCEDURE IF EXISTS `setRestart`;
DELIMITER ;;
CREATE PROCEDURE `setRestart`(
  `tDaysInterval` tinyint(3),
  `tTime` varchar(8),
  `tDelay` smallint(3)
  )
BEGIN
		DECLARE isRightTime BOOLEAN DEFAULT 0;

	/* TODO: Add TYPE_RESTART_TASK into com\l2jserver\gameserver\taskmanager\TaskManager.java and fix it */
	IF SUBSTRING(tTime,2,1) = ':' AND SUBSTRING(tTime,5,1) = ':' THEN
		SET tTime = CONCAT('0',tTime);
		SET isRightTime = 1;
	ELSEIF SUBSTRING(tTime,3,1) = ':' AND SUBSTRING(tTime,6,1) = ':' THEN
		SET isRightTime = 1;
	END IF;

	IF isRightTime THEN
		/* checking if Restart exist on database */
		SELECT COUNT(task) 
			INTO @counter 
				FROM global_tasks 
					WHERE task = 'Restart';
		
		/* if exist we will update only this */
		IF @counter THEN
			UPDATE global_tasks 
				SET param1 = tDaysInterval, param2 = tTime, param3 = tDelay
					WHERE task = 'Restart';
		ELSE
			INSERT INTO global_tasks(task,type,last_activation,param1,param2,param3) 
				VALUES ('Restart','TYPE_GLOBAL_TASK','0', tDaysInterval, tTime, tDelay);
		END IF;
	END IF;
END
;;
DELIMITER ;

-- ----------------------------
-- Function `charIsLeader`(characterId)
--   Function checking if character is ClanLeader.
-- ----------------------------
DROP FUNCTION IF EXISTS `charIsLeader`;
DELIMITER ;;
CREATE FUNCTION `charIsLeader`(
  `tLeaderId` INT(11)
  ) 
    RETURNS tinyint(1)
    READS SQL DATA
BEGIN
	SELECT COUNT(clan_id) INTO @isLeader FROM clan_data WHERE `leader_id` = tLeaderId;
	RETURN @isLeader;
END
;;
DELIMITER ;

-- ----------------------------
-- Function `checkingAccountExist`(userLogin)
--   Function for checking if account exist.
-- ----------------------------
DROP FUNCTION IF EXISTS `checkingAccountExist`;
DELIMITER ;;
CREATE FUNCTION `checkingAccountExist`(
  `log` VARCHAR(45)
  ) 
    RETURNS tinyint(1)
    READS SQL DATA
BEGIN
		/* checking if account exist */
		SELECT COUNT(login)
		INTO @existAccount
		FROM accounts
		WHERE login = log;

	RETURN @existAccount;
END
;;
DELIMITER ;

-- ----------------------------
-- Function structure for `getClanName`(clanLeaderId)
--   Funcrion returning name of the clan.
-- ----------------------------
DROP FUNCTION IF EXISTS `getClanName`;
DELIMITER ;;
CREATE FUNCTION `getClanName`(
  `tLeaderId` INT(10)
  ) 
    RETURNS varchar(50) CHARSET utf8
    READS SQL DATA
BEGIN
	SELECT clan_name INTO @clanName FROM clan_data WHERE `leader_id` = tLeaderId;
	RETURN @clanName;
END
;;
DELIMITER ;

-- ----------------------------
-- Function structure for `getRightAccessLvl`
--   Checking if entering access level is not greater than max value and not smaller than min value.
-- ----------------------------
DROP FUNCTION IF EXISTS `getRightAccessLvl`;
DELIMITER ;;
CREATE FUNCTION `getRightAccessLvl`(
  `acc` TINYINT(3)
  ) 
    RETURNS tinyint(3)
    READS SQL DATA
BEGIN
	IF acc > 127 THEN
		SET @lAccessLevel = 127;
	ELSEIF acc < -1 THEN
		SET @lAccessLevel = -1;
	ELSE
		SET @lAccessLevel = acc;
	END IF;

	RETURN @lAccessLevel;
END
;;
DELIMITER ;
