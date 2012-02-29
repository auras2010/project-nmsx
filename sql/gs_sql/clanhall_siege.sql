-- --------------------------------------------
-- Table structure for clanhall_siege
-- ---------------------------------------------
DROP TABLE IF EXISTS `clanhall_siege`;
CREATE TABLE `clanhall_siege` (
  `clanhall_id` int(3) DEFAULT NULL,
  `siege_date` double(20,0) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=latin1;

INSERT INTO `clanhall_siege` VALUES 
('21', '1265857200000'),
('34', '1265857200000'),
('62', '1286676000000'),
('64', '1265943600000');