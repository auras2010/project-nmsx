-- --------------------------------------------
-- Table structure for global_tasks
-- ---------------------------------------------
CREATE TABLE IF NOT EXISTS `global_tasks` (
  `id` int(11) NOT NULL auto_increment,
  `task` varchar(50) NOT NULL default '',
  `type` varchar(50) NOT NULL default '',
  `last_activation` bigint(13) unsigned NOT NULL DEFAULT '0',
  `param1` varchar(100) NOT NULL default '',
  `param2` varchar(100) NOT NULL default '',
  `param3` varchar(255) NOT NULL default '',
  PRIMARY KEY (`id`)
);

-- INSERT INTO `global_tasks` VALUES
-- (100,'Restart','TYPE_GLOBAL_TASK','2147483647','1','03:50:00','600');