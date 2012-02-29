-- ---------------------------------
-- Table structure for vote_system
-- ---------------------------------
DROP TABLE IF EXISTS `vote_system`;
CREATE TABLE `vote_system` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `account` varchar(20) NOT NULL DEFAULT '',
  `lastip` varchar(50) DEFAULT NULL,
  `lasttime` int(11) DEFAULT NULL,
  `bannerid` int(11) DEFAULT NULL,
  PRIMARY KEY (`id`,`account`)
) ENGINE=MyISAM AUTO_INCREMENT=1 DEFAULT CHARSET=latin1;