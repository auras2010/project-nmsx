-- --------------------------------------------
-- Table structure for custom_npcAIData
-- --------------------------------------------
DROP TABLE IF EXISTS `custom_npcaidata`;
CREATE TABLE IF NOT EXISTS `custom_npcaidata` (
  `npc_id` int(5) NOT NULL DEFAULT '0',
  `skill_chance` int(3) DEFAULT '15',
  `primary_attack` int(1) DEFAULT NULL,
  `can_move` tinyint(1) NOT NULL DEFAULT 1,
  `minrangeskill` int(5) DEFAULT NULL,
  `minrangechance` int(3) DEFAULT NULL,
  `maxrangeskill` int(5) DEFAULT NULL,
  `maxrangechance` int(3) DEFAULT NULL,
  `soulshot` int(4) DEFAULT '0',
  `spiritshot` int(4) DEFAULT '0',
  `spschance` int(3) DEFAULT '0',
  `sschance` int(3) DEFAULT '0',
  `ischaos` int(4) DEFAULT NULL,
  `clan` varchar(40) DEFAULT '',
  `clan_range` int(4) DEFAULT '0',
  `enemyRange` int(4) DEFAULT NULL,
  `enemyClan` varchar(40) DEFAULT NULL,
  `dodge` int(3) DEFAULT NULL,
  `ai_type` varchar(8) DEFAULT 'fighter',
  PRIMARY KEY (`npc_id`)
) ENGINE = MYISAM DEFAULT CHARSET=utf8;

INSERT INTO custom_npcaidata VALUES
-- Ai data for BloodSheed event
(2010001,15,NULL,1,NULL,NULL,NULL,NULL,0,0,0,0,NULL,'',0,NULL,NULL,NULL,'fighter'),
(2010002,15,NULL,1,NULL,NULL,NULL,NULL,0,0,0,0,NULL,'',0,NULL,NULL,NULL,'fighter'),
(2010003,15,NULL,1,NULL,NULL,NULL,NULL,0,0,0,0,NULL,'',0,NULL,NULL,NULL,'fighter'),
(2010004,15,NULL,1,NULL,NULL,NULL,NULL,0,0,0,0,NULL,'',0,NULL,NULL,NULL,'fighter'),
(2010005,15,NULL,1,NULL,NULL,NULL,NULL,0,0,0,0,NULL,'',0,NULL,NULL,NULL,'fighter'),
(2010006,15,NULL,1,NULL,NULL,NULL,NULL,0,0,0,0,NULL,'',0,NULL,NULL,NULL,'fighter'),
(2010007,15,NULL,1,NULL,NULL,NULL,NULL,0,0,0,0,NULL,'',0,NULL,NULL,NULL,'fighter'),
(2010008,15,NULL,1,NULL,NULL,NULL,NULL,0,0,0,0,NULL,'',0,NULL,NULL,NULL,'fighter'),
(2010009,15,NULL,1,NULL,NULL,NULL,NULL,0,0,0,0,NULL,'',0,NULL,NULL,NULL,'fighter'),
(2010013,15,NULL,1,NULL,NULL,NULL,NULL,0,0,0,0,NULL,'',0,NULL,NULL,NULL,'fighter'),
-- Seed of Infinity
(22509,40,NULL,1,NULL,NULL,NULL,NULL,0,0,0,0,NULL,'SeedOfInfinity',900,NULL,NULL,NULL,'balanced'),
(22510,40,NULL,1,NULL,NULL,NULL,NULL,0,0,0,0,NULL,'SeedOfInfinity',900,NULL,NULL,NULL,'balanced'),
(22511,40,NULL,1,NULL,NULL,NULL,NULL,0,0,0,0,NULL,'SeedOfInfinity',900,NULL,NULL,NULL,'balanced'),
(22512,40,NULL,1,NULL,NULL,NULL,NULL,0,0,0,0,NULL,'SeedOfInfinity',900,NULL,NULL,NULL,'balanced'),
(22513,40,NULL,1,NULL,NULL,NULL,NULL,0,0,0,0,NULL,'SeedOfInfinity',900,NULL,NULL,NULL,'balanced'),
(22514,40,NULL,1,NULL,NULL,NULL,NULL,0,0,0,0,NULL,'SeedOfInfinity',900,NULL,NULL,NULL,'balanced'),
(25665,25,NULL,1,NULL,NULL,NULL,NULL,0,0,0,0,NULL,'SeedOfInfinity',900,NULL,NULL,NULL,'balanced'),
(25666,25,NULL,1,NULL,NULL,NULL,NULL,0,0,0,0,NULL,'SeedOfInfinity',900,NULL,NULL,NULL,'balanced');