-- --------------------------------------------------
-- Table structure for character_premium_items
-- --------------------------------------------------
CREATE TABLE IF NOT EXISTS `character_premium_items` (
  `Id` int(11) NOT NULL auto_increment,
  `charId` int(11) default NULL,
  `charName` varchar(35) default NULL,
  `itemId` int(11) NOT NULL,
  `itemCount` bigint(20) unsigned NOT NULL,
  `itemSender` varchar(50) NOT NULL,
  `vitamine` tinyint(2) NOT NULL default '0',
  KEY `Id` (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;