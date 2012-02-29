-- --------------------------------------------
-- Table structure for comteleport
-- ---------------------------------------------
DROP TABLE IF EXISTS `comteleport`;
CREATE TABLE IF NOT EXISTS `comteleport` (
`TpId` INT NOT NULL AUTO_INCREMENT ,
`name` VARCHAR( 45 ) NOT NULL ,
`charId` INT( 11 ) NOT NULL DEFAULT '0',
`xPos` INT( 9 ) NOT NULL DEFAULT '0',
`yPos` INT( 9 ) NOT NULL DEFAULT '0',
`zPos` INT( 9 ) NOT NULL DEFAULT '0',
PRIMARY KEY (`TpId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;