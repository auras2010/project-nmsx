-- --------------------------------------------
-- Table structure for ctf
-- ---------------------------------------------
DROP TABLE IF EXISTS `ctf`;
CREATE TABLE `ctf` (
`eventName` varchar(255) NOT NULL DEFAULT '',
`eventDesc` varchar(255) NOT NULL DEFAULT '',
`joiningLocation` varchar(255) NOT NULL DEFAULT '',
`minlvl` int(4) NOT NULL DEFAULT '0',
`maxlvl` int(4) NOT NULL DEFAULT '0',
`npcId` int(11) NOT NULL DEFAULT '0',
`npcX` int(11) NOT NULL DEFAULT '0',
`npcY` int(11) NOT NULL DEFAULT '0',
`npcZ` int(11) NOT NULL DEFAULT '0',
`npcHeading` int(11) NOT NULL DEFAULT '0',
`rewardId` int(11) NOT NULL DEFAULT '0',
`rewardAmount` int(11) NOT NULL DEFAULT '0',
`teamsCount` int(4) NOT NULL DEFAULT '0',
`joinTime` int(11) NOT NULL DEFAULT '0',
`eventTime` int(11) NOT NULL DEFAULT '0',
`minPlayers` int(4) NOT NULL DEFAULT '0',
`maxPlayers` int(4) NOT NULL DEFAULT '0'
) ENGINE = MYISAM DEFAULT CHARSET=utf8;

INSERT INTO `ctf` VALUES 
('CTF', 'Capture The Flag', 'Giran', '76', '86', '70004', '82862', '148332', '-3469', '20467', '20234', '1', '2', '5', '15', '2', '30');
