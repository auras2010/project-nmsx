-- ------------------------------------------------
-- Table structure for record
-- ------------------------------------------------
DROP TABLE IF EXISTS `record`;
CREATE TABLE IF NOT EXISTS `record` (
  `maxplayer` int(5) NOT NULL DEFAULT '0',
  `date` date NOT NULL,
  PRIMARY KEY (`date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;