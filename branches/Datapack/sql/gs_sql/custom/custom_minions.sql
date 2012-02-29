-- --------------------------------------------
-- Table structure for custom_minions
-- --------------------------------------------
DROP TABLE IF EXISTS `custom_minions`;
CREATE TABLE `custom_minions` (
  `boss_id` int(11) NOT NULL default '0',
  `minion_id` int(11) NOT NULL default '0',
  `amount_min` int(4) NOT NULL default '0',
  `amount_max` int(4) NOT NULL default '0',
  PRIMARY KEY (`boss_id`,`minion_id`)
) ENGINE = MYISAM DEFAULT CHARSET=utf8;

INSERT INTO `custom_minions` VALUES
-- Bloodshed Event Minion
(2009010,2009011,1,1),
(2009010,2009012,1,1),
-- Beleth minion
(2010008,2010006,2,6),
-- Tiat minion
(2010009,22538,2,12);