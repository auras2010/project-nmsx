-- --------------------------------------------
-- Table structure for ctf_teams
-- ---------------------------------------------
DROP TABLE IF EXISTS `ctf_teams`;
CREATE TABLE `ctf_teams` (
`teamId` int(4) NOT NULL DEFAULT '0',
`teamName` varchar(255) NOT NULL DEFAULT '',
`teamX` int(11) NOT NULL DEFAULT '0',
`teamY` int(11) NOT NULL DEFAULT '0',
`teamZ` int(11) NOT NULL DEFAULT '0',
`teamColor` int(11) NOT NULL DEFAULT '0',
`flagX` int(11) NOT NULL DEFAULT '0',
`flagY` int(11) NOT NULL DEFAULT '0',
`flagZ` int(11) NOT NULL DEFAULT '0',
PRIMARY KEY (`teamId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `ctf_teams` VALUES 
('0', 'Red', '146829', '152586', '-12170', '80915', '147042', '152630', '-12173'),
('1', 'Blue', '145179', '152573', '-12170', '330099', '144848', '152573', '-12184');