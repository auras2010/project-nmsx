CREATE TABLE `buffer_configuration` (
  `section_id` int(11) DEFAULT NULL,
  `configDesc` varchar(30) DEFAULT NULL,
  `configInfo` varchar(150) DEFAULT NULL,
  `configName` varchar(30) NOT NULL DEFAULT '',
  `configValue` varchar(30) DEFAULT NULL,
  `usableValues` varchar(40) DEFAULT NULL,
  PRIMARY KEY (`configName`)
) ENGINE=InnoDB DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of buffer_configuration
-- ----------------------------
INSERT INTO `buffer_configuration` VALUES 
('3', 'Buff price', 'Enter a value from 1 to 2000000000', 'buffPrice', '2500', 'range,1,2000000000'),
('3', 'Buff removing price', 'Enter a value from 1 to 2000000000', 'buffRemovePrice', '5000', 'range,1,2000000000'),
('3', 'Buff Set Price', 'Enter a value from 0 to 2000000000', 'buffSetPrice', '1000000', 'range,0,2000000000'),
('1', 'Buff player with karma', 'Select a value from box and hit update', 'buffWithKarma', 'True', 'bool,True,False'),
('3', 'Chant price', 'Enter a value from 1 to 2000000000', 'chantPrice', '10000', 'range,1,2000000000'),
('1', 'Consumable ID', 'Enter a valid item ID', 'consumableId', '57', 'range,1,10000'),
('3', 'Dance price', 'Enter a value from 1 to 2000000000', 'dancePrice', '10000', 'range,1,2000000000'),
('2', 'Enable removing buffs', 'Select a value from box and hit update', 'enableBuffRemove', 'True', 'bool,True,False'),
('2', 'Enable Buffs', 'Select a value from box and hit update', 'enableBuffs', 'True', 'bool,True,False'),
('2', 'Enable Buff Set', 'Select a value from box and hit update', 'enableBuffSet', 'True', 'bool,True,False'),
('2', 'Enable Chants', 'Select a value from box and hit update', 'enableChants', 'True', 'bool,True,False'),
('2', 'Enable Dances', 'Select a value from box and hit update', 'enableDances', 'True', 'bool,True,False'),
('2', 'Enable healing option', 'Select a value from box and hit update', 'enableHeal', 'True', 'bool,True,False'),
('2', 'Enable Kamael buffs', 'Select a value from box and hit update', 'enableKamael', 'True', 'bool,True,False'),
('2', 'Enable Songs', 'Select a value from box and hit update', 'enableSongs', 'True', 'bool,True,False'),
('2', 'Enable Special buffs', 'Select a value from box and hit update', 'enableSpecial', 'True', 'bool,True,False'),
('1', 'Buffs for free', 'Select a value from box and hit update\r\n', 'freeBuffs', 'False', 'bool,True,False'),
('3', 'Healing price', 'Enter a value from 1 to 2000000000\r\n', 'healPrice', '1000', 'range,1,2000000000'),
('3', 'Kamael buff price', 'Enter a value from 1 to 2000000000', 'kamaelPrice', '10000', 'range,1,2000000000'),
('1', 'Minimum level', 'Enter a value from 1 to 85', 'minLevel', '5', 'range,1,85'),
('3', 'PVP Zone Price Multiplier', 'Enter a value from 1 to 100', 'pvpMultiplier', '5', 'range,1,100'),
('3', 'Scheme Buff price', 'Enter a value from 0 to 2000000000', 'schemeBuffPrice', '1000000', 'range,0,2000000000'),
('1', 'Schemes per player', 'Enter a value from 1 to 10', 'schemeCount', '5', 'range,1,10'),
('1', 'Scheme system', 'Select a value from box and hit update', 'schemeSystem', 'Enabled', 'custom2,0,0'),
('3', 'Song price', 'Enter a value from 1 to 2000000000', 'songPrice', '10000', 'range,1,2000000000'),
('3', 'Special buff price', 'Enter a value from 1 to 2000000000', 'specialPrice', '100000', 'range,1,2000000000'),
('1', 'Window Style', 'Select a value from box and hit update\r\n', 'style', 'Icons+Buttons', 'custom,0,0'),
('1', 'Enable time out', 'Select a value from box and hit update', 'timeOut', 'True', 'bool,True,False'),
('1', 'Time out time', 'Enter a value from 1 to 10', 'timeOutTime', '2', 'range,1,10'),
('1', 'Window Title', 'Seperate new words with \',\' !<br>Example: My,buffer\r\n\r\n\r\n', 'title', 'Buffer', 'string,3,36'),
('4', 'VIP Buffer', 'Select a value from box and hit update', 'vipBuffer', 'False', 'bool,True,False'),
('4', 'VIP Minimum access level', 'Enter a value from 1 to 200', 'vipBufferMinAccessLevel', '1', 'range,1,200');
