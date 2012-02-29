-- --------------------------------------------
-- Table structure for grandboss_list
-- ---------------------------------------------
CREATE TABLE IF NOT EXISTS `grandboss_list` (
  `player_id` decimal(11,0) NOT NULL,
  `zone` decimal(11,0) NOT NULL,
  PRIMARY KEY (`player_id`,`zone`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;