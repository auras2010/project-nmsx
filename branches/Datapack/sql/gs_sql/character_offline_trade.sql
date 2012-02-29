-- --------------------------------------------------
-- Table structure for character_offline_trade
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS `character_offline_trade` (
  `charId` INT UNSIGNED NOT NULL DEFAULT 0,
  `time` bigint(13) unsigned NOT NULL DEFAULT '0',
  `type` tinyint(4) NOT NULL DEFAULT '0',
  `title` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`charId`)
);