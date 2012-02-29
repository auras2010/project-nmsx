-- ------------------------------------
-- Table structure for hide_and_seek
-- -------------------------------------
DROP TABLE IF EXISTS `hide_and_seek`;
CREATE TABLE IF NOT EXISTS `hide_and_seek` (
  `npc` int(10) DEFAULT NULL,
  `x` int(10) DEFAULT NULL,
  `y` int(10) DEFAULT NULL,
  `z` int(10) DEFAULT NULL,
  `first_clue` varchar(100) DEFAULT NULL,
  `second_clue` varchar(100) DEFAULT NULL,
  `third_clue` varchar(100) DEFAULT NULL,
  `rewards` varchar(100) DEFAULT NULL
) ENGINE = MYISAM DEFAULT CHARSET=utf8;

INSERT INTO `hide_and_seek` VALUES
(90001, 169754, 16519, -3397, 'Im near Aden. Many Skeletons around...', 'Im in a rest zone...', 'Im behind a tree of cemetary!', '57,10000;5575,10'),
(90002, 83467, 150593, -3533, 'I can hear how potions are cooked...', 'Im behind a shop...', 'Im in the Giran Castle Town!', '57,10000;5575,10'),
(90003, 178289, -85594, -7217, 'Im in a undergound site...', 'I can see 4 entrances to anywhere...', 'You can go to Frintezza from here!', '57,10000;5575,10');
