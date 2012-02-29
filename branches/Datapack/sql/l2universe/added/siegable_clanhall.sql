DROP TABLE IF EXISTS `siegable_clanhall`;
CREATE TABLE `siegable_clanhall` (
  `clanHallId` int(10) NOT NULL DEFAULT '0',
  `name` varchar(45) DEFAULT NULL,
  `ownerId` int(10) DEFAULT NULL,
  `desc` varchar(100) DEFAULT NULL,
  `location` varchar(100) DEFAULT NULL,
  `nextSiege` bigint(20) DEFAULT NULL,
  `siegeInterval` int(10) DEFAULT NULL,
  `siegeLenght` int(10) DEFAULT NULL,
  PRIMARY KEY (`clanHallId`)
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

INSERT INTO `siegable_clanhall` (`clanHallId`, `name`, `ownerId`, `desc`, `location`, `nextSiege`, `siegeInterval`, `siegeLenght`) VALUES
(21, 'Fortress of Resistance', 0, 'Contestable Clan Hall', 'Dion', 1293980399906, 604800000, 3600000),
(34, 'Devastated Castle', 0, 'Contestable Clan Hall', 'Aden', 1293980399906, 604800000, 3600000),
(35, 'Bandit StrongHold', 0, 'Contestable Clan Hall', 'Oren', 1293980399906, 604800000, 3600000),
(62, 'Rainbow Springs', 0, 'Contestable Clan Hall', 'Goddard', 1293980399906, 604800000, 3600000),
(63, 'Beast Farm', 0, 'Contestable Clan Hall', 'Rune', 1293980399906, 604800000, 3600000),
(64, 'Fortresss of the Dead', 0, 'Contestable Clan Hall', 'Rune', 1293980399906, 604800000, 3600000);