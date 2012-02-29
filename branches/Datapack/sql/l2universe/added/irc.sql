-- ----------------------------
-- Table structure for irc
-- ----------------------------
DROP TABLE IF EXISTS `irc`;
CREATE TABLE IF NOT EXISTS `irc` (
  `login` varchar(45) NOT NULL,
  `password` varchar(45) NOT NULL,
  `access_level` int(11) DEFAULT '0',
  PRIMARY KEY  (`login`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;