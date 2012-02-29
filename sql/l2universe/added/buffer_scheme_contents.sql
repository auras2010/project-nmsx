DROP TABLE IF EXISTS `buffer_scheme_contents`;
CREATE TABLE `buffer_scheme_contents` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `scheme_id` int(11) DEFAULT NULL,
  `skill_id` int(8) DEFAULT NULL,
  `skill_level` int(4) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of buffer_scheme_contents
-- ----------------------------
INSERT INTO `buffer_scheme_contents` VALUES ('28', '2', '1304', '3');
INSERT INTO `buffer_scheme_contents` VALUES ('29', '2', '1354', '1');
INSERT INTO `buffer_scheme_contents` VALUES ('30', '2', '1243', '6');
INSERT INTO `buffer_scheme_contents` VALUES ('31', '2', '1048', '6');
INSERT INTO `buffer_scheme_contents` VALUES ('32', '2', '4699', '13');
